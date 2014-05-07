package pt.utl.ist.cmov.wifidirect.console.commands;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TreeMap;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Device;
import pt.utl.ist.cmov.wifidirect.console.Devices;
import pt.utl.ist.cmov.wifidirect.console.Network;

/**
 * Check the status the currently registered devices by opening a socket
 * to their respective control ports
 *
 */
public class PingCommand extends Command {

	private static int PROBE_TIMEOUT = 1000;
	
	public PingCommand(String name, String abrv) {
		super(name,abrv);
	}

	public PingCommand() {
		super("ping","g");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		Devices devices = network.getDevices();

		if (args.length != 1) {
			printError("Wrong number of input arguments.");
			return false;
		}
		
		// nothing to do if true
		if (devices.numDevices() == 0) {
			return true;
		}

		// check if the devices are online
		Socket client;
		boolean online;
		TreeMap<String,Boolean> probe = new TreeMap<String,Boolean>();
		for(Device dev : devices.getDevices()) {
			online = true;
			try {
				printMsg("Probing " + dev.getName() + "...");
				client = new Socket();
				client.connect(new InetSocketAddress(
						dev.getCtrlRealIp(), dev.getCtrlRealPort()), PROBE_TIMEOUT);
				client.close();
			} catch (UnknownHostException e) {
				online = false;
			} catch (IOException e) {
				online = false;
			}
			probe.put(dev.getName(), new Boolean(online));
		}

		// print the results
		for(Device dev : devices.getDevices()) {
			System.out.println(dev.getName() + "\t" + dev.getCtrlRealIp() + 
					"\t" + dev.getCtrlRealPort() + "\t" + 
					(probe.get(dev.getName()).booleanValue()?"ONLINE":"OFFLINE"));
		}

		return true;
	}

	public void printHelp() {
		System.out.println("Syntax: ping|g");
	}
}
