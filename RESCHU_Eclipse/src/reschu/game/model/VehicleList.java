package reschu.game.model;

import java.util.*;

import reschu.constants.*;
import reschu.game.controller.GUI_Listener;

public class VehicleList {
	private Game g;
    private static LinkedList<Vehicle> v_list = new LinkedList<Vehicle>();
    
    public VehicleList(Game g) { this.g = g;}
    
    /**
     * Checks whether a vehicle list has a vehicle with a given name
     */
    public boolean hasVehicle(String v_name) {
        for( int i = 0; i < v_list.size(); i++)
            if( v_list.get(i).getName() == v_name) return true;
        return false;
    }
        
    /**
     * Add a vehicle
     * @param idx index number
     * @param v_type vehicle type
     * @param v_name vehicle name
     * @param v_payload vehicle's payload
     * @param milliseconds vehicle's velocity
     * @param rnd random seed to give the initial position of a vehicle
     * @param m map
     * @param l listener
     * @param g game
     * @throws UserDefinedException
     */
    public void addVehicle(int idx, String v_type, String v_name, String v_payload, int milliseconds, 
    		Random rnd, Map m, GUI_Listener l, Game g) throws UserDefinedException {
    	if( this.hasVehicle(v_name) )
            throw new UserDefinedException(v_name + " already exists.");
    	int x = rnd.nextInt(MySize.width);
    	int y = rnd.nextInt(MySize.height);
    	
        if(v_type == Vehicle.TYPE_UUV) {
            UUV v_uuv = new UUV(m, g);
            while (m.getCellType(x, y) == MyGame.LAND) { x = rnd.nextInt(MySize.width); y = rnd.nextInt(MySize.height); }            
            v_uuv.setIndex(idx);
            v_uuv.setName(v_name);
            v_uuv.setType(v_type);
            v_uuv.setPayload(v_payload);
            // v_uuv.setPos(x, y);
            v_uuv.setPos64((double)(x), (double)(y));
            v_uuv.setVelocity(milliseconds);
            v_uuv.setGuiListener(l);
            v_list.addLast(v_uuv);
        } else if( v_type == Vehicle.TYPE_UAV ) {
            UAV v_uav = new UAV(m, g);
            v_uav.setIndex(idx);            
            v_uav.setName(v_name);
            v_uav.setType(v_type);
            v_uav.setPayload(v_payload);
            // v_uav.setPos(x, y);
            v_uav.setPos64((double)(x), (double)(y));
            v_uav.setVelocity(milliseconds);
            v_uav.setGuiListener(l);
            v_list.addLast(v_uav);
        }
    }
    
    // Add ghost mission UAV
    public Vehicle AddGhostUAV(Vehicle v, Map m, Game g, GUI_Listener l) {
        UAV v_uav = new UAV(m, g);
        v_uav.setIndex(v.getIndex());
        // v_uav.setIndex(v_list.size()+1);
        v_uav.setName(v.getName()+" GHOST");
        v_uav.setType(v.getType());
        v_uav.setPayload(v.getPayload());
        // v_uav.setPos(v.getX(), v.getY());
        v_uav.setPos64(v.getX64(), v.getY64());
        v_uav.setGroundTruthPath(v.getGroundTruthPath());
        v_uav.setVelocity(v.getVelocity());
        v_uav.setGuiListener(l);
        return v_uav;
    }
    
    // add an vehicle to the last position of the list
    public void AddVehicleToList(Vehicle v) {
    	v_list.addLast(v);
    }
    
    // Returns the properties of the vehicle list
    public int size() { return v_list.size(); }
    public LinkedList<Vehicle> getLinkedList() { return v_list; };
    
    public static Vehicle getVehicle(int i){ return v_list.get(i); }
    
    /**
     * Get a vehicle with a given name
     * @param v_name Vehicle name
     * @throws UserDefinedException 
     */
    public Vehicle getVehicle(String v_name) throws UserDefinedException {
        if( !hasVehicle(v_name) )
            throw new UserDefinedException("No such vehicle(" + v_name + ") in Vehicle List.");
                
        for( int i = 0 ; i < v_list.size(); i++) 
            if( v_list.get(i).getName() == v_name ) return v_list.get(i);        
        
        return new Vehicle(new Map(), g);	// Never reaches.
    }
    
    /**
     * Get a vehicle at a given position.
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Vehicle getVehicle(int x, int y) {
    	for( int i = 0; i < v_list.size(); i++) {
    		if( v_list.get(i).getGroundTruthX() == x && v_list.get(i).getGroundTruthY() == y)
    			return v_list.get(i);
    	}
    	return new Vehicle(new Map(), g);	// Never reaches.
    }
    
    /**
     * Returns total damage of all vehicles
     */
    public int getTotalDamage() {
    	int total_damage = 0;
    	for( int i=0; i<size(); i++ ) {
    		total_damage += getVehicle(i).getDamage();
    	}
    	return total_damage;
    }
}