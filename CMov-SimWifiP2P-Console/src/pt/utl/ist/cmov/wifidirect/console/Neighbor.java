package pt.utl.ist.cmov.wifidirect.console;

public class Neighbor {
	
	private String mName;
	private String mIp;
	private int mPort;

	public Neighbor(String name, String ip, int port) {
		mName = name;
		mIp = ip;
		mPort = port;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getIp() {
		return mIp;
	}
	
	public int getPort() {
		return mPort;
	}
	
	public void print() {
		System.out.println("Neighbor = " + mName + ", " + mIp + ", " + mPort);
	}
}
