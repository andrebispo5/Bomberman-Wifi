package ist.utl.pt.bbcm.server.networking;

import ist.utl.pt.bbcm.server.Main;
import ist.utl.pt.bbcm.server.Matrix;


public class ServerLoopThread extends Thread{
	
	
	static final long FPS = 30;
    private boolean running = false;
    
    public ServerLoopThread() {}

  public void setRunning(boolean run) {
        running = run;
  }

  @Override
  public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
               startTime = System.currentTimeMillis();
               Message.sendToAllPlayers(Matrix.updateRobotsPosition());
               sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
               try {
                      if (sleepTime > 0)
                             sleep(sleepTime);
                      else
                             sleep(10);
               } catch (Exception e) {}
        }
  }
}
