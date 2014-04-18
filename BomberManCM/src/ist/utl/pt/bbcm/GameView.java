package ist.utl.pt.bbcm;

import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.map.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameView extends SurfaceView {
    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
	private Map map;
	public float scaleDen;
	public Context context;
	public MainActivity communicationChannel;
	
    public Map getMap() {
		return map;
	}


	public GameView(Context context, MainActivity act) {
          super(context);
          this.context=context;
          communicationChannel = act;
          scaleDen = getResources().getDisplayMetrics().density;
          gameLoopThread = new GameLoopThread(this);
          holder = getHolder();
          //holder.setFixedSize(32*19, 32*13);
          addHolderCallback();
          map = new Map(this);
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
		communicationChannel.setVal(val);
	}

}
