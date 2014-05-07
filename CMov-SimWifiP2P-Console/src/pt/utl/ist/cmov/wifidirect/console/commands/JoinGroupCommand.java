package pt.utl.ist.cmov.wifidirect.console.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Device;
import pt.utl.ist.cmov.wifidirect.console.Devices;
import pt.utl.ist.cmov.wifidirect.console.Groups;
import pt.utl.ist.cmov.wifidirect.console.Network;

public class JoinGroupCommand extends Command {

	public JoinGroupCommand(String name, String abrv) {
		super(name,abrv);
	}

	public JoinGroupCommand() {
		super("joingroup","jg");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		Devices devices = network.getDevices();
		Groups groups = network.getGroups();

		if (args.length != 3) {
			printError("Wrong number of input arguments.");
			printHelp();
			return false;
		}
		
		// Check that the device exists
		String target = args[1];
		if (!devices.existsDevice(target)) {
			printError("Targeted device is not registered.");
			return false;
		}

		// Parse GOs list
		Set<String> gos = new TreeSet<String>();
		String nlist = args[2];
		if (!nlist.startsWith("(") || !nlist.endsWith(")")) {
			printError("Clients list malformed.");
			printHelp();
			return false;
		}
		nlist = nlist.substring(1, nlist.length() - 1);
		if (nlist != null && !nlist.equals("")) {
			Collections.addAll(gos,nlist.split(","));
		}

		// check that the GOs exist and are reachable to the target
		Device targetDevice = devices.getDevice(target);
		for (String go : gos) {
			if (groups.getGroup(go) == null) {
				printError("Device \"" + go + "\" is not GO.");
				return false;
			}
			if (!devices.existsDevice(go)) {
				printError("GO device \"" + go + "\" does not exist.");
				return false;
			}
			if (!targetDevice.hasNeighbor(go)) {
				printError("Device \"" + go + "\" is not reachable to \""
					+ target + "\".");
				return false;
			}
		}

		// OK. join the target device to the desired groups
		for (String go : gos) {
			ArrayList<Device> group = groups.getGroup(go).getClientList();
			if (group.contains(targetDevice)) {
				continue;
			}
			group.add(targetDevice);
		}
		return true;
	}

	public void printHelp() {
		System.out.println("Syntax: joingroup|jg <client> (<go_1>,...,<go_n>)");
	}
}
