package pt.utl.ist.cmov.wifidirect.console;

import java.util.ArrayList;

public class Group {

	private Device mGo;
	private ArrayList<Device> mClients;

	public Group(Device go) {
		mGo = go;
		mClients = new ArrayList<Device>();
	}

	public Device getGo() {
		return mGo;
	}
	
	public void setGo(Device go) {
		mGo = go;
	}
	
	public ArrayList<Device> getClientList() {
		return mClients;
	}
	
	public void print() {
		System.out.println("Group = [Go:" + mGo.getName() + ", Clients:" + getClientsStr());
	}
	
	public String getClientsStr() {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (Device client : mClients) {
			str.append(client.getName());
			if (++i != mClients.size()) {
				str.append(",");
			}
		}
		return str.toString();
	}

	public boolean hasClient(String c) {
		for (Device client : mClients) {
			if (client.getName().equals(c)) {
				return true;
			}
		}
		return false;
	}

	public Device getClient(String c) {
		for (Device client : mClients) {
			if (client.getName().equals(c)) {
				return client;
			}
		}
		return null;
	}

	public void removeClient(String c) {
		Device client = getClient(c);
		if (client != null) {
			mClients.remove(client);
		}
	}
}
