
package ist.utl.pt.bbcm.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.util.Log;

public class ClientConnectorTask extends AsyncTask<String, Void, Integer> {
        private Socket client;
        protected Integer doInBackground(String... cmd) {
                // validate input parameters
                if (cmd.length <= 0) {
                        return 0;
                }
                // connect to the server and send the message
                try {
                        client = new Socket("192.168.137.1", 4545);
                        ObjectOutputStream  oos = new ObjectOutputStream(client.getOutputStream());
	              		oos.writeObject(cmd[0]);
	              		Log.w("REQUESTSNED", "NEW REQUEST SENT" + cmd[0]);
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
