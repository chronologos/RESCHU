package reschu.database;
 
import java.sql.*;

import reschu.game.controller.Reschu;

public class DBWriter {
	final public static int NUM_OF_CONNECTION_IN_POOL = 3;
	final private boolean PRINT_QUREY_RESULT = false;
	/**
	 * In order to be able to check the actual order of events, 
	 * this variable is incremented every time when there is a write request. 
	 */
	private int sortIdx = 0;
        
    private DBConnectionMgr conn = DBConnectionMgr.getInstance(); 
	
    public DBWriter() { 
    	try { 
    		conn.setEnableTrace(false);
    		conn.setInitOpenConnections(NUM_OF_CONNECTION_IN_POOL);
    	} catch (SQLException e) {
    		System.err.println("Error: Unable to set InitOpenConnections");
    		e.printStackTrace();
    	} 
    }
    
    private void ExecuteSQL(final String sql) {
    	if( !Reschu.train() && !Reschu.tutorial() ) {	// writes to database only if the game is in experiment mode
	     	new Thread(new Runnable() { 	
	    		public void run() {
	    	    	try { 
			    		Connection c = conn.getConnection();
			    		Statement stmt = c.createStatement();    		
						stmt.execute(sql);
			    		conn.freeConnection(c);
			    		if( PRINT_QUREY_RESULT ) System.err.println("DB: " + sql + " finished");		    		
			    	} catch( Exception e ) {
			    		System.err.println("Error: failed to execute sql. " + sql);
			    	}
			    	
	         }}).start();
    	}
    }

	public void CreateTable(String username) {
		// table name doesn't support the email format. So we regularize the username.
		String table_username = regularizeUsername(username);
    	final String sql = "CREATE TABLE " + table_username + " (" +
						"idx INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
						"sortIdx SMALLINT UNSIGNED NOT NULL, " +
						"gameTime TIME NOT NULL, " +
						"serverTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
						"invoker TINYINT NOT NULL COMMENT '0:Server, 1:User', " +
						"type TINYINT NOT NULL DEFAULT 0, " +
						"vehicle TINYINT, " +					
						"log varchar(128) NOT NULL DEFAULT 'none', " +
						"mouseCoordX SMALLINT, " +
						"mouseCoordY SMALLINT" +
						")";
    	ExecuteSQL(sql); 
	}
	
    public void Write(String tableName, int msecTime, int invoker, int type, int vIdx, String log, int mouseCoordX, int mouseCoordY) {
    	// Increment the sortIdx by one.
    	incrOrder();    	
    	// table name doesn't support the email format. So we regularize the username.
    	final String table = regularizeUsername(tableName);
    	final String sql = 
    		"INSERT INTO " + table + "(sortIdx, gameTime, invoker, type, vehicle, log, mouseCoordX, mouseCoordY) VALUES (" +
    		getOrder() + ", " +
    		"'" + setTimeFormat(msecTime) + "', " +
    		invoker + ", " +
    		type + ", " +
    		vIdx + ", " +
    		"'" + log + "', " +
    		mouseCoordX + ", " +
    		mouseCoordY + 
    		")";
    	ExecuteSQL(sql); 
    }
    /*
    public void UserTable_SetState(String username, int state) {    	
    	final String sql =  
    		"UPDATE USER SET state=" +state + " WHERE username='" + username + "'";
    	ExecuteSQL(sql);
    }
    */
    public void UserTable_SetGameStart(String username) {    	
    	final String sql =  
    		"UPDATE USER SET GameStart=1 WHERE username='" + username + "'";
    	ExecuteSQL(sql);
    }
    
    public void UserTable_SetGameFinish(String username) {    	
    	final String sql =  
    		"UPDATE USER SET GameFinish=1 WHERE username='" + username + "'";
    	ExecuteSQL(sql);
    }
 
    public void UserTable_SetTutorialFinish(String username) {    	
    	final String sql =  
    		"UPDATE USER SET TutorialFinish=1 WHERE username='" + username + "'";
//    	final String name = username;
    	
    	// we don't write to the database in tutorial() or train() mode
    	// so, we force the program to write to the database here.    	
    	new Thread( new Runnable() {
    		public void run() {
    			// TODO: We need to come up with some nicer idea here.
    			// Try five times, and reports failure.
    			for( int i=0; i<5; i++ ) {
	    	    	//Write to the USER table.
    				try {
    					// Update the TutorialFinished column
			    		Connection c1 = conn.getConnection();
			    		Statement stmt1 = c1.createStatement();
						stmt1.execute(sql);
			    		conn.freeConnection(c1);			    		
			    		if( PRINT_QUREY_RESULT ) System.err.println("DB: " + sql + " finished");			    		
			    	} catch( Exception e ) {			    		
			    		System.err.println("Error: failed to execute sql. " + sql);
			    	}		  
			    	try {
			    		Thread.sleep(5000); 
			    	} catch (InterruptedException e) {
			    		e.printStackTrace();
			    	}
    			}
         }}).start();
    }
        
//    private void Check_TutorialFinish(String username) {
//    	final String sql = 
//    		"SELECT TutorialFinish FROM USER WHERE username='" + username + "'";
//    	
//    	new Thread( new Runnable() {
//    		public void run() {
//    			try {						
//					// Check if the last query was succeeded
//					Connection c = conn.getConnection();
//		    		Statement stmt = c.createStatement();
//		    		stmt.execute(sql);
//					ResultSet rs = stmt.getResultSet();
//					System.out.println("RS = " + rs.getInt(0));
//		    		conn.freeConnection(c);
//		    		rs = null;
//		    		
//		    		if( PRINT_QUREY_RESULT ) System.err.println("DB: " + sql + " finished");
//			    		
//		    	} catch( Exception e ) {			    		
//		    		System.err.println("Error: failed to execute sql. " + sql);
//		    	}
//         }}).start();
//    }
    
    public void UserTable_SetTime(String username) {
    	final String sql = 
    		"UPDATE USER SET last_login=CURRENT_TIMESTAMP WHERE username ='" + username + "'";
    	ExecuteSQL(sql);
    } 
    
    public void ScoreTable_SetScore(String username, int score) {
    	final String sql = 
    		"INSERT INTO SCORE(username, score) VALUES ('" + username + "', " + score + ")";
    	ExecuteSQL(sql);
    }
    
	public String setTimeFormat(int time) {
		String time_min = "" + time/1000/60;
		String time_sec = "" + time/1000%60;
				
		if(time_min.length() == 1) time_min = "0" + time_min;
		if(time_sec.length() == 1) time_sec = "0" + time_sec;
		
		return time_min + ":" + time_sec;
	} 
    
	/**
	 * Change an irregular username format to durable format so that we can create/write to database
	 */
	private String regularizeUsername(String username) {
		String ret = username.replace('@', '_');
		ret = ret.replace('.', '_');
		ret = ret.replace('-', '_');
		return ret;
	}
	
	/**
	 * Increment the ordering index by one.
	 */
	private synchronized void incrOrder() {sortIdx++;}
	/**
	 * Get the order index number.
	 */
	private synchronized int getOrder() {return sortIdx;}
		
	public void finalizeDB() {
		conn.finalize();		
	}
}