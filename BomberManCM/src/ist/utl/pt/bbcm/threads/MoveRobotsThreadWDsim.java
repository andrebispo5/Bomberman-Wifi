package ist.utl.pt.bbcm.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

import ist.utl.pt.bbcm.ApplicationContext;
import ist.utl.pt.bbcm.GameView;
import ist.utl.pt.bbcm.MainActivity;
import ist.utl.pt.bbcm.enums.MODE;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.networking.KnownPorts;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;

public class MoveRobotsThreadWDsim extends Thread implements GameLoopThread {
	static final long FPS = 1;
	private GameView view;
	private boolean running = false;
	private ApplicationContext appCtx;

	public MoveRobotsThreadWDsim(GameView view) {
		this.view = view;
		appCtx = ((ApplicationContext) view.mainActivityContext
				.getApplicationContext());
	}

	public void setRunning(boolean run) {
		running = run;
	}

	@Override
	public void run() {
		long ticksPS = 2000 / FPS;
		long startTime;
		long sleepTime;

		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		while (running) {
			startTime = System.currentTimeMillis();
			if(appCtx.isGO){
				view.moveObjects();
			}
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

}