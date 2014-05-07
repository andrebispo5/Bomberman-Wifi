package pt.utl.ist.cmov.wifidirect.console.commands;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Context.Properties;
import pt.utl.ist.cmov.wifidirect.console.Network;

/**
 * Blocks the WDSim console for the amount of time specified by the user.
 *
 */
public class WaitCommand extends Command {
	
	public WaitCommand(String name, String abrv) {
		super(name,abrv);
	}

	public WaitCommand() {
		super("wait","w");
		
		mProperties = new String [] {
			Properties.WAIT_ITERATIVE,
		};
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		if (context.getProperties().hasProperty(Properties.WAIT_ITERATIVE) 
				|| args.length == 1) {
			// ignore the time and wait for key press
			System.out.print("Press enter to continue...");
			try {
				System.in.read();
			} catch (IOException e) {
			}
			return true;
		}
		
		if (args.length != 2 && args.length != 3) {
			printError("Wrong number of input arguments.");
			return false;
		}

		// check the input time
		int time = 1;
		try {
			time = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			printError("Wrong time value.");
			return false;
		}
		
		// check the time units
		if (args.length == 3) {
			String unit = args[2];
			if (!unit.equals("s")) {
				if (unit.equals("m")) {
					time *= 60;
				} else {
					printError("Wrong time unit.");
					return false;
				}
			}
		}

		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
			printError("Wait interrupted.");
			return false;
		}
		return true;
	}

	public void printHelp() {
		System.out.println("Syntax: wait|w [<time> [s|m]]");
	}
}
