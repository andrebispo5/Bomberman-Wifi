package ist.utl.pt.bbcm.threads;

import ist.utl.pt.bbcm.GameView;
import android.graphics.Canvas;

public class GameThreadSingleplayer extends Thread  implements GameLoopThread {
	static final long FPS = 30;
	private GameView view;
	private boolean running = false;

	public GameThreadSingleplayer(GameView view) {
		this.view = view;
	}

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
			singlePlayerLoop();
			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			} catch (Exception e) {
			}
		}
	}

	private void drawCanvas() {
		Canvas c = null;
		try {
			c = view.getHolder().lockCanvas();
			synchronized (view.getHolder()) {
				view.drawToCanvas(c);
			}
		} finally {
			if (c != null) {
				view.getHolder().unlockCanvasAndPost(c);
			}
		}
	}
	
	private void singlePlayerLoop() {
		drawCanvas();
		view.moveObjects();
	}

}
