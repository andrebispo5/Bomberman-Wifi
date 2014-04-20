package ist.utl.pt.bbcm.map;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.LEVELS;
import ist.utl.pt.bbcm.sprites.Bomb;
import ist.utl.pt.bbcm.sprites.EmptySpace;
import ist.utl.pt.bbcm.sprites.Obstacle;
import ist.utl.pt.bbcm.sprites.Player;
import ist.utl.pt.bbcm.sprites.Robot;
import ist.utl.pt.bbcm.sprites.interfaces.Moveable;
import ist.utl.pt.bbcm.sprites.interfaces.Sprite;
import ist.utl.pt.bbcm.sprites.interfaces.Walkable;
import ist.utl.pt.bbcm.sprites.Wall;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


public class Map {
    private GameView gameView;
    private Player player1;
//    private Player player2;
//    private Player player3;
	private Sprite[][] mapMatrix;
	private int mapWidth;
	private int mapHeight;
	private int lastTransX;
	private int lastTransY;
    
	public Map(GameView gameView) {
		super();
		this.gameView = gameView;
        this.createMap();
	}

	public Sprite[][] getMapMatrix() {
		return mapMatrix;
	}

	private void createMap() {
		String mapContent = LEVELS.LVL1.getMap();
		String[] totalRows = mapContent.split("\n");
		int numCols = totalRows.length;
		int numRows = totalRows[0].length();
		mapWidth = numRows*32;
		mapHeight = numCols*32;
		mapMatrix = new Sprite[numRows][numCols];
		for(int col=0; col < totalRows.length; col++){
			String rowContent = totalRows[col];
			for(int row=0; row < rowContent.length(); row++){
				char cell = rowContent.charAt(row);
				if(cell=='-'){
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}
				if(cell=='W'){
					mapMatrix[row][col] = new Wall(gameView,32*row,32*col);
				}else if(cell=='O'){
					mapMatrix[row][col] = new Obstacle(gameView,32*row,32*col);
				}else if(cell=='R'){
					mapMatrix[row][col] = new Robot(gameView,32*row,32*col);
				}else if(cell=='1'){
					player1 = new Player(gameView,R.drawable.player1,32*row,32*col);
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
					player1.spawn();
				}else if(cell=='2'){
//					player2 = new Player(gameView,R.drawable.player2,32*row,32*col);
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}else if(cell=='3'){
//					player3 = new Player(gameView,R.drawable.player3,32*row,32*col);
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}
			}
		}

		lastTransX = player1.getX()+player1.width/2;
		lastTransY = player1.getY()+player1.height/2;
	}


	
	public void drawToCanvas(Canvas canvas) {
		int backgroundGreen = Color.rgb(0, 100, 30);
		Paint paint = new Paint();  
		paint.setColor(backgroundGreen); 
		moveCamera(canvas);
		canvas.drawRect(0, 0, mapWidth, mapHeight, paint);
		player1.drawToCanvas(canvas);
		for(int col=0; col < mapMatrix[0].length; col++){
			for(int row=0; row < mapMatrix.length; row++){
				mapMatrix[row][col].drawToCanvas(canvas);
			}
		}
		gameView.refreshHUD(player1.getScore());
		//DEBUUUUUUUUUG!!!
		//DEBUGPRINTMATRIX(canvas);
	}


	private void moveCamera(Canvas canvas) {
		float scaleDensity = gameView.scaleDen;
		int transX = player1.getX()+player1.width/2;
		int transY = player1.getY()+player1.height/2;
		int minX = (int) (canvas.getWidth()/(2*scaleDensity));
		int minY = (int) (canvas.getHeight()/(2*scaleDensity))-2;
		int limitX = (int) (mapWidth-canvas.getWidth()/(2*scaleDensity));
		int limitY = (int) (mapHeight-canvas.getWidth()/(2*scaleDensity))-2;
		canvas.translate(canvas.getWidth()/2,canvas.getHeight()/2);
		canvas.scale(scaleDensity , scaleDensity);
		if(transX >= minX && transX <= limitX){
			lastTransX = transX;
		}
		if(transY >= minY && transY <= limitY){
			lastTransY = transY;
		}
		canvas.translate(-lastTransX, -lastTransY);
	}

	
	/**
	 * DEBUG FUNCTION TO DRAW THE MATRIX INGAME!
	 * @param canvas
	 */
	@SuppressWarnings("unused")
	private void DEBUGPRINTMATRIX(Canvas canvas) {
		Paint paint = new Paint();  
		paint.setColor(Color.WHITE); 
		paint.setTextSize(8); 
		for(int col=0; col < mapMatrix[0].length; col++){
			for(int row=0; row < mapMatrix.length; row++){
				canvas.drawText(row+","+col+","+mapMatrix[row][col].toString(), row*32, col*32+16, paint); 
			}
		}
	}
	
	public boolean posIsFree(int i, int j) {
		int x = i ;
		int y = j ;
		Sprite nextPos = mapMatrix[x][y];
		try{
			if(nextPos instanceof Walkable)	
				return true;
			else
				return false;
		}catch(ArrayIndexOutOfBoundsException e){
			Log.w("DEBUG", "PROBLEM IN: MAP POSITION IS FREE FUNCTION");
		}
		return true;
	}

	public void updateMatrix(Sprite obj,int x, int y, int xNext, int yNext){
		mapMatrix[xNext][yNext] = obj;
		mapMatrix[x][y] = new EmptySpace(gameView,obj.getX(),obj.getY());
	}
	
	public void movePlayer(DIRECTION direction){
		player1.move(direction);
	}

	public void moveObjects() {
		for(int col=0; col < mapMatrix[0].length; col++){
			for(int row=0; row < mapMatrix.length; row++){
				if(mapMatrix[row][col] instanceof Moveable)
					((Moveable)mapMatrix[row][col]).moveRandom(); 
			}
		}
	}


	public void placeBomb() {
		int playerMatrixX = player1.getMatrixX();
		int playerMatrixY = player1.getMatrixY();
		int playerX = player1.getX();
		int playerY = player1.getY();
		if(mapMatrix[playerMatrixX][playerMatrixY] instanceof Walkable && player1.canMove()){
			mapMatrix[playerMatrixX][playerMatrixY] = new Bomb(gameView,playerX,playerY,player1);
		}
	}
	
	public void removeBomb(Bomb b){
		mapMatrix[b.getMatrixX()][b.getMatrixY()] = new EmptySpace(gameView,b.getX(),b.getY());
	}
	
	public int getPlayerPosX(){
		int x = player1.getMatrixX();
		return x;
	}
	
	public int getPlayerPosY(){
		int y = player1.getMatrixY();
		return y;
	}
	
	public void killPlayer(){
		Log.w("KILLING ORDER!","Killing order on map.KillPlayer");
		player1.kill();
	}
}
