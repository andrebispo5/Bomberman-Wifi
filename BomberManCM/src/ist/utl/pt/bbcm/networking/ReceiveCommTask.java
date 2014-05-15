package ist.utl.pt.bbcm.networking;

import ist.utl.pt.bbcm.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import android.os.AsyncTask;
import android.util.Log;

public class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {
	SimWifiP2pSocket s;
	private ApplicationContext appCtx;
	
	public ReceiveCommTask(ApplicationContext context){
		super();
		appCtx = context;
	}
	
	@Override
	protected Void doInBackground(SimWifiP2pSocket... params) {
		BufferedReader sockIn;
		String st;
		
		s = params[0];
		try {
			sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));

			while ((st = sockIn.readLine()) != null) {
				publishProgress(st);
			}
		} catch (IOException e) {
			Log.d("Error reading socket:", e.getMessage());
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		
	}

	@Override
	protected void onProgressUpdate(String... values) {
		Log.i("RECEIVED COMM",values[0]);
	}

	@Override
	protected void onPostExecute(Void result) {
		if (!s.isClosed()) {
			try {
				s.close();
			}
			catch (Exception e) {
				Log.d("Error closing socket:", e.getMessage());
			}
		}
		s = null;
		if (appCtx.mBound) {
			Log.i("RECEIVED COMM","Esta bound");
		} else {
			Log.i("RECEIVED COMM","Nao esta bound");
		}
	}
}
