package reschu.game.model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
	public String getFilename() throws IOException {
		
	//	BufferedReader br = new BufferedReader(new FileReader("PayloadFileNames.txt"));  
		String name = "Pictures/Payloads/" + filename;
		/*
		String line = null;  
		String index = Integer.toString(idx);
		while ((line = br.readLine()) != null)  
		{  
			
			for(int i = 0; i<line.length(); i++){
				if(i<line.length()-3 && i >1){
				if(index.length() == 1){
								 
					if(line.substring(i,i+1).equals(index) && line.substring(i-1,i).equals("/") && line.substring(i+1, i+2).equals("_")){
						
						return line;
						
					}
				}
				if(index.length() == 2){
					if(line.substring(i, i+2).equals(index)){
						
						return line;
					}
				}
				}
			}
		   // do stuff to file here  
		}
		*/
		return name;
	//	return MyURL.URL_PAYLOAD + filename;
		}
	public String getVehicleType() {return vehicleType;}
	public String getTargetType() {return targetType;}
	public int[] getLocation() {return location;}
	public boolean isDone() {return done;}
	public void setDone(boolean b) {done = b;}
}
