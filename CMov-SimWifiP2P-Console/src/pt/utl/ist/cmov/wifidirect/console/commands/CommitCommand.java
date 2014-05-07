package pt.utl.ist.cmov.wifidirect.console.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Context.Properties;
import pt.utl.ist.cmov.wifidirect.console.Device;
import pt.utl.ist.cmov.wifidirect.console.Devices;
import pt.utl.ist.cmov.wifidirect.console.Groups;
import pt.utl.ist.cmov.wifidirect.console.Network;


/**
 * Propagate the network state to the devices
 *
 */
public class CommitCommand extends Command {

	private static int PROBE_TIMEOUT = 1000;
	
	private Context mContext;
	
	public CommitCommand(String name, String abrv) {
		super(name,abrv);
	}

	public CommitCommand() {
		super("commit","c");
		
		mProperties = new String [] {
				Properties.COMMIT_SHOWMARSHAL,
			};
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		Devices devices = network.getDevices();
		mContext = context;

		if (args.length != 1) {
			printError("Wrong number of input arguments.");
			return false;
		}
		
		// nothing to do if true
		if (devices.numDevices() == 0) {
			return true;
		}

		// check if the devices are online
		Hashtable<Thread,CommitWorker> workers = new Hashtable<Thread,CommitWorker>();
		for(Device dev : devices.getDevices()) {
			CommitWorker cw = new CommitWorker(dev,network);
			Thread t = new Thread(cw);
	        t.start();
			workers.put(t, cw);
		}

		// wait for the termination of all threads
		for (Thread t : workers.keySet()) {
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}

		// display the results
		for(CommitWorker worker : workers.values()) {
			Device dev = worker.getDevice();
			boolean success = worker.getStatus();
			System.out.println(dev.getName() + "\t" + dev.getCtrlRealIp() + 
					"\t" + dev.getCtrlRealPort() + "\t" + 
					(success?"SUCCESS":"FAIL"));
		}
		return true;
	}

	class CommitWorker implements Runnable {

		private Device mDevice;
		private boolean mSuccess;
		private Devices mDevices;
		private Groups mGroups;
		private String mMarshalled;

		public CommitWorker(Device device, Network network) {
			mDevice = device;
			mSuccess = true;
			mDevices = network.getDevices();
			mGroups = network.getGroups();
			mMarshalled = null;
		}

		public Device getDevice() {
			return mDevice;
		}
		
		public boolean getStatus() {
			return mSuccess;
		}

		public String getMarshalled() {
			return mMarshalled;
		}

		@Override
		public void run() {
			Socket client;
			PrintWriter printwriter;
			mMarshalled = marshalDeviceInfo();

			if (mContext.getProperties().hasProperty(Properties.COMMIT_SHOWMARSHAL)) {
				System.out.println("MARSHALED:" + mMarshalled);
			}

			try {
				printMsg("Commiting to " + mDevice.getName() + "...");
				client = new Socket();
				client.connect(new InetSocketAddress(
						mDevice.getCtrlRealIp(), mDevice.getCtrlRealPort()), PROBE_TIMEOUT);
				printwriter = new PrintWriter(client.getOutputStream(), true);
                printwriter.write(mMarshalled);
                printwriter.write("\n");
                printwriter.flush();
                printwriter.close();
				client.close();
				mSuccess = true;
			} catch (UnknownHostException e) {
				mSuccess = false;
			} catch (IOException e) {
				mSuccess = false;
			}
		}
		
		public String marshalDeviceInfo() {
			StringBuilder sb = new StringBuilder();
			sb.append(mGroups.marshalDeviceGroups(mDevice));
			sb.append("+");
			sb.append(mDevices.marshalDeviceNeighbors(mDevice));
			sb.append("+");
			sb.append(mDevices.marshalDevices());
			sb.append("+");
			return sb.toString();
		}
	}
}
