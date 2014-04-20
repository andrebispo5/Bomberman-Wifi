package ist.utl.pt.bbcm;


import ist.utl.pt.bbcm.enums.DIRECTION;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public TextView tvScore;
	public Chronometer gameTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen);
		tvScore = (TextView) findViewById(R.id.scoreTxt);
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.gameFrame);
		GameView newGame = new GameView(this);
		gameLayout.addView(newGame);
		initButtons();
		gameTime = (Chronometer)findViewById(R.id.gameTime);
		gameTime.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initButtons(){
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.gameFrame);
		final GameView newGame = (GameView) gameLayout.getChildAt(0);
		final Vibrator myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		Button upBtn = (Button) findViewById(R.id.upBtn);
		upBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(50);
				newGame.movePlayer(DIRECTION.UP);
			}
		});
		Button downBtn = (Button) findViewById(R.id.downBtn);
		downBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(50);
				newGame.movePlayer(DIRECTION.DOWN);
			}
		});
		Button leftBtn = (Button) findViewById(R.id.leftBtn);
		leftBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(50);
				newGame.movePlayer(DIRECTION.LEFT);
			}
		});
		Button rightBtn = (Button) findViewById(R.id.rightBtn);
		rightBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(50);
				newGame.movePlayer(DIRECTION.RIGHT);
			}
		});
		
		Button bombBtn = (Button) findViewById(R.id.bombBtn);
		bombBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(50);
				newGame.placeBomb();
			}
		});
	}
	
	public void setVal(final int val){
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 tvScore.setText("Score:" + val);
		    }
		});
		
	 }
}
