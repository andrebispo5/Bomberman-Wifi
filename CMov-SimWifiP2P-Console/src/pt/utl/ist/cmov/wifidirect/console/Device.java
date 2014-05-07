package pt.utl.ist.cmov.wifidirect.console;

import java.util.ArrayList;

public class Device {
	
	private String mName;
	private String mCtrlRealIp;
	private int mCtrlRealPort;
	private String mAppRealIp;
	private int mAppRealPort;
	private String mAppVirtIp;
	private int mAppVirtPort;
	private ArrayList<String> mNeighbors;

	public Device(String name, String ctrlRealIp, int ctrlRealPort,
			String appRealIp, int appRealPort,
			String appVirtIp, int appVirtPort) {
		mName = name;
		mCtrlRealIp = ctrlRealIp;
		mCtrlRealPort = ctrlRealPort;
		mAppRealIp = appRealIp;
		mAppRealPort = appRealPort;
		mAppVirtIp = appVirtIp;
		mAppVirtPort = appVirtPort;
		mNeighbors = new ArrayList<String>();
	}
	
	public String getName() {
		return mName;
	}
	
	public String getCtrlRealIp() {
		return mCtrlRealIp;
	}
	
	public int getCtrlRealPort() {
		return mCtrlRealPort;
	}
	
	public String getAppRealIp() {
		return mAppRealIp;
	}
	
	public int getAppRealPort() {
		return mAppRealPort;
	}
	
	public String getAppVirtIp() {
		return mAppVirtIp;
	}
	
	public int getAppVirtPort() {
		return mAppVirtPort;
	}
	
	public ArrayList<String> getNeighbors() {
		return mNeighbors;
	}

	public boolean hasNeighbor(String neighbor) {
		for (String peer : getNeighbors()) {
			if (peer.equals(neighbor)) {
				return true;
			}
		}
		return false;
	}

	public void removeNeighbor(String neighbor) {
		if (hasNeighbor(neighbor)) {
			getNeighbors().remove(neighbor);
		}
	}

	public void print() {
		System.out.println("Device = [Name:" + mName + 
				", CtrlRealIP:" + mCtrlRealIp + ", CtrlRealPort:" + mCtrlRealPort +
				", AppRealIP:" + mAppRealIp + ", AppRealPort:" + mAppRealPort +
				", AppVirtIP:" + mAppVirtIp + ", AppVirtPort:" + mAppVirtPort + "]");
	}
}
