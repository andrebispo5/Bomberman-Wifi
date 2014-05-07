package pt.utl.ist.cmov.wifidirect.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import pt.utl.ist.cmov.wifidirect.console.exceptions.NeighborMalformedException;

public class Devices {

	private TreeMap<String,Device> mDevices;

	public Devices () {
		mDevices = new TreeMap<String,Device>();
	}

	public void clear() {
		mDevices.clear();
	}

	public boolean checkDevice(String name) {
		return !mDevices.containsKey(name);
	}

	public boolean checkAddress(String ip, int port) {
		for (Device device : mDevices.values()) {
			if (device.getCtrlRealIp().equals(ip) && device.getCtrlRealPort() == port) {
				return false;
			}
			if (device.getAppRealIp().equals(ip) && device.getAppRealPort() == port) {
				return false;
			}
			if (device.getAppVirtIp().equals(ip) && device.getAppVirtPort() == port) {
				return false;
			}
		}
		return true;
	}

	public boolean existsDevice(String name) {
		return mDevices.containsKey(name);
	}

	public void addDevice(String name, String ctlrRealIp, int ctlrRealPort,
			String appRealIp, int appRealPort, String appVirtIp, int appVirtPort) {
		assert !mDevices.containsKey(name) : "Check if device already exists";
		mDevices.put(name, new Device(name, ctlrRealIp, ctlrRealPort,
				appRealIp, appRealPort, appVirtIp, appVirtPort));
	}
	
	public void removeDevice(String name) {
		for (Device device : mDevices.values()) {
			device.removeNeighbor(name);
		}
		mDevices.remove(name);
	}

	public Device getDevice(String name) {
		return mDevices.get(name);
	}

	public Collection<Device> getDevices() {
		return mDevices.values();
	}

	public int numDevices() {
		return mDevices.size();
	}
	
	public String marshalDeviceNeighbors(Device device) {
		StringBuilder sb = new StringBuilder(128);
		for (String neighbor : device.getNeighbors()) {
			Device ndev = getDevice(neighbor);
			sb.append(neighbor + ":" + 
				ndev.getAppVirtIp() + ":" + ndev.getAppVirtPort() + ":" +
				ndev.getAppRealIp() + ":" + ndev.getAppRealPort() + "@");
		}
		return sb.toString();
	}

	public static ArrayList<Neighbor> unmarshalNeighbors(String msg)
			throws NeighborMalformedException {
		if (msg == null) {
			return new ArrayList<Neighbor>();
		}
		ArrayList<Neighbor> nb = new ArrayList<Neighbor>();
		try {
			String [] nbmsg = msg.split("@");
			for (String nbstr : nbmsg) {
				String [] fields = nbstr.split(":");
				Neighbor n = new Neighbor(fields[0], fields[1], 
						Integer.parseInt(fields[2]));
				nb.add(n);
			}
		} catch (Exception e) {
			throw new NeighborMalformedException();
		}
		return nb;
	}

	public String marshalDevices() {
		StringBuilder sb = new StringBuilder();
		for (Device device : mDevices.values()) {
			sb.append(device.getName() + ":" + 
					device.getAppVirtIp() + ":" + device.getAppVirtPort() + ":" +
					device.getAppRealIp() + ":" + device.getAppRealPort() + "@");
		}
		return sb.toString();
	}
}
