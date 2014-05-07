package pt.utl.ist.cmov.wifidirect.console.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.utl.ist.cmov.wifidirect.console.Command;
import pt.utl.ist.cmov.wifidirect.console.Context;
import pt.utl.ist.cmov.wifidirect.console.Devices;
import pt.utl.ist.cmov.wifidirect.console.Network;

public class RegisterCommand extends Command {

	public RegisterCommand(String name, String abrv) {
		super(name,abrv);
	}

	public RegisterCommand() {
		super("register","r");
	}

	public boolean executeCommand(Network network, Context context, 
			String [] args) {

		assert network != null && context != null && args != null;

		Devices devices = network.getDevices();

		// parse input arguments
		if (args.length != 5) {
			printError("Wrong number of input arguments.");
			return false;
		}
		
		// check the device name
		String deviceName = args[1];
		if (!devices.checkDevice(deviceName)) {
			printError("Device already registered with name \"" + deviceName + "\"");
			return false;
		}

		// parse the sequence of addresses
		DeviceAddr appVirtAddr = new DeviceAddr();
		if (!parseDeviceAddr(devices, args[2], appVirtAddr)) {
			printError("Unable to parse \"" + args[2] + "\"");
			return false;
		}
		DeviceAddr appRealAddr = new DeviceAddr();
		if (!parseDeviceAddr(devices, args[3], appRealAddr)) {
			printError("Unable to parse \"" + args[3] + "\"");
			return false;
		}
		DeviceAddr ctlrRealAddr = new DeviceAddr();
		if (!parseDeviceAddr(devices, args[4], ctlrRealAddr)) {
			printError("Unable to parse \"" + args[4] + "\"");
			return false;
		}

		// register the device
		devices.addDevice(deviceName, ctlrRealAddr.mIP, ctlrRealAddr.mPort,
			appRealAddr.mIP, appRealAddr.mPort, appVirtAddr.mIP, appVirtAddr.mPort);
		return true;
	}

	class DeviceAddr {
		public String mIP;
		public int mPort;
	}
	
	private boolean parseDeviceAddr(Devices devices,
		String addrStrIn, DeviceAddr addrOut) {
		assert addrStrIn != null && addrOut != null;

		// split the address into two tokens IP and port
		String [] tokens = addrStrIn.split(":");
		if (tokens.length != 2) {
			printError("Malformed address format IP:port.");
			return false;
		}

		// validate the IP address
		String ip = tokens[0];
		IPAddressValidator ipChecker = new IPAddressValidator();
		if (!ipChecker.validate(ip)) {
			printError("Wrong IP address format.");
			return false;
		}

		// validate the port number
		int port = 0;
		try {
			port = Integer.parseInt(tokens[1]);
		} catch (NumberFormatException e) {
			printError("Wrong port number format.");
			return false;
		}

		// check that the address is not in use
		if (!devices.checkAddress(ip,port)) {
			printError("Device already registered with address \"" + ip + 
					":" + port + "\"");
			return false;
		}

		// everything is ok
		addrOut.mIP = ip;
		addrOut.mPort = port;
		return true;
	}
	
	public void printHelp() {
		System.out.println("Syntax: register <DeviceName> " + 
			"<AppVirtIP>:<AppVirtPort> <AppRealIP>:<AppRealPort> " +
			"<CtrlRealIP>:<CtlrRealPort>");
	}
	
	class IPAddressValidator {
		 
	    private Pattern pattern;
	    private Matcher matcher;
	 
	    private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	 
	    public IPAddressValidator() {
		  pattern = Pattern.compile(IPADDRESS_PATTERN);
	    }
	 
	   /**
	    * Validate ip address with regular expression
	    * @param ip ip address for validation
	    * @return true valid ip address, false invalid ip address
	    */
	    public boolean validate(final String ip) {		  
		  matcher = pattern.matcher(ip);
		  return matcher.matches();	    	    
	    }
	}
}
