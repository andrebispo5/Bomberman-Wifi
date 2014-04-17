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
import ist.utl.pt.bbcm.sprites.Sprite;
import ist.utl.pt.bbcm.sprites.Wall;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


public class Map {
    private GameView gameView;
    private Player player1;
    private Player player2;
    private Player player3;
	private Sprite[][] mapMatrix;
	public Sprite[][] getMapMatrix() {
		return mapMatrix;
	}

	private int camWidth;
	private int camHeight;
    
	public Map(GameView gameView) {
		super();
		this.gameView = gameView;
        this.createMap();
	}


	private void createMap() {
		String mapContent = LEVELS.LVL1.getMap();
		String[] totalRows = mapContent.split("\n");
		int numCols = totalRows.length;
		int numRows = totalRows[0].length();
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
					player2 = new Player(gameView,R.drawable.player2,32*row,32*col);
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}else if(cell=='3'){
					player3 = new Player(gameView,R.drawable.player3,32*row,32*col);
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}
			}
		}
	}


	
	public void drawToCanvas(Canvas canvas) {
		moveCamera(canvas);
		player1.drawToCanvas(canvas);
		for(int col=0; col < mapMatrix[0].length; col++){
			for(int row=0; row < mapMatrix.length; row++){
				mapMatrix[row][col].drawToCanvas(canvas);
			}
		}
		//DEBUUUUUUUUUG!!!
		DEBUGPRINTMATRIX(canvas);
	}


	private void moveCamera(Canvas canvas) {
		canvas.scale(1.5f, 1.5f);
		if(player1.getX()<canvas.getWidth() && player1.getX()>canvas.getWidth()/4 + 16 && player1.getY()>canvas.getHeight()/4 +16 && player1.getY()<canvas.getHeight()/2 +32){
			this.camWidth=player1.getX()+20;
			this.camHeight=player1.getY()+20;
			canvas.translate(-player1.getX(), -player1.getY());
			canvas.translate((canvas.getWidth()/3)-16,(canvas.getHeight()/3)-16);
			Log.w("DebugPlayerPosition","x:" + player1.getX() + " y:" + player1.getY() );
			
		}
		else if(player1.getX() >= canvas.getWidth()){
			canvas.translate(-camWidth, -player1.getY());
			canvas.translate((canvas.getWidth()/3)-16,(canvas.getHeight()/3)-16);
			Log.w("DebugPlayerPositionIF1","x:" + player1.getX() + " y:" + player1.getY() );
			
		}
		
		else if(player1.getX() <= canvas.getWidth()/4 + 16){
			canvas.translate(-camWidth, -player1.getY());
			canvas.translate((canvas.getWidth()/3)+4,(canvas.getHeight()/3)-16);
			Log.w("DebugPlayerPositionIF1","x:" + player1.getX() + " y:" + player1.getY() );
			
		}
		else if(player1.getY()<=canvas.getHeight()/4 +16){
			canvas.translate(-player1.getX(), -camHeight);
			canvas.translate((canvas.getWidth()/3)-16,(canvas.getHeight()/3)+4);
		}
		else if(player1.getY()>=canvas.getHeight()/2 +32){
			canvas.translate(-player1.getX(), -camHeight);
			canvas.translate((canvas.getWidth()/3)-16,(canvas.getHeight()/3)+4);
		}
		/*
		else if(player1.getY() >= (canvas.getHeight()/2)+32){
			canvas.translate(-player1.getX(), -camHeight);
			canvas.translate((canvas.getWidth()/3)-16,(canvas.getHeight()/3)-16);
			Log.w("DebugPlayerPositionIF2","x:" + player1.getX() + " y:" + player1.getY() );
			Log.w("DebugPlayerPositionIF2","" + canvas.getHeight()/2 );
		}*/
	}

	
	/**
	 * DEBUG FUNCTION TO DRAW THE MATRIX INGAME!
	 * @param canvas
	 */
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
			if(nextPos.isWalkable())	
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
				mapMatrix[row][col].moveRandom(); 
			}
		}
	}


	public void placeBomb() {
		int playerMatrixX = player1.getMatrixX();
		int playerMatrixY = player1.getMatrixY();
		int playerX = player1.getX();
		int playerY = player1.getY();
		if(mapMatrix[playerMatrixX][playerMatrixY].isWalkable()){
			mapMatrix[playerMatrixX][playerMatrixY] = new Bomb(gameView,playerX,playerY);
		}
	}
	
	public void removeBomb(Bomb b){
		mapMatrix[b.getMatrixX()][b.getMatrixY()] = new EmptySpace(gameView,b.getX(),b.getY());
	}
}
