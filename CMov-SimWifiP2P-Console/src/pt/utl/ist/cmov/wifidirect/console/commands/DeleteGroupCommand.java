package pt.utl.ist.cmov.wifidirect.console.commands;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Devices;
import pt.utl.ist.cmov.wifidirect.console.Groups;
import pt.utl.ist.cmov.wifidirect.console.Network;

public class DeleteGroupCommand extends Command {

	public DeleteGroupCommand(String name, String abrv) {
		super(name,abrv);
	}

	public DeleteGroupCommand() {
		super("deletegroup","dg");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		Devices devices = network.getDevices();
		Groups groups = network.getGroups();

		if (args.length != 2) {
			printError("Wrong number of input arguments.");
			printHelp();
			return false;
		}
		
		// Delete the group if it exists
		String go = args[1];
		if (!devices.existsDevice(go)) {
			printError("Group does not exist.");
			return false;
		}
		groups.deleteDevice(go);
		return true;
	}

	public void printHelp() {
		System.out.println("Syntax: deletegroup|dg <go>");
	}
}
