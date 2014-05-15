package ist.utl.pt.bbcm.networking;

import ist.utl.pt.bbcm.ApplicationContext;
import ist.utl.pt.bbcm.enums.MODE;
import ist.utl.pt.bbcm.enums.SETTINGS;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map.Entry;

import pt.utl.ist.cmov.wifidirect.SimWifiP2pDevice;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import android.os.AsyncTask;
import android.util.Log;

public class ClientConnectorTask extends AsyncTask<String, Void, Integer> {
	private Socket client;
	private String serverIP = "192.168.137.1";
	private ApplicationContext appCtx;
	private SimWifiP2pSocket mCliSocket;

	
	public ClientConnectorTask(ApplicationContext context) {
		appCtx = context;
	}
	
	@Override
	protected Integer doInBackground(String... cmd) {

		// validate input parameters
		if (cmd.length <= 0) {
			return 0;
		}
		if (SETTINGS.mode == MODE.MLP) {
			multiplayerConnection(cmd);
		} else if (SETTINGS.mode == MODE.WDS) {
			if(cmd[0].equals("sendToPeer"))
				sendToPeer(cmd[1],cmd[2]);
			else if(cmd[0].equals("sendToGO"))
				sendToGO(cmd[1]);
			else
				sendToAllPeersAndGO(cmd);
		}
		return 0;
	}

	private void sendToGO(String msg) {
		try {
			mCliSocket = new SimWifiP2pSocket(appCtx.ipGO,KnownPorts.WDsimPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			OutputStream io = mCliSocket.getOutputStream();
			io.write((msg).getBytes());
			io.flush();
			Log.i("REQUESTSNED", "Message sent to peer:\n" + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mCliSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendToPeer(String ip, String msg) {
		try {
			mCliSocket = new SimWifiP2pSocket(ip,
				KnownPorts.WDsimPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			OutputStream io = mCliSocket.getOutputStream();
			io.write((msg).getBytes());
			io.flush();
			Log.i("REQUESTSNED", "Message sent to peer:\n" + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mCliSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendToAllPeersAndGO(String[] cmd) {
		ArrayList<String> toRemove = new ArrayList<String>();
		for (Entry<String,String> entry : appCtx.peersIP.entrySet()) {
			try {
				mCliSocket = new SimWifiP2pSocket(entry.getValue(),KnownPorts.WDsimPort);
			} catch (UnknownHostException e) {
				toRemove.add(entry.getKey());
				e.printStackTrace();
			} catch (IOException e) {
				toRemove.add(entry.getKey());
				e.printStackTrace();
			}
			try {
				OutputStream io = mCliSocket.getOutputStream();
				io.write((cmd[0]).getBytes());
				io.flush();
				Log.i("REQUESTSNED", "Message sent to peer:\n" + cmd[0]);
				mCliSocket.close();
			} catch (IOException e) {
				toRemove.add(entry.getKey());
				e.printStackTrace();
			} catch (NullPointerException e) {
				toRemove.add(entry.getKey());
				e.printStackTrace();
			}
		}
		for(String id : toRemove){
			appCtx.peersIP.remove(id);
		}
		if(!appCtx.isGO){
			try {
				mCliSocket = new SimWifiP2pSocket(appCtx.ipGO,KnownPorts.WDsimPort);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				OutputStream io = mCliSocket.getOutputStream();
				io.write((cmd[0]).getBytes());
				io.flush();
				Log.i("REQUESTSNED", "Message sent to peer:\n" + cmd[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				mCliSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void multiplayerConnection(String... cmd) {
		// connect to the server and send the message
		try {
			if (cmd.length == 2) {
				client = new Socket(serverIP, KnownPorts.updatesPort);
			} else {
				client = new Socket(serverIP, KnownPorts.acknowledgePort);
			}

			ObjectOutputStream oos = new ObjectOutputStream(
					client.getOutputStream());
			oos.writeObject(cmd[0]);
			oos.flush();
			Log.i("REQUESTSNED", "Message sent to server: " + cmd[0]);
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void onPostExecute(Long result) {
		return;
	}
}
