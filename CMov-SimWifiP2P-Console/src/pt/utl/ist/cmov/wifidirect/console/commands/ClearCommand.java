package pt.utl.ist.cmov.wifidirect.console.commands;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Devices;
import pt.utl.ist.cmov.wifidirect.console.Groups;
import pt.utl.ist.cmov.wifidirect.console.Network;


public class ClearCommand extends Command {
	
	public ClearCommand(String name, String abrv) {
		super(name,abrv);
	}

	public ClearCommand() {
		super("clear","e");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		Devices devices = network.getDevices();
		Groups groups = network.getGroups();

		if (args.length != 1) {
			printError("Wrong number of input arguments.");
			return false;
		}

		devices.clear();
		groups.clear();
		return true;
	}
}
