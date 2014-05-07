package pt.utl.ist.cmov.wifidirect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import android.util.Log;

public class SimWifiP2pInfo implements Serializable {

	private static final long serialVersionUID = 5926396711144243398L;

	public static String TAG = "SimWifiP2pInfo";

	private String mDeviceName;
	private boolean mIsClient;
	private Set<String> mHomeGroups;
	private boolean mIsGo;
	private Set<String> mGroupClients;
	private TreeMap<String,ArrayList<String>> mGroups;
	
	public SimWifiP2pInfo() {
		
		mDeviceName = new String();
		mIsClient = false;
		mHomeGroups = new TreeSet<String>();
		mIsGo = false;
		mGroupClients = new TreeSet<String>();
		mGroups = new TreeMap<String,ArrayList<String>>();
	}

	public SimWifiP2pInfo(String deviceName, boolean isClient,
			Set<String> homeGroups, boolean isGo, Set<String> groupClients,
			TreeMap<String,ArrayList<String>> groups) {
		
		mDeviceName = deviceName;
		mIsClient = isClient;
		mHomeGroups = homeGroups;
		mIsGo = isGo;
		mGroupClients = groupClients;
		mGroups = groups;
	}
	
    public void clear() {
    	
        mDeviceName = new String();
        mIsClient = false;
        mHomeGroups.clear();
        mIsGo = false;
        mGroupClients.clear();
        mGroups.clear();
    }

	public void mergeUpdate(SimWifiP2pInfo update) {
		
		assert update != null && update.mHomeGroups != null &&
				update.mGroupClients != null && update.mGroups != null;
		mDeviceName = update.mDeviceName;
		mIsClient = update.mIsClient;
		mHomeGroups.clear();
		mHomeGroups.addAll(update.mHomeGroups);
		mIsGo = update.mIsGo;
		mGroupClients.clear();
		mGroupClients.addAll(update.mGroupClients);
		mGroups.clear();
		mGroups.putAll(update.mGroups);
	}
	
	public String getDeviceName() {
		
		return mDeviceName;
	}

	public Set<String> getDevicesInNetwork() {
		
		Set<String> inGroup = new TreeSet<String>();
		if (mIsGo) {
			inGroup.addAll(mGroupClients);
		}
		if (mIsClient) {
			for (String go : mHomeGroups) {
				inGroup.add(go);
				for (String client : mGroups.get(go)) {
					inGroup.add(client);
				}
			}
		}
		inGroup.remove(mDeviceName);
		return inGroup;
	}

	public void print() {
		
		Log.d(TAG, "DeviceName:" + mDeviceName);
		Log.d(TAG, "IsClient:" + ((mIsClient)?"Yes":"No"));
		Log.d(TAG, "HomeGroups:" + mHomeGroups.toString());
		Log.d(TAG, "IsGO:" + ((mIsGo)?"Yes":"No"));
		Log.d(TAG, "GroupClients:" + mGroupClients.toString());
		Log.d(TAG, "Groups:" + mGroups.toString());
	}

	/*
	 * Methods for querying usefult details of the network
	 */

	public boolean askIsConnected() {
		
		return mIsGo || mIsClient;
	}

	public boolean askIsGO() {
		
		return mIsGo;
	}

	public boolean askIsClient() {
		
		return mIsGo;
	}

	public boolean askHasGroupMembershipChanged(SimWifiP2pInfo update) {
		/*
		 * This yields true if the network membership changes
		 */

		assert update != null;

		Set<String> oldNodes = getDevicesInNetwork();
		Set<String> newNodes = update.getDevicesInNetwork();
		return !oldNodes.equals(newNodes);
	}

	public boolean askHasGroupOwnershipChanged(SimWifiP2pInfo update) {
		/*
		 * This yields true if the node's GO state has changed
		 */

		assert update != null;
		return mIsGo != update.mIsGo;
	}

	public boolean askIsConnectionPossible(String deviceName) {

		Log.d(TAG, "Checking if connection possible for " + deviceName);
		print();

		if (mIsGo) {
			if (mGroupClients.contains(deviceName)) {
				return true; // the device is one of its clients
			}
		}
		if (mIsClient) {
			for (String homeGroup : mHomeGroups) {
				if (deviceName.equals(homeGroup)) {
					return true; // the device is the go of one of its groups
				}
				for (String client : mGroups.get(homeGroup)) {
					if (deviceName.equals(client)) {
						return true; // the device is in one of its groups
					}
				}
			}
		}
		return false;
	}

	/*
	 * If necessary, extend with additional introspection methods...
	 */
}