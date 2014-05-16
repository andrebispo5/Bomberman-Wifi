package ist.utl.pt.bbcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDevice;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDeviceList;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.Channel;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.utl.ist.cmov.wifidirect.service.SimWifiP2pService;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketServer;
import ist.utl.pt.bbcm.enums.DIRECTION;
import ist.utl.pt.bbcm.enums.MODE;
import ist.utl.pt.bbcm.enums.SETTINGS;
import ist.utl.pt.bbcm.networking.ClientConnectorTask;
import ist.utl.pt.bbcm.networking.KnownPorts;
import ist.utl.pt.bbcm.sprites.Player;
import ist.utl.pt.bbcm.wdsim.SimWifiP2pBroadcastReceiver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  {
	
	public TextView tvScore;
	public TextView gameTime;
	public TextView tvNumPlayers;
	public int ElapsedTime;
	private GameView newGame;
	private CountDownTimer timer;
	private ApplicationContext appCtx;	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_screen);
		appCtx = (ApplicationContext) getApplicationContext();
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
		    	 SETTINGS.numPlayers = newGame.getMap().getNumPlayersAlive();
		    	 tvNumPlayers.setText("Players:\n"+SETTINGS.numPlayers);
		    	 ElapsedTime+=1000;
		     }
		     public void onFinish() {
		    	 setTime(0);
	    		 if(SETTINGS.mode == MODE.SGP)
	    			 endGame();
	    		 else if(SETTINGS.mode == MODE.MLP){
	    			 new ClientConnectorTask((ApplicationContext)getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"timeOut:" + newGame.getMap().myPlayer.id+","+ newGame.getMap().myPlayer.getScore() ,"died");
	    		 }else if(SETTINGS.mode == MODE.WDS && appCtx.isGO){
	    			 Player winner = newGame.getMap().getWinner();
	    			 endGame(new String[]{winner.id,String.valueOf(winner.getScore())});
	    			 new ClientConnectorTask((ApplicationContext)getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"timeOut:" + winner.id+","+ winner.getScore() ,"died");
	    		 }
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
				    alert.setCancelable(false);
				    alert.setMessage(args[0]+" wins with " + args[1] + " points.");
				    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
					        if(SETTINGS.mode == MODE.WDS)
					        		unbindService(appCtx.mConnection);
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
	public void onStop() {
		super.onStop();
		if(SETTINGS.mode == MODE.SGP)
			timer.cancel();
	}
	@Override
	public void onResume() {
		super.onResume();
		if(SETTINGS.mode == MODE.SGP)
			initTimer();
	}
	@Override
	public void onPause() {
		super.onPause();
		if(SETTINGS.mode == MODE.SGP)
			timer.cancel();
	}
	
	
}