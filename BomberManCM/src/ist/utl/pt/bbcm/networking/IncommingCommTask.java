package ist.utl.pt.bbcm.networking;

import ist.utl.pt.bbcm.ApplicationContext;

import java.io.IOException;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketServer;
import android.os.AsyncTask;
import android.util.Log;

public class IncommingCommTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {

	private ApplicationContext appCtx;

	
	public IncommingCommTask (ApplicationContext context){
		super();
		appCtx = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		Log.d("WDSIM", "IncommingCommTask started (" + this.hashCode() + ").");

		try {
			appCtx.mSrvSocket = new SimWifiP2pSocketServer(KnownPorts.WDsimPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		while (!Thread.currentThread().isInterrupted()) {
//			try {
//				SimWifiP2pSocket sock = appCtx.mSrvSocket.accept();
//				if (appCtx.mCliSocket != null && appCtx.mCliSocket.isClosed()) {
//					appCtx.mCliSocket = null;
//				}
//				if (appCtx.mCliSocket != null) {
//					Log.d("WDsim", "Closing accepted socket because mCliSocket still active.");
//					sock.close();
//				} else {
//					publishProgress(sock);
//				}
//			} catch (IOException e) {
//				Log.d("Error accepting socket:", e.getMessage());
//				break;
//				//e.printStackTrace();
//			}
//		}
		return null;
	}

	@Override
	protected void onProgressUpdate(SimWifiP2pSocket... values) {
//		appCtx.mCliSocket = values[0];
//		appCtx.mComm = new ReceiveCommTask(appCtx);
//		appCtx.mComm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,appCtx.mCliSocket);
	}
}
