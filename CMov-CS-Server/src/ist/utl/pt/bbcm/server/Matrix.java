package ist.utl.pt.bbcm.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.LEVELS;
import ist.utl.pt.bbcm.networking.Message;

public class Matrix {

	private static final int X = 0;
	private static final int Y = 1;
	private static char[][] mapMatrix;
	private static int[] p1StartPos;
	private static int[] p2StartPos;
	private static int[] p3StartPos;

	public int[] getP1StartPos() {
		return p1StartPos;
	}

	public int[] getP2StartPos() {
		return p2StartPos;
	}

	public int[] getP3StartPos() {
		return p3StartPos;
	}

	public Matrix(LEVELS lvl) {
		createMap(lvl);
	}

	private void createMap(LEVELS mapContent) {
		mapMatrix = null;
		String[] totalRows = mapContent.getMap().split("\n");
		int numCols = totalRows.length;
		int numRows = totalRows[0].length();
		mapMatrix = new char[numRows][numCols];
		for (int col = 0; col < totalRows.length; col++) {
			String rowContent = totalRows[col];
			for (int row = 0; row < rowContent.length(); row++) {
				char cell = rowContent.charAt(row);
				if (cell == 'W') {
					mapMatrix[row][col] = 'W';
				} else if (cell == 'O') {
					mapMatrix[row][col] = 'O';
				} else if (cell == 'R') {
					mapMatrix[row][col] = 'R';
				} else if (cell == '1') {
					p1StartPos = new int[] { row, col };
					mapMatrix[row][col] = 'f';
				} else if (cell == '2') {
					p2StartPos = new int[] { row, col };
					mapMatrix[row][col] = 'f';
				} else if (cell == '3') {
					p3StartPos = new int[] { row, col };
					mapMatrix[row][col] = 'f';
				} else {
					mapMatrix[row][col] = 'f';
				}
			}
		}
	}

	public static boolean posIsFree(int[] pos) {
		int x = pos[X];
		int y = pos[Y];
		try {
			char position = mapMatrix[x][y];
			if (position == 'f')
				return true;
			else
				return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Array index is out of bounds.x:" + x + ".y:"
					+ y);
		}
		return true;
	}

	public static void movePos(int[] pos1, int[] pos2) {
		try {
			mapMatrix[pos2[X]][pos2[Y]] = mapMatrix[pos1[X]][pos1[Y]];
			mapMatrix[pos1[X]][pos1[Y]] = 'f';
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Array index is out of bounds in movePos.");
		}
	}

	public static String updateRobotsPosition() {
		String msgToSendToPlayers = "moveRobots:";
		ArrayList<int[]> inicio = new ArrayList<int[]>();
		ArrayList<int[]> destino = new ArrayList<int[]>();

		for (int col = 0; col < mapMatrix[0].length; col++) {
			for (int row = 0; row < mapMatrix.length; row++) {
				char thisChar = mapMatrix[row][col];
				if (thisChar == 'R') {
					boolean findMove = true;
					ArrayList<DIRECTION> dirsUsed = new ArrayList<DIRECTION>();
					while(findMove){
						DIRECTION dir = DIRECTION.randomDir();
						int[] currPos = new int[] { row, col };
						int[] nextPos = new int[] { row + dir.x, col + dir.y };
						if(!dirsUsed.contains(dir))
							dirsUsed.add(dir);
						if(dirsUsed.size() == 4)
							findMove= false;
						if (posIsFree(nextPos)) {
							msgToSendToPlayers += row + "," + col + "," + dir.x
									+ "," + dir.y + ";";
							inicio.add(currPos);
							destino.add(nextPos);
							findMove = false;
						}
					}
				}
			}
		}
		if (!msgToSendToPlayers.equals("moveRobots:")) {
			waitForPlayersAcknowledge(msgToSendToPlayers, inicio, destino);
		}
		return msgToSendToPlayers;
	}

	private static void waitForPlayersAcknowledge(String msgToSendToPlayers,
			ArrayList<int[]> inicio, ArrayList<int[]> destino) {
		Message.sendToAllPlayers(msgToSendToPlayers);
		int numAcks = 0;
		int activePlayers = Server.getPlayersAlive();
		while (numAcks < activePlayers) {
			Socket clientSocket;
			try {
				clientSocket = Server.ackSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(
						clientSocket.getInputStream());
				String message = (String) ois.readObject();
				String[] msg = message.split(":");
				if (msg[0].equals("ack")) {
					numAcks++;
				}
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {}
		}
		for (int i = 0; i < inicio.size(); i++) {
			movePos(inicio.get(i), destino.get(i));
		}
	}

	public static void placeBomb(String x, String y) {
		int mx = Integer.parseInt(x);
		int my = Integer.parseInt(y);
		mapMatrix[mx][my] = 'B';
	}

	public static void placeExplosion(String x, String y) {
		int mx = Integer.parseInt(x);
		int my = Integer.parseInt(y);
		mapMatrix[mx][my] = 'E';
	}

	public static void removeExplosion(String x, String y) {
		int mx = Integer.parseInt(x);
		int my = Integer.parseInt(y);
		mapMatrix[mx][my] = 'f';
	}
}
