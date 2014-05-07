package pt.utl.ist.cmov.wifidirect.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class Groups {

	private TreeMap<String,Group> mGroups;
	
	public Groups() {
		mGroups = new TreeMap<String,Group>();
	}
	
	public Collection<Group> getGroups() {
		return mGroups.values();
	}
	
	public void addGroup(Group group) {
		mGroups.put(group.getGo().getName(), group);
	}

	public void deleteDevice(String name) {
		mGroups.remove(name);
	}
	
	public boolean existsGroup(String go) {
		return mGroups.containsKey(go);
	}

	public Group getGroup(String name) {
		return mGroups.get(name);
	}

	public void removeGroup(String name) {
		for (Group group : mGroups.values()) {
			group.removeClient(name);
		}
		mGroups.remove(name);
	}

	public void clear() {
		mGroups.clear();
	}
	
	public ArrayList<Group> getGroupsContaining(String client) {
		ArrayList<Group> groupSet = new ArrayList<Group> ();
		for (Group group : mGroups.values()) {
			if (group.hasClient(client)) {
				groupSet.add(group);
			}
		}
		return groupSet;
	}
	
	public String getStrGroupsContaining(String client) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		ArrayList<Group> groups = getGroupsContaining(client);
		for (Group group : groups) {
			str.append(group.getGo().getName());
			if (++i != groups.size()) {
				str.append(",");
			}
		}
		return str.toString();
	}

	public String marshalGroups() {
		StringBuilder str = new StringBuilder();
		for (Group group : mGroups.values()) {
			str.append(group.getGo().getName());
			str.append(",");
			str.append(group.getClientsStr());
			str.append("%");
		}
		return str.toString();
	}
	
	public String marshalDeviceGroups(Device device) {
		StringBuilder sb = new StringBuilder(128);
		
		// Get the list of groups that the current node is client of
		String clientOf = getStrGroupsContaining(device.getName());
		String clientOfStat =
				((clientOf != null && !clientOf.equals(""))?"Yes":"No");

		// Print details of the group that the current node is owner of
		Group group = getGroup(device.getName());
		String go = ((group != null)?group.getClientsStr():"");
		String goStat = ((group != null)?"Yes":"No");
		
		// Add the constitution of all groups
		String groups = marshalGroups();
		
		// Build envelope
		sb.append(device.getName());
		sb.append("@");
		sb.append(clientOfStat);
		sb.append("@");
		sb.append(clientOf);
		sb.append("@");
		sb.append(goStat);
		sb.append("@");
		sb.append(go);
		sb.append("@");
		sb.append(groups);
		sb.append("@");
		return sb.toString();
	}
}
