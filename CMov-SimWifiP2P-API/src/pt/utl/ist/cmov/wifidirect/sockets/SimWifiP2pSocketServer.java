package pt.utl.ist.cmov.wifidirect.sockets;

import java.io.IOException;

import android.util.Log;

public class SimWifiP2pSocketServer implements SimWifiP2pSocketWrapper {
	
	public static String TAG = "SimWifiP2pSocketServer";

	public SimWifiP2pSocketServer() throws IOException {

		SimWifiP2pSocketManager sockManager = 
			SimWifiP2pSocketManager.getSockManager();
		sockManager.sockOpenSocketServer(this);
		Log.d(TAG, "Socket server accept.");
	}

	public SimWifiP2pSocketServer(int port) throws IOException {

		SimWifiP2pSocketManager sockManager = 
				SimWifiP2pSocketManager.getSockManager();
		sockManager.sockOpenSocketServer(this, port);
		Log.d(TAG, "Socket server initialized.");
	}

	public SimWifiP2pSocket accept() throws IOException {

		SimWifiP2pSocketManager sockManager = 
				SimWifiP2pSocketManager.getSockManager();
		Log.d(TAG, "Socket server accept.");
		return sockManager.sockAccept(this);
	}
	
	public void close() throws IOException {
		
		SimWifiP2pSocketManager sockManager = 
				SimWifiP2pSocketManager.getSockManager();
		Log.d(TAG, "Socket server close " + this.hashCode());
		sockManager.sockClose(this);
	}
}
