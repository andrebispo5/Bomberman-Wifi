package ist.utl.pt.bbcm;



import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.SETTINGS;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public TextView tvScore;
	public TextView gameTime;
	public TextView tvNumPlayers;
	public int ElapsedTime;
	private static boolean active;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen);
		tvScore = (TextView) findViewById(R.id.scoreTxt);
		tvNumPlayers = (TextView) findViewById(R.id.playersTxt);
		tvNumPlayers.setText("Players:\n"+SETTINGS.numPlayers);
		LinearLayout gameLayout = (LinearLayout)findViewById(R.id.gameFrame);
		TextView nameTV = (TextView)findViewById(R.id.nameTxt);
		nameTV.setText("Name:\n"+SETTINGS.playerName);
		GameView newGame = new GameView(this);
		gameLayout.addView(newGame);
		initButtons();
		initTimer();
	}

	private void initTimer() {
		ElapsedTime=0;
		gameTime = (TextView)findViewById(R.id.gameTime);
		new CountDownTimer(SETTINGS.gameDuration, 1000) {
		     public void onTick(long millisUntilFinished) {
		    	 setTime(SETTINGS.gameDuration - ElapsedTime);
		    	 ElapsedTime+=1000;
		     }
		     public void onFinish() {if(active)endGame();}
		  }.start();
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
				myVib.vibrate(20);
				newGame.movePlayer(DIRECTION.UP);
			}
		});
		Button downBtn = (Button) findViewById(R.id.downBtn);
		downBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(20);
				newGame.movePlayer(DIRECTION.DOWN);
			}
		});
		Button leftBtn = (Button) findViewById(R.id.leftBtn);
		leftBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(20);
				newGame.movePlayer(DIRECTION.LEFT);
			}
		});
		Button rightBtn = (Button) findViewById(R.id.rightBtn);
		rightBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(20);
				newGame.movePlayer(DIRECTION.RIGHT);
			}
		});
		
		Button bombBtn = (Button) findViewById(R.id.bombBtn);
		bombBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(20);
				newGame.placeBomb();
			}
		});
	}
	
	public void setScore(final int val){
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 tvScore.setText("Score:\n" + val);
		    }
		});
		
	 }
	
	public void setNumPlayers(final int val){
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 tvNumPlayers.setText("Players:\n " + val);
		    }
		});
		
	 }
	
	public void setTime(final int val){
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 gameTime.setText("Time:\n " + val/1000);
		    }
		});
		
	 }
	public void endGame(){
		finish();
	}
	
    @Override
    public void onStart() {
       super.onStart();
       active = true;
    } 

    @Override
    public void onStop() {
       super.onStop();
       active = false;
    }
    
    @Override
    public void onPause() {
       super.onStop();
       active = false;
    }
    
}
