package pt.utl.ist.cmov.wifidirect;

public class SimWifiP2pBroadcast {
    /**
     * Broadcast intent action to indicate whether Wi-Fi p2p is enabled or disabled. An
     * extra {@link #EXTRA_WIFI_STATE} provides the state information as int.
     *
     * @see #EXTRA_WIFI_STATE
     */
    public static final String WIFI_P2P_STATE_CHANGED_ACTION =
        "pt.utl.ist.cmov.wifidirect.STATE_CHANGED";

    /**
     * The lookup key for an int that indicates whether Wi-Fi p2p is enabled or disabled.
     * Retrieve it with {@link android.content.Intent#getIntExtra(String,int)}.
     *
     * @see #WIFI_P2P_STATE_DISABLED
     * @see #WIFI_P2P_STATE_ENABLED
     */
    public static final String EXTRA_WIFI_STATE = "wifi_p2p_state";

    /**
     * Wi-Fi p2p is disabled.
     *
     * @see #WIFI_P2P_STATE_CHANGED_ACTION
     */
    public static final int WIFI_P2P_STATE_DISABLED = 1;

    /**
     * Wi-Fi p2p is enabled.
     *
     * @see #WIFI_P2P_STATE_CHANGED_ACTION
     */
    public static final int WIFI_P2P_STATE_ENABLED = 2;
    
    /**
     * Broadcast intent action indicating that the available peer list has changed. Fetch
     * the changed list of peers with {@link #requestPeers}
     */
    public static final String WIFI_P2P_PEERS_CHANGED_ACTION =
        "pt.utl.ist.cmov.wifidirect.PEERS_CHANGED";

    public static final String EXTRA_DEVICE_LIST = "deviceList";


    public static final String WIFI_P2P_DEVICE_INFO_CHANGED_ACTION =
            "pt.utl.ist.cmov.wifidirect.DEVICE_INFO_CHANGED";

    public static final String EXTRA_DEVICE_INFO = "deviceInfo";


    public static final String WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION =
            "pt.utl.ist.cmov.wifidirect.NETWORK_MEMBERSHIP_CHANGED";

    public static final String WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION =
            "pt.utl.ist.cmov.wifidirect.GROUP_OWNERSHIP_CHANGED";
    
    public static final String EXTRA_GROUP_INFO = "groupInfo";

    /**
     * Broadcast intent action indicating that peer discovery has either started or stopped.
     * One extra {@link #EXTRA_DISCOVERY_STATE} indicates whether discovery has started
     * or stopped.
     *
     * Note that discovery will be stopped during a connection setup. If the application tries
     * to re-initiate discovery during this time, it can fail.
     */
    public static final String WIFI_P2P_DISCOVERY_CHANGED_ACTION =
        "pt.utl.ist.cmov.wifidirect.DISCOVERY_STATE_CHANGE";

    /**
     * The lookup key for an int that indicates whether p2p discovery has started or stopped.
     * Retrieve it with {@link android.content.Intent#getIntExtra(String,int)}.
     *
     * @see #WIFI_P2P_DISCOVERY_STARTED
     * @see #WIFI_P2P_DISCOVERY_STOPPED
     */
    public static final String EXTRA_DISCOVERY_STATE = "discoveryState";
    
    /**
     * p2p discovery has stopped
     *
     * @see #WIFI_P2P_DISCOVERY_CHANGED_ACTION
     */
    public static final int WIFI_P2P_DISCOVERY_STOPPED = 1;

    /**
     * p2p discovery has started
     *
     * @see #WIFI_P2P_DISCOVERY_CHANGED_ACTION
     */
    public static final int WIFI_P2P_DISCOVERY_STARTED = 2;

}
