package ist.utl.pt.bbcm;

import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.map.Map;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView {
    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
	private Map map;
	public float scaleDen;
	public Context context;
	public MainActivity communicationChannel;


	public GameView(Context context) {
          super(context);
          this.context=context;
          communicationChannel = (MainActivity)context;
          scaleDen = getResources().getDisplayMetrics().density;
          gameLoopThread = new GameLoopThread(this);
          holder = getHolder();
          addHolderCallback();
          map = new Map(this);
          map.scaleDensity = scaleDen;
    }


	private void addHolderCallback() {
		holder.addCallback(new SurfaceHolder.Callback() {

                 @Override
                 public void surfaceDestroyed(SurfaceHolder holder) {
                        boolean retry = true;
                        gameLoopThread.setRunning(false);
                        while (retry) {
                               try {
                                     gameLoopThread.join();
                                     retry = false;
                               } catch (InterruptedException e) {
                               }
                        }
                 }

                 @Override
                 public void surfaceCreated(SurfaceHolder holder) {
                        gameLoopThread.setRunning(true);
                        gameLoopThread.start();
                 }

                 @Override
                 public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
                 }
          });
	}

    
	protected void drawToCanvas(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        map.drawToCanvas(canvas);
		this.refreshHUD(map.myPlayer.getScore());
    }
    
    public void movePlayer(DIRECTION direction){
    	map.movePlayer(direction);
    }


	public void moveObjects() {
		map.moveObjects();
	}


	public void placeBomb() {
		map.placeBomb();
	}
	
	public void refreshHUD(int val){
		communicationChannel.setScore(val);
	}
	
	public void refreshNumberPlayers(int val){
		communicationChannel.setNumPlayers(val);
	}

	public Map getMap() {
		return map;
	}
	
	public void endGame() {
		if(SETTINGS.singlePlayer){
			try {
				gameLoopThread.setRunning(false);
				gameLoopThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			communicationChannel.endGame();
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
		communicationChannel.makeBomb(args);
	}


	public void endGame(String[] args) {
		communicationChannel.endGame(args);		
	}


	public void giveLoot(String[] split) {
		map.giveLoot(split);
	}


	public void quitGame() {
		if(SETTINGS.singlePlayer){
			this.endGame();
		}else{
			new ClientConnectorTask().execute("quitGame:"+map.myPlayer.id,"quit");
		}
	}


	public void quitGame(String[] arg) {
		communicationChannel.endGame();
	}


	public void pauseGame() {
		map.killPlayer();
	}


	public void resumePlayer(String[] split) {
		map.resumePlayer(split);
	}

	
}
