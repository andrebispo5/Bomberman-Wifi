package ist.utl.pt.bbcm.networking;

import ist.utl.pt.bbcm.ApplicationContext;

import java.io.IOException;
import java.net.UnknownHostException;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import android.os.AsyncTask;

public class OutgoingCommTask extends AsyncTask<String, Void, String> {

	private ApplicationContext appCtx;
	
	public OutgoingCommTask(ApplicationContext context){
		super();
		appCtx = context;
	}
	
	@Override
	protected void onPreExecute() {}

	@Override
	protected String doInBackground(String... params) {
		try {
			appCtx.mCliSocket = new SimWifiP2pSocket(params[0],
					KnownPorts.WDsimPort);
		} catch (UnknownHostException e) {
			return "Unknown Host:" + e.getMessage();
		} catch (IOException e) {
			return "IO error:" + e.getMessage();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
//			mTextOutput.setText(result);
//			findViewById(R.id.idConnectButton).setEnabled(true);
		}
		else {
			appCtx.mComm = new ReceiveCommTask(appCtx);
			appCtx.mComm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,appCtx.mCliSocket);
		}		
	}
}
