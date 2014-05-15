package ist.utl.pt.bbcm.threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ist.utl.pt.bbcm.ApplicationContext;
import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.networking.KnownPorts;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;

public class GameThreadMultiplayer extends Thread implements GameLoopThread {
	static final long FPS = 30;
	private GameView view;
	private boolean running = false;
	private ServerSocket gameLoopSocket;

	public GameThreadMultiplayer(GameView view) {
		this.view = view;
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		multiPlayerLoop();
		drawCanvas();	
	}

	private void drawCanvas() {
		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;
		while (running) {
			startTime = System.currentTimeMillis();
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

	private void multiPlayerLoop() {
		try {
			gameLoopSocket = new ServerSocket(KnownPorts.updatesPort);
			gameLoopSocket.setReuseAddress(true);
		} catch (IOException e) {
			Log.e("ERROR", "Cannot create gameloop socket!");
		}
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
				while (running) {
					try {
						Socket mainServer = gameLoopSocket.accept();
						Log.w("D:GAMELOOP", "Received new msg!");
						ObjectInputStream ois = new ObjectInputStream(
								mainServer.getInputStream());
						String received = (String) ois.readObject();
						String[] msg = received.split(":");

						if (msg.length > 1) {
							String command = msg[0];
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
								mainServer.close();
								gameLoopSocket.close();
								setRunning(false);
								break;
							} else if (command.equals("giveLoot")) {
								giveLootAsync(args);
							} else if (command.equals("playerResume")) {
								new AsyncTask<Void, Void, Void>() {
									@Override
									protected Void doInBackground(final Void... params) {
										view.resumePlayer(args[0].split(","));
										return null;
									}

									@Override
									protected void onPostExecute(final Void result) {
									}
								}.execute();
							} else if (command.equals("quitGame")) {
								quitGameAsync(args);
								mainServer.close();
								gameLoopSocket.close();
								setRunning(false);
								break;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				return null;
			}

			private void quitGameAsync(final String[] args) {
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(final Void... params) {
						view.quitGame(args[0]);
						return null;
					}

					@Override
					protected void onPostExecute(final Void result) {
					}
				}.execute();
			}

			@Override
			protected void onPostExecute(final Void result) {
			}
		}.execute();
	}

	private void giveLootAsync(final String[] args) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
				view.giveLoot(args[0].split(","));
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
			}
		}.execute();
	}

	private void endGameAsync(final String[] args) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
				view.endGame(args[0].split(","));
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
			}
		}.execute();
	}

	private void killPlayerAsync(final String[] args) {

		view.killPlayer(args[0]);

	}

	private void placeBombAsync(final String[] args) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
				view.placeBombMultiplayer(args);
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
			}
		}.execute();
	}

	private void movePlayerAsync(final String[] args) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
				view.movePlayerX(args);
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
			}
		}.execute();
	}

	private void moveRobotsAsync(final String[] args) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
				view.updateRobotsInMatrix(args);
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				Log.w("D: RobotUpdate", "Finished moving robots!");
				new ClientConnectorTask((ApplicationContext)view.mainActivityContext.getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"ack:");
			}
		}.execute();
	}
}
