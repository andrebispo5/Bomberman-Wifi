package pt.utl.ist.cmov.wifidirect;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import pt.utl.ist.cmov.wifidirect.util.AsyncChannel;
import pt.utl.ist.cmov.wifidirect.util.Protocol;

import java.util.HashMap;


public class SimWifiP2pManager {

    private static final String TAG = "SimWifiP2pManager";

    private Messenger mService;

    private static final int BASE = Protocol.BASE_SIM_WIFI_P2P_MANAGER;

    /** @hide */
    public static final int DISCOVER_PEERS                          = BASE + 1;
    /** @hide */
    public static final int DISCOVER_PEERS_FAILED                   = BASE + 2;
    /** @hide */
    public static final int DISCOVER_PEERS_SUCCEEDED                = BASE + 3;

    /** @hide */
    public static final int REQUEST_PEERS                           = BASE + 19;
    /** @hide */
    public static final int RESPONSE_PEERS                          = BASE + 20;

    /** @hide */
    public static final int REQUEST_GROUP_INFO                      = BASE + 21;
    /** @hide */
    public static final int RESPONSE_GROUP_INFO                     = BASE + 22;


    /**
     * Create a new SimWifiP2pManager instance. Applications use
     * {@link android.content.Context#getSystemService Context.getSystemService()} to retrieve
     * the standard {@link android.content.Context#WIFI_P2P_SERVICE Context.WIFI_P2P_SERVICE}.
     * @param service the Binder interface
     */
    public SimWifiP2pManager(Messenger service) {
        mService = service;
    }

    /**
     * Passed with {@link ActionListener#onFailure}.
     * Indicates that the operation failed due to an internal error.
     */
    public static final int ERROR               = 0;

    /**
     * Passed with {@link ActionListener#onFailure}.
     * Indicates that the operation failed because p2p is unsupported on the device.
     */
    public static final int P2P_UNSUPPORTED     = 1;

    /**
     * Passed with {@link ActionListener#onFailure}.
     * Indicates that the operation failed because the framework is busy and
     * unable to service the request
     */
    public static final int BUSY                = 2;


    /** Interface for callback invocation when framework channel is lost */
    public interface ChannelListener {
        /**
         * The channel to the framework has been disconnected.
         * Application could try re-initializing using {@link #initialize}
         */
        public void onChannelDisconnected();
    }

    /** Interface for callback invocation on an application action */
    public interface ActionListener {
        /** The operation succeeded */
        public void onSuccess();
        /**
         * The operation failed
         * @param reason The reason for failure could be one of {@link #P2P_UNSUPPORTED},
         * {@link #ERROR} or {@link #BUSY}
         */
        public void onFailure(int reason);
    }

    /** Interface for callback invocation when peer list is available */
    public interface PeerListListener {
        /**
         * The requested peer list is available
         * @param peers List of available peers
         */
        public void onPeersAvailable(SimWifiP2pDeviceList peers);
    }

