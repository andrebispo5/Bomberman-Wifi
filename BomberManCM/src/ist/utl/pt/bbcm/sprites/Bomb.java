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
		int score = 0;
		for(DIRECTION dir : DIRECTION.values()){
			for(int r=0; r<=SETTINGS.explosionRange; r++ ){
				int posNextMatrixX = this.getMatrixX() + dir.x*r;
				int posNextMatrixY = this.getMatrixY() + dir.y*r;
				Object adjObj = mapMatrix[posNextMatrixX][posNextMatrixY];
				if(adjObj instanceof Wall){
					break;
				}else{
					if(map.myPlayer.getMatrixX()==posNextMatrixX && map.myPlayer.getMatrixY()==posNextMatrixY){
						if(SETTINGS.singlePlayer)
							map.myPlayer.kill();
						else
							new ClientConnectorTask().execute("playerDied:" + map.myPlayer.id ,"died");
					}
//					killPlayers(map, posNextMatrixX,posNextMatrixY);
					if(adjObj instanceof Killable){
						score+=((Killable) adjObj).getLoot();
						((Killable)adjObj).kill();
						mapMatrix[posNextMatrixX][posNextMatrixY] = new Explosion(gameView, x + 32*dir.x*r, y + 32*dir.y*r);
						new ClientConnectorTask().execute("explodeBomb:"+posNextMatrixX+","+posNextMatrixY,"Explosion");
						break;
					}else if(adjObj instanceof Walkable){
						mapMatrix[posNextMatrixX][posNextMatrixY] = new Explosion(gameView, x + 32*dir.x*r, y + 32*dir.y*r);
						new ClientConnectorTask().execute("explodeBomb:"+posNextMatrixX+","+posNextMatrixY,"Explosion");
					}
				}
				
			}
		}
		bombOwner.updateScore(score);
	}


	private void killPlayers(Map map, int posX, int posY) {
		if(map.player1.getMatrixX() == posX && map.player1.getMatrixY()==posY){
			new ClientConnectorTask().execute("playerDied:" + map.player1.id ,"died");
			map.player1.kill();
		}
		if(map.player2.getMatrixX() == posX && map.player2.getMatrixY()==posY){
			new ClientConnectorTask().execute("playerDied:" + map.player2.id ,"died");
			map.player2.kill();
		}
		if(map.player3.getMatrixX() == posX && map.player3.getMatrixY()==posY){
			new ClientConnectorTask().execute("playerDied:" + map.player3.id ,"died");
			map.player3.kill();
		}
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
