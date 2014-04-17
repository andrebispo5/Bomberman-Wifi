package ist.utl.pt.bbcm.sprites;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.map.Map;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import ist.utl.pt.bbcm.enums.DIRECTION;

public class Bomb implements Sprite {


	private static final int CELL_SPACING = 32;
	private Bitmap bmp;
	private int x;
	private int y;
	private boolean needsDrawing;
	public boolean exploded;
	private GameView gameView;

	public Bomb(GameView gameView, int x, int y) {
		this.gameView = gameView;
		this.bmp = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bomb);;
		this.x = x;
		this.y = y;
		this.needsDrawing = true;
		new CountDownTimer(4000, 1000) {
		     public void onTick(long millisUntilFinished) {}
		     public void onFinish() {stopDrawing();}
		  }.start();
	}
	
	
	public void drawToCanvas(Canvas canvas) {
		if(needsDrawing){
			canvas.drawBitmap(bmp, x, y, null);
		}
	}
	
	public void stopDrawing (){
		this.needsDrawing = false;
		gameView.getMap().removeBomb(this);
		this.explode();
	}
	
	private void explode() {
		Map map = gameView.getMap();
		Sprite[][] mapMatrix = map.getMapMatrix();
		for(DIRECTION dir : DIRECTION.values()){
			int posNextMatrixX = this.getMatrixX() + dir.x;
			int posNextMatrixY = this.getMatrixY() + dir.y;
			if(mapMatrix[posNextMatrixX][posNextMatrixY].isKillable()){
				mapMatrix[posNextMatrixX][posNextMatrixY].kill();
			}
			if(mapMatrix[posNextMatrixX][posNextMatrixY].isKillable()||mapMatrix[posNextMatrixX][posNextMatrixY].isWalkable()){
				mapMatrix[posNextMatrixX][posNextMatrixY] = new Explosion(gameView, x + 32*dir.x, y + 32*dir.y);
			}
		}
		mapMatrix[this.getMatrixX()][this.getMatrixY()] = new Explosion(gameView, x, y);
	}


	@Override
	public void startDrawing() {
		this.needsDrawing = true;
	}
	public int getX() {
		return x;
	}
	
	
	public int getY() {
		return y;
	}
	
	public int getMatrixX() {
		return x/CELL_SPACING;
	}
	
	
	public int getMatrixY() {
		return y/CELL_SPACING;
	}
	
	@Override 
	public String toString(){
		return "B";
	}


	@Override
	public boolean isWalkable() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void moveRandom() {		
	}


	@Override
	public boolean isKillable() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}
}
