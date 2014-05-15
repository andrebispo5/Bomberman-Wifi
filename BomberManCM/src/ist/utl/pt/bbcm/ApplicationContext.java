package ist.utl.pt.bbcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ist.utl.pt.bbcm.networking.IncommingCommTask;
import ist.utl.pt.bbcm.networking.KnownPorts;
import ist.utl.pt.bbcm.wdsim.SimWifiP2pBroadcastReceiver;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDevice;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDeviceList;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.Channel;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.utl.ist.cmov.wifidirect.service.SimWifiP2pService;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketServer;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;
import ist.utl.pt.bbcm.networking.ReceiveCommTask;

public class ApplicationContext extends Application implements
		PeerListListener, GroupInfoListener {
	private SimWifiP2pManager mManager = null;
	private Channel mChannel = null;
	private Messenger mService = null;
	public boolean mBound = false;
	public SimWifiP2pSocketServer mSrvSocket = null;
	public ReceiveCommTask mComm = null;
	public SimWifiP2pSocket mCliSocket = null;
	private Context ctx = this;
	public String myIp = "noMyIp";
	public HashMap<String,String> peersIP = new HashMap<String,String>();
	public boolean isGO = false;
	public String ipGO = "noIP";
	public ServiceConnection mConnection = newServiceConnection();
	public SimWifiP2pSocketServer ackSocket;
	public int numAcks = 0;

	@Override
	public void onCreate(){
		initWDsim();
	}

	private void initWDsim() {
			// initialize the WDSim API
			SimWifiP2pSocketManager.Init(getApplicationContext());

			// register broadcast receiver
			IntentFilter filter = new IntentFilter();
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
			SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(this);
			registerReceiver(receiver, filter);

			Intent intent = new Intent(getApplicationContext(),
					SimWifiP2pService.class);
			mBound = getApplicationContext().bindService(intent, mConnection,
					Context.BIND_AUTO_CREATE);
			// spawn the chat server background task
			new IncommingCommTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}

	public void storeGroupPlayersWDsim() {
		if (mBound) {
			mManager.requestGroupInfo(mChannel,
					(GroupInfoListener) ApplicationContext.this);
		} else {
			Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public SimWifiP2pManager getManager() {
		return mManager;
	}
	
	public Channel getChannel() {
		return mChannel;
	}
	
		
	/*
	 * Listeners associated to WDSim
	 */
	@Override
	public void onPeersAvailable(SimWifiP2pDeviceList peers) {
		ArrayList<String> peersIP = new ArrayList<String>();
		
		// compile list of devices in range
		for (SimWifiP2pDevice device : peers.getDeviceList()) {
			String ip = device.getVirtIp();
			peersIP.add(ip);
			Toast.makeText(ctx, "Found Peer: "+ ip,
    				Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, 
			SimWifiP2pInfo groupInfo) {
			Log.i("onGroupInfoAvailable", "Group info changed");
			String myDevName = groupInfo.getDeviceName();
			SimWifiP2pDevice myDev = devices.getByName(myDevName);
			this.myIp = myDev.getVirtIp();
			removeUnreachablePeers(devices, groupInfo);
			if(!isGO && groupInfo.askIsConnectionPossible(groupInfo.getGO())){
				SimWifiP2pDevice GO = devices.getByName(groupInfo.getGO());
				this.ipGO = GO.getVirtIp();
				Toast.makeText(ctx, "Found GO: "+ this.ipGO,
	    				Toast.LENGTH_SHORT).show();
			}else {
				this.ipGO = "none";
			}
	}

	private void removeUnreachablePeers(SimWifiP2pDeviceList devices,
			SimWifiP2pInfo groupInfo) {
		ArrayList<String> toRemove = new ArrayList<String>();
		for(Entry<String,String> e : peersIP.entrySet()){
			SimWifiP2pDevice storeDev = devices.get(e.getValue());
			if(storeDev!=null){
				if(!groupInfo.askIsConnectionPossible(storeDev.deviceName)){
					toRemove.add(e.getKey());
				}
			}
		}
		for(String id : toRemove){
			peersIP.remove(id);
		}
	}
	
	private ServiceConnection newServiceConnection() {
		return new ServiceConnection() {
			// callbacks for service binding, passed to bindService()
			@Override
			public void onServiceConnected(ComponentName className, IBinder service) {
				mService = new Messenger(service);
				mManager = new SimWifiP2pManager(mService);
				mChannel = mManager.initialize(ctx, getMainLooper(), null);
				mBound = true;
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				mService = null;
				mManager = null;
				mChannel = null;
				mBound = false;
			}
		};
	}

	public String getPeersIps() {
		String ips = "";
		if(peersIP.size()==0){
			ips="none";
		}else{	
			for(Entry p : peersIP.entrySet()){
				ips += p.getKey()+"="+p.getValue()+";";
			}	
		}
		return ips;
	}
	
}

