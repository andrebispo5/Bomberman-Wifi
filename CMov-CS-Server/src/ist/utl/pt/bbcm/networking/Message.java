package ist.utl.pt.bbcm.networking;

import ist.utl.pt.bbcm.server.Main;
import ist.utl.pt.bbcm.server.Player;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Message {
	
	public static void sendToPlayer(Player p, String msg) {
		if(p.isActive){
			try {
			        Socket client = new Socket(p.url, p.port);
			        ObjectOutputStream  oos = new ObjectOutputStream(client.getOutputStream());
			        oos.writeObject(msg);
			        long time = System.currentTimeMillis()/1000 % 100;
	                System.out.println("["+time+"]"+"Sent msg["+msg+"] to player: "+ p.url +":" +p.port);
			        client.close();
			} catch (UnknownHostException e) {
				System.out.println("ERROR SENDING!! msg["+msg+"] to player: "+ p.url +":" +p.port);
				
			} catch (IOException e) {
				System.out.println("ERROR SENDING!! msg["+msg+"] to player: "+ p.url +":" +p.port);
				
			}
		}
	}
	

	public static void sendToAllPlayers(String msg) {
		sendToPlayer(Main.p1,msg);
		sendToPlayer(Main.p2,msg);
		sendToPlayer(Main.p3,msg);
	}

	public static void sendToAllOtherPlayers(String message, String[] args) {
		if(args[0].equals("p1")){
			sendToPlayer(Main.p2,message);
			sendToPlayer(Main.p3,message);
		}else if(args[0].equals("p2")){
			sendToPlayer(Main.p1,message);
			sendToPlayer(Main.p3,message);
		}else if(args[0].equals("p3")){
			sendToPlayer(Main.p2,message);
			sendToPlayer(Main.p1,message);
		}
	}
}
