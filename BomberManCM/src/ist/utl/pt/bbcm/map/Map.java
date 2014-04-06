package ist.utl.pt.bbcm.map;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.LEVELS;
import ist.utl.pt.bbcm.sprites.Obstacle;
import ist.utl.pt.bbcm.sprites.Player;
import ist.utl.pt.bbcm.sprites.Robot;
import ist.utl.pt.bbcm.sprites.Sprite;
import ist.utl.pt.bbcm.sprites.Wall;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.util.Log;


public class Map {
    private GameView gameView;
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private ArrayList<Robot> robots = new ArrayList<Robot>();
    private Player player1;
    private Player player2;
    private Player player3;
	private char[][] mapMatrix; 
    
	public Map(GameView gameView) {
		super();
		this.gameView = gameView;
        this.createMap();
	}


	private void createMap() {
		String mapContent = LEVELS.LVL2.getMap();
		String[] totalRows = mapContent.split("\n");
		int numCols = totalRows.length;
		int numRows = totalRows[0].length();
		mapMatrix = new char[numRows][numCols];
		
		for(int col=0; col < totalRows.length; col++){
			String rowContent = totalRows[col];
			for(int row=0; row < rowContent.length(); row++){
				char cell = rowContent.charAt(row);
				if(cell=='W'){
					walls.add(new Wall(gameView,32*row,32*col));
					mapMatrix[row][col] = cell;
				}else if(cell=='O'){
					obstacles.add(new Obstacle(gameView,32*row,32*col));
					mapMatrix[row][col] = cell;
				}else if(cell=='R'){
					robots.add(new Robot(gameView,32*row,32*col));
					mapMatrix[row][col] = cell;
				}else if(cell=='1'){
					player1 = new Player(gameView,R.drawable.player1,32*row,32*col);
					player1.spawn();
					mapMatrix[row][col] = cell;
				}else if(cell=='2'){
					player2 = new Player(gameView,R.drawable.player2,32*row,32*col);
				}else if(cell=='3'){
					player3 = new Player(gameView,R.drawable.player3,32*row,32*col);
				}
			}
		}
	}


	
	public void drawToCanvas(Canvas canvas) {
		for(Sprite cell : walls){
			cell.drawToCanvas(canvas);
		}
		for(Sprite cell : robots){
			cell.drawToCanvas(canvas);
		}
		for(Sprite cell : obstacles){
			cell.drawToCanvas(canvas);
		}
		player1.drawToCanvas(canvas);
		player2.drawToCanvas(canvas);
		player3.drawToCanvas(canvas);
	}
	
	public void movePlayer(DIRECTION direction){
		int posMatrixX = player1.getMatrixX();
		int posMatrixY = player1.getMatrixY();
		int posNextMatrixX = player1.getMatrixY() + direction.y;
		int posNextMatrixY = player1.getMatrixY() + direction.y;
		if(this.posIsFree(posMatrixX, posMatrixY)){
			player1.move(direction);
			this.updateMatrix(posMatrixX, posMatrixY, posNextMatrixX, posNextMatrixY);
		}
	}
	
	public boolean posIsFree(int i, int j) {
		int x = i ;
		int y = j ;
		try{
			Log.w("DebugNEXTposCHAR","x: "+ x +"  y: "+ y + "    char: "+ mapMatrix[x][y] );
			if(mapMatrix[x][y] == '-')	
				return true;
			else
				return false;
		}catch(ArrayIndexOutOfBoundsException e){
			Log.w("DEBUG", "PROBLEM IN: MAP POSITION IS FREE FUNCTION");
		}
		return true;
	}

	private void updateMatrix(int x, int y, int xNext, int yNext){
		char charInXY = mapMatrix[x][y];
		mapMatrix[x][y] = '-';
		mapMatrix[xNext][yNext] = charInXY;
	}

	public void moveRobots() {
		for(Robot r : robots){
			DIRECTION dir = DIRECTION.randomDir();
			int posMatrixX = r.getMatrixX();
			int posMatrixY = r.getMatrixY();
			int posNextMatrixX = r.getMatrixX() + dir.x;
			int posNextMatrixY = r.getMatrixY() + dir.y;
			if(this.posIsFree(posNextMatrixX, posNextMatrixY)){
				r.move(dir);
				this.updateMatrix(posMatrixX, posMatrixY, posNextMatrixX, posNextMatrixY);
			}
		}
	}
}