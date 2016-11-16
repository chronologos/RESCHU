package reschu.database;

public class ConnectivityCheck {
	private static boolean connectivity = false;
	
	public ConnectivityCheck() {
		try {
			JDBC jdbc = new JDBC();
			setConnectable(jdbc.isConnected());
			jdbc.disconnectConnection();
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The static variable 'connectivity' should be synchronized. 
	 */
	private synchronized void setConnectable(boolean b) {connectivity = b;}	
	public synchronized boolean isConnectable() {return connectivity;}
	
	public static void main(String[] arg) {
		//Sample usage
		ConnectivityCheck c = new ConnectivityCheck();
		System.out.println(c.isConnectable());		
	}
}
