package pt.utl.ist.cmov.wifidirect.console.commands;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Network;

public class QuitCommand extends Command {
	
	public QuitCommand(String name, String abrv) {
		super(name,abrv);
	}

	public QuitCommand() {
		super("quit","q");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		System.exit(0);
		return true;
	}
}
