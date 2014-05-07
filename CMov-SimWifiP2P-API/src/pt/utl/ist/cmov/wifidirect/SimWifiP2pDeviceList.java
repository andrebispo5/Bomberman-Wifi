package pt.utl.ist.cmov.wifidirect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * A class representing a Wi-Fi P2p device list.
 *
 * Note that the operations are not thread safe.
 * {@see SimWifiP2pManager}
 */
public class SimWifiP2pDeviceList implements Serializable {

    private static final long serialVersionUID = 7222558546962665741L;
	
	private HashMap<String, SimWifiP2pDevice> mDevices;

    public SimWifiP2pDeviceList() {

        mDevices = new HashMap<String, SimWifiP2pDevice>();
    }

    /** copy constructor */
    public SimWifiP2pDeviceList(SimWifiP2pDeviceList source) {

        if (source != null) {
            for (SimWifiP2pDevice d : source.getDeviceList()) {
                mDevices.put(d.virtDeviceAddress, d);
            }
        }
    }

    /** @hide */
    public SimWifiP2pDeviceList(ArrayList<SimWifiP2pDevice> devices) {

    	mDevices = new HashMap<String, SimWifiP2pDevice>();
        for (SimWifiP2pDevice device : devices) {
            if (device.virtDeviceAddress != null) {
                mDevices.put(device.virtDeviceAddress, device);
            }
        }
    }

    public boolean hasPeerListChanged(SimWifiP2pDeviceList update) {

    	if (update == null) {
    		return (mDevices != null);
    	}
    	Set<String> oldval = mDevices.keySet();
    	Set<String> newval = update.mDevices.keySet();
        return !oldval.equals(newval);
    }

    public void mergeUpdate(SimWifiP2pDeviceList update) {
    	
    	mDevices.clear();
    	mDevices.putAll(update.mDevices);
    }

    /** @hide */
    public void addList(ArrayList<SimWifiP2pDevice> devices) {
    	
        for (SimWifiP2pDevice device : devices) {
            if (device.virtDeviceAddress != null) {
                mDevices.put(device.virtDeviceAddress, device);
            }
        }
    }

    /** @hide */
    public boolean clear() {
    	
        if (mDevices.isEmpty()) return false;
        mDevices.clear();
        return true;
    }

    /** @hide */
    public void update(SimWifiP2pDevice device) {
    	
        if (device == null || device.virtDeviceAddress == null) return;
        SimWifiP2pDevice d = mDevices.get(device.virtDeviceAddress);
        if (d != null) {
            d.deviceName = device.deviceName;
            return;
        }
        //Not found, add a new one
        mDevices.put(device.virtDeviceAddress, device);
    }

    /** @hide */
    public SimWifiP2pDevice get(String deviceAddress) {
    	
        if (deviceAddress == null) return null;

        return mDevices.get(deviceAddress);
    }

    public SimWifiP2pDevice getByName(String deviceName) {
    	
        if (deviceName == null) return null;

        for (SimWifiP2pDevice device : mDevices.values()) {
        	if (deviceName.equals(device.deviceName)) {
        		return device;
        	}
        }
        return null;
    }

    /** @hide */
    public boolean remove(SimWifiP2pDevice device) {
    	
        if (device == null || device.virtDeviceAddress == null) return false;
        return mDevices.remove(device.virtDeviceAddress) != null;
    }

    /** Get the list of devices */
    public Collection<SimWifiP2pDevice> getDeviceList() {
    	
        return Collections.unmodifiableCollection(mDevices.values());
    }

    public String toString() {
    	
        StringBuffer sbuf = new StringBuffer();
        for (SimWifiP2pDevice device : mDevices.values()) {
            sbuf.append("\n").append(device);
        }
        return sbuf.toString();
    }
}