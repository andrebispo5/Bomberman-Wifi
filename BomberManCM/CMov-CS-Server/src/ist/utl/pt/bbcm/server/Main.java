package ist.utl.pt.bbcm.server;
 

import ist.utl.pt.bbcm.server.enums.LEVELS;
import ist.utl.pt.bbcm.server.networking.Message;
import ist.utl.pt.bbcm.server.networking.ServerLoopThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

 
public class Main {
 
	private static int  PORT = 4545;
    public static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static String message;
    private static int numPlayers = 0;
    public static Player p1;
    public static Player p2;
    public static Player p3;
    public static Matrix matrix;
    
    
    public static void main(String[] args) {
        createServerSocket();
        waitingForPlayers();
        startGame();
    }

    
    
	private static void startGame() {
		matrix = new Matrix(LEVELS.LVL1);
		Message.sendToPlayer(p1,"startGame,lvl1,p1,"+matrix.getP1StartPos()[0]+","+matrix.getP1StartPos()[1]);
//		sendToMsgClient(p2,"startGame,lvl1,p2");
//		sendToMsgClient(p3,"startGame,lvl1,p3");
		ServerLoopThread slt = new ServerLoopThread();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		slt.setRunning(true);
		slt.start();
	}



	private static void createServerSocket() {
		try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Could not listen on port:" +PORT);
        }
 
        System.out.println("Server started. Listening to the port "+PORT);
	}

	private static void waitingForPlayers() {
		while (numPlayers<1) {
            try {
                clientSocket = serverSocket.accept();
                ObjectInputStream ois =new ObjectInputStream(clientSocket.getInputStream());
                message = (String) ois.readObject();
                String[] msg = message.split(",");
                if(msg[0].equals("login")){
                	if(p1==null){
	                	p1 = new Player(msg[1],msg[2],msg[3],msg[4]);
	                	numPlayers++;
	                	System.out.println("Player 1 Logged in!");
	  //////////////////////////TAKE THIS OUT THIS IS HERE TO START WITH ONE PHONE!
	                	Message.sendToPlayer(p1,"playerLogin");
                	}else if(p2==null){
	                	p2 = new Player(msg[1],msg[2],msg[3],msg[4]);
	                	numPlayers++;
	                	System.out.println("Player 2 Logged in!");	
	                	Message.sendToPlayer(p1,"playerLogin");
                	}else if(p3==null){
	                	p3 = new Player(msg[1],msg[2],msg[3],msg[4]);
	                	numPlayers++;
	                	System.out.println("Player 3 Logged in!");
	                	Message.sendToPlayer(p1,"playerLogin");
	                	Message.sendToPlayer(p2,"playerLogin");
                	}
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



	
	
}