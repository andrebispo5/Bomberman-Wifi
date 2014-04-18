package ist.utl.pt.bbcm.sprites.interfaces;


import android.graphics.Canvas;

public interface Sprite {
    public void drawToCanvas(Canvas canvas);
    public void stopDrawing ();
    public void startDrawing ();
	public int getX();
	public int getY();
	public String toString();
}
