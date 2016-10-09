package reschu.game.model;

import reschu.constants.MyURL;

public class Payload {
	private int idx;
	private String statement;
	private String filename;
	private String vehicleType;
	private String targetType;
	private int[] location;	
	private boolean done;
	
	public Payload(int i, int[] loc, String vType, String tType, String stmt) {
		idx = i;
		filename = idx + "_" + vType + "_" + tType + ".jpg";
		vehicleType = vType;
		targetType = tType;
		location = loc;
		statement = stmt;
		done = false;
	}	
	public String getStatement() {return statement;}
	public String getFilename() {return MyURL.URL_PAYLOAD + filename;}
	public String getVehicleType() {return vehicleType;}
	public String getTargetType() {return targetType;}
	public int[] getLocation() {return location;}
	public boolean isDone() {return done;}
	public void setDone(boolean b) {done = b;}
}