    /** Interface for callback invocation when group information is available */
    public interface GroupInfoListener {
        /**
         * The requested group information is available
         * @param devices List of all devices in the system
         */
    	public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
    			SimWifiP2pInfo groupInfo);
    }
    
    /**
     * A channel that connects the application to the Wifi p2p framework.
     * Most p2p operations require a Channel as an argument. An instance of Channel is obtained
     * by doing a call on {@link #initialize}
     */
    public static class Channel {
        Channel(Context context, Looper looper, ChannelListener l) {
            mAsyncChannel = new AsyncChannel();
            mHandler = new P2pHandler(looper);
            mChannelListener = l;
            mContext = context;
        }
        private final static int INVALID_LISTENER_KEY = 0;
        private ChannelListener mChannelListener;
        private HashMap<Integer, Object> mListenerMap = new HashMap<Integer, Object>();
        private Object mListenerMapLock = new Object();
        private int mListenerKey = 0;

        private AsyncChannel mAsyncChannel;
        private P2pHandler mHandler;
        Context mContext;
        class P2pHandler extends Handler {
            P2pHandler(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message message) {
                Object listener = getListener(message.arg2);
                switch (message.what) {
                    case AsyncChannel.CMD_CHANNEL_DISCONNECTED:
                        if (mChannelListener != null) {
                            mChannelListener.onChannelDisconnected();
                            mChannelListener = null;
                        }
                        break;
                    case SimWifiP2pManager.DISCOVER_PEERS_FAILED:
                        if (listener != null) {
                            ((ActionListener) listener).onFailure(message.arg1);
                        }
                        break;
                    case SimWifiP2pManager.DISCOVER_PEERS_SUCCEEDED:
                        if (listener != null) {
                            ((ActionListener) listener).onSuccess();
                        }
                        break;
                    case SimWifiP2pManager.RESPONSE_PEERS:
                        SimWifiP2pDeviceList peers = (SimWifiP2pDeviceList) message.obj;
                        if (listener != null) {
                            ((PeerListListener) listener).onPeersAvailable(peers);
                        }
                        break;
                    case SimWifiP2pManager.RESPONSE_GROUP_INFO:
                    	Object [] content = (Object []) message.obj;
                        SimWifiP2pDeviceList devices = (SimWifiP2pDeviceList) content[0];
                    	SimWifiP2pInfo groupInfo = (SimWifiP2pInfo) content[1];
                        if (listener != null) {
                            ((GroupInfoListener) listener).onGroupInfoAvailable(
                            		devices, groupInfo);
                        }
                        break;
                   default:
                        Log.d(TAG, "Ignored " + message);
                        break;
                }
            }
        }

        private int putListener(Object listener) {
            if (listener == null) return INVALID_LISTENER_KEY;
            int key;
            synchronized (mListenerMapLock) {
                do {
                    key = mListenerKey++;
                } while (key == INVALID_LISTENER_KEY);
                mListenerMap.put(key, listener);
            }
            return key;
        }

        private Object getListener(int key) {
            if (key == INVALID_LISTENER_KEY) return null;
            synchronized (mListenerMapLock) {
                return mListenerMap.remove(key);
            }
        }
    }

    private static void checkChannel(Channel c) {
        if (c == null) throw 
        	new IllegalArgumentException("Channel needs to be initialized");
    }

    /**
     * Get a reference to WifiP2pService handler. This is used to establish
     * an AsyncChannel communication with WifiService
     *
     * @return Messenger pointing to the WifiP2pService handler
     * @hide
     */
    public Messenger getMessenger() {
    	return mService;
    }

    /**
     * Registers the application with the Wi-Fi framework. This function
     * must be the first to be called before any p2p operations are performed.
     *
     * @param srcContext is the context of the source
     * @param srcLooper is the Looper on which the callbacks are receivied
     * @param listener for callback at loss of framework communication. Can be null.
     * @return Channel instance that is necessary for performing any further p2p operations
     */
    public Channel initialize(Context srcContext, Looper srcLooper, ChannelListener listener) {
        Messenger messenger = getMessenger();
        if (messenger == null) return null;

        Channel c = new Channel(srcContext, srcLooper, listener);
        if (c.mAsyncChannel.connectSync(srcContext, c.mHandler, messenger)
                == AsyncChannel.STATUS_SUCCESSFUL) {
            return c;
        } else {
            return null;
        }
    }

    /**
     * Initiate peer discovery. A discovery process involves scanning for available Wi-Fi peers
     * for the purpose of establishing a connection.
     *
     * <p> The function call immediately returns after sending a discovery request
     * to the framework. The application is notified of a success or failure to initiate
     * discovery through listener callbacks {@link ActionListener#onSuccess} or
     * {@link ActionListener#onFailure}.
     *
     * <p> The discovery remains active until a connection is initiated or
     * a p2p group is formed. Register for {@link #WIFI_P2P_PEERS_CHANGED_ACTION} intent to
     * determine when the framework notifies of a change as peers are discovered.
     *
     * <p> Upon receiving a {@link #WIFI_P2P_PEERS_CHANGED_ACTION} intent, an application
     * can request for the list of peers using {@link #requestPeers}.
     *
     * @param c is the channel created at {@link #initialize}
     * @param listener for callbacks on success or failure. Can be null.
     */
    public void discoverPeers(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(DISCOVER_PEERS, 0, c.putListener(listener));
    }

    /**
     * Request the current list of peers.
     *
     * @param c is the channel created at {@link #initialize}
     * @param listener for callback when peer list is available. Can be null.
     */
    public void requestPeers(Channel c, PeerListListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(REQUEST_PEERS, 0, c.putListener(listener));
    }

    /**
     * Request the current group information.
     *
     * @param c is the channel created at {@link #initialize}
     * @param listener for callback when peer list is available. Can be null.
     */
    public void requestGroupInfo(Channel c, GroupInfoListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(REQUEST_GROUP_INFO, 0, c.putListener(listener));
    }
}
