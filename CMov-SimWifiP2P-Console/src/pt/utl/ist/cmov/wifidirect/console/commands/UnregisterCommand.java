package pt.utl.ist.cmov.wifidirect.console.commands;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Devices;
import pt.utl.ist.cmov.wifidirect.console.Groups;
import pt.utl.ist.cmov.wifidirect.console.Network;

/**
 * Unregisters a currently registered device from the console
 *
 */
public class UnregisterCommand extends Command {
	
	public UnregisterCommand(String name, String abrv) {
		super(name,abrv);
	}

	public UnregisterCommand() {
		super("unreg","u");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		Devices devices = network.getDevices();
		Groups groups = network.getGroups();

		if (args.length != 2) {
			printError("Wrong number of input arguments.");
			return false;
		}

		String deviceName = args[1];
		if (devices.checkDevice(deviceName)) {
			printError("No device registered with name \"" + deviceName + "\"");
			return false;
		}
		devices.removeDevice(deviceName);
		groups.removeGroup(deviceName);
		return true;
	}
}
