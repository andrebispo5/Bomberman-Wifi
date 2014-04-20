package ist.utl.pt.bbcm;


import android.os.Bundle;
import android.app.Activity;
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
		Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
	}
	
	public void exitGame(View view){
	            finish();
	    	    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
	            System.exit(0);
	}
	
}
