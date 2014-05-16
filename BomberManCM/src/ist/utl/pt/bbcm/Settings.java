package ist.utl.pt.bbcm;


import ist.utl.pt.bbcm.enums.LEVELS;
import ist.utl.pt.bbcm.enums.SETTINGS;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.Activity;

public class Settings extends Activity {

	private static boolean created;
	private static Spinner gD;
	private static Spinner bT;
	private static Spinner eR;
	private static Spinner eT;
	private static Spinner rS;
	private static Spinner rP;
	private static Spinner pP;
	private static Spinner ll;
	private static EditText na;
	private static String name;
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
		getSpinners();
		if(created){
			loadPreviousSettings();
		}
	}

	private void loadPreviousSettings() {
		gD.setSelection(gDPos);
		bT.setSelection(bTPos);
		eR.setSelection(eRPos);
		eT.setSelection(eTPos);
		rS.setSelection(rSPos);
		rP.setSelection(rPPos);
		pP.setSelection(pPPos);
		ll.setSelection(llPos);
		na.setText(name);
	}

	private void getSpinners() {
		gD = (Spinner)findViewById(R.id.gameDurationOption);
		bT =(Spinner)findViewById(R.id.bombTimerOption);
		eR=(Spinner)findViewById(R.id.explosionRangeOption);
		eT=(Spinner)findViewById(R.id.explosionTimerOption);
		rS=(Spinner)findViewById(R.id.robotSpeedOption);
		rP=(Spinner)findViewById(R.id.robotPointsOption);
		pP=(Spinner)findViewById(R.id.playerPointsOption);
		ll=(Spinner)findViewById(R.id.levelOption);
		na=(EditText)findViewById(R.id.nameOption);
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
		String nameTyped =(String) na.getText().toString();

		SETTINGS.gameDuration = Integer.parseInt(gameDurationSpinner)*1000;
		SETTINGS.bombTimer = Integer.parseInt(bombTimeSpinner)*1000;
		SETTINGS.explosionRange = Integer.parseInt(explosionRangeSpinner);
		SETTINGS.explosionDuration = Integer.parseInt(explosionDurationSpinner)*1000;
		SETTINGS.robotSpeed = Integer.parseInt(robotSpeedSpinner);
		SETTINGS.ptsPerRobot = Integer.parseInt(ptsPerRobotSpinner);
		SETTINGS.ptsPerPlayer = Integer.parseInt(ptsPerPlayerSpinner);
		if(!nameTyped.equals(""))
			SETTINGS.playerName = nameTyped;
		
		gDPos = gD.getSelectedItemPosition();
		bTPos = bT.getSelectedItemPosition();
		eRPos = eR.getSelectedItemPosition();
		eTPos = eT.getSelectedItemPosition();
		rSPos = rS.getSelectedItemPosition();
		rPPos = rP.getSelectedItemPosition();
		pPPos = pP.getSelectedItemPosition();
		llPos = ll.getSelectedItemPosition();
		name = nameTyped;
		
		if(levelSpinner.equals("One")){
			SETTINGS.lvl = LEVELS.LVL1;
		}else if(levelSpinner.equals("Two")){
			SETTINGS.lvl = LEVELS.LVL2;
		}else if(levelSpinner.equals("Three")){
			SETTINGS.lvl = LEVELS.LVL3;
		}
		created=true;
		finish();
	}
	
	public void cancelSettings(View view){
		finish();
	}

	
}
