package reschu.game.utils;

import java.net.*;
import java.io.*;

public class FileReader {
	private String url;
	private BufferedReader br;
	
	public FileReader(String url) {
		this.url = url;
	}
	
	public void openFile() { 		
	    try {
	      URL source = new URL(url);
	      br = new BufferedReader(new InputStreamReader(source.openStream()));
	    }
	    catch(Exception e) {
	      e.printStackTrace();
	    }
	}
	
	public void closeFile() {
		try{
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public String readLineByLine() {
		String ret="";
		
		try{
			ret = br.readLine();
			
		} catch( IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	static public boolean isNumber(String s) {
		char[] testSet = s.replace("-","").replace("+","").toCharArray();
		for(int i=0; i<testSet.length; i++) {
			if( (int)testSet[i] < 48 || (int)testSet[i] > 57 ) return false;
		}
		return true;
	}
	
}
