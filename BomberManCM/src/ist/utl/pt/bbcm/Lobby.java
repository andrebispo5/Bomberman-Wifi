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
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
		new ClientConnectorTask((ApplicationContext)getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"login:" + SETTINGS.playerName
				+ ",20,20," + getLocalIpAddress() + "," + PORT);
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
							Log.e("DEBUG", "Apanhei player login!");
							newPlayerLoggedIn(sliptedString[1]);
						} else if (message.contains("startGame")) {
							goToNewGame(sliptedString);
							receiverSocket.close();
							break;
						} else if (message.contains("settings")) {
							String[] args = sliptedString[1].split(",");
							updateSettings(args);
						}
					} catch (IOException e) {
					} catch (ClassNotFoundException e) {
						Log.e("ERROR", "class received is unknown!");
					}
				}
				return null;
			}

			private void newPlayerLoggedIn(final String name) {
				numPlayers++;
				Log.e("DEBUG", "Antes do runnable!");
				final TextView tv1 = (TextView) findViewById(R.id.waitingP1);
				final TextView tv2 = (TextView) findViewById(R.id.waitingP2);
				final TextView tv3 = (TextView) findViewById(R.id.waitingP3);
				final String message = "Player "+name+" Connected!";
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (numPlayers == 1) {
							tv1.setText(message);
							tv1.setTextColor(Color.rgb(0, 120, 0));
						} else if (numPlayers == 2) {
							tv2.setText(message);
							tv2.setTextColor(Color.rgb(0, 120, 0));
						} else if (numPlayers == 3) {
							tv3.setText(message);
							tv3.setTextColor(Color.rgb(0, 120, 0));
						}
					}
				});
			}

			private void goToNewGame(String[] sliptedString) {
				String[] args = sliptedString[1].split(",");
				SETTINGS.myPlayer = args[0];
				SETTINGS.numPlayers = Integer.parseInt(args[1]);
				finish();
				Intent intent = new Intent(context,
						ist.utl.pt.bbcm.MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_out,
						R.anim.fade_in);
			}

			@Override
			protected void onPostExecute(final Void result) {}
		}.execute();

	}

	private void updateSettings(String[] args) {
		SETTINGS.bombTimer = Integer.parseInt(args[2]);
		SETTINGS.explosionDuration = Integer.parseInt(args[4]);
		SETTINGS.explosionRange = Integer.parseInt(args[3]);
		SETTINGS.gameDuration = Integer.parseInt(args[1]);
		SETTINGS.lvl = LEVELS.values()[Integer.parseInt(args[0])];
		SETTINGS.ptsPerPlayer = Integer.parseInt(args[7]);
		SETTINGS.robotSpeed = Integer.parseInt(args[5]);
		SETTINGS.ptsPerRobot = Integer.parseInt(args[6]);
	}

	private void createServerSocket() {
		try {
			receiverSocket = new ServerSocket(PORT);
			receiverSocket.setReuseAddress(true);
		} catch (IOException e) {
			Log.e("ERROR","Cannot create a new socket, port already in use.");
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
					String ipv4;
					// for getting IPV4 format
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils
									.isIPv4Address(ipv4 = inetAddress
											.getHostAddress())) {
						return ipv4;
					}
				}
			}
		} catch (Exception ex) {
			Log.e("IP Address", ex.toString());
		}
		return null;
	}

	public void playerReady(View view) {
		new ClientConnectorTask((ApplicationContext)getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"ready:");
	}

	@Override
	public void onBackPressed() {
		try {
			receiverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
	}

}
