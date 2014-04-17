package ist.utl.pt.bbcm.sprites;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.enums.DIRECTION;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Robot implements Sprite {
	// direction = 0 up, 1 left, 2 down, 3 right,
	// animation = 3 back, 1 left, 0 front, 2 right
	private static final int CELL_SPACING = 32;
	private int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };
	private static final int BMP_ROWS = 4;
	private static final int BMP_COLUMNS = 3;
	private Bitmap bmp;
	private int currentFrame = 0;
	private int x = 0;
	private int y = 0;
	private int width;
	private int height;
	private int numSteps;
	private int xSpeed;
	private int ySpeed;
	private boolean needsDrawing;
	private boolean isAlive;
	private GameView gameView;
	
	public Robot(GameView gameView, int x, int y) {
		this.gameView = gameView;
    	this.bmp = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.robot);
    	this.x = x;
    	this.y = y;
        this.width = bmp.getWidth() /BMP_COLUMNS;
        this.height = bmp.getHeight()/BMP_ROWS ;
        this.xSpeed = 0;
        this.ySpeed = 0;
        this.needsDrawing = true;
        this.isAlive = true;
	}

	@Override
	public void drawToCanvas(Canvas canvas) {
		if(needsDrawing && isAlive){
			updateSprite();
			int srcX = currentFrame * width;
			int srcY = getAnimationRow() * height;
			Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
			Rect dst = new Rect(x, y, x + width, y + height);
			canvas.drawBitmap(bmp, src, dst, null);
		}
	}

	@Override
	public void stopDrawing() {
		this.needsDrawing = false;
	}
	
	@Override
	public void startDrawing() {
		this.needsDrawing = true;
	}
	
	public void moveRandom() {
		DIRECTION direction = DIRECTION.randomDir();
		int posMatrixX = this.getMatrixX();
		int posMatrixY = this.getMatrixY();
		int posNextMatrixX = this.getMatrixX() + direction.x;
		int posNextMatrixY = this.getMatrixY() + direction.y;
		if(gameView.getMap().posIsFree(posNextMatrixX, posNextMatrixY) && this.canMove()){
			int tmpx = direction.x * CELL_SPACING/8;
			int tmpy = direction.y * CELL_SPACING/8;
			gameView.getMap().updateMatrix(this,posMatrixX,posMatrixY, posNextMatrixX, posNextMatrixY);
			if (numSteps == 0) {
				numSteps = CELL_SPACING;
				xSpeed = tmpx;
				ySpeed = tmpy;
			}
		}
	}
	
	private void updateSprite() {
		currentFrame = ++currentFrame % BMP_COLUMNS;
		if (numSteps > 0) {
			x = x + xSpeed;
			numSteps -= Math.abs(xSpeed);
			y = y + ySpeed;
			numSteps -= Math.abs(ySpeed);
		}
	}

	// direction = 0 up, 1 left, 2 down, 3 right,
	// animation = 3 back, 1 left, 0 front, 2 right
	private int getAnimationRow() {
		double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
		int direction = (int) Math.round(dirDouble) % BMP_ROWS;
		return DIRECTION_TO_ANIMATION_MAP[direction];
	}

	public int getMatrixX() {
		return this.x/CELL_SPACING;
	}

	public int getMatrixY() {
		return this.y/CELL_SPACING;
	}
	
	public void kill(){
		this.isAlive = false;
		this.stopDrawing();
		Sprite[][] matrix = gameView.getMap().getMapMatrix();
		matrix[x/32][y/32] = new EmptySpace(gameView,0,0);
	}
	
	public void spawn(){
		this.isAlive = false;
		this.startDrawing();
	}

	public boolean canMove() {
		if (numSteps==0)
			return true;
		else
			return false;
	}	
	
	@Override 
	public String toString(){
		return "R";
	}

	@Override
	public boolean isWalkable() {
		return false;
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
	public boolean isKillable() {
		// TODO Auto-generated method stub
		return true;
	}
}
