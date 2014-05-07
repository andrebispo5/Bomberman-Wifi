package pt.utl.ist.cmov.wifidirect;

import java.io.Serializable;

public class SimWifiP2pDevice implements Serializable {

	private static final long serialVersionUID = 444204637169333542L;

	/**
     * The device name is a user friendly string to identify a Wi-Fi p2p device
     */
    public String deviceName = "";

    /**
     * The device address uniquely identifies a Wi-Fi p2p device
     */
    public String virtDeviceAddress = "";
    public String realDeviceAddress = "";


    public SimWifiP2pDevice() {
    }

    public SimWifiP2pDevice(String string) throws IllegalArgumentException {

    	String[] tokens = string.split(":");

        if (tokens.length != 5) {
            throw new IllegalArgumentException("Malformed device specification");
        }
        deviceName = tokens[0];
        virtDeviceAddress = tokens[1] + ":" + tokens[2];
        realDeviceAddress = tokens[3] + ":" + tokens[4];
    }

    public String getVirtIp() {
    	
    	if (virtDeviceAddress != null) {
    		String[] tokens = virtDeviceAddress.split(":");
    		return tokens[0];
    	}
    	return null;
    }

    public int getVirtPort() {
    	
    	if (virtDeviceAddress != null) {
    		String[] tokens = virtDeviceAddress.split(":");
    		return Integer.parseInt(tokens[1]);
    	}
    	return 0;
    }

    public String getRealIp() {
    	
    	if (realDeviceAddress != null) {
    		String[] tokens = realDeviceAddress.split(":");
    		return tokens[0];
    	}
    	return null;
    }

    public int getRealPort() {
    	
    	if (realDeviceAddress != null) {
    		String[] tokens = realDeviceAddress.split(":");
    		return Integer.parseInt(tokens[1]);
    	}
    	return 0;
    }

    @Override
    public boolean equals(Object obj) {
    	
        if (this == obj) return true;
        if (!(obj instanceof SimWifiP2pDevice)) return false;

        SimWifiP2pDevice other = (SimWifiP2pDevice) obj;
        if (other == null || other.virtDeviceAddress == null) {
            return (virtDeviceAddress == null);
        }
        return other.virtDeviceAddress.equals(virtDeviceAddress);
    }

    public String toString() {
    	
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("Device: ").append(deviceName);
        sbuf.append("\n virtSeviceAddress: ").append(virtDeviceAddress);
        sbuf.append("\n realSeviceAddress: ").append(realDeviceAddress);
        return sbuf.toString();
    }

    /** copy constructor */
    public SimWifiP2pDevice(SimWifiP2pDevice source) {
    	
        if (source != null) {
            deviceName = source.deviceName;
            virtDeviceAddress = source.virtDeviceAddress;
            realDeviceAddress = source.realDeviceAddress;
        }
    }
}
