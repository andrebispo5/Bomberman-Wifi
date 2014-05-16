package ist.utl.pt.bbcm.sprites;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.map.Map;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.sprites.interfaces.Killable;
import ist.utl.pt.bbcm.sprites.interfaces.Sprite;
import ist.utl.pt.bbcm.sprites.interfaces.Walkable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.Log;
import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.SETTINGS;

public class Bomb implements Sprite {


	private static final int CELL_SPACING = 32;
	private Bitmap bmp;
	private int x;
	private int y;
	private boolean needsDrawing;
	public boolean exploded;
	private GameView gameView;
	private Player bombOwner;

	public Bomb(GameView gameView, int x, int y, Player bombOwner) {
		this.gameView = gameView;
		this.bmp = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bomb);;
		this.x = x;
		this.y = y;
		this.needsDrawing = true;
		this.bombOwner=bombOwner;
		new CountDownTimer(SETTINGS.bombTimer, 1000) {
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
		int score = propagateExplosion(map, mapMatrix);
		if(bombOwner.isAlive && !bombOwner.isPaused)
			bombOwner.updateScore(score);
	}


	private int propagateExplosion(Map map, Sprite[][] mapMatrix) {
		int score = 0;
		for(DIRECTION dir : DIRECTION.values()){
			for(int r=0; r<=SETTINGS.explosionRange; r++ ){
				int posNextMatrixX = this.getMatrixX() + dir.x*r;
				int posNextMatrixY = this.getMatrixY() + dir.y*r;
				Object adjObj = mapMatrix[posNextMatrixX][posNextMatrixY];
				if(adjObj instanceof Wall){
					break;
				}else{
					if(map.myPlayer.getMatrixX()==posNextMatrixX && map.myPlayer.getMatrixY()==posNextMatrixY && map.myPlayer.isAlive && !map.myPlayer.isPaused){
						if(bombOwner != map.myPlayer){
							new ClientConnectorTask().execute("giveLoot:" + bombOwner.id+","+ map.myPlayer.getLoot() ,"giveLoot");
							Log.w("SCORE","KILLED PLAYER MAIS SCORE!");
						}
						if(!SETTINGS.singlePlayer)
							new ClientConnectorTask().execute("playerDied:" + map.myPlayer.id+","+ map.myPlayer.getScore() ,"died");
						map.myPlayer.kill();
					}
					if(adjObj instanceof Killable){
						score+= ((Killable)adjObj).getLoot();
						((Killable)adjObj).kill();
						mapMatrix[posNextMatrixX][posNextMatrixY] = new Explosion(gameView, x + 32*dir.x*r, y + 32*dir.y*r);
						if(!SETTINGS.singlePlayer && bombOwner == map.myPlayer)
							new ClientConnectorTask().execute("explodeBomb:"+posNextMatrixX+","+posNextMatrixY,"Explosion");
						break;
					}else if(adjObj instanceof Walkable){
						mapMatrix[posNextMatrixX][posNextMatrixY] = new Explosion(gameView, x + 32*dir.x*r, y + 32*dir.y*r);
						if(!SETTINGS.singlePlayer && bombOwner == map.myPlayer)
							new ClientConnectorTask().execute("explodeBomb:"+posNextMatrixX+","+posNextMatrixY,"Explosion");
					}
				}
				
			}
		}
		return score;
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

}
