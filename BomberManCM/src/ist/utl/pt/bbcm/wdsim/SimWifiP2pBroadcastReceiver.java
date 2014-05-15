package ist.utl.pt.bbcm.wdsim;

import ist.utl.pt.bbcm.ApplicationContext;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDevice;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {

	private ApplicationContext appCtx;

	public SimWifiP2pBroadcastReceiver(ApplicationContext context) {
		super();
		this.appCtx = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

			// This action is triggered when the WDSim service changes state:
			// - creating the service generates the WIFI_P2P_STATE_ENABLED event
			// - destroying the service generates the WIFI_P2P_STATE_DISABLED
			// event

			int state = intent.getIntExtra(
					SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
			if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
				Toast.makeText(appCtx, "WiFi Direct enabled",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(appCtx, "WiFi Direct disabled",
						Toast.LENGTH_SHORT).show();
			}

		} else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION
				.equals(action)) {

			// Request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()

			Toast.makeText(appCtx, "Peer list changed", Toast.LENGTH_SHORT)
					.show();

		} else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION
				.equals(action)) {

			SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent
					.getSerializableExtra(SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
			ginfo.print();
			Toast.makeText(appCtx, "Network membership changed",
					Toast.LENGTH_SHORT).show();

		} else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION
				.equals(action)) {
			SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent
					.getSerializableExtra(SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
			ginfo.print();
			appCtx.isGO = ginfo.askIsGO();
			Toast.makeText(appCtx,
					"Group ownership changed isGO=" + appCtx.isGO,
					Toast.LENGTH_SHORT).show();
		}
	}
}
