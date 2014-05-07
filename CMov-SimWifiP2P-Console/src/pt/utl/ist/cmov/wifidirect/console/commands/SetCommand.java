package pt.utl.ist.cmov.wifidirect.console.commands;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Network;

public class SetCommand extends Command {
	
	public SetCommand(String name, String abrv) {
		super(name,abrv);
	}

	public SetCommand() {
		super("set","set");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		if (args.length != 2) {
			printError("Wrong number of input arguments.");
			printHelp();
			return false;
		}
		
		context.getProperties().addProperty(args[1]);
		return true;
	}
	
	public void printHelp() {
		System.out.println("Syntax: set <property>");
	}
}
