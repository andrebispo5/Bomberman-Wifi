package ist.utl.pt.bbcm.server.threads;

import ist.utl.pt.bbcm.server.Server;

public class EndGameThread extends Thread {

	static final long checksPS = 1;
	private boolean running = false;

	public EndGameThread() {
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		long ticksPS = 1000 / checksPS;
		long startTime;
		long sleepTime;
		while (running) {
			startTime = System.currentTimeMillis();
			checkIfPlayersAreAlive();
			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 50)
					sleep(ticksPS);
				else
					sleep(50);
			} catch (Exception e) {
			}
		}
	}

	private void checkIfPlayersAreAlive() {
		if(Server.getPlayersAlive() == 0){
			Thread one = new Thread() {
			    public void run() {
			        Server.endGame();
			    }  
			};
			one.start();
		}
		
	}
}
