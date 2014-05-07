package pt.utl.ist.cmov.wifidirect.console;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;


public class Context {
	
	private Properties mCtxProps;
	private Command [] mCtxCommands;
	private History mCtxHistory;

	public Context(Command [] commands) {
		assert commands != null;
		mCtxProps = new Properties();
		mCtxHistory = new History();
		mCtxCommands = commands;
	}
	
	public Properties getProperties() {
		return mCtxProps;
	}
	
	public Command [] getCommands() {
		return mCtxCommands;
	}
	
	public History getHistory() {
		return mCtxHistory;
	}

	public class History {
	
		private String mLast;
		
		public History() {
			mLast = null;
		}
		
		public void setLast(String cmdLine) {
			mLast = cmdLine;
		}
		
		public String getLast() {
			return mLast;
		}
	}
	
	public class Properties {

		public static final String WAIT_ITERATIVE = "wait.iterative";
		public static final String COMMIT_SHOWMARSHAL = "commit.showmarshal";

		private Set<String> mProps;
		
		public Properties() {
			mProps = new TreeSet<String>();
		}
		
		public void addProperty(String prop) {
			mProps.add(prop);
		}
		
		public boolean hasProperty(String prop) {
			return mProps.contains(prop);
		}
		
		public void removeProperty(String prop) {
			mProps.remove(prop);
		}
		
		public Set<String> get() {
			return mProps;
		}
		
		public void print() {
			ArrayList<String> pList = new ArrayList<String>(mProps);
			java.util.Collections.sort(pList);
			System.out.println(pList.toString());
		}
	}
}
