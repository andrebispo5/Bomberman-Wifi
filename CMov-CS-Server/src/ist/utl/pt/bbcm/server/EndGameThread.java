package ist.utl.pt.bbcm.server;

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
		if(Main.getPlayersAlive() == 0){
			
			Main.endGame();
		}

	}
}
