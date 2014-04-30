package ist.utl.pt.bbcm;


import java.io.IOException;

import ist.utl.pt.bbcm.enums.SETTINGS;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

public class Menu extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
	}
	
	/** Called when the user clicks the Send button */
	public void startNewGame(View view) {
		final Context context = this;
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
 
			// set title
			alertDialogBuilder.setTitle("Pick Game Type:");
 
			// set dialog message
			alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Single",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						SETTINGS.singlePlayer=true;
						Intent intent = new Intent(context, ist.utl.pt.bbcm.MainActivity.class);
					    startActivity(intent);
					    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
					}
				  })
				.setNegativeButton("Multi",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						SETTINGS.singlePlayer=false;
						Intent intent = new Intent(context, ist.utl.pt.bbcm.Lobby.class);
					    startActivity(intent);
					    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
			
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
	
}
