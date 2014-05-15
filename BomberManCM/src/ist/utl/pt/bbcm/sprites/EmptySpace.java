package ist.utl.pt.bbcm.sprites;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.interfaces.Sprite;
import ist.utl.pt.bbcm.interfaces.Walkable;
import android.graphics.Canvas;

public class EmptySpace implements Sprite, Walkable {
	
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
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}
	
}
