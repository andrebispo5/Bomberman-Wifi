package ist.utl.pt.bbcm;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

import ist.utl.pt.bbcm.enums.MODE;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Menu extends Activity {
	private ApplicationContext appCtx;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		appCtx = (ApplicationContext) getApplicationContext();
	}
	
	public void startNewGame(View view) {
		final Context context = this;
		try {
			((ApplicationContext)getApplicationContext()).storeGroupPlayersWDsim();
		} catch (Exception e) {
		}
    	showDialogNewGame(context);
	}

	
	public void exitGame(View view){
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}
	
	public void goToSettings(View view){
		Intent intent = new Intent(this, ist.utl.pt.bbcm.Settings.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
	}
	
	
	
	private void launchMultiplayerAlertDialog(
			final Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Multiplayer Type:");
		alertDialogBuilder
				.setPositiveButton("Centralized",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SETTINGS.mode = MODE.MLP;
								Intent intent = new Intent(context,
										ist.utl.pt.bbcm.Lobby.class);
								startActivity(intent);
								overridePendingTransition(R.anim.fade_out,
										R.anim.fade_in);
								
							}
						})
				.setNegativeButton("WDsim",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SETTINGS.mode = MODE.WDS;
								if(appCtx.isGO){
									Intent intent = new Intent(context,
											ist.utl.pt.bbcm.MainActivity.class);
									startActivity(intent);
									overridePendingTransition(R.anim.fade_out,
											R.anim.fade_in);
								}else{
									Intent intent = new Intent(context,
											ist.utl.pt.bbcm.LobbyWDsim.class);
									startActivity(intent);
									overridePendingTransition(R.anim.fade_out,
											R.anim.fade_in);
								}
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	private void showDialogNewGame(final Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Pick Game Type:");
		alertDialogBuilder
		.setPositiveButton("Single",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				SETTINGS.mode = MODE.SGP;
				Intent intent = new Intent(context,
						ist.utl.pt.bbcm.MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_out,
						R.anim.fade_in);
			}
		})
		.setNegativeButton("Multi",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				launchMultiplayerAlertDialog(context);
			}});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	
	
	
}
