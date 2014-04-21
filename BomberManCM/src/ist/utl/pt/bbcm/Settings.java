package ist.utl.pt.bbcm;


import ist.utl.pt.bbcm.enums.LEVELS;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Spinner;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class Settings extends Activity {
	public static LEVELS lvl;
	public static int gameDuration;
	public static int bombTimer;
	public static int explosionRange;
	public static int explosionDuration;
	public static int robotSpeed;
	public static int ptsPerRobot;
	public static int ptsPerPlayer;
	private static boolean created;
	private static Spinner gD;
	private static Spinner bT;
	private static Spinner eR;
	private static Spinner eT;
	private static Spinner rS;
	private static Spinner rP;
	private static Spinner pP;
	private static Spinner ll;

	private static int gDPos;
	private static int bTPos;
	private static int eRPos;
	private static int eTPos;
	private static int rSPos;
	private static int rPPos;
	private static int pPPos;
	private static int llPos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_screen);
		gD = (Spinner)findViewById(R.id.gameDurationOption);
		bT =(Spinner)findViewById(R.id.bombTimerOption);
		eR=(Spinner)findViewById(R.id.explosionRangeOption);
		eT=(Spinner)findViewById(R.id.explosionTimerOption);
		rS=(Spinner)findViewById(R.id.robotSpeedOption);
		rP=(Spinner)findViewById(R.id.robotPointsOption);
		pP=(Spinner)findViewById(R.id.playerPointsOption);
		ll=(Spinner)findViewById(R.id.levelOption);
		if(created){
			gD.setSelection(gDPos);
			bT.setSelection(bTPos);
			eR.setSelection(eRPos);
			eT.setSelection(eTPos);
			rS.setSelection(rSPos);
			rP.setSelection(rPPos);
			pP.setSelection(pPPos);
			ll.setSelection(llPos);
		}
	}
	
	public void saveSettings(View view){
		String gameDurationSpinner =(String) gD.getSelectedItem();
		String bombTimeSpinner =(String) bT.getSelectedItem();
		String explosionRangeSpinner =(String) eR.getSelectedItem();
		String explosionDurationSpinner =(String) eT.getSelectedItem();
		String robotSpeedSpinner =(String) rS.getSelectedItem();
		String ptsPerRobotSpinner =(String) rP.getSelectedItem();
		String ptsPerPlayerSpinner =(String) pP.getSelectedItem();
		String levelSpinner =(String) ll.getSelectedItem();

		gameDuration = Integer.parseInt(gameDurationSpinner)*1000;
		bombTimer = Integer.parseInt(bombTimeSpinner)*1000;
		explosionRange = Integer.parseInt(explosionRangeSpinner);
		explosionDuration = Integer.parseInt(explosionDurationSpinner)*1000;
		robotSpeed = Integer.parseInt(robotSpeedSpinner);
		ptsPerRobot = Integer.parseInt(ptsPerRobotSpinner);
		ptsPerPlayer = Integer.parseInt(ptsPerPlayerSpinner);
		
		gDPos = gD.getSelectedItemPosition();
		bTPos = bT.getSelectedItemPosition();;
		eRPos = eR.getSelectedItemPosition();;
		eTPos = eT.getSelectedItemPosition();;
		rSPos = rS.getSelectedItemPosition();;
		rPPos = rP.getSelectedItemPosition();;
		pPPos = pP.getSelectedItemPosition();;
		llPos = ll.getSelectedItemPosition();;
		
		if(levelSpinner.equals("One")){
			lvl = LEVELS.LVL1;
		}else {
			lvl = LEVELS.LVL2;
		}
		created=true;
		Intent intent = new Intent(this, ist.utl.pt.bbcm.Menu.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
	}
	
	public void cancelSettings(View view){
		Intent intent = new Intent(this, ist.utl.pt.bbcm.Menu.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
	}

	
}
