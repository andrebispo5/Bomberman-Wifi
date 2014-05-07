package pt.utl.ist.cmov.wifidirect.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDevice;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDeviceList;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;

public class SimWifiP2pSocketManager {

	public static String TAG = "SimWifiP2pSocketManager";

	private static SimWifiP2pSocketManager mSockManager = null;

	private SimWifiP2pInfo mGroupInfo;
    private SimWifiP2pDeviceList mDeviceList;
    private SimWifiP2pDeviceList mDevices;
    private HashMap<SimWifiP2pSocket, SimWifiP2pConnection> mSockets;
    private HashMap<SimWifiP2pSocketServer, SimWifiP2pConnection> mSockServer;

	private class SimWifiP2pConnection {

		public SimWifiP2pSocketWrapper mWrapper;
		public Socket mSock;
		public ServerSocket mSockServer;
		public SimWifiP2pDevice mTarget;
	}
    
	private SimWifiP2pSocketManager() {

		mGroupInfo = new SimWifiP2pInfo();
    	mDeviceList = new SimWifiP2pDeviceList();
    	mDevices = new SimWifiP2pDeviceList();
    	mSockets = new HashMap<SimWifiP2pSocket, SimWifiP2pConnection>();
    	mSockServer = new HashMap<SimWifiP2pSocketServer, SimWifiP2pConnection>();
	}

    public static SimWifiP2pSocketManager getSockManager() {
    	
        if(null == mSockManager) {
        	mSockManager = new SimWifiP2pSocketManager();
        }
        return mSockManager;
    }

