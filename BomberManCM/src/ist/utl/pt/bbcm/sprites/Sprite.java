package ist.utl.pt.bbcm.sprites;


import android.graphics.Canvas;

public interface Sprite {
    public void drawToCanvas(Canvas canvas);
    public void stopDrawing ();
    public void startDrawing ();
	public boolean isWalkable();
	public int getX();
	public int getY();
	public void moveRandom();
	public boolean isKillable();
	public void kill();

}
