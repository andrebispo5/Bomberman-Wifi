package ist.utl.pt.bbcm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.networking.Sockets;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;

public class GameLoopThread extends Thread{
	 static final long FPS = 30;
     private GameView view;
     private boolean running = false;
    
     public GameLoopThread(GameView view) {
           this.view = view;
     }

     public void setRunning(boolean run) {
           running = run;
     }

     @Override
     public void run() {
           long ticksPS = 1000 / FPS;
           long startTime;
           long sleepTime;
           if(!SETTINGS.singlePlayer){
	           new AsyncTask<Void,Void,Void>(){
	     		    @Override
	     		    protected Void doInBackground(final Void... params){
	     		    	multiPlayerLoop();
	     		        return null;
	     		    }
	     		    @Override
	     		    protected void onPostExecute(final Void result){
	     		    }
	     		}.execute();
           }
           while (running) {
                  startTime = System.currentTimeMillis();
                  if(SETTINGS.singlePlayer){
                	  singlePlayerLoop();
                  }else{
          			drawCanvas();
                  }
                  sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
                  try {
                         if (sleepTime > 0)
                                sleep(sleepTime);
                         else
                                sleep(10);
                  } catch (Exception e) {}
           }
     }

	private void drawCanvas() {
		Canvas c = null;
		  try {
		      c = view.getHolder().lockCanvas();
		      synchronized (view.getHolder()) {
		    	  view.drawToCanvas(c);
		      }
		  } finally {
		     if (c != null) {
		            view.getHolder().unlockCanvasAndPost(c);
		     }
		  }
	}

	private void multiPlayerLoop() {
		ServerSocket thisClientSocket = null;
		try {
			thisClientSocket = new ServerSocket(4747);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true){
            try {
            	Log.w("GAMELOOP", "Enter Multiplayer");
            	Socket mainServer = thisClientSocket.accept();
				ObjectInputStream ois =new ObjectInputStream(mainServer.getInputStream());
				String received = (String) ois.readObject();
				String[] msg = received.split(":");

				if(msg.length>1){
					String command = msg[0];
					String[] args = msg[1].split(";");
					if(command.equals("moveRobots")){
						view.updateRobotsInMatrix(args);
						Log.w("GAMELOOP", "Updated Matrix!");
						new ClientConnectorTask().execute("ack:");
					}
				}
				mainServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            try {
				thisClientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void singlePlayerLoop() {
		drawCanvas();
		view.moveObjects();
	}
}
