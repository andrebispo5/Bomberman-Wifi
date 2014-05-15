package ist.utl.pt.bbcm.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketServer;

import ist.utl.pt.bbcm.ApplicationContext;
import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.R;
import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.MODE;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.interfaces.Moveable;
import ist.utl.pt.bbcm.interfaces.Sprite;
import ist.utl.pt.bbcm.interfaces.Walkable;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.networking.KnownPorts;
import ist.utl.pt.bbcm.sprites.Bomb;
import ist.utl.pt.bbcm.sprites.EmptySpace;
import ist.utl.pt.bbcm.sprites.Obstacle;
import ist.utl.pt.bbcm.sprites.Player;
import ist.utl.pt.bbcm.sprites.Robot;
import ist.utl.pt.bbcm.sprites.Wall;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
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
	private ApplicationContext appCtx;
    
	public Map(GameView gameView) {
		super();
		this.gameView = gameView;
        this.createMap();
        this.setMyPlayer();
        this.spawnPlayers();
        appCtx = (ApplicationContext)gameView.mainActivityContext.getApplicationContext();
	}

	public void spawnPlayers() {
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

	public void setMyPlayer() {
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
		String mapContent;
		if(SETTINGS.obtainedMap.equals("none"))
			mapContent = SETTINGS.lvl.getMap();
		else	
			mapContent = SETTINGS.obtainedMap; 
		String[] totalRows = mapContent.split("=");
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
		String msgToSendToPlayers = "moveRobots:";
		ArrayList<Moveable> robots = new ArrayList<Moveable>();
		ArrayList<DIRECTION> dirs = new ArrayList<DIRECTION>();
		for(int col=0; col < mapMatrix[0].length; col++){
			for(int row=0; row < mapMatrix.length; row++){
				if(mapMatrix[row][col] instanceof Moveable){
					Robot robot = (Robot)mapMatrix[row][col]; 
					DIRECTION dir = DIRECTION.randomDir();
					ArrayList<DIRECTION> dirsUsed = new ArrayList<DIRECTION>();
					while(dirsUsed.size() != 4){
						int posNextMatrixX = robot.getX()/32 + dir.x;
						int posNextMatrixY = robot.getY()/32 + dir.y;
						if(posIsFree(posNextMatrixX,posNextMatrixY)){
							if(SETTINGS.mode == MODE.SGP){
								((Moveable)robot).moveRandom(dir); 
							}else if(SETTINGS.mode == MODE.WDS){
								msgToSendToPlayers += row + "," + col + "," + dir.x
								+ "," + dir.y +"," + robot.getX() +"," + robot.getY() + ";";
								robots.add((Moveable)robot);
								dirs.add(dir);
							}
							break;
						}else{
							dir = DIRECTION.randomDir();
							if(!dirsUsed.contains(dir))
								dirsUsed.add(dir);
						}
					}
				}
			}
		}
		if(SETTINGS.mode == MODE.WDS){
			waitForPlayersAcknowledge(msgToSendToPlayers,robots,dirs);
		}
	}

	private void waitForPlayersAcknowledge(String msgToSendToPlayers,
			ArrayList<Moveable> robots, ArrayList<DIRECTION> dirs) {
		new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,msgToSendToPlayers, "moveRobots!");
//		int activePlayers = getPlayersAlive();
		
		Log.i("MR", "Waiting ...");
//		while (appCtx.numAcks < activePlayers-1) {
//		}
		appCtx.numAcks = 0;
		for (int i = 0; i < robots.size(); i++) {
			Robot r = (Robot) robots.get(i);
			DIRECTION dir = dirs.get(i);
			r.quickMove(dir);
			Log.i("MR", "Updated ROBOT");
		}
	}

	public int getPlayersAlive() {
		int activePlayers = 0;

		try {
			if (player1.isAlive)
				activePlayers++;
		} catch (NullPointerException e) {}
		try {
			if (player2.isAlive)
				activePlayers++;
		} catch (NullPointerException e) {}
		try {
			if (player3.isAlive)
				activePlayers++;
		} catch (NullPointerException e) {}
		try {
			if (player4.isAlive)
				activePlayers++;
		} catch (NullPointerException e) {}

		return activePlayers;
	}

	public void placeBomb() {
		int playerMatrixX = myPlayer.getMatrixX();
		int playerMatrixY = myPlayer.getMatrixY();
		int playerX = myPlayer.getX();
		int playerY = myPlayer.getY();
		if(mapMatrix[playerMatrixX][playerMatrixY] instanceof Walkable && myPlayer.canMove()){
			if(SETTINGS.mode == MODE.SGP)
				mapMatrix[playerMatrixX][playerMatrixY] = new Bomb(gameView,playerX,playerY,myPlayer);
			else if(SETTINGS.mode == MODE.MLP)
				new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"placeBomb:"+playerMatrixX +","+ playerMatrixY+","+myPlayer.id, "PlaceBomb!");
			else if(SETTINGS.mode == MODE.WDS){
				new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"placeBomb:"+playerMatrixX +","+ playerMatrixY+","+myPlayer.id, "PlaceBomb!");
				mapMatrix[playerMatrixX][playerMatrixY] = new Bomb(gameView,playerX,playerY,myPlayer);
			}
			
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
			new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"playerDied:" + myPlayer.id + "," + myPlayer.getScore() ,"died");
		}else{
			myPlayer.spawn();
			new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"playerResume:" + myPlayer.id ,"died");
		}
	}
	
	public Player getPlayer1(){
		return player1;
	}

	public void updateRobotsInMatrix(String[] args) {
		for(int col=0; col < mapMatrix[0].length; col++){
			for(int row=0; row < mapMatrix.length; row++){
				if(mapMatrix[row][col] instanceof Robot){
					mapMatrix[row][col] = new EmptySpace(gameView,32*row,32*col);
				}
			}
		}
		for(String s:args){
			String[] robotArgs = s.split(",");
			int posMatX = Integer.parseInt(robotArgs[0]);
			int posMatY = Integer.parseInt(robotArgs[1]);
			int dirX = Integer.parseInt(robotArgs[2]);
			int dirY = Integer.parseInt(robotArgs[3]);
			int row = posMatX + dirX;
			int col = posMatY + dirY;
//			DIRECTION direction = null;
//			for(DIRECTION dir:DIRECTION.values()){
//				if(dirX == dir.x && dirY==dir.y)
//					direction = dir;
//			}
//			if(mapMatrix[posMatX][posMatY] instanceof Robot){
//				((Robot)mapMatrix[posMatX][posMatY]).quickMove(direction);
//			}else{
//				Log.w("D: RobotUpdate"," No robot found!!!!!!!!");
//			}
			mapMatrix[row][col] = new Robot(gameView,32*row,32*col);
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

	public void killPlayer(String inputReceived) {
		String[] args = inputReceived.split(",");
		String playerID = args[0];
		if(playerID.contains("p1")){
			player1.kill();
			player1.resetPosToStart();
			player1.score = 0;
			SETTINGS.numPlayers--;
		}else if(playerID.contains("p2")){
			player2.kill();
			player2.resetPosToStart();
			player2.score = 0;
			SETTINGS.numPlayers--;
		}else if(playerID.contains("p3")){
			player3.kill();
			player3.resetPosToStart();
			player3.score = 0;
			SETTINGS.numPlayers--;
		}else if(playerID.contains("p4")){
			player4.kill();
			player4.resetPosToStart();
			player4.score = 0;
			SETTINGS.numPlayers--;
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
		if(SETTINGS.mode == MODE.MLP)
			myPlayer.updateScore(Integer.parseInt(lootArgs[1]));
		else if(SETTINGS.mode == MODE.WDS){
			String id = lootArgs[0];
			if(id.equals("p1")){
				player1.updateScore(Integer.parseInt(lootArgs[1]));
			}else if(id.equals("p2")){
				player2.updateScore(Integer.parseInt(lootArgs[1]));
			}else if(id.equals("p3")){
				player3.updateScore(Integer.parseInt(lootArgs[1]));
			}else if(id.equals("p4")){
				player4.updateScore(Integer.parseInt(lootArgs[1]));
			}
		}
			
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

	public String getAvailablePlayer() {
		try {
			if (!player1.isAlive)
				return "p1";
		} catch (NullPointerException e) {}
		try {
			if (!player2.isAlive)
				return "p2";
		} catch (NullPointerException e) {}
		try {
			if (!player3.isAlive)
				return "p3";
		} catch (NullPointerException e) {}
		try {
			if (!player4.isAlive)
				return "p4";
		} catch (NullPointerException e) {}
		return null;
	}

	public Player getWinner() {
		Player winner;
		if (SETTINGS.numPlayers == 1)
			winner = player1;
		else {
			if (player1.getScore() > player2.getScore()) {
				winner = player1;
			} else {
				winner = player2;
			}
			if (winner.getScore() < player3.getScore()) {
				winner = player3;
			}
			if (winner.getScore() < player4.getScore()) {
				winner = player4;
			}
		}
		Log.i("new winner score",""+ winner.getScore());
		return winner;
	}

	public String getCurrentMatrix() {
		String currentMatrix="";
		int x1 = player1.startX/32;
		int y1 = player1.startY/32;
		int x2 = player2.startX/32;
		int y2 = player2.startY/32;
		int x3 = player3.startX/32;
		int y3 = player3.startY/32;
		int x4 = player4.startX/32;
		int y4 = player4.startY/32;
		for(int col=0; col < mapMatrix[0].length; col++){
			for(int row=0; row < mapMatrix.length; row++){
				String spr = mapMatrix[row][col].toString();
				if(row==x1 && col==y1)
					currentMatrix += "1";
				else if(row==x2 && col==y2)
					currentMatrix += "2";
				else if(row==x3 && col==y3)
					currentMatrix += "3";
				else if(row==x4 && col==y4 && row!=0 && col!=0)
					currentMatrix += "4";
				else if(spr.equals("B") || spr.equals("E") || spr.equals("R"))
					currentMatrix += "-";
				else
					currentMatrix += mapMatrix[row][col].toString(); 
			}
			currentMatrix += "=";
		}
		return currentMatrix;
	}
	
	public int getNumPlayersAlive() {
		int pAlive = 0;
		try {
			if (player1.isAlive)
				pAlive++;
		} catch (NullPointerException e) {}
		try {
			if (player2.isAlive)
				pAlive++;
		} catch (NullPointerException e) {}
		try {
			if (player3.isAlive)
				pAlive++;
		} catch (NullPointerException e) {}
		try {
			if (player4.isAlive)
				pAlive++;
		} catch (NullPointerException e) {}
		return pAlive;
	}

	public ArrayList<String> getIdsOfPlayersAlive() {
		ArrayList<String> playersAlive = new ArrayList<String>();
		try {
			if (player1.isAlive)
				playersAlive.add(player1.id);
		} catch (NullPointerException e) {}
		try {
			if (player2.isAlive)
				playersAlive.add(player2.id);
		} catch (NullPointerException e) {}
		try {
			if (player3.isAlive)
				playersAlive.add(player3.id);
		} catch (NullPointerException e) {}
		try {
			if (player4.isAlive)
				playersAlive.add(player4.id);
		} catch (NullPointerException e) {}
		
		if(playersAlive.contains(myPlayer.id)){
			playersAlive.remove(myPlayer.id);
		}
		return playersAlive;
	}
}
