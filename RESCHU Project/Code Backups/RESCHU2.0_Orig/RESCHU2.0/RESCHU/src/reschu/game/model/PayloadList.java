package reschu.game.model;

import java.util.LinkedList;
import java.util.Random; 
 
import reschu.game.controller.Reschu;

public class PayloadList { 
    private LinkedList<Payload> payload_list = new LinkedList<Payload>();
    private Random rnd;
	
	public PayloadList() {rnd = new Random(100);}
	
	public int size() { return payload_list.size(); }	
 
	public void addPayload(int idx, String vType, String tType, int[] loc, String stmt) {
		payload_list.addLast(new Payload(idx, loc, vType, tType, stmt));
	}	 
	
	public Payload getPayload(String vType, String tType) {	 
		Payload p = null; 
		int cnt = 0;
		
		if( Reschu.train() || Reschu.tutorial() )  {			
			for( int i=0; i<payload_list.size(); i++ ) {
				p = payload_list.get(i);
				if( p.getVehicleType().equals(vType) && p.getTargetType().equals(tType) )
					break;
			}				
		}
		
		else {
			do {
				p = payload_list.get(rnd.nextInt(payload_list.size()-1)); 
				if( ++cnt >= payload_list.size() ) return null;
			} while(!p.getVehicleType().equals(vType) 
					|| !p.getTargetType().equals(tType) 
					|| p.isDone());		
			p.setDone(true);			
		} 
		return p;
	}	
}
