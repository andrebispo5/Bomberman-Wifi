package ist.utl.pt.bbcm.server;

public class Player {
	public int x;
	public int y;
	public int score;
	public String url;
	public int port;
	
	public Player(String x, String y, String url, String port) {
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
		this.score = 0;
		this.url = 	url;
		this.port = Integer.parseInt(port);
	}
}
