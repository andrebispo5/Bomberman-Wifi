package ist.utl.pt.bbcm.server;


import ist.utl.pt.bbcm.networking.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class UpdateMatrixThread extends Thread {
	private boolean running = false;

	public UpdateMatrixThread() {
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		while (running) {
			Socket clientSocket;
			try {
				clientSocket = Main.updateSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(
						clientSocket.getInputStream());
				String message = (String) ois.readObject();
				String[] msg = message.split(":");
				if (msg[0].equals("movePlayer")) {
					System.out.println("Received move player event!");
					Message.sendToAllPlayers(message);
				}else if(msg[0].equals("placeBomb")){
					System.out.println("Received place bomb event!");
					Message.sendToAllPlayers(message);
				}else if(msg[0].equals("explodeBomb")){
					System.out.println("Received explode bomb event!");
					Message.sendToAllPlayers(message);
				}else if(msg[0].equals("removeExplosion")){
					System.out.println("Received removeExplosion event!");
					Message.sendToAllPlayers(message);
				}else if(msg[0].equals("playerDied")){ 
					System.out.println("Received player died event!");
					try{
						if(msg[1].equals("p1"))
							Main.p1.isAlive = false;
					}catch(NullPointerException e){}
					try{
						if(msg[1].equals("p2"))
							Main.p2.isAlive = false;
					}catch(NullPointerException e){}
					try{
						if(msg[1].equals("p3"))
							Main.p3.isAlive = false;
					}catch(NullPointerException e){}
					Message.sendToAllPlayers(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
}
