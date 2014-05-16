package ist.utl.pt.bbcm.networking;

import java.io.Serializable;
import ist.utl.pt.bbcm.enums.LEVELS;
import ist.utl.pt.bbcm.enums.SETTINGS;

public class SettingsExtractor implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public  LEVELS lvl = SETTINGS.lvl;
	public  int gameDuration = SETTINGS.gameDuration;
	public  int bombTimer = SETTINGS.bombTimer;
	public  int explosionRange = SETTINGS.explosionRange;
	public  int explosionDuration = SETTINGS.explosionDuration;
	public  int robotSpeed = SETTINGS.robotSpeed;
	public  int ptsPerRobot = SETTINGS.ptsPerRobot;
	public  int ptsPerPlayer = SETTINGS.ptsPerPlayer;
	
	public String getMessage() {
		String message = "settings:"+lvl.ordinal()+","+gameDuration+","+bombTimer+","
							+explosionRange+","+explosionDuration+","+robotSpeed+","
							+ptsPerRobot+","+ptsPerPlayer;
		return message;
	}
	public String getWDSimMessage() {
		String message = bombTimer+","+explosionRange+","+explosionDuration+","
						+robotSpeed+","+ptsPerRobot+","+ptsPerPlayer;
		return message;
	}
}
