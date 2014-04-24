package ist.utl.pt.bbcm.networking.infoWrapper;



import java.io.Serializable;

public class GameInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public Object player1;
	public Object player2;
	public Object player3;
	public Object[][] mapMatrix;

	public GameInfo(Object player12, Object player22, Object player32,
			Object[][] mapMatrix2) {
		this.player1 = player12;
		this.player2 = player22;
		this.player3 = player32;
		this.mapMatrix = mapMatrix2;
	}
	
}
