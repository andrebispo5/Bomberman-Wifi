package pt.utl.ist.cmov.wifidirect.console.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Network;

/**
 * This is the start command, which implements the main console loop
 *
 */
public class StartCommand extends Command {
	
	public StartCommand(String name, String abrv) {
		super(name,abrv);
	}

	public StartCommand() {
		super("-","-");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		String [] tokens = null;
		
		printIntro();
		while (true) {
			System.out.print(">");
			try {
				s = in.readLine();
				tokens = s.split("\\s+");
				if (tokens.length == 0) {
					continue;
				}
				String cmd = tokens[0];
				if (cmd.equals("")) {
					continue;
				}
				if (cmd.startsWith("#")) {
					continue;
				}				
				boolean found = false;
				boolean ok = false;
				for (Command command : context.getCommands()) {
					if (command.getName().equals(cmd) || command.getAbvr().equals(cmd)) {
						found = true;
						ok = command.executeCommand(network, context, tokens);
						if (ok) context.getHistory().setLast(s);
						break;
					}
				}
				if (!found)
					Command.printWrongCommand(cmd);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error occurred!\n");
			}
		}
	}
	
	public static void printIntro() {
		System.out.println("WiFi Direct Simulator\n");
		System.out.println("  Working Directory = " + System.getProperty("user.dir"));
		System.out.println("  Type \"help\" or \"h\" for the full command list\n");
	}
}
