package pt.utl.ist.cmov.wifidirect.console.commands;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Network;

public class LastCommand extends Command {
	
	public LastCommand(String name, String abrv) {
		super(name,abrv);
	}

	public LastCommand() {
		super("last","z");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		boolean ret = false; // always return false to avoid updating the history

		String s = context.getHistory().getLast();
		if (s == null) {
			return ret;
		}

		String [] tokens = null;
		
		tokens = s.split("\\s+");
		if (tokens.length == 0) {
			return ret;
		}
		String cmd = tokens[0];
		if (cmd.equals("")) {
			return ret;
		}
		if (cmd.startsWith("#")) {
			return ret;
		}
		boolean found = false;
		for (Command command : context.getCommands()) {
			if (command.getName().equals(cmd) || command.getAbvr().equals(cmd)) {
				found = true;
				command.executeCommand(network, context, tokens);
				break;
			}
		}
		assert found;
		return ret;
	}	
}
