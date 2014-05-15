package ist.utl.pt.bbcm.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

import ist.utl.pt.bbcm.ApplicationContext;
import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.sprites.Player;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

public class GameThreadWDsim extends Thread implements GameLoopThread {
	static final long FPS = 30;
	private GameView view;
	private boolean running = false;
	private ApplicationContext appCtx;
	private SimWifiP2pSocket clientSocket;

	public GameThreadWDsim(GameView view) {
		this.view = view;
		appCtx = ((ApplicationContext) view.mainActivityContext
				.getApplicationContext());
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;
		
		multiPlayerLoop();
		
		while (running) {
			startTime = System.currentTimeMillis();
			drawCanvas();
			if(appCtx.isGO){
				checkIfGameHasEnded();
				updateNotReachablePlayers();
			}
			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			} catch (Exception e) {
			}
		}
	}

	
	private void updateNotReachablePlayers() {
		ArrayList<String> playersIds = new ArrayList<String>();
		
		playersIds = view.getMap().getIdsOfPlayersAlive();
		for(String id:playersIds){
			if(!appCtx.peersIP.containsKey(id)){
				new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"playerDied:" +id+","+ 0 ,"died");
				view.getMap().killPlayer("playerDied:" +id+","+ 0);
			}
		}
	}

	private void checkIfGameHasEnded() {
		if(view.getMap().getNumPlayersAlive() == 0){
			Player winner = view.getMap().getWinner();
			view.mainActivityContext.endGame(new String[]{winner.id,String.valueOf(winner.getScore())});
			new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"timeOut:" + winner.id+","+ winner.getScore() ,"died");
			running = false;
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
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
					try {
						SimWifiP2pSocket sock = appCtx.mSrvSocket.accept();
						if (appCtx.mCliSocket != null
								&& appCtx.mCliSocket.isClosed()) {
							appCtx.mCliSocket = null;
						}
						if (appCtx.mCliSocket != null) {
							Log.d("WDsim",
									"Closing accepted socket because mCliSocket still active.");
							sock.close();
						} else {
							//READ AND PROCESS A NEW MESSAGE RECEIVED
							new AsyncTask<SimWifiP2pSocket, String, Void>() {

								@Override
								protected Void doInBackground(
										final SimWifiP2pSocket... params) {
										BufferedReader sockIn;
										String st;
										clientSocket = params[0];
										try {
											sockIn = new BufferedReader(
													new InputStreamReader(
															clientSocket.getInputStream()));
											while ((st = sockIn.readLine()) != null) {
												publishProgress(st);
											}
										} catch (IOException e) {
											Log.d("Error reading socket:",
													e.getMessage());
										}
									
									return null;
								}

								@Override
								protected void onProgressUpdate(
										String... received) {
									String[] msg = received[0].split(":");
									if (msg.length > 1) {
										Log.i("PROTOCOL", received[0]);
										processMsgReceived(msg);
									}
								}

								@Override
								protected void onPostExecute(final Void result) {
									multiPlayerLoop();
								}
							}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
									sock);
						}
					} catch (IOException e) {
						Log.d("Error accepting socket:", e.getMessage());
						
					}
				
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	
	private void processMsgReceived(String[] msg) {
		String command = msg[0];
		Log.w("RM", "Vou processar a msg");
		final String[] args = msg[1].split(";");
		if (command.equals("moveRobots")) {
			moveRobotsAsync(args);
		} else if (command.equals("movePlayer")) {
			movePlayerAsync(args);
		} else if (command.equals("placeBomb")) {
			placeBombAsync(args);
		} else if (command.equals("playerDied")) {
			killPlayerAsync(args);
		} else if (command.equals("endGame")) {
			endGameAsync(args);
			setRunning(false);
		} else if (command.equals("giveLoot")) {
			giveLootAsync(args);
		} else if (command.equals("playerResume")) {
			view.resumePlayer(args[0].split(","));
		} else if (command.equals("quitGame")) {
			quitGameAsync(args);
		}else if (command.equals("timeOut")) {
			endGameAsync(args);
			setRunning(false);
		}else if (command.equals("ack")) {
			appCtx.numAcks++;
		}else if (command.equals("login")) {
			loginAsync(msg);
		}else if (command.equals("playerLogin")) {
			Log.e("DEBUG", "Apanhei novo player login!");
			newPlayerLoggedIn(args);
		}
	}

	
	
	
	
	
	
	private void quitGameAsync(final String[] args) {
				view.quitGame(args[0]);
	}

	private void giveLootAsync(final String[] args) {
				view.giveLoot(args[0].split(","));
	}

	private void endGameAsync(final String[] args) {
				view.endGame(args[0].split(","));
	}

	private void killPlayerAsync(final String[] args) {
			view.killPlayer(args[0]);
	}

	private void placeBombAsync(final String[] args) {
				view.placeBombMultiplayer(args);
	}

	private void movePlayerAsync(final String[] args) {
				view.movePlayerX(args);
	}

	private void moveRobotsAsync(final String[] args) {
//		new AsyncTask<Void, Void, Void>() {
//			@Override
//			protected Void doInBackground(final Void... params) {
////				new ClientConnectorTask(appCtx).executeOnExecutor(
////						AsyncTask.THREAD_POOL_EXECUTOR, "ack:debugtext");
				view.updateRobotsInMatrix(args);
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(final Void result) {
//				Log.w("D: RobotUpdate", "Finished moving robots!");
//			}
//		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	private void loginAsync(String[] msg) {
		Log.e("Login","RECEIVE LOGIN CMD! "+ SETTINGS.numPlayers +"<"+SETTINGS.lvl.getMaxP());
		if(SETTINGS.numPlayers < SETTINGS.lvl.getMaxP()){
			String id = view.getMap().getAvailablePlayer();
			if(appCtx.peersIP.size()>0)
				new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"playerLogin:"+id+","+msg[1]);
			view.resumePlayer(new String[]{id});
			appCtx.peersIP.put(id,msg[1]);
			SETTINGS.numPlayers = appCtx.peersIP.size()+1;
			String currentMatrix = view.getMap().getCurrentMatrix();
			String peersIP = appCtx.getPeersIps();
			int timeLeft = SETTINGS.gameDuration - view.mainActivityContext.ElapsedTime;
			Log.e("Login","time Left:" + timeLeft);
			Log.e("Login","Settings time:" + SETTINGS.gameDuration);
			new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					"sendToPeer",msg[1],"loginAck:"+id+","+currentMatrix+","+peersIP+","+SETTINGS.numPlayers+","+timeLeft);
		}else{
			Log.e("Login","false");
			new ClientConnectorTask(appCtx).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, "sendToPeer",msg[1],"loginDenied:");
		}
	}
	private void newPlayerLoggedIn(String[] args) {
		String[] playerArgs = args[0].split(",");
		String id = playerArgs[0];
		String ip = playerArgs[1];
		appCtx.peersIP.put(id, ip);
		SETTINGS.numPlayers++;
		view.resumePlayer(playerArgs);
	}
}

