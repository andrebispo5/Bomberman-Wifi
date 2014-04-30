package ist.utl.pt.bbcm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;

public class GameLoopThread extends Thread{
	 static final long FPS = 30;
     private GameView view;
     private boolean running = false;
	 private ServerSocket receiverSocket;
    
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
		try {
			receiverSocket = new ServerSocket(4545);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			while(running){
            	Log.w("D:GAMELOOP", "Waiting Connection");
            	Socket mainServer = receiverSocket.accept();
            	Log.w("D:GAMELOOP", "Received Connection");
				ObjectInputStream ois =new ObjectInputStream(mainServer.getInputStream());
				String received = (String) ois.readObject();
				String[] msg = received.split(":");

				if(msg.length>1){
					String command = msg[0];
					final String[] args = msg[1].split(";");
					if(command.equals("moveRobots")){
						 moveRobotsAsync(args);
					}else if (command.equals("movePlayer")) {
						 movePlayerAsync(args);
					}else if(command.equals("placeBomb")){
//						 new AsyncTask<Void,Void,Void>(){
//				     		    @Override
//				     		    protected Void doInBackground(final Void... params){
//				     		    		view.updateRobotsInMatrix(args);
//				     		    		 return null;
//				     		    }
//				     		    @Override
//				     		    protected void onPostExecute(final Void result){
//				     		    }}.execute();
					}else if(command.equals("explodeBomb")){
//						 new AsyncTask<Void,Void,Void>(){
//				     		    @Override
//				     		    protected Void doInBackground(final Void... params){
//				     		    		view.updateRobotsInMatrix(args);
//				     		    		 return null;
//				     		    }
//				     		    @Override
//				     		    protected void onPostExecute(final Void result){
//				     		    }}.execute();
					}else if(command.equals("removeExplosion")){
//						 new AsyncTask<Void,Void,Void>(){
//				     		    @Override
//				     		    protected Void doInBackground(final Void... params){
//				     		    		view.updateRobotsInMatrix(args);
//				     		    		 return null;
//				     		    }
//				     		    @Override
//				     		    protected void onPostExecute(final Void result){
//				     		    }}.execute();
					}else if(command.equals("playerDied")){
						 new AsyncTask<Void,Void,Void>(){
				     		    @Override
				     		    protected Void doInBackground(final Void... params){
				     		    		view.killPlayer(args[0]);
				     		    		 return null;
				     		    }
				     		    @Override
				     		    protected void onPostExecute(final Void result){
				     		    }}.execute();
					}
				}
				mainServer.close();
			}
			receiverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void movePlayerAsync(final String[] args) {
		new AsyncTask<Void,Void,Void>(){
			    @Override
			    protected Void doInBackground(final Void... params){
			    		view.movePlayerX(args);
			    		return null;
			    }
			    @Override
			    protected void onPostExecute(final Void result){
			    }}.execute();
	}

	private void moveRobotsAsync(final String[] args) {
		new AsyncTask<Void,Void,Void>(){
			    @Override
			    protected Void doInBackground(final Void... params){
			    		view.updateRobotsInMatrix(args);
			    		 return null;
			    }
			    @Override
			    protected void onPostExecute(final Void result){
			    	Log.w("GAMELOOP", "Sending ACK!");
					new ClientConnectorTask().execute("ack:");
			    }}.execute();
	}
	

	private void singlePlayerLoop() {
		drawCanvas();
		view.moveObjects();
	}

}
