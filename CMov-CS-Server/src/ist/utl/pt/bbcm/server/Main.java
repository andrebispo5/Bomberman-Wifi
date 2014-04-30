package ist.utl.pt.bbcm.server;
 
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.KnownPorts;
import ist.utl.pt.bbcm.networking.Message;
import ist.utl.pt.bbcm.networking.SettingsDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

 
public class Main {
 
    public static ServerSocket ackSocket;
    public static ServerSocket updateSocket;
    private static Socket clientSocket;
    private static String message;
    public static int numPlayers = 0;
    public static Player p1;
    public static Player p2;
    public static Player p3;
    public static Matrix matrix;
	private static MoveRobotsThread slt;
	private static UpdateMatrixThread umt;
	private static EndGameThread egt;
    
    
    public static void main(String[] args) {
        mainLoop();
    }



	private static void mainLoop() {
		p1 = new Player("0","0","0","0","0");
		p2 = new Player("0","0","0","0","0");
		p3 = new Player("0","0","0","0","0");
		p1.isActive = false;
		p2.isActive = false;
		p3.isActive = false;
		p1.isAlive = false;
		p2.isAlive = false;
		p3.isAlive = false;
		createServerSockets();
        waitingForPlayers();
        startGame();
	}

    
    
	private static void startGame() {
		Message.sendToAllPlayers(new SettingsDTO().getMessage());
		matrix = new Matrix(SETTINGS.lvl);
		if(numPlayers==1){
			Message.sendToPlayer(p1,"startGame:p1,"+numPlayers);
		}else
		if(numPlayers==2){
			Message.sendToPlayer(p1,"startGame:p1,"+numPlayers);
			Message.sendToPlayer(p2,"startGame:p2,"+numPlayers);
		}else
		if(numPlayers==3)
			Message.sendToPlayer(p1,"startGame:p1,"+numPlayers);
			Message.sendToPlayer(p2,"startGame:p2,"+numPlayers);
			Message.sendToPlayer(p3,"startGame:p3,"+numPlayers);
		
		slt = new MoveRobotsThread();
		umt = new UpdateMatrixThread();
		egt = new EndGameThread();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		slt.setRunning(true);
		umt.setRunning(true);
		egt.setRunning(true);
		slt.start();
		umt.start();
		egt.start();
	}



	private static void createServerSockets() {
		try {
            ackSocket = new ServerSocket(KnownPorts.acknowledgePort);
            updateSocket = new ServerSocket(KnownPorts.updatesPort);
        } catch (IOException e) {
            System.out.println("Could not listen on port:" +KnownPorts.acknowledgePort);
        }
 
        System.out.println("Server started. Listening to the port "+KnownPorts.acknowledgePort);
	}

	private static void waitingForPlayers() {
		while (true) {
            try {
                clientSocket = ackSocket.accept();
                ObjectInputStream ois =new ObjectInputStream(clientSocket.getInputStream());
                message = (String) ois.readObject();
                String[] msg = message.split(":");
                if(msg[0].equals("login")){
                	String[] args = msg[1].split(",");
                	if(!p1.isActive){
	                	p1 = new Player(args[0],args[1],args[2],args[3],args[4]);
	                	numPlayers++;
	                	System.out.println("Player 1 Logged in!");
                	}else if(!p2.isActive){
	                	p2 = new Player(args[0],args[1],args[2],args[3],args[4]);
	                	numPlayers++;
	                	System.out.println("Player 2 Logged in!");	
	                	Message.sendToPlayer(p1,"playerLogin:");
	                	Message.sendToPlayer(p2,"playerLogin:");
                	}else if(!p3.isActive){
	                	p3 = new Player(args[0],args[1],args[2],args[3],args[4]);
	                	numPlayers++;
	                	System.out.println("Player 3 Logged in!");
	                	Message.sendToPlayer(p1,"playerLogin:");
	                	Message.sendToPlayer(p2,"playerLogin:");
	                	Message.sendToPlayer(p3,"playerLogin:");
                	}
                }if(msg[0].equals("ready")){
                	clientSocket.close();
                	break;
                }
                clientSocket.close();
            } catch (IOException ex) {
            	ex.printStackTrace();
                System.out.println("Problem in message reading");
            } catch (ClassNotFoundException e) {
                System.out.println("Problem in message reading: CLASS NOT FOUND");
				e.printStackTrace();
			} 
        }
	}

	public static void endGame(){
		Player winner;
		if(numPlayers == 1)
			winner = p1;
		else{
			if(p1.score > p2.score){
				winner = p1;
			}else {
				winner = p2;
			}
			if(numPlayers==3){
				if(winner.score < p3.score)
					winner = p3;
			}
		}
		Message.sendToAllPlayers("endGame:"+ winner.name +","+winner.score);
		resetAllStats();
        mainLoop();
	}



	private static void resetAllStats() {
		slt.setRunning(false);
		umt.setRunning(false);
		egt.setRunning(false);

		System.out.println("GAAAAAAAAAAAAME EEEENNDDDD!");
		
		p1=null;
		p2=null;
		p3=null;
		numPlayers=0;
	    message = null;
	    matrix=null;
	    
	}



	public static int getPlayersAlive() {
		int activePlayers = 0;
		
		try{
			if(p1.isAlive)
				activePlayers++;
		}catch(NullPointerException e){}
		try{
			if(p2.isAlive)
				activePlayers++;
		}catch(NullPointerException e){}
		try{
			if(p3.isAlive)
				activePlayers++;
		}catch(NullPointerException e){}
		
		return activePlayers;
	}


	
	
}