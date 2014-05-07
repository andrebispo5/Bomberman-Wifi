package pt.utl.ist.cmov.wifidirect.sockets;

import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDeviceList;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SimWifiP2pSocketBroadcastReceiver extends BroadcastReceiver {

	public static String TAG = "SimWifiP2pSocketBroadcastReceiver";
	
    private SimWifiP2pSocketManager mSockManager;

    public SimWifiP2pSocketBroadcastReceiver(SimWifiP2pSocketManager sockManager) {
        super();
        mSockManager = sockManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        	boolean wifion = false;
            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
        		wifion = true;
            } else {
        		wifion = false;
            }
            mSockManager.handleActionStateChanged(wifion);

        } else if (SimWifiP2pBroadcast.WIFI_P2P_DEVICE_INFO_CHANGED_ACTION.equals(action)) {
        	SimWifiP2pDeviceList devs = (SimWifiP2pDeviceList) intent.getSerializableExtra(
        		SimWifiP2pBroadcast.EXTRA_DEVICE_INFO);
        	mSockManager.handleActionDeviceInfoChanged(devs);

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	SimWifiP2pDeviceList dlist = (SimWifiP2pDeviceList) intent.getSerializableExtra(
        		SimWifiP2pBroadcast.EXTRA_DEVICE_LIST);
            mSockManager.handleActionPeersChanged(dlist);

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {
        	
        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        	mSockManager.handleActionGroupMembershipChanged(ginfo);
    		
        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        	mSockManager.handleActionGroupOwnershipChanged(ginfo);
        }
    }
}
