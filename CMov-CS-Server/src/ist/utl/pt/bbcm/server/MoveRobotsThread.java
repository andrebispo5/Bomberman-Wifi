package ist.utl.pt.bbcm.server;


public class MoveRobotsThread extends Thread{
	
	
	static final long messagePS = 1;
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
