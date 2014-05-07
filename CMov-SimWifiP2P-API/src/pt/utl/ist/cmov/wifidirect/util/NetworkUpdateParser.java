package pt.utl.ist.cmov.wifidirect.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import android.util.Log;

import pt.utl.ist.cmov.wifidirect.SimWifiP2pDevice;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDeviceList;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;

public class NetworkUpdateParser {
	
	public static String TAG = "NetworkUpdateParser";

	private String mTmpDeviceName;
	private boolean mTmpIsClient;
	private boolean mTmpIsGo;
	private TreeMap<String,ArrayList<String>> mTmpGroups;
	private ArrayList<SimWifiP2pDevice> mTmpDlist;
	private ArrayList<SimWifiP2pDevice> mTmpDall;	
	private Set<String> mTmpHomeGroups;
	private Set<String> mTmpGroupClients;

	// parsed elements
	public SimWifiP2pInfo mOutGroupInfo;
	public SimWifiP2pDeviceList mOutDeviceList;
	public SimWifiP2pDeviceList mOutDevices;
	public boolean mOutSuccess;
	
	public NetworkUpdateParser() {

		mTmpDlist = new ArrayList<SimWifiP2pDevice>();
		mTmpDeviceName = new String();
		mTmpIsClient = false;
		mTmpIsGo = false;
		mTmpGroups = new TreeMap<String,ArrayList<String>>();
		mTmpDall = new ArrayList<SimWifiP2pDevice>();
    	mTmpHomeGroups = new HashSet<String> ();
    	mTmpGroupClients = new HashSet<String> ();

		mOutGroupInfo = null;
		mOutDeviceList = null;
		mOutDevices = null;
		mOutSuccess = false;
	}

	public static NetworkUpdateParser parse(String msg) {
		NetworkUpdateParser p = new NetworkUpdateParser();
		p.mOutSuccess = parseNetworkUpdateMsg(p,msg);
		return p;
	}

	public void print() {
		Log.d(TAG, "Parsed:" + this.toString());
	}

	public static boolean parseNetworkUpdateMsg(NetworkUpdateParser parser, String msg) {

		if (msg == null) {
			return false;
		}

		Log.d(TAG, "Full msg:" + msg);

		// parse the individual elements of the message
		String [] parts = msg.split("\\+");
		if (parts.length != 3) {
			Log.e(TAG, "Wrong number of parts in message.");
			return false;
		}
		if (!parseGroupsInfoMsg(parser,parts[0])) {
    		Log.e(TAG, "Malformed message: groups info part");
			return false;
		}
		if (!parseNeighborsListMsg(parser,parts[1])) {
    		Log.e(TAG, "Malformed message: neighbors list part");
			return false;
		}
		if (!parseDevicesMsg(parser,parts[2])) {
    		Log.e(TAG, "Malformed message: devices part");
			return false;
		}

    	// rebuild the original mOut* objects
    	parser.mOutGroupInfo = new SimWifiP2pInfo(
    			parser.mTmpDeviceName, parser.mTmpIsClient, parser.mTmpHomeGroups,
    			parser.mTmpIsGo, parser.mTmpGroupClients, parser.mTmpGroups);
    	parser.mOutDeviceList = new SimWifiP2pDeviceList(parser.mTmpDlist);
    	parser.mOutDevices = new SimWifiP2pDeviceList(parser.mTmpDall);
    	return true;
	}

	public static boolean parseGroupsInfoMsg(NetworkUpdateParser parser, String msg) {
		
		assert msg != null;
		if (msg.equals("")) {
			return true;
		}

    	String [] fields = msg.split("@");

    	if (fields.length < 3) {
    		Log.e(TAG, "Wrong number of group info fields.");
    		return false;
    	}

    	// Parse first field: DeviceName
    	parser.mTmpDeviceName = fields[0];

    	// Parse second field: IsClient
    	if (fields[1].equals("Yes")) {
    		parser.mTmpIsClient = true;
    	} else if (fields[1].equals("No")) {
    		parser.mTmpIsClient = false;
    	} else {
    		Log.e(TAG, "Malformed 'is client' field.");
    		return false;
    	}

    	// Parse third field: HomeGroups
    	if (parser.mTmpIsClient) {
    		if (fields.length < 4) {
        		Log.e(TAG, "Wrong number of group info fields.");
        		return false;
        	}
    		String [] tokens = fields[2].split(",");
    		HashSet<String> homeGroups = new HashSet<String>(Arrays.asList(tokens));
    		homeGroups.remove("");
    		parser.mTmpHomeGroups = homeGroups;
    	}

    	// Parse fourth field: IsGo
    	if (fields[3].equals("Yes")) {
    		parser.mTmpIsGo = true;
    	} else if (fields[3].equals("No")) {
    		parser.mTmpIsGo = false;
    	} else {
    		Log.e(TAG, "Malformed 'is GO' field.");
    		return false;
    	}

    	// Parse fifth field: GroupClients
    	if (parser.mTmpIsGo) {
    		if (fields.length < 5) {
        		Log.e(TAG, "Wrong number of group info fields.");
        		return false;
        	}
    		String [] tokens = fields[4].split(",");
    		HashSet<String> groupClients = new HashSet<String>(Arrays.asList(tokens));
    		groupClients.remove("");
    		parser.mTmpGroupClients = groupClients;
    	}

    	// Parse sixth field: Groups
    	if (fields.length >= 5) {
    		String [] groups = fields[5].split("%");
    		for (String group : groups) {
    			String [] elements = group.split(",");
    			if (elements.length < 1) {
    				Log.e(TAG, "Malformed group description.");
            		return false;
    			}
    			String go = elements[0];
    			ArrayList<String> clients = 
    				new ArrayList<String>(Arrays.asList(elements));
    			clients.remove("");
    			clients.remove(0); // the GO is not a client
    			parser.mTmpGroups.put(go, clients);
    		}
    	}
    	return true;
	}

	public static boolean parseNeighborsListMsg(NetworkUpdateParser parser, String msg) {
		
    	boolean malformed = false;
    	assert msg != null;
    	if (msg.equals("")) {
    		return true;
    	}
    	String [] neighbors = msg.split("@");
    	for (String n : neighbors) {
    		try {
    			parser.mTmpDlist.add(new SimWifiP2pDevice(n));
    		} catch (IllegalArgumentException e) {
    			malformed = true;
    			break;
    		} finally {
    		}
    	}
    	return !malformed;
	}

	public static boolean parseDevicesMsg(NetworkUpdateParser parser, String msg) {
    	boolean malformed = false;

    	assert msg != null;
    	if (msg.equals("")) {
    		return true;
    	}
    	String [] devices = msg.split("@");
    	for (String n : devices) {
    		try {
    			parser.mTmpDall.add(new SimWifiP2pDevice(n));
    		} catch (IllegalArgumentException e) {
    			malformed = true;
    			break;
    		} finally {
    		}
    	}
    	return !malformed;
	}
}
