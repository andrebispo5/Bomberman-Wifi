package ist.utl.pt.bbcm.networking;

import ist.utl.pt.bbcm.server.Server;
import ist.utl.pt.bbcm.server.Player;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Message {

	public static void sendToPlayer(Player p, String msg) {
		if (p.isActive) {
			try {
				Socket client = new Socket(p.url, p.port);
				ObjectOutputStream oos = new ObjectOutputStream(
						client.getOutputStream());
				oos.writeObject(msg);
				long time = System.currentTimeMillis() / 1000 % 100;
				System.out.println("[" + time + "]" + "Sent msg[" + msg
						+ "] to player: " + p.url + ":" + p.port);
				client.close();
			} catch (UnknownHostException e) {
				System.out.println("ERROR SENDING!! msg[" + msg
						+ "] to player: " + p.url + ":" + p.port);

			} catch (IOException e) {
				System.out.println("ERROR SENDING!! msg[" + msg
						+ "] to player: " + p.url + ":" + p.port);

			}
		}
	}

	public static void sendToAllPlayers(String msg) {
		sendToPlayer(Server.p1, msg);
		sendToPlayer(Server.p2, msg);
		sendToPlayer(Server.p3, msg);
		sendToPlayer(Server.p4, msg);
	}

	public static void sendToAllOtherPlayers(String message, String[] args) {
		if (args[0].equals("p1")) {
			sendToPlayer(Server.p2, message);
			sendToPlayer(Server.p3, message);
			sendToPlayer(Server.p4, message);
		} else if (args[0].equals("p2")) {
			sendToPlayer(Server.p1, message);
			sendToPlayer(Server.p3, message);
			sendToPlayer(Server.p4, message);
		} else if (args[0].equals("p3")) {
			sendToPlayer(Server.p1, message);
			sendToPlayer(Server.p2, message);
			sendToPlayer(Server.p4, message);
		} else if (args[0].equals("p4")) {
			sendToPlayer(Server.p1, message);
			sendToPlayer(Server.p2, message);
			sendToPlayer(Server.p3, message);
		}
	}
}
