package pt.utl.ist.cmov.wifidirect.console.commands;

import java.util.ArrayList;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Device;
import pt.utl.ist.cmov.wifidirect.console.Devices;
import pt.utl.ist.cmov.wifidirect.console.Group;
import pt.utl.ist.cmov.wifidirect.console.Groups;
import pt.utl.ist.cmov.wifidirect.console.Network;

public class PrintCommand extends Command {
	
	public PrintCommand(String name, String abrv) {
		super(name,abrv);
	}

	public PrintCommand() {
		super("print","p");
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
		
		Command [] commands = context.getCommands();
		String option = args[1];
		if (option.equals("devices") || option.equals("d")) {
			printDevices(devices,commands,args);
			return true;
		}
		if (option.equals("neighbors") || option.equals("n")) {
			printNeighbors(devices,commands,args);
			return true;
		}
		if (option.equals("groups") || option.equals("g")) {
			printGroups(groups,commands,args);
			return true;
		}
		if (option.equals("network") || option.equals("t")) {
			printNetwork(network,commands,args);
			return true;
		}
		if (option.equals("properties") || option.equals("p")) {
			printProps(context);
			return true;
		}
		if (option.equals("history") || option.equals("h")) {
			printHistory(context);
			return true;
		}
		printError("Unknown option \"" + option + "\"");
		return false;
	}

	protected void printDevices(Devices devices, Command [] commands, String [] args) {
		for(Device dev : devices.getDevices()) {
			System.out.println(dev.getName() + "\t" + 
				dev.getAppVirtIp() + ":" + dev.getAppVirtPort() + "\t" +
				dev.getAppRealIp() + ":" + dev.getAppRealPort() + "\t" +
				dev.getCtrlRealIp() + ":" + dev.getCtrlRealPort());
		}
	}

	protected void printGroups(Groups groups, Command [] commands, String [] args) {
		for (Group group : groups.getGroups()) {
			System.out.print(group.getGo().getName() + " => ");
			int i = 0;
			for (Device client : group.getClientList()) {
				System.out.print(client.getName());
				i++;
				if (i < group.getClientList().size()) {
					System.out.print(",");
				}
			}
			System.out.println("");
		}
	}

	protected void printNeighbors(Devices devices, Command [] commands, String [] args) {
		for (Device device: devices.getDevices()) {
			System.out.print(device.getName() + " => ");
			ArrayList<String> neighbors = device.getNeighbors();
			int i = 0;
			for (String client : neighbors) {
				System.out.print(client);
				i++;
				if (i < neighbors.size()) {
					System.out.print(",");
				}
			}
			System.out.println();
		}
	}

	protected void printNetwork(Network network, Command [] commands, String [] args) {
		Devices devices = network.getDevices();
		Groups groups = network.getGroups();
		for (Device device: devices.getDevices()) {

			// Print details of the neighbors of the current node
			System.out.println("Node " + device.getName());
			System.out.print("   Peers:");
			ArrayList<String> neighbors = device.getNeighbors();
			int i = 0;
			for (String client : neighbors) {
				System.out.print(client);
				i++;
				if (i < neighbors.size()) {
					System.out.print(",");
				}
			}
			System.out.println();
			
			System.out.println("   Groups:");

			// Print the list of groups that the current node is client of
			String clientOf = groups.getStrGroupsContaining(device.getName());
			System.out.println("      ClientOf: " +
					((clientOf != null && !clientOf.equals(""))?clientOf:"No"));

			// Print details of the group that the current node is owner of
			Group group = groups.getGroup(device.getName());
			System.out.println("      GO: " + ((group != null)?"Yes":"No ") +
				((group != null)?(" (" + group.getClientsStr() + ")"):""));
		}
	}

	protected void printProps(Context context) {
		for (String prop : context.getProperties().get()) {
			System.out.println(prop);
		}
	}
	
	protected void printHistory(Context context) {
		String last = context.getHistory().getLast();
		if (last != null) {
			System.out.println(last);
		}
	}
	
	public void printHelp() {
		System.out.println("Syntax: print|p <what>");
		System.out.println("   devices|d, groups|g, neighbors|n, network|t, properties|p, history|h");
	}
}

