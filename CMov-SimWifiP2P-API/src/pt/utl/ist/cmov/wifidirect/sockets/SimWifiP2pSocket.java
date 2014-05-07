package pt.utl.ist.cmov.wifidirect.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;


public class SimWifiP2pSocket implements SimWifiP2pSocketWrapper {
	
	public static String TAG = "SimWifiP2pSocket";

	public SimWifiP2pSocket() {
	}

	public SimWifiP2pSocket(String dstName, int dstPort) 
			throws UnknownHostException, IOException {
		
		SimWifiP2pSocketManager sockManager = 
			SimWifiP2pSocketManager.getSockManager();
		sockManager.sockOpenSocket(this, dstName, dstPort);
	}

	public InputStream getInputStream () throws IOException {
		
		SimWifiP2pSocketManager sockManager = 
				SimWifiP2pSocketManager.getSockManager();
		return sockManager.sockGetInputStream(this);
	}

	public OutputStream getOutputStream() throws IOException {
		
		SimWifiP2pSocketManager sockManager = 
				SimWifiP2pSocketManager.getSockManager();
		return sockManager.sockGetOutputStream(this);
	}

	public void close() throws IOException {
		
		SimWifiP2pSocketManager sockManager = 
				SimWifiP2pSocketManager.getSockManager();
		sockManager.sockClose(this);
	}

	public boolean isClosed() {
		
		SimWifiP2pSocketManager sockManager = 
				SimWifiP2pSocketManager.getSockManager();
		return sockManager.sockIsClosed(this);
	}
}
