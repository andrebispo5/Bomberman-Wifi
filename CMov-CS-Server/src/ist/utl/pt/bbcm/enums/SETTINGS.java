package ist.utl.pt.bbcm.enums;

import java.io.Serializable;

public class SETTINGS implements Serializable{
	private static final long serialVersionUID = 1L;
	public static LEVELS lvl = LEVELS.LVL1;
	public static int gameDuration = 30*1000;
	public static int bombTimer = 2000;
	public static int explosionRange = 1;
	public static int explosionDuration = 1000;
	public static int robotSpeed = 2;
	public static int ptsPerRobot = 15;
	public static int ptsPerPlayer = 25;
}
