package ist.utl.pt.bbcm.networking;

import ist.utl.pt.bbcm.enums.SETTINGS;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.util.Log;

public class ClientConnectorTask extends AsyncTask<String, Void, Integer> {
	private Socket client;
	private String serverIP = "192.168.137.1";

	protected Integer doInBackground(String... cmd) {
		// validate input parameters
		if (cmd.length <= 0 && !SETTINGS.singlePlayer) {
			return 0;
		}
		// connect to the server and send the message
		try {
			if (cmd.length == 2) {
				client = new Socket(serverIP, KnownPorts.updatesPort);
			} else {
				client = new Socket(serverIP, KnownPorts.acknowledgePort);
			}

			ObjectOutputStream oos = new ObjectOutputStream(
					client.getOutputStream());
			oos.writeObject(cmd[0]);
			oos.flush();
			Log.w("REQUESTSNED", "Message sent to server: " + cmd[0]);
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected void onPostExecute(Long result) {
		return;
	}
}
