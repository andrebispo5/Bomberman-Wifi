package ist.utl.pt.bbcm;


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
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class Lobby extends Activity {
	private static int PORT ;
	private int numPlayers = 0;
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_lobby);
		createServerSocket();
		
		new ClientConnectorTask().execute("login,20,20,"+getLocalIpAddress()+","+PORT );
		
		new AsyncTask<Void,Void,Void>(){
		    @Override
		    protected Void doInBackground(final Void... params){
				    	 waitingForPlayers();
		        return null;
		    }
		    @Override
		    protected void onPostExecute(final Void result){
		    }
		}.execute();

	}

	
	private void waitingForPlayers() {
		while (numPlayers < 1) {
            try {
            	Socket centralSocket = thisClientSocket.accept();
				ObjectInputStream ois =new ObjectInputStream(centralSocket.getInputStream());
				String message = (String) ois.readObject();
				if(message.equals("playerLogin")){
					numPlayers++;
					runOnUiThread(new Runnable() {
						@Override
					     public void run() {
					    	 if(numPlayers==1){
									TextView tv = (TextView) findViewById(R.id.waitingP1);
									tv.setText("Connected!");
									tv.setTextColor(Color.rgb(0, 120, 0));
								}else if(numPlayers==2){
									TextView tv = (TextView) findViewById(R.id.waitingP2);
									tv.setText("Connected!");
									tv.setTextColor(Color.rgb(0, 120, 0));
								}
					    }
					});
				}
				centralSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		receiveFurtherCmds();
		
	}


	private void receiveFurtherCmds() {
		while (true) {
            try {
            	Socket centralSocket = Settings.thisClientSocket.accept();
				ObjectInputStream ois =new ObjectInputStream(centralSocket.getInputStream());
				String message = (String) ois.readObject();
				if(message.contains("startGame")){
					Intent intent = new Intent(context, ist.utl.pt.bbcm.MainActivity.class);
				    startActivity(intent);
				    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
				    
				}
				centralSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}



	private void createServerSocket() {
		try {
			Settings.thisClientSocket = create(new int[]{4001,4002,4003,4004,4005,4006,4444,5555,4548,4565,4412,1122,6598,7852,3589});
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	public ServerSocket create(int[] ports) throws IOException {
	    for (int port : ports) {
	        try {
	            ServerSocket sC = new ServerSocket(4747);
	            PORT = port;
	            return sC;
	        } catch (IOException ex) {
	            continue; // try next port
	        }
	    }
	    // if the program gets here, no port in the range was found
	    throw new IOException("no free port found");
	}

	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
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
	
}
