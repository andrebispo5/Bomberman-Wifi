package ist.utl.pt.bbcm.server.networking;

import ist.utl.pt.bbcm.server.Main;
import ist.utl.pt.bbcm.server.Player;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Message {
	
	public static void sendToPlayer(Player p, String msg) {
		try {
		        Socket client = new Socket(p.url, p.port);
		        ObjectOutputStream  oos = new ObjectOutputStream(client.getOutputStream());
		  		oos.writeObject(msg);
                System.out.println("Sent msg["+msg+"] to player: "+ p.url +":" +p.port);
		        client.close();
		} catch (UnknownHostException e) {
		        e.printStackTrace();
		} catch (IOException e) {
		        e.printStackTrace();
		}
	}

	public static void sendToAllPlayers(String msg) {
		sendToPlayer(Main.p1,msg);
//		sendToPlayer(Main.p2,msg);
//		sendToPlayer(Main.p3,msg);
	}
}
