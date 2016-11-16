package reschu.game.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random; 
 




import reschu.game.controller.Reschu;

public class PayloadList { 
    private LinkedList<Payload> payload_list = new LinkedList<Payload>();
    private Random rnd;
	private int time = (int) System.currentTimeMillis();
	public PayloadList() {rnd = new Random(time);}
	
	public int size() { return payload_list.size(); }	
 
	public void addPayload(int idx, String vType, String tType, int[] loc, String stmt) {
		payload_list.addLast(new Payload(idx, loc, vType, tType, stmt));
	}	 
	
	public Payload getPayload(String vType, String tType) throws IOException {	 
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("PayloadText.txt",true)));
		BufferedReader br1 = new BufferedReader(new FileReader("PayloadText.txt"));
		String line = "";
		Boolean infile = false;
		ArrayList<Integer> loads = new ArrayList<Integer>();
		while((line = br1.readLine()) != null){
			int index = Integer.parseInt(line);
			loads.add(index);
			
			}
		
		
		int payloadindex = 100;
		Payload p = null; 
		int cnt = 0;
		
		if( Reschu.train() || Reschu.tutorial() )  {			
			for( int i=0; i<payload_list.size(); i++ ) {
				p = payload_list.get(i);
				payloadindex = i;
				if( p.getVehicleType().equals(vType) && p.getTargetType().equals(tType) )
					break;
			}				
		}
		
		else {
			do {
				int x = rnd.nextInt(payload_list.size());
				
				payloadindex = x;
				infile = false;
				for(int j = 0; j< loads.size(); j++){
					if(x == loads.get(j)){
						infile = true;
					}
				}
				p = payload_list.get(x); 
				if( ++cnt >= payload_list.size() ) {
					out.close();
					br1.close();
					return null;
				}
			} while(!p.getVehicleType().equals(vType) 
					|| !p.getTargetType().equals(tType) 
					|| p.isDone() || infile);		
			p.setDone(true);
			
		} 
		br1.close();
		out.println(payloadindex);
		out.close();
		return p;
	}	
}
