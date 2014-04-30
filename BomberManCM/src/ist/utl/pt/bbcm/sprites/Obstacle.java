package ist.utl.pt.bbcm.sprites;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.sprites.interfaces.Killable;
import ist.utl.pt.bbcm.sprites.interfaces.Sprite;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Obstacle implements Sprite,Killable {

	private Bitmap bmp;
	private int x;
	private int y;
	private boolean needsDrawing;
	private GameView gameView;
	

	public Obstacle(GameView gameView, int x, int y) {
		this.gameView = gameView;
		this.bmp = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.brick);;
		this.x = x;
		this.y = y;
		this.needsDrawing = true;
	}
	
	
	public void drawToCanvas(Canvas canvas) {
		if(needsDrawing){
			canvas.drawBitmap(bmp, x, y, null);
		}
	}
	
	public void stopDrawing (){
		this.needsDrawing = false;
	}
	
	@Override
	public void startDrawing() {
		this.needsDrawing = true;
	}
	
	@Override 
	public String toString(){
		return "O";
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}


	@Override
	public void kill() {
		needsDrawing = false;
		Sprite[][] matrix = gameView.getMap().getMapMatrix();
		matrix[x/32][y/32] = new EmptySpace(gameView,0,0);
	}


	@Override
	public int getLoot() {
		return 1;
	}
}
