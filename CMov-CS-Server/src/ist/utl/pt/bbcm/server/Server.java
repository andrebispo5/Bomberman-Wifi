package ist.utl.pt.bbcm.server;

import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.KnownPorts;
import ist.utl.pt.bbcm.networking.Message;
import ist.utl.pt.bbcm.networking.SettingsExtractor;
import ist.utl.pt.bbcm.server.threads.EndGameThread;
import ist.utl.pt.bbcm.server.threads.MoveRobotsThread;
import ist.utl.pt.bbcm.server.threads.UpdateMatrixThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static ServerSocket ackSocket;
	public static ServerSocket updateSocket;
	private static Socket clientSocket;
	private static String message;
	public static int numPlayers = 0;
	public static Player p1;
	public static Player p2;
	public static Player p3;
	public static Player p4;
	public static Matrix matrix;
	private static MoveRobotsThread mrt;
	private static UpdateMatrixThread umt;
	private static EndGameThread egt;

	public static void main(String[] args) {
		mainLoop();
	}

	private static void mainLoop() {
		initPlayers();
		createServerSockets();
		waitingForPlayers();
		startGame();
	}

	private static void initPlayers() {
		p1 = new Player("0", "0", "0", "0", "0");
		p2 = new Player("0", "0", "0", "0", "0");
		p3 = new Player("0", "0", "0", "0", "0");
		p4 = new Player("0", "0", "0", "0", "0");
		p1.isActive = false;
		p2.isActive = false;
		p3.isActive = false;
		p4.isActive = false;
		p1.isAlive = false;
		p2.isAlive = false;
		p3.isAlive = false;
		p4.isAlive = false;
	}

	private static void startGame() {
		Message.sendToAllPlayers(new SettingsExtractor().getMessage());
		matrix = new Matrix(SETTINGS.lvl);
		Message.sendToPlayer(p1, "startGame:p1," + numPlayers);
		Message.sendToPlayer(p2, "startGame:p2," + numPlayers);
		Message.sendToPlayer(p3, "startGame:p3," + numPlayers);
		Message.sendToPlayer(p4, "startGame:p4," + numPlayers);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//change port to go to gameLoopSocket on Player
		p1.port = KnownPorts.updatesPort;
		p2.port = KnownPorts.updatesPort;
		p3.port = KnownPorts.updatesPort;
		p4.port = KnownPorts.updatesPort;
		mrt = new MoveRobotsThread();
		umt = new UpdateMatrixThread();
		egt = new EndGameThread();
		mrt.setRunning(true);
		umt.setRunning(true);
		egt.setRunning(true);
		mrt.start();
		umt.start();
		egt.start();
		System.out.println("GAMESTARTED");
	}

	private static void createServerSockets() {
		try {
			ackSocket = new ServerSocket(KnownPorts.acknowledgePort);
			System.out.println("# Listening to the port "
					+ KnownPorts.acknowledgePort);
		} catch (IOException e) {
			System.out.println("! Could not listen on port:"
					+ KnownPorts.acknowledgePort);
		}
		try {
			updateSocket = new ServerSocket(KnownPorts.updatesPort);
			System.out.println("# Listening to the port "
					+ KnownPorts.updatesPort);
		} catch (IOException e) {
			System.out.println("! Could not listen on port:"
					+ KnownPorts.updatesPort);
		}
		System.out.println("# Server Started!");
	}

	private static void waitingForPlayers() {
		while (true) {
			try {
				clientSocket = ackSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(
						clientSocket.getInputStream());
				message = (String) ois.readObject();
				String[] msg = message.split(":");
				if (msg[0].equals("login")) {
					newPlayerLoggedIn(msg);
				}
				if (msg[0].equals("ready")) {
					break;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("Problem in message reading");
			} catch (ClassNotFoundException e) {
				System.out
						.println("Problem in message reading: CLASS NOT FOUND");
				e.printStackTrace();
			}
		}
	}

	private static void newPlayerLoggedIn(String[] msg) {
		String[] args = msg[1].split(",");
		if (!p1.isActive) {
			p1 = new Player(args[0], args[1], args[2], args[3], args[4]);
			numPlayers++;
			System.out.println("Player 1 Logged in!");
		} else if (!p2.isActive) {
			p2 = new Player(args[0], args[1], args[2], args[3], args[4]);
			numPlayers++;
			System.out.println("Player 2 Logged in!");
			Message.sendToPlayer(p1, "playerLogin:"+p2.name);
			Message.sendToPlayer(p2, "playerLogin:"+p1.name);
		} else if (!p3.isActive) {
			p3 = new Player(args[0], args[1], args[2], args[3], args[4]);
			numPlayers++;
			System.out.println("Player 3 Logged in!");
			Message.sendToPlayer(p1, "playerLogin:"+p3.name);
			Message.sendToPlayer(p2, "playerLogin:"+p3.name);
			Message.sendToPlayer(p3, "playerLogin:"+p1.name);
			Message.sendToPlayer(p3, "playerLogin:"+p2.name);
		}else if (!p4.isActive) {
			p4 = new Player(args[0], args[1], args[2], args[3], args[4]);
			numPlayers++;
			System.out.println("Player 4 Logged in!");
			Message.sendToPlayer(p1, "playerLogin:"+p4.name);
			Message.sendToPlayer(p2, "playerLogin:"+p4.name);
			Message.sendToPlayer(p3, "playerLogin:"+p4.name);
			Message.sendToPlayer(p4, "playerLogin:"+p1.name);
			Message.sendToPlayer(p4, "playerLogin:"+p2.name);
			Message.sendToPlayer(p4, "playerLogin:"+p3.name);
		}
	}

	public static void endGame() {
		Player winner = getWinner();
		Message.sendToAllPlayers("endGame:" + winner.name + "," + winner.score);
		System.out.println("Game Finished! " + winner.name + " wins with "
				+ winner.score + " points!");
		resetAllStats();
		mainLoop();
	}

	private static Player getWinner() {
		Player winner;
		if (numPlayers == 1)
			winner = p1;
		else {
			if (p1.score > p2.score) {
				winner = p1;
			} else {
				winner = p2;
			}
			if (winner.score < p3.score) {
				winner = p3;
			}
			if (winner.score < p4.score) {
				winner = p4;
			}
		}
		return winner;
	}

	private static void resetAllStats() {
		p1 = null;
		p2 = null;
		p3 = null;
		p4 = null;
		numPlayers = 0;
		message = null;
		matrix = null;
		mrt.setRunning(false);
		umt.setRunning(false);
		egt.setRunning(false);
		try {
			mrt.join();
		} catch (InterruptedException e) {
			System.err.println("Cannot Join MR Thread!");
		}
		try {
			ackSocket.close();
		} catch (IOException e2) {
			System.err.println("Cannot Close Ack Socket!");
		}
		try {
			umt.join();
		} catch (InterruptedException e) {
			System.err.println("Cannot Join UM Thread!");
		}
		try {
			updateSocket.close();
		} catch (IOException e1) {
			System.err.println("Cannot Close Update Socket!");
		}
		try {
			egt.join();
		} catch (InterruptedException e) {
			System.err.println("Cannot Join EG Thread!");
		}
	}

	public static int getPlayersAlive() {
		int activePlayers = 0;

		try {
			if (p1.isAlive)
				activePlayers++;
		} catch (NullPointerException e) {}
		try {
			if (p2.isAlive)
				activePlayers++;
		} catch (NullPointerException e) {}
		try {
			if (p3.isAlive)
				activePlayers++;
		} catch (NullPointerException e) {}
		try {
			if (p4.isAlive)
				activePlayers++;
		} catch (NullPointerException e) {}

		return activePlayers;
	}

}