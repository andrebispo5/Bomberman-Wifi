package ist.utl.pt.bbcm.sprites;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.sprites.interfaces.Sprite;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.CountDownTimer;

public class Explosion implements Sprite {

	private GameView gameView;
	private Bitmap bmp;
	
	private int x;
	private int y;
	private boolean needsDrawing;

	public Explosion(GameView gameView, int x, int y) {
			this.gameView = gameView;
			this.bmp = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.explosion);
			this.x = x;
			this.y = y;
			this.needsDrawing = true;;
			new CountDownTimer(SETTINGS.explosionDuration, 1000) {
			     public void onTick(long millisUntilFinished) {}
			     public void onFinish() {stopDrawing();}
			  }.start();
	}


	@Override
	public void drawToCanvas(Canvas canvas) {
		if(needsDrawing){
			canvas.drawBitmap(bmp, x, y, null);
		}
	}

	@Override
	public void stopDrawing() {
		this.needsDrawing = false;
		this.clean();
	}

	@Override
	public void startDrawing() {
		this.needsDrawing = true;
	}


	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}


	public void clean() {
		Sprite[][] matrix = gameView.getMap().getMapMatrix();
		matrix[x/32][y/32] = new EmptySpace(gameView,0,0);
		if(!SETTINGS.singlePlayer)
			new ClientConnectorTask().execute("removeExplosion:"+(x/32)+","+(y/32),"Explosion");
	}
	
	@Override
	public String toString() {
		return "E";
	}
	
}
