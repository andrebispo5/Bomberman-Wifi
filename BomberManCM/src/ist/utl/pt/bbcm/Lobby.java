package ist.utl.pt.bbcm;

import ist.utl.pt.bbcm.enums.LEVELS;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class Lobby extends Activity {
	private static int PORT = 4545;
	private int numPlayers = 0;
	final Context context = this;
	public static ServerSocket receiverSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_lobby);
		createServerSocket();
		sendLoginRequest();
		waitingForPlayers();

	}

	private void sendLoginRequest() {
		new ClientConnectorTask().execute("login:"+SETTINGS.playerName+",20,20," + getLocalIpAddress()
				+ "," + PORT);
	}

	private void waitingForPlayers() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... params) {
				while (true) {
					try {
						Socket centralSocket = receiverSocket.accept();
						ObjectInputStream ois = new ObjectInputStream(
								centralSocket.getInputStream());
						String receivedString = (String) ois.readObject();
						String[] sliptedString = receivedString.split(":");
						String message = sliptedString[0];
						if (message.equals("playerLogin")) {
							numPlayers++;
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (numPlayers == 1) {
										TextView tv = (TextView) findViewById(R.id.waitingP1);
										tv.setText("Connected!");
										tv.setTextColor(Color.rgb(0, 120, 0));
									} else if (numPlayers == 2) {
										TextView tv = (TextView) findViewById(R.id.waitingP2);
										tv.setText("Connected!");
										tv.setTextColor(Color.rgb(0, 120, 0));
									}
								}
							});
						} else if (message.contains("startGame")) {
							String[] args = sliptedString[1].split(",");
							SETTINGS.myPlayer = args[0];
							SETTINGS.numPlayers = Integer.parseInt(args[1]);
							Log.w("D: SETTINGS",""+SETTINGS.myPlayer+","+SETTINGS.numPlayers);
							Intent intent = new Intent(context,
									ist.utl.pt.bbcm.MainActivity.class);
							startActivity(intent);
							finish();
							overridePendingTransition(R.anim.fade_out,
									R.anim.fade_in);
							break;
						}else if (message.contains("settings")) {
							String[] args = sliptedString[1].split(",");
							updateSettings(args);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				try {
					receiverSocket.close();
				} catch (IOException e) {
				}
				return null;
			}


			@Override
			protected void onPostExecute(final Void result) {
				try {
					receiverSocket.close();
				} catch (IOException e) {
				}
			}
		}.execute();
	}

	private void updateSettings(String[] args) {
		Log.w("UPDATING SETTINGS", "pts:" + Integer.parseInt(args[0]));
		SETTINGS.bombTimer = Integer.parseInt(args[2]);
		SETTINGS.explosionDuration = Integer.parseInt(args[4]);
		SETTINGS.explosionRange = Integer.parseInt(args[3]);
		SETTINGS.gameDuration = Integer.parseInt(args[1]);
		SETTINGS.lvl = LEVELS.values()[Integer.parseInt(args[0])];
		SETTINGS.ptsPerPlayer = Integer.parseInt(args[7]);
		SETTINGS.robotSpeed = Integer.parseInt(args[5]);
		SETTINGS.ptsPerRobot = Integer.parseInt(args[6]);
		Log.w("UPDATED SETTINGS", "pts:" + SETTINGS.ptsPerRobot);
	}
	
	private void createServerSocket() {
		try {
				receiverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
		}
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("GETLOCALIP", ex.toString());
		}
		return null;
	}

	public void playerReady(View view){
		new ClientConnectorTask().execute("ready:");
	}
	
	@Override
	public void onBackPressed() {
		try {
			receiverSocket.close();
			finish();
	} catch (IOException e) {
	}
	}
	
}
