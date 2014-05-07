package ist.utl.pt.bbcm.server;




public class Player {
	public int x;
	public int y;
	public int score;
	public String url;
	public int port;
	public String name;
	public boolean isAlive;
	public boolean isActive;
	
	public Player(String name, String x, String y, String url, String port) {
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
		this.score = 0;
		this.url = 	url;
		this.port = Integer.parseInt(port);
		this.name = name;
		this.isAlive = true;
		this.isActive = true;
	}
}
