package pt.utl.ist.cmov.wifidirect.console.commands;

import java.util.TreeMap;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Network;

/**
 * Print the list of supported commands
 *
 */
public class HelpCommand extends Command {
	
	public HelpCommand(String name, String abrv) {
		super(name,abrv);
	}

	public HelpCommand() {
		super("help","h");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		if (args.length > 1) {
			if (args[1].equals("properties")) {
				printHelpProperties(network, context, args);
			} else if (args[1].equals("commands")) {
				printHelpCommands(network, context, args);
			} else {
				printHelp();
				return false;
			}
			return true;
		}

		System.out.println("Properties:");
		printHelpProperties(network, context, args);
		System.out.println();
		System.out.println("Commands:");
		printHelpCommands(network, context, args);
		return true;
	}
	
	private boolean printHelpProperties(Network network, Context context, 
			String [] args) {
		
		Command [] commands = context.getCommands();
		TreeMap<String,String> propList = new TreeMap<String,String> ();
		for (Command cmd : commands) {
			String [] props = cmd.getProperties();
			if (props == null) {
				continue;
			}
			for (int i = 0; i < props.length; i++) {
				propList.put(props[i], cmd.getName());
			}
		}
		for (String prop : propList.keySet()) {
			System.out.println(prop + " (" + propList.get(prop) + ")");
		}

		return true;
	}
	
	private boolean printHelpCommands(Network network, Context context, 
			String [] args) {
	
		Command [] commands = context.getCommands();
		for (Command cmd : commands) {
			System.out.println(cmd.getName() + " (" + cmd.getAbvr() + ")");
		}

		return true;
	}
	
	public void printHelp() {
		System.out.println("Syntax: help|h [properties|commands]");
	}
}
