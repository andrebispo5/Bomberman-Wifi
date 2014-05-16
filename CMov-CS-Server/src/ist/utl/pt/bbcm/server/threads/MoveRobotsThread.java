package ist.utl.pt.bbcm.server.threads;

import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.server.Server;
import ist.utl.pt.bbcm.server.Matrix;


public class MoveRobotsThread extends Thread{
	
	
	static final long messagePS = SETTINGS.robotSpeed;
    private boolean running = false;
    
    public MoveRobotsThread() {}

  public void setRunning(boolean run) {
        running = run;
  }

  @Override
  public void run() {
        long ticksPS = 1000 / messagePS;
        long startTime;
        long sleepTime;
        while (running) {
               startTime = System.currentTimeMillis();
               if(Server.getPlayersAlive() != 0)
            	   Matrix.updateRobotsPosition();
               sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
               try {
                      if (sleepTime > 50)
                             sleep(ticksPS);
                      else
                             sleep(50);
               } catch (Exception e) {}
        }
  }
}
