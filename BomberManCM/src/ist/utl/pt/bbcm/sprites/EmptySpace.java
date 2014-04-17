package ist.utl.pt.bbcm.sprites;

import ist.utl.pt.bbcm.GameView;
import android.graphics.Canvas;

public class EmptySpace implements Sprite {
	
	

	private int x;
	private int y;

	public EmptySpace(GameView gameView, int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void drawToCanvas(Canvas canvas) {

	}

	@Override
	public void stopDrawing() {

	}

	@Override
	public void startDrawing() {

	}
	@Override 
	public String toString(){
		return "-";
	}

	@Override
	public boolean isWalkable() {
		return true;
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
