package ist.utl.pt.bbcm;




import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public TextView tvScore;
	public TextView gameTime;
	public TextView tvNumPlayers;
	public int ElapsedTime;
	private GameView newGame;
	private CountDownTimer timer;

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
		newGame = new GameView(this);
		gameLayout.addView(newGame);
		initButtons();
		ElapsedTime=0;
		initTimer();
	}

	private void initTimer() {
		gameTime = (TextView)findViewById(R.id.gameTime);
		timer = new CountDownTimer(SETTINGS.gameDuration-ElapsedTime, 1000) {
		     public void onTick(long millisUntilFinished) {
		    	 setTime(SETTINGS.gameDuration - ElapsedTime);
		    	 ElapsedTime+=1000;
		     }
		     public void onFinish() {
		    	 setTime(0);
	    		 if(SETTINGS.singlePlayer)
	    			 endGame();
	    		 else
	    			 new ClientConnectorTask().execute("timeOut:" + newGame.getMap().myPlayer.id+","+ newGame.getMap().myPlayer.getScore() ,"died");	 
		     }		
		  }.start();
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
		Button quitBtn = (Button) findViewById(R.id.quitBtn);
		quitBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(20);
				newGame.quitGame();
			}
		});
		
		Button pauseBtn = (Button) findViewById(R.id.pauseBtn);
		pauseBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myVib.vibrate(20);
				newGame.pauseGame();
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
	
	public void makeBomb(final String[] args){
		runOnUiThread(new Runnable() {
		  public void run() {
			  newGame.getMap().placeBombMultiplayer(args);
		  }
		});
	}
	
	public void endGame(){
		finish();
	}

    

	public void endGame(final String[] args) {
		final Context ctx = this;
		timer.cancel();
		runOnUiThread(new Runnable() {
			  public void run() {
				  AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
				    alert.setTitle("Game Over");
				    alert.setMessage(args[0]+" wins with " + args[1] + " points.");

				    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	SETTINGS.myPlayer = "p1";
							SETTINGS.numPlayers = 1;
				        	finish();
				        }
				    });
				    alert.show();
			}});
	}
	
	@Override
	public void onBackPressed() {
		newGame.pauseGame();
		Intent intent = new Intent(this, ist.utl.pt.bbcm.Menu.class);
	    startActivity(intent);
	    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
	}
    
	@Override
	public void onPause() {
		super.onPause();
		Log.e("BACKGROUND", "fiz pause!");
	}
	@Override
	public void onStart() {
		super.onStart();
		Log.e("BACKGROUND", "Fiz start!!");
	}
	@Override
	public void onResume() {
		super.onResume();
		if(SETTINGS.singlePlayer)
			initTimer();
		Log.e("BACKGROUND", "Fiz resume!!");
	}
	@Override
	public void onRestart() {
		super.onRestart();
		Log.e("BACKGROUND", "Fiz restart!!");
	}
	@Override
	public void onStop() {
		super.onStop();
		if(SETTINGS.singlePlayer)
			timer.cancel();

		Log.e("BACKGROUND", "Fiz stop!!");
	}
}
