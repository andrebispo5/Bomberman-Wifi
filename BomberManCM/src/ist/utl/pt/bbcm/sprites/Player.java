package ist.utl.pt.bbcm.sprites;

import ist.utl.pt.bbcm.ApplicationContext;
import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.MODE;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.interfaces.Killable;
import ist.utl.pt.bbcm.interfaces.Moveable;
import ist.utl.pt.bbcm.interfaces.Sprite;
import ist.utl.pt.bbcm.interfaces.Walkable;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

public class Player implements Sprite, Killable, Moveable, Walkable {

	// direction = 0 up, 1 left, 2 down, 3 right,
	// animation = 3 back, 1 left, 0 front, 2 right
	public String id;
	private static final int CELL_SPACING = 32;
	private int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };
	private static final int BMP_ROWS = 4;
	private static final int BMP_COLUMNS = 3;
	private Bitmap bmp;
	private int currentFrame = 0;
	private int x = 0;
	private int y = 0;
	public int startX;
	public int startY;
	public int width;
	public int height;
	private int numSteps;
	private int xSpeed;
	private int ySpeed;
	private boolean needsDrawing;
	public boolean isAlive;
	private GameView gameView;
	public int score;
	private DIRECTION nextMove;

	public Player(GameView gameView, int image, int x, int y, String id) {
		this.gameView = gameView;
		this.bmp = BitmapFactory.decodeResource(gameView.getResources(), image);
		this.setX(x);
		this.setY(y);
		this.width = bmp.getWidth() / BMP_COLUMNS;
		this.height = bmp.getHeight() / BMP_ROWS;
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.needsDrawing = true;
		this.isAlive = false;
		this.score = 0;
		this.nextMove = null;
		this.id = id;
		this.startX = x;
		this.startY = y;
	}

	@Override
	public void drawToCanvas(Canvas canvas) {
		if (needsDrawing && isAlive) {
			updateSprite();
			int srcX = currentFrame * width;
			int srcY = getAnimationRow() * height;
			Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
			Rect dst = new Rect(getX(), getY(), getX() + width, getY() + height);
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

	private void updateSprite() {
		if (numSteps > 0) {
			setX(getX() + xSpeed);
			numSteps -= Math.abs(xSpeed);
			setY(getY() + ySpeed);
			numSteps -= Math.abs(ySpeed);
			currentFrame = ++currentFrame % BMP_COLUMNS;
		} else if (nextMove != null && SETTINGS.mode == MODE.SGP) {
			Log.w("QUEUE", "Using next dir:" + nextMove.name());
			this.move(nextMove);
			nextMove = null;
		}
	}

	@Override
	public void move(DIRECTION direction) {
		int posNextMatrixX = this.getMatrixX() + direction.x;
		int posNextMatrixY = this.getMatrixY() + direction.y;
		if (gameView.getMap().posIsFree(posNextMatrixX, posNextMatrixY)) {
				if (this.canMove()) {
						int tmpx = direction.x * CELL_SPACING / 8;
						int tmpy = direction.y * CELL_SPACING / 8;
						if(SETTINGS.mode == MODE.WDS){
							this.quickMove(direction);
						}else{
							numSteps = CELL_SPACING;
							xSpeed = tmpx;
							ySpeed = tmpy;
						}
						new ClientConnectorTask((ApplicationContext)gameView.mainActivityContext.getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"movePlayer:"
								+ SETTINGS.myPlayer + "," + direction.x + ","
								+ direction.y + "," + getX() +","+getY() , "updatePlayerOnServer");
				} else {
					Log.w("QUEUE", "ADDED dir:" + direction.name());
					nextMove = direction;
				}
			
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
		return this.getX() / CELL_SPACING;
	}

	public int getMatrixY() {
		return this.getY() / CELL_SPACING;
	}

	public void kill() {
		this.isAlive = false;
		this.stopDrawing();
		gameView.endGame();
	}

	public void spawn() {
		this.isAlive = true;
		this.startDrawing();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean canMove() {
		if (numSteps == 0)
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return "P";
	}

	@Override
	public int getLoot() {
		return SETTINGS.ptsPerPlayer;
	}

	public int getScore() {
		return score;
	}

	public void updateScore(int score) {
		Log.i("scoreUpdate", this.id +"- "+ score + " score:"+this.score);
		this.score += score;	
	}

	@Override
	public void moveRandom(DIRECTION direction) {
	}

	public void updateMultiplayerPos(DIRECTION direction, String argX, String argY) {
		int x = Integer.parseInt(argX);
		int y = Integer.parseInt(argY);
		int tmpx = direction.x * CELL_SPACING / 8;
		int tmpy = direction.y * CELL_SPACING / 8;
		if(SETTINGS.mode == MODE.WDS)
		{
			this.x=x;
			this.y=y;
		}else{
			if (!this.canMove()) {
				this.fixPosition();
			}else{
				this.x = x;
				this.y = y;
			}
			numSteps = CELL_SPACING;
			xSpeed = tmpx;
			ySpeed = tmpy;
		}

	}

	private void fixPosition() {
		while (numSteps != 0) {
			updateSprite();
		}
	}
	
	public void quickMove(DIRECTION direction) {
		x = x + 32 * direction.x;
		y = y + 32 * direction.y;
		numSteps = 0;
	}

	public void resetPosToStart() {
		this.x = this.startX;
		this.y = this.startY;
	}
}
