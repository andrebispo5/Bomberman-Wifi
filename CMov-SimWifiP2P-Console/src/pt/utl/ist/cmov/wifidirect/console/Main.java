package pt.utl.ist.cmov.wifidirect.console;

import pt.utl.ist.cmov.wifidirect.console.commands.*;

public class Main {

	public static void main(String[] args) {

		// Supported commands
		Command [] commands = {
				new ClearCommand(),
				new CommitCommand(),
				new CreateGroupCommand(),
				new DeleteGroupCommand(),
				new HelpCommand(),
				new JoinGroupCommand(),
				new LastCommand(),
				new LeaveGroupCommand(),
				new LoadCommand(),
				new MoveCommand(),
				new PingCommand(),
				new PrintCommand(),
				new QuitCommand(),
				new RegisterCommand(),
				new SetCommand(),
				new UnregisterCommand(),
				new UnsetCommand(),
				new WaitCommand(),
		};

		Context context = new Context(commands);
		Network network = new Network();
		
		/*
		 * Add default properties here, e.g.:
		 * 
		 *  Properties props = context.getProperties();
		 *  props.addProperty(Properties.WAIT_ITERATIVE);
		 * 
		 */
		
		// The root command
		StartCommand rootcmd = new StartCommand();
		rootcmd.executeCommand(network, context, args);
	}
}