	public static void Init(Context context) {
		
		if (mSockManager == null) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
			filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_DEVICE_INFO_CHANGED_ACTION);
			SimWifiP2pSocketBroadcastReceiver receiver = new SimWifiP2pSocketBroadcastReceiver(
					SimWifiP2pSocketManager.getSockManager());
			context.registerReceiver(receiver, filter);
		}
	}

	/*
	 * Client socket methods invoked by the application via the API
	 */

	public synchronized void sockOpenSocket(SimWifiP2pSocket wrapper,
			String dstName, int dstPort) 
			throws UnknownHostException, IOException {

		assert wrapper != null;

		// validate the virtual address of destination
		String deviceAddr = dstName + ":" + dstPort;
    	Log.d(TAG, "Looking for virtual address " + deviceAddr);
		SimWifiP2pDevice device = mDevices.get(deviceAddr);
		if (device == null) {
			throw new UnknownHostException("Device with virtual address " 
					+ deviceAddr + " could not be resolved.");
		}
		if(!mGroupInfo.askIsConnectionPossible(device.deviceName)) {
			throw new UnknownHostException("Device with virtual address " 
					+ deviceAddr + " is not in the network.");
		}

		// open the socket on the device's real address and keep track of connection
    	Log.d(TAG, "Connecting to " + device.getRealIp() + ":" + device.getRealPort());
		@SuppressWarnings("resource")
		Socket socket = new Socket(device.getRealIp(), device.getRealPort());

		// keep trail of the opened socket
		SimWifiP2pConnection conn = new SimWifiP2pConnection();
		conn.mWrapper = wrapper;
		conn.mSock = socket;
		conn.mTarget = device;
		mSockets.put(wrapper, conn);
	}

	private synchronized void sockOpenSocket(SimWifiP2pSocket wrapper,
			Socket socket) throws IOException {
		
		assert socket != null;

		SimWifiP2pConnection conn = new SimWifiP2pConnection();
		conn.mWrapper = wrapper;
		conn.mSock = socket;
		conn.mTarget = null;
		mSockets.put(wrapper, conn);
	}

	public synchronized InputStream sockGetInputStream(SimWifiP2pSocket wrapper) 
			throws IOException {
		
		assert wrapper != null;

		SimWifiP2pConnection conn = mSockets.get(wrapper);
		if (conn == null) {
			throw new IOException("Socket wrapper " + wrapper.hashCode() 
					+ " not found.");
		}
		return conn.mSock.getInputStream();
	}

	public synchronized OutputStream sockGetOutputStream(SimWifiP2pSocket wrapper)
			throws IOException {
		
		assert wrapper != null;

		SimWifiP2pConnection conn = mSockets.get(wrapper);
		if (conn == null) {
			throw new IOException("Socket wrapper " + wrapper.hashCode() 
					+ " not found.");
		}
		return conn.mSock.getOutputStream();
	}

	public synchronized void sockClose(SimWifiP2pSocket wrapper)
			throws IOException {

		assert wrapper != null;

		SimWifiP2pConnection conn = mSockets.get(wrapper);
		if (conn == null) {
			throw new IOException("Socket wrapper " + wrapper.hashCode() 
					+ " not found.");
		}

		Socket socket = conn.mSock;
		mSockets.remove(wrapper);
		socket.close();
	}

	public synchronized boolean sockIsClosed(SimWifiP2pSocket wrapper) {

		assert wrapper != null;

		SimWifiP2pConnection conn = mSockets.get(wrapper);
		if (conn == null) {
			return true;
		}
		return conn.mSock.isClosed();
	}

	/*
	 * Server socket methods invoked by the application via the API
	 */

	public synchronized void sockOpenSocketServer(
			SimWifiP2pSocketServer wrapper) throws IOException {

		assert wrapper != null;

		@SuppressWarnings("resource")
		ServerSocket sockServer = new ServerSocket();
		SimWifiP2pConnection conn = new SimWifiP2pConnection();
		conn.mWrapper = wrapper;
		conn.mSockServer = sockServer;
		mSockServer.put(wrapper, conn);
	}

	public synchronized void sockOpenSocketServer(
			SimWifiP2pSocketServer wrapper, int port) throws IOException {

		assert wrapper != null;

		@SuppressWarnings("resource")
		ServerSocket sockServer = new ServerSocket(port);
		SimWifiP2pConnection conn = new SimWifiP2pConnection();
		conn.mWrapper = wrapper;
		conn.mSockServer = sockServer;
		mSockServer.put(wrapper, conn);
	}

	public SimWifiP2pSocket sockAccept(
			SimWifiP2pSocketServer wrapper) throws IOException {

		assert wrapper != null;

		ServerSocket sockServer;
		synchronized (SimWifiP2pSocketManager.class) {
			SimWifiP2pConnection conn = mSockServer.get(wrapper);
			if (conn == null) {
				throw new IOException("SocketServer wrapper " + 
						wrapper.hashCode() + " not found.");
			}
			sockServer = conn.mSockServer;
		}

		Socket socket = sockServer.accept();

		SimWifiP2pSocket connSocket;
		connSocket = new SimWifiP2pSocket();
		sockOpenSocket(connSocket, socket);
		return connSocket;
	}

	public synchronized void sockClose(SimWifiP2pSocketServer wrapper)
			throws IOException {

		assert wrapper != null;

		SimWifiP2pConnection conn = mSockServer.get(wrapper);
		if (conn == null) {
			throw new IOException("SocketServer wrapper " + 
					wrapper.hashCode() + " not found.");
		}

		ServerSocket socket = conn.mSockServer;
		mSockServer.remove(wrapper);
		socket.close();
	}

	/*
	 * Methods invoked by the broadcast receiver, which in turn reacts to the
	 * notifications received from the WDSim service
	 */

	public synchronized void handleActionStateChanged(boolean wifion) {        
    	Log.d(TAG, "Received WIFI_P2P_STATE_CHANGED_ACTION");
    	if (!wifion) {
    		connCloseAllClients();
    		connCloseAllServers();
    		connGarbageCollect();
    		mGroupInfo.clear();
    	    mDeviceList.clear();
    	    mDevices.clear();
    	}
	}

	public synchronized void handleActionPeersChanged(SimWifiP2pDeviceList dlist) {
    	Log.d(TAG, "Received WIFI_P2P_PEERS_CHANGED_ACTION");
    	mDeviceList.mergeUpdate(dlist);
		Log.d(TAG, "Devicelist (after peers changed): " + mDeviceList.toString());
		connCloseUnreachableClients();
		connGarbageCollect();
	}

	public synchronized void handleActionGroupMembershipChanged(SimWifiP2pInfo ginfo) {
    	Log.d(TAG, "Received WIFI_P2P_GROUP_MEMBERSHIP_CHANGED_ACTION");
    	mGroupInfo.mergeUpdate(ginfo);
		connCloseUnreachableClients();
		connGarbageCollect();
	}

	public synchronized void handleActionGroupOwnershipChanged(SimWifiP2pInfo ginfo) {
    	Log.d(TAG, "Received WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION");		
    	mGroupInfo.mergeUpdate(ginfo);
		connCloseUnreachableClients();
		connGarbageCollect();
	}

	public void handleActionDeviceInfoChanged(SimWifiP2pDeviceList devices) {
    	Log.d(TAG, "Received WIFI_P2P_DEVICE_INFO_CHANGED_ACTION");		
    	mDevices.mergeUpdate(devices);
		connCloseUnreachableClients();
		connGarbageCollect();
	}
	
	/*
	 * Helper methods for maintaining the active connections
	 */

	private void connCloseAllClients() {
		for (SimWifiP2pConnection conn : mSockets.values()) {
			if (!conn.mSock.isClosed()) {
				try {
					conn.mSock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void connCloseAllServers() {
		for (SimWifiP2pConnection conn : mSockServer.values()) {
			if (!conn.mSockServer.isClosed()) {
				try {
					conn.mSockServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void connCloseUnreachableClients() {

		for (SimWifiP2pConnection conn : mSockets.values()) {
			if (conn.mSock.isClosed() || conn.mTarget == null) {
				continue;
			}
			if (!mGroupInfo.askIsConnectionPossible(conn.mTarget.deviceName)) {
				try {
					conn.mSock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void connGarbageCollect() {

		ArrayList<SimWifiP2pSocketWrapper> toDelete = 
				new ArrayList<SimWifiP2pSocketWrapper>();
		
		// garbage collect the clients
		for (SimWifiP2pConnection conn : mSockets.values()) {
			if (conn.mSock.isClosed()) {
				toDelete.add(conn.mWrapper);
			}
		}
		for (SimWifiP2pSocketWrapper element : toDelete) {
			mSockets.remove(element);
		}
		toDelete.clear();

		// garbage collect the servers
		for (SimWifiP2pConnection conn : mSockServer.values()) {
			if (conn.mSockServer.isClosed()) {
				toDelete.add(conn.mWrapper);
			}
		}
		for (SimWifiP2pSocketWrapper element : toDelete) {
			mSockServer.remove(element);
		}
	}
}
