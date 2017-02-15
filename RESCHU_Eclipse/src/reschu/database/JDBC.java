package reschu.database;

import java.sql.*;
import java.util.Arrays;

public class JDBC {
	final private String DRIVER_NAME = "org.gjt.mm.mysql.Driver";
	final private String SERVER_NAME = "cummingslab3.mit.edu";
	final private String DATABASE = "RESCHU Security-Aware";
	final private String DB_USERNAME = "";
	final private String DB_PASSWORD = "";
		
	Connection connection = null;
		
	public JDBC() throws Exception { 
		// Load the JDBC driver
		String driverName = DRIVER_NAME; // MySQL MM JDBC driver
		Class.forName(driverName);
	
		// Create a connection to the database
		String serverName = SERVER_NAME;
		String mydatabase = DATABASE;
		String username = DB_USERNAME;
		String password = DB_PASSWORD;
		String url = "jdbc:mysql://" + serverName + "/" + mydatabase;  
			
		connection = DriverManager.getConnection(url, username, password); 
	}
	
	public boolean isConnected() { 
		try {
			return !connection.isClosed(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public ResultSet getResultSet(String str) {
		ResultSet rs = null;
		
		try {
			Statement stmt;			
			stmt = connection.createStatement();
			rs = stmt.executeQuery(str);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}		
		return rs;
	}
	
	public int user_login(String username, char[] password) {
		ResultSet rs; 
		int scenario = -1;
		
		rs = getResultSet("SELECT * FROM USER WHERE username='"+username+"'");
		
		try {
			if( rs.next() && isPasswordCorrect(rs.getString(3), password)) {
				scenario = rs.getInt("scenario");
			}
			rs.close();
		} catch(SQLException e) {} 
		
		return scenario;
	}
	
	public void disconnectConnection() {
		try {
			if( !connection.isClosed() ) 
				connection.close();
		} catch (SQLException e) {
			System.err.println("Can not disconnect the init connection.");
			e.printStackTrace();
		}
	}
	
	private boolean isPasswordCorrect(String correctPassword, char[] userInput) {
		if( Arrays.equals(correctPassword.toCharArray(), userInput) ) 
			return true;	 
		return false;		
	}
	
}
