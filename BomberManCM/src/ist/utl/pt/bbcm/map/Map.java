package ist.utl.pt.bbcm.map;

import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
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
    public Player player1;
    public Player player2;
    public Player player3;
    public Player player4;
    public Player myPlayer;
	private Sprite[][] mapMatrix;
	private int mapWidth;
	private int mapHeight;
	private int lastTransX;
	private int lastTransY;
	public float scaleDensity;
    
	public Map(GameView gameView) {
		super();
		this.gameView = gameView;
        this.createMap();
        this.setMyPlayer();
        this.spawnPlayers();
	}

	private void spawnPlayers() {
		if(SETTINGS.numPlayers==1){
			player1.spawn();
			player4 = new Player(gameView,R.drawable.player4,0,0,"p4");
		}else if(SETTINGS.numPlayers==2){
			player1.spawn();
			player2.spawn();
			player4 = new Player(gameView,R.drawable.player4,0,0,"p4");
		}else if(SETTINGS.numPlayers==3){
			player1.spawn();
			player2.spawn();
			player3.spawn();
			player4 = new Player(gameView,R.drawable.player4,0,0,"p4");
		}else if(SETTINGS.numPlayers==4){
			player1.spawn();
			player2.spawn();
			player3.spawn();
			player4.spawn();
		} 
	}

	private void setMyPlayer() {
		if(SETTINGS.myPlayer.equals("p1"))
			myPlayer = player1;
		else if(SETTINGS.myPlayer.equals("p2"))
			myPlayer = player2;
		else if(SETTINGS.myPlayer.equals("p3"))
			myPlayer = player3;
		else if(SETTINGS.myPlayer.equals("p4"))
			myPlayer = player4;
		
		
		lastTransX = myPlayer.getX()+myPlayer.width/2;
		lastTransY = myPlayer.getY()+myPlayer.height/2;

	}

	public Sprite[][] getMapMatrix() {
		return mapMatrix;
	}

	private void createMap() {
		String mapContent = SETTINGS.lvl.getMap();
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
					player1 = new Player(gameView,R.drawable.player1,32*row,32*col,"p1");
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}else if(cell=='2'){
					player2 = new Player(gameView,R.drawable.player2,32*row,32*col,"p2");
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}else if(cell=='3'){
					player3 = new Player(gameView,R.drawable.player3,32*row,32*col,"p3");
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}else if(cell=='4'){
					player4 = new Player(gameView,R.drawable.player4,32*row,32*col,"p4");
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}
			}
		}
	}


	
	public void drawToCanvas(Canvas canvas) {
		int backgroundGreen = Color.rgb(0, 100, 30);
		Paint paint = new Paint();  
		paint.setColor(backgroundGreen); 
		moveCamera(canvas);
		canvas.drawRect(0, 0, mapWidth, mapHeight, paint);
		player1.drawToCanvas(canvas);
		player2.drawToCanvas(canvas);
		player3.drawToCanvas(canvas);
		player4.drawToCanvas(canvas);
		for(int col=0; col < mapMatrix[0].length; col++){
			for(int row=0; row < mapMatrix.length; row++){
				mapMatrix[row][col].drawToCanvas(canvas);
			}
		}
	}


	private void moveCamera(Canvas canvas) {
		int transX = myPlayer.getX()+myPlayer.width/2;
		int transY = myPlayer.getY()+myPlayer.height/2;
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
		myPlayer.move(direction);
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
		int playerMatrixX = myPlayer.getMatrixX();
		int playerMatrixY = myPlayer.getMatrixY();
		int playerX = myPlayer.getX();
		int playerY = myPlayer.getY();
		if(mapMatrix[playerMatrixX][playerMatrixY] instanceof Walkable && myPlayer.canMove()){
			if(SETTINGS.singlePlayer)
				mapMatrix[playerMatrixX][playerMatrixY] = new Bomb(gameView,playerX,playerY,myPlayer);
			else
				new ClientConnectorTask().execute("placeBomb:"+playerMatrixX +","+ playerMatrixY+","+myPlayer.id, "PlaceBomb!");
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
		if(myPlayer.isAlive){
			myPlayer.kill();
			new ClientConnectorTask().execute("playerDied:" + myPlayer.id + "," + myPlayer.getScore() ,"died");
		}else{
			myPlayer.spawn();
			new ClientConnectorTask().execute("playerResume:" + myPlayer.id ,"died");
		}
	}
	
	public Player getPlayer1(){
		return player1;
	}

	public void updateRobotsInMatrix(String[] args) {
		for(String s:args){
			String[] robotArgs = s.split(",");
			int posX = Integer.parseInt(robotArgs[0]);
			int posY = Integer.parseInt(robotArgs[1]);
			int dirX = Integer.parseInt(robotArgs[2]);
			int dirY = Integer.parseInt(robotArgs[3]);
			DIRECTION direction = null;
			for(DIRECTION dir:DIRECTION.values()){
				if(dirX == dir.x && dirY==dir.y)
					direction = dir;
			}
			if(mapMatrix[posX][posY] instanceof Robot){
				((Robot)mapMatrix[posX][posY]).move(direction);
				gameView.getMap().updateMatrix(mapMatrix[posX][posY], posX, posY,
						posX+dirX, posY+dirY);

			}else{
				Log.w("D: RobotUpdate"," No robot found!!!!!!!!");
			}
		}
	}

	public void movePlayerX(String[] args) {
		for(String s:args){
			String[] playerArgs = s.split(",");
			int dirX = Integer.parseInt(playerArgs[1]);
			int dirY = Integer.parseInt(playerArgs[2]);
			DIRECTION direction = null;
			for(DIRECTION dir:DIRECTION.values()){
				if(dirX == dir.x && dirY==dir.y)
					direction = dir;
			}
			if(playerArgs[0].equals("p1")){
				Log.w("D: PlayerUpdate","Moving Player1. . . .");
				player1.updateMultiplayerPos(direction, playerArgs[3],playerArgs[4]);
			}else if(playerArgs[0].equals("p2")){
				Log.w("D: PlayerUpdate","Moving Player2. . . .");
				player2.updateMultiplayerPos(direction, playerArgs[3],playerArgs[4]);
			}else if(playerArgs[0].equals("p3")){
				Log.w("D: PlayerUpdate","Moving Player3. . . .");
				player3.updateMultiplayerPos(direction, playerArgs[3],playerArgs[4]);
			}else if(playerArgs[0].equals("p4")){
				Log.w("D: PlayerUpdate","Moving Player4. . . .");
				player4.updateMultiplayerPos(direction, playerArgs[3],playerArgs[4]);
			}else{
				Log.w("D: PlayerUpdate"," No player found!!!!!!!!");
			}
		}
	}

	public void killPlayer(String playerID) {
		if(playerID.contains("p1")){
			player1.kill();
		}else if(playerID.contains("p2")){
			player2.kill();
		}else if(playerID.contains("p3")){
			player3.kill();
		}else if(playerID.contains("p4")){
			player4.kill();
		}
	}

	public void placeBombMultiplayer(String[] args) {
		String[] bombArgs = args[0].split(",");
		int matX = Integer.parseInt(bombArgs[0]);
		int matY = Integer.parseInt(bombArgs[1]);
		Player bombOwner = null;
		if(bombArgs[2].equals("p1") && player1.isAlive){
			bombOwner = player1;
			mapMatrix[matX][matY] = new Bomb(gameView,matX*32,matY*32,bombOwner);
		}else if(bombArgs[2].equals("p2") && player2.isAlive){
			bombOwner = player2;
			mapMatrix[matX][matY] = new Bomb(gameView,matX*32,matY*32,bombOwner);
		}else if(bombArgs[2].equals("p3") && player3.isAlive){
			bombOwner = player3;
			mapMatrix[matX][matY] = new Bomb(gameView,matX*32,matY*32,bombOwner);
		}else if(bombArgs[2].equals("p4") && player4.isAlive){
			bombOwner = player4;
			mapMatrix[matX][matY] = new Bomb(gameView,matX*32,matY*32,bombOwner);
		}
	}

	public void giveLoot(String[] lootArgs) {
		myPlayer.updateScore(Integer.parseInt(lootArgs[1]));
	}

	public void resumePlayer(String[] args) {
		String id = args[0];
		if(id.equals("p1")){
			player1.spawn();
		}else if(id.equals("p2")){
			player2.spawn();
		}else if(id.equals("p3")){
			player3.spawn();
		}else if(id.equals("p4")){
			player4.spawn();
		}
	}

	public void pausePlayer(String id){
		Player p = getPlayerWithId(id);
		Log.i("PAUSE", id);
		Log.i("PAUSE", p.id);
		if(p.isAlive){
			if(p.isPaused){
				p.spawn();
				if(p.equals(myPlayer))
					new ClientConnectorTask().execute("playerResume:" + p.id,"resume");
			}else{
				p.pause();
				if(p.equals(myPlayer))
					new ClientConnectorTask().execute("playerPaused:" + myPlayer.id +"," + myPlayer.getScore() ,"paused");
			}
		}
	}
	
	private Player getPlayerWithId(String id) {
		if(id.equals("p1")){
			return player1;
		}else if(id.equals("p2")){
			return player2;
		}else if(id.equals("p3")){
			return player3;
		}else if(id.equals("p4")){
			return player4;
		}
		return null;
	}

}
