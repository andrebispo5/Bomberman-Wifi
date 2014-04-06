package ist.utl.pt.bbcm.sprites;


import android.graphics.Canvas;

public interface Sprite {
    public void drawToCanvas(Canvas canvas);
    public void stopDrawing ();
    public void startDrawing ();

}
