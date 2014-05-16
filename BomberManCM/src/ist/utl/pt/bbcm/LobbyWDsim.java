package ist.utl.pt.bbcm;

import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class LobbyWDsim extends Activity {
	final Context context = this;
	private ApplicationContext appCtx;
	private ProgressBar loader;
	private EditText ipInput;
	private TextView tv;
	private Button connectBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_lobby_wdsim);
		appCtx = (ApplicationContext) getApplicationContext();
		loader = (ProgressBar) findViewById(R.id.loader);
		connectBtn = (Button) findViewById(R.id.connectBtn);
		tv = (TextView) findViewById(R.id.WDwaiting);
		tv.setVisibility(View.INVISIBLE);
		loader.setVisibility(View.INVISIBLE);
		
		waitLoginResponse();
	}

	private void waitLoginResponse() {
		Toast.makeText(appCtx, "MyIp: "+ appCtx.myIp,
				Toast.LENGTH_SHORT).show();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
				boolean joining = true;
				while(joining ){
					Log.e("Login", "sending login");
					SimWifiP2pSocket sock = null;
					BufferedReader sockIn = null;
					String st ="";
					try {
						sock = appCtx.mSrvSocket.accept();
						sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
						while ((st = sockIn.readLine()) != null) {
							String[] msg = st.split(":");
							Log.e("Login", "Received: " + msg[0]);
							if(msg[0].equals("loginAck")){
								/*Login:px,matrix,p1=192.168.0.2;p2=192.168.0.3,4,20
								 * msg[0]:msg[1]{args[0],args[1],args[2](ips[0];ip[1]),args[3],args[4]}*/
								String[] args = msg[1].split(",");
								String[] ips = args[2].split(";");
								String id = args[0];
								String currentMatrix = args[1];
								Log.e("Login", msg[1]);
								SETTINGS.obtainedMap = currentMatrix;
								SETTINGS.myPlayer = id;
								SETTINGS.numPlayers = Integer.parseInt(args[3]);
								SETTINGS.gameDuration = Integer.parseInt(args[4])-2000;
								SETTINGS.bombTimer = Integer.parseInt(args[5]);
								SETTINGS.explosionRange = Integer.parseInt(args[6]);
								SETTINGS.explosionDuration = Integer.parseInt(args[7]);
								SETTINGS.robotSpeed = Integer.parseInt(args[8]);
								SETTINGS.ptsPerRobot = Integer.parseInt(args[9]);
								SETTINGS.ptsPerPlayer = Integer.parseInt(args[10]);
								Log.e("Login", "Received: " +Integer.parseInt(args[4]));
								if(!ips[0].equals("none")){
									for(String p:ips){
										int name=0;
										int ip=1;
										String[] player = p.split("=");
										if(!player[ip].equals(appCtx.myIp))
											appCtx.peersIP.put(player[name], player[ip]);
									}
								}
								sock.close(); 
								joining = false;
								Intent intent = new Intent(context,
										ist.utl.pt.bbcm.MainActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.fade_out,
										R.anim.fade_in);
								finish();
								break;
							}
							Log.e("Login", "NextLoopStart: " + st);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void connectToGO(View v){
		tv.setVisibility(View.VISIBLE);
		loader.setVisibility(View.VISIBLE);
		connectBtn.setEnabled(false);
		Log.i("null", "fds: " + appCtx.ipGO);
		if(appCtx.ipGO.equals("noIP")){
			tv.setText("No group owner to join!");
			loader.setVisibility(View.INVISIBLE);
			connectBtn.setEnabled(true);
		}else{
			new ClientConnectorTask(appCtx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"sendToGO", "login:"+appCtx.myIp);
		}
	}
}
