package ist.utl.pt.bbcm.server.threads;


import ist.utl.pt.bbcm.networking.Message;
import ist.utl.pt.bbcm.server.Server;
import ist.utl.pt.bbcm.server.Matrix;

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
		Socket clientSocket = null;
		while (running && Server.getPlayersAlive()>0) {
			try {
				clientSocket = Server.updateSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(
						clientSocket.getInputStream());
				String message = (String) ois.readObject();
				String[] msg = message.split(":");
				if (msg[0].equals("movePlayer")) {
					System.out.println("Received move player event!");
					Message.sendToAllOtherPlayers(message,msg[1].split(","));
				}else if(msg[0].equals("placeBomb")){
					System.out.println("Received place bomb event!");
					String[] args = msg[1].split(",");
					Matrix.placeBomb(args[0],args[1]);
					Message.sendToAllPlayers(message);
				}else if(msg[0].equals("explodeBomb")){
					System.out.println("Received explode bomb event!");
					String[] args = msg[1].split(",");
					Matrix.placeExplosion(args[0],args[1]);
				}else if(msg[0].equals("removeExplosion")){
					System.out.println("Received removeExplosion event!");
					String[] args = msg[1].split(",");
					Matrix.removeExplosion(args[0],args[1]);
				}else if(msg[0].equals("playerDied")){ 
					System.out.println("Received player died event!");
					propagateDeadPlayerMsg(message, msg);
				}else if(msg[0].equals("timeOut")){ 
					System.out.println("Received timeout  event!");
					processTimeoutEvent(msg);
				}else if(msg[0].equals("giveLoot")){ 
					System.out.println("Received player died event!");
					giveLootToPlayer(message, msg);
				}else if(msg[0].equals("playerResume")){ 
					System.out.println("Received player resume event!");
					playerResumed(message, msg);
				}else if(msg[0].equals("quitGame")){ 
					System.out.println("Received player quit game event!");
					playerQuitGame(msg[1]);
					Message.sendToAllOtherPlayers("playerDied:"+msg[1], msg[1].split(","));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}

	private void playerResumed(String message, String[] msg) {
		String[] resumeArgs = msg[1].split(",");
		Message.sendToAllOtherPlayers(message,resumeArgs);
		if(resumeArgs[0].equals("p1")){
			Server.p1.isAlive = true;
		}
		else if(resumeArgs[0].equals("p2")){
			Server.p2.isAlive = true;
		}
		else if(resumeArgs[0].equals("p3")){
			Server.p3.isAlive = true;
		}
		else if(resumeArgs[0].equals("p4")){
			Server.p4.isAlive = true;
		}
	}

	private void playerQuitGame(String name) {
		if(name.equals("p1")){
			Message.sendToPlayer(Server.p1, "quitGame:nada");
			Server.p1.isActive = false;
			Server.p1.isAlive = false;
		}else if(name.equals("p2")){
			Message.sendToPlayer(Server.p2, "quitGame:nada");
			Server.p2.isActive = false;
			Server.p2.isAlive = false;
		}else if(name.equals("p3")){
			Message.sendToPlayer(Server.p3, "quitGame:nada");
			Server.p3.isActive = false;
			Server.p3.isAlive = false;
		}else if(name.equals("p4")){
			Message.sendToPlayer(Server.p4, "quitGame:nada");
			Server.p4.isActive = false;
			Server.p4.isAlive = false;
		}
	}

	private void giveLootToPlayer(String message, String[] msg) {
		String[] args = msg[1].split(",");
		if(args[0].equals("p1")){
			Message.sendToPlayer(Server.p1,message);						
		}else if(args[0].equals("p2")){
			Message.sendToPlayer(Server.p2,message);						
		}else if(args[0].equals("p3")){
			Message.sendToPlayer(Server.p3,message);						
		}else if(args[0].equals("p4")){
			Message.sendToPlayer(Server.p4,message);						
		}
	}

	private void processTimeoutEvent(String[] msg) {
		String[] args = msg[1].split(",");
		if(args[0].equals("p1")){
			Server.p1.isAlive = false;
			Server.p1.score = Integer.parseInt(args[1]);
		}
		else if(args[0].equals("p2")){
			Server.p2.score = Integer.parseInt(args[1]);
			Server.p2.isAlive = false;
		}
		else if(args[0].equals("p3")){
			Server.p3.score = Integer.parseInt(args[1]);
			Server.p3.isAlive = false;
		}
		else if(args[0].equals("p4")){
			Server.p4.score = Integer.parseInt(args[1]);
			Server.p4.isAlive = false;
		}
	}

	private void propagateDeadPlayerMsg(String message, String[] msg) {
		String[] scoreArgs = msg[1].split(",");
		Message.sendToAllOtherPlayers(message,scoreArgs);
		if(scoreArgs[0].equals("p1")){
			Server.p1.isAlive = false;
			Server.p1.score = Integer.parseInt(scoreArgs[1]);
		}
		else if(scoreArgs[0].equals("p2")){
			Server.p2.score = Integer.parseInt(scoreArgs[1]);
			Server.p2.isAlive = false;
		}
		else if(scoreArgs[0].equals("p3")){
			Server.p3.score = Integer.parseInt(scoreArgs[1]);
			Server.p3.isAlive = false;
		}
		else if(scoreArgs[0].equals("p4")){
			Server.p4.score = Integer.parseInt(scoreArgs[1]);
			Server.p4.isAlive = false;
		}
	}
}
