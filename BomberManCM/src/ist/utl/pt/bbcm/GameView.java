package ist.utl.pt.bbcm;

import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.MODE;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.map.Map;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.threads.GameLoopThread;
import ist.utl.pt.bbcm.threads.GameThreadMultiplayer;
import ist.utl.pt.bbcm.threads.GameThreadSingleplayer;
import ist.utl.pt.bbcm.threads.GameThreadWDsim;
import ist.utl.pt.bbcm.threads.MoveRobotsThreadWDsim;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView {
	private SurfaceHolder holder;
	public GameLoopThread gameLoopThread;
	private Map map;
	public float scaleDen;
	public Context context;
	public MainActivity mainActivityContext;
	private MoveRobotsThreadWDsim MoveRobotsThread;
	private ApplicationContext appCtx;

	public GameView(Context context) {
		super(context);
		this.context = context;
		mainActivityContext = (MainActivity) context;
		scaleDen = getResources().getDisplayMetrics().density;
		appCtx = (ApplicationContext)mainActivityContext.getApplicationContext();
		startThread();
		holder = getHolder();
		addHolderCallback();
		map = new Map(this);
		map.scaleDensity = scaleDen;
	}

	private void startThread() {
		if(SETTINGS.mode == MODE.SGP)
			gameLoopThread = new GameThreadSingleplayer(this);
		else if(SETTINGS.mode == MODE.MLP)
			gameLoopThread = new GameThreadMultiplayer(this);
		else if(SETTINGS.mode == MODE.WDS){
			gameLoopThread = new GameThreadWDsim(this);
			MoveRobotsThread = new MoveRobotsThreadWDsim(this);
		}
	}

	private void addHolderCallback() {
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// boolean retry = true;
				// gameLoopThread.setRunning(false);
				// while (retry) {
				// try {
				// gameLoopThread.join();
				// retry = false;
				// } catch (InterruptedException e) {
				// }
				// }
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					gameLoopThread.setRunning(true);
					((Thread)gameLoopThread).start();
					MoveRobotsThread.setRunning(true);
					((Thread)MoveRobotsThread).start();
				} catch (Exception e) {
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});
	}

	public void drawToCanvas(Canvas canvas) {
		try {
			canvas.drawColor(Color.BLACK);
			map.drawToCanvas(canvas);
			this.refreshHUD(map.myPlayer.getScore());
		} catch (Exception e) {
		}
	}

	public void movePlayer(DIRECTION direction) {
		map.movePlayer(direction);
	}

	public void moveObjects() {
		map.moveObjects();
	}

	public void placeBomb() {
		map.placeBomb();
	}

	public void refreshHUD(int val) {
		mainActivityContext.setScore(val);
	}

	public void refreshNumberPlayers(int val) {
		mainActivityContext.setNumPlayers(val);
	}

	public Map getMap() {
		return map;
	}

	public void endGame() {
		if (SETTINGS.mode == MODE.SGP) {
			mainActivityContext.endGame(new String[] { map.myPlayer.id,
					String.valueOf(map.myPlayer.getScore()) });
		}
	}

	public void updateRobotsInMatrix(String[] args) {
		map.updateRobotsInMatrix(args);
	}

	public void movePlayerX(String[] args) {
		map.movePlayerX(args);
	}

	public void killPlayer(String playerID) {
		map.killPlayer(playerID);
	}

	public void placeBombMultiplayer(String[] args) {
		mainActivityContext.makeBomb(args);
	}

	public void endGame(String[] args) {
		mainActivityContext.endGame(args);
	}

	public void giveLoot(String[] split) {
		map.giveLoot(split);
	}

	public void quitGame() {
		if (SETTINGS.mode == MODE.SGP) {
			this.endGame();
		} else if (SETTINGS.mode == MODE.MLP) {
			new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"quitGame:" + map.myPlayer.id+","+ map.myPlayer.score,
					"quit");
		}  else if (SETTINGS.mode == MODE.WDS) {
			new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"quitGame:" + map.myPlayer.id+","+ map.myPlayer.score,"quit");
			mainActivityContext.unbindService(appCtx.mConnection);
		}
	}

	public void quitGame(String args) {
		String id = args.split(",")[0];
		map.killPlayer(args);
		appCtx.peersIP.remove(id);
		SETTINGS.numPlayers--;
//		communicationChannel.endGame();
	}

	public void pauseGame() {
		map.killPlayer();
	}

	public void resumePlayer(String[] split) {
		map.resumePlayer(split);
	}

}
