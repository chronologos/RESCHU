package reschu.game.model;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import reschu.constants.*;
import reschu.game.controller.GUI_Listener;
import reschu.game.view.PanelMsgBoard;

public class Vehicle { 
	public static final String TYPE_UAV = "UAV";
	public static final String TYPE_UUV = "UUV";
	public static final String PAYLOAD_ISR = "ISR";
	public static final String PAYLOAD_COM = "COM";

	private String name;
	private String type;
	private String payload;
	private int xPosGroundTruth, yPosGroundTruth, xPosObserved, yPosObserved;
	private double s64XposGdTruth, s64YposGdTruth, s64XposObs, s64YposObs;
	private double s64GtAngle = 0, s64GtAngleOld = 0,s64ObsAngle = 0, s64ObsAngleOld = 0;
	private Target target;
	private LinkedList<int[]> groundTruthPath = new LinkedList<int[]>();
	private Map map;
	private int index;
	private int velocity; 
	private GUI_Listener lsnr;
	private int status;
	private double vDamage;
	private Game g;
	// private int velocity_buffer;
	// private int velocity_scale;
	private int stuckCount;
	private boolean isStuck;
	private boolean intersect;
	private boolean isHijacked;
	public boolean isDisappeared;
	public boolean isInvestigated;
	public boolean isEngaged;
	public boolean isNotified;
	private LinkedList<int[]> observedPath;
	private int[] HackLocation;
	private double HackAngle;
	
	public boolean getHijackStatus () {
		return isHijacked;
	}
	public void setHijackStatus (boolean b) {
		isHijacked = b;
	}
	public boolean getInvestigateStatus() {
		return isInvestigated;
	}
	public void setInvestigateStatus(boolean b) {
		isInvestigated = b;
	}
	public boolean getEngageStatus () {
		return isEngaged;
	}
	public void setEngageStatus (boolean b) {
		isEngaged = b;
	}
	public boolean getLostStatus () {
		return isDisappeared;
	}
	public void setLostStatus (boolean b) {
		isDisappeared = b;
	}
	public boolean getNotifiedStatus () {
		return isNotified;
	}
	public void setNotifiedStatus (boolean b) {
		isNotified = b;
	}
	
	/**
	 * Set the position of this vehicle (synchronized)
	 */
	public synchronized void setPos(int x, int y) {
		setGroundTruthX(x);
		setGroundTruthY(y);
	}
	public synchronized void setPos64(double x, double y) {
		setGroundTruthX64(x);
		setGroundTruthY64(y);
	}

	/**
	 * Get a path of this vehicle  (synchronized) 
	 */
	public synchronized LinkedList<int[]> getPath() {
		if (isHijacked){
			return observedPath;
		}
		else{
			return groundTruthPath;
		}
	}
	
	public synchronized LinkedList<int[]> getGroundTruthPath() {
		return groundTruthPath;
	}
	
	public synchronized void setGroundTruthPath(LinkedList<int[]> path_list) {
		groundTruthPath = path_list;
	}
	
	public synchronized LinkedList<int[]> getObservedPath() {
		return observedPath;
	}
	
	public synchronized void setObservedPath(LinkedList<int[]> path_list) {
		observedPath = path_list;
	}

	/**
	 * Add a waypoint to the path of this vehicle  (synchronized)
	 */
	public synchronized void addPath(int idx, int[] e) {
		if (isHijacked){
			observedPath.add(idx, e);
			// add waypoint for smarter attacker
			int[] temp = CreateMatchedPoint(HackLocation[0], HackLocation[1], e[0], e[1], HackAngle);
			groundTruthPath.add(idx, temp);
		} else {
			groundTruthPath.add(idx, e);
		}
	}

	/**
	 * Add a waypoint to the last path of this vehicle (synchronized)
	 */
	public synchronized void addPathLast(int[] e) { 
		if (isHijacked){
			observedPath.addLast(e);
			// add waypoint for smarter attacker
			int[] temp = CreateMatchedPoint(HackLocation[0], HackLocation[1], e[0], e[1], HackAngle);
			groundTruthPath.add(observedPath.size()-1, temp);
		} else {
			groundTruthPath.addLast(e);
		}
	}

	/**
	 * Set a path of this vehicle  (synchronized)
	 */
	public synchronized void setPath(int idx, int[] e) { 
		if (isHijacked){
			observedPath.set(idx, e);
			// set waypoint for smarter attacker
			int[] temp = CreateMatchedPoint(HackLocation[0], HackLocation[1], e[0], e[1], HackAngle);
			groundTruthPath.set(idx, temp);
			// set end point for smarter attacker
			if(idx == getPathSize()-1) {
				temp = GenerateEndPoint(HackLocation[0], HackLocation[1], e[0], e[1], HackAngle);
				groundTruthPath.set(getPathSize(), temp);
				// System.out.println("SIZE = "+getPathSize()+" IDX = "+idx);
			}
		} else {
			groundTruthPath.set(idx, e);
		}
	}

	/**
	 * Get the size of a path of this vehicle  (synchronized)
	 */
	public synchronized int getPathSize() {
		if(isHijacked) return observedPath.size();
		else return groundTruthPath.size();
	}
	
	public synchronized int getGroundPathSize() {
		return groundTruthPath.size();
	}

	/**
	 * Get a coordinate of a waypoint of this vehicle  (synchronized)
	 */
	public synchronized int[] getPathAt(int idx) {
		if(isHijacked) return observedPath.get(idx);
		else return groundTruthPath.get(idx);
	}
	
	public synchronized int[] getGroundPathAt(int idx) {
		return groundTruthPath.get(idx);
	}

	/**
	 * Remove a waypoint in the path of this vehicle  (synchronized)
	 */
	public synchronized void removePathAt(int idx) {
		if(isHijacked) {
			observedPath.remove(idx);
			// remove waypoint for smarter attacker
			removeGroundPathAt(idx);
		}
		else {
			groundTruthPath.remove(idx);
		}
	}
	
	// basically for smarter attacker use
	public synchronized void removeGroundPathAt(int idx) {
		groundTruthPath.remove(idx);
	}

	/**
	 * Get a coordinate at the first path of this vehicle  (synchronized)
	 */
	public synchronized int[] getFirstPathGround() {return groundTruthPath.getFirst();}
	public synchronized int[] getFirstPathObserved() {return isHijacked ? observedPath.getFirst() : groundTruthPath.getFirst();}

	/**
	 * Get a coordinate at the last path of this vehicle  (synchronized)
	 */
	public synchronized int[] getLastPath() {return groundTruthPath.getLast();}

	/**
	 * Remove the first waypoint of path of this vehicle  (synchronized)
	 */
	public synchronized void removeFirstPath() {
		if(isHijacked) observedPath.removeFirst();
		else groundTruthPath.removeFirst();
	}
	
	public synchronized void removeObservedFirstPath() {observedPath.removeFirst();}
	public synchronized void removeGroundFirstPath() {groundTruthPath.removeFirst();}

	/**
	 * Returns a map that this vehicle is assigned to 
	 */
	public synchronized Map getMap() { return map; }

	/*
	public synchronized void setGroundTruthX(int x){ xPosGroundTruth = x; }
	public synchronized int getGroundTruthX(){ return xPosGroundTruth; } 
	public synchronized void setGroundTruthY(int y){ yPosGroundTruth = y; }   
	public synchronized int getGroundTruthY(){ return yPosGroundTruth; }
	public synchronized void setObservedX(int x){ xPosObserved = x; }
	public synchronized int getX(){ 
		if (isHijacked){
			return xPosObserved;
		}
		else{
			return xPosGroundTruth;
		}
	} 
	public synchronized void setObservedY(int y){ yPosObserved = y; }   
	public synchronized int getY(){
		if (isHijacked){
			return yPosObserved;
		}
		else{
			return yPosGroundTruth;
		}
	}
	*/
	
	public synchronized void setGroundTruthX(int x)	{ s64XposGdTruth = x; }
	public synchronized int  getGroundTruthX()		{ return (int)(s64XposGdTruth); } 
	public synchronized void setGroundTruthY(int y)	{ s64YposGdTruth = y; }   
	public synchronized int  getGroundTruthY()		{ return (int)(s64YposGdTruth); }
	public synchronized void setObservedX(int x)	{ s64XposObs = x; }
	public synchronized int  getX()					{ return (int)(isHijacked? s64XposObs : s64XposGdTruth); }
	public synchronized void setObservedY(int y)	{ s64YposObs = y; }   
	public synchronized int  getY()					{ return (int)(isHijacked? s64YposObs : s64YposGdTruth); }
	public synchronized void 	setGroundTruthX64(double x)	{ s64XposGdTruth = x; }
	public synchronized double  getGroundTruthX64()			{ return s64XposGdTruth; } 
	public synchronized void 	setGroundTruthY64(double y)	{ s64YposGdTruth = y; }   
	public synchronized double  getGroundTruthY64()			{ return s64YposGdTruth; }
	public synchronized void 	setObservedX64(double x)	{ s64XposObs = x; }
	public synchronized double  getX64()					{ return (isHijacked? s64XposObs : s64XposGdTruth); }
	public synchronized void 	setObservedY64(double y)	{ s64YposObs = y; }   
	public synchronized double  getY64()					{ return (isHijacked? s64YposObs : s64YposGdTruth); }
	public synchronized void  	setGtAngle64(double a)		{ s64GtAngle  = a; }
	public synchronized void  	setObsAngle64(double a)		{ s64ObsAngle = a; }
	public synchronized double  getGtAngle64()				{ return s64GtAngle;  }
	public synchronized double  getObsAngle64()				{ return s64ObsAngle; }

	public void setName(String strName) { name = strName; }
	public String getName() {return name;}

	public void setType(String strType) { type = strType; }  
	public String getType() { return type; }  

	public void setIndex(int idx) { index = idx; }
	public int getIndex() {return index;}

	public void setPayload(String setPayload) { payload = setPayload; }
	public String getPayload() {return payload;}

	public void setVelocity(int milliseconds) { velocity = milliseconds; }
	public int getVelocity() {return velocity;}

	public void setTarget(Target t) {target = t;}
	public Target getTarget() { return target; }

	public void setStatus(int i) { status = i; }
	public int getStatus() { return status; }

	public void setIntersect(boolean b) { intersect = b; }
	public boolean getIntersect() { return intersect; }

	static public boolean isVehicleType(String s) {
		if( s.equals(TYPE_UAV) || s.equals(TYPE_UUV) ) return true;
		else return false;
	}

	public double getDamage() { return vDamage; }

	public void setGuiListener(GUI_Listener l) {lsnr = l;}

	public Vehicle(Map m, Game g) {
		// setGroundTruthX(0); setGroundTruthY(0);
		setGroundTruthX64(0); setGroundTruthY64(0);
		setTarget(null); 
		this.g = g;
		map = m; 
		setStatus(MyGame.STATUS_VEHICLE_STASIS);
		vDamage = 0;
		// velocity_scale = MySpeed.SPEED_TIMER;
		// velocity_buffer = 0;
		stuckCount = 0;
		isStuck = false;
		intersect = false;
		isDisappeared = false;
		isHijacked = false;
		isInvestigated = false;
		isEngaged = false;
		isNotified = false;
	}

	// check if reach a target
	private boolean boundaryCheck(int x, int y, int[] target_pos) {
		int w = Math.round(MySize.SIZE_TARGET_PXL / MySize.SIZE_CELL / 2);    	 
		if( (x<=target_pos[0]+w)&&(x>=target_pos[0]-w)&&(y<=target_pos[1]+w)&&(y>=target_pos[1]-w) ) return true;
		return false;	
	}
	
	/**
	 * Returns true if a vehicle's goal point is one of occupied targets.
	 */
	public boolean isAssignededTarget(int x, int y) {
		int[] target_pos;

		for( int i=0; i < getMap().getListAssignedTarget().size(); i++ ) {
			target_pos = getMap().getListAssignedTarget().get(i).getPos();	

			if( boundaryCheck(x, y, target_pos) ) {
				return true;    			
			}
		}
		return false;
	}

	public void addGoal(int x, int y) {
		int[] target_pos;
		Target t;
		boolean assigned = false;

		for( int i=0; i < getMap().getListUnassignedTarget().size(); i++ ) {
			setTarget(null);
			target_pos = getMap().getListUnassignedTarget().get(i).getPos();    
			t = getMap().getListUnassignedTarget().get(i);	

			if( !t.isVisible() ) {
				if( getPayload()==Vehicle.PAYLOAD_COM && boundaryCheck(x, y, target_pos) ) {
					x = target_pos[0]; y = target_pos[1];            		
					setTarget(getMap().getListUnassignedTarget().get(i));
					PanelMsgBoard.Msg("Vehicle ["+index+"] has been assigned to target "+this.getTarget().getName()+".");
					getMap().assignTarget(new int[]{x, y});
					assigned = true;
					break;
				}
				else if(getPayload()!=Vehicle.PAYLOAD_COM && boundaryCheck(x, y, target_pos) ) {
					//2008-04-05
					//UAV to grey target
					lsnr.showMessageOnTopOfMap("You cannot assign a UAV to a grey target, please reassign " + type + " " + index, 5);
				}
			}
			else {
				if( getPayload()!=Vehicle.PAYLOAD_COM && boundaryCheck(x, y, target_pos) ) {
					x = target_pos[0]; y = target_pos[1];
					if(type==Vehicle.TYPE_UUV && getMap().getListUnassignedTarget().get(i).getMission() != "SHORE") {
						//2008-04-05
						//UUV to land target (grey or red)
						lsnr.showMessageOnTopOfMap("You cannot assign a UUV to a land target, please reassign " + type + " " + index, 5);
						break;
					}
					setTarget(getMap().getListUnassignedTarget().get(i));
					PanelMsgBoard.Msg("Vehicle ["+index+"] has been assigned to target "+this.getTarget().getName()+".");
					getMap().assignTarget(new int[]{x, y});
					assigned = true;
					break;	
				}
				else if( getPayload()==Vehicle.PAYLOAD_COM && boundaryCheck(x, y, target_pos) ) {
					//2008-04-05
					//HALE to red target
					lsnr.showMessageOnTopOfMap("You cannot assign a HALE to a red target, please reassign " + type + " " + index, 5);
				}
			}
		}
		addPathLast(new int[]{x, y});
		setStatus(MyGame.STATUS_VEHICLE_MOVING);
		if( g.getElapsedTime() != 0 ) {
			if( assigned ) lsnr.EVT_GP_SetGP_End_Assigned(index, x, y, getTarget().getName());
			else lsnr.EVT_GP_SetGP_End_Unassigned(index, x, y);
		}
	}

	public void changeGoal(int[] ex_goal, int x, int y) {
		if( x < 0 || x > MySize.width || y < 0 || y > MySize.height ) return;

		int[] ex_target_pos, new_target_pos;
		Target t;
		boolean assigned = false;

		for( int i=0; i < getMap().getListAssignedTarget().size(); i++ ) {
			ex_target_pos = getMap().getListAssignedTarget().get(i).getPos();

			if( ex_goal[0] == ex_target_pos[0] && ex_goal[1] == ex_target_pos[1] ) { 
				setTarget(null);    			
				getMap().unassignTarget(ex_target_pos);
				break;
			}
		}

		for( int i=0; i < getMap().getListUnassignedTarget().size(); i++ ) {
			new_target_pos = getMap().getListUnassignedTarget().get(i).getPos();
			t = getMap().getListUnassignedTarget().get(i);

			if( !t.isVisible() ) {
				if( getPayload()==Vehicle.PAYLOAD_COM && boundaryCheck(x, y, new_target_pos) ) {
					x = new_target_pos[0]; y = new_target_pos[1];            		
					setTarget(getMap().getListUnassignedTarget().get(i)); 
					PanelMsgBoard.Msg("Vehicle ["+index+"] has been assigned to target "+this.getTarget().getName()+".");
					getMap().assignTarget(new int[]{x, y});
					assigned = true;
					break;
				}
				else if(getPayload()!=Vehicle.PAYLOAD_COM && boundaryCheck(x, y, new_target_pos) ) {
					//2008-04-05
					//UAV to grey target
					lsnr.showMessageOnTopOfMap("You cannot assign a UAV to a grey target, please reassign " + type + " " + index, 5);
				}
			}
			else {
				if( getPayload()!=Vehicle.PAYLOAD_COM && boundaryCheck(x, y, new_target_pos) ) {
					x = new_target_pos[0]; y = new_target_pos[1];
					if(type==Vehicle.TYPE_UUV && getMap().getListUnassignedTarget().get(i).getMission() != "SHORE") {
						//2008-04-05
						//UUV to land target (grey or red)
						lsnr.showMessageOnTopOfMap("You cannot assign a UUV to a land target, please reassign " + type + " " + index, 5);
						break; 
					}
					setTarget(getMap().getListUnassignedTarget().get(i)); 
					PanelMsgBoard.Msg("Vehicle ["+index+"] has been assigned to target "+this.getTarget().getName()+".");
					getMap().assignTarget(new int[]{x, y});
					assigned = true;
					break;	
				}
				else if( getPayload()==Vehicle.PAYLOAD_COM && boundaryCheck(x, y, new_target_pos) ) {
					//2008-04-05
					//HALE to red target
					lsnr.showMessageOnTopOfMap("You cannot assign a HALE to a red target, please reassign " + type + " " + index, 5);
				}
			}	
		}    	
		setPath(getPathSize()-1, new int[]{x, y}); 
		if( g.getElapsedTime() != 0 ) {
			if( assigned ) lsnr.EVT_GP_ChangeGP_End_Assigned(index, x, y, getTarget().getName());
			else lsnr.EVT_GP_ChangeGP_End_Unassigned(index, x, y);
		}
	}

	public synchronized int addWaypoint(int x, int y) {
		double d, idx = 0;
		double distance = 9999999;	// infinite

		d = Game.getDistance(getGroundTruthX(), getGroundTruthY(), x, y) + Game.getDistance(getPathAt(0)[0], getPathAt(0)[1], x, y);    	
		if( d < distance ) distance = d; idx = 0;

		for( int i=0; i<getPathSize()-1; i++ ) {
			d = Game.getDistance(getPathAt(i)[0], getPathAt(i)[1], x, y) + Game.getDistance(getPathAt(i+1)[0], getPathAt(i+1)[1], x, y);
			if( d < distance ) {distance = d; idx = i+1;} 
		}
		if( x > 0 && x < MySize.width && y > 0 && y < MySize.height )
			addPath((int)idx, new int[]{x, y});
		return (int)idx;
	} 

	public synchronized int addWaypoint(int x, int y, int idx) {
		addPath(idx, new int[]{x, y});
		return idx;
	}

	public void delWaypoint(int x, int y) {
		for(int i=0; i<getPathSize()-1; i++) {
			if(getPathAt(i)[0]==x && getPathAt(i)[1]==y) {
				removePathAt(i);
			}
		}
	}

	public void delWaypoint(int[] coordinate) {
		for(int i=0; i<getPathSize()-1; i++) {
			if(getPathAt(i) == coordinate) {
				removePathAt(i);
			}
		}
	}

	public void changeWaypoint( int ex_x, int ex_y, int new_x, int new_y ) {
		if( new_x < 0 || new_x > MySize.width || new_y < 0 || new_y > MySize.height ) return;

		for(int i=0; i<getPathSize()-1; i++) {
			if( getPathAt(i)[0] == ex_x && getPathAt(i)[1] == ex_y ) {
				getPathAt(i)[0] = new_x; getPathAt(i)[1] = new_y;
				if(isHijacked) {
					// set waypoint for smarter attacker
					int[] temp = CreateMatchedPoint(HackLocation[0], HackLocation[1], new_x, new_y, HackAngle);
					groundTruthPath.set(i, temp);
				}
			}
		}
	}

	public boolean hasGoal() { if( getPathSize() == 0 ) return false; return true; }

	public boolean hasWaypoint() { if( getPathSize()-1 == 0 ) return false; return true; }

	// Moving Algorithms
	public double getDistanceGround(int pos_x, int pos_y) {
		if( getPathSize() == 0) return 0;
		return Math.sqrt( Math.pow( (double)(pos_x - getFirstPathGround()[0]), 2.0 ) 
				+ Math.pow( (double)(pos_y - getFirstPathGround()[1]), 2.0 ) );
	}
	
	public double getDistanceObserved(int pos_x, int pos_y) {
		if( getPathSize() == 0) return 0;
		return Math.sqrt( Math.pow( (double)(pos_x - getFirstPathObserved()[0]), 2.0 ) 
				+ Math.pow( (double)(pos_y - getFirstPathObserved()[1]), 2.0 ) );
	}
	
	public double getDistanceGround64(double pos_x, double pos_y) {
		if( getPathSize() == 0) return 0;
		return Math.sqrt( Math.pow( (pos_x - (double)(getFirstPathGround()[0])), 2.0 ) 
						+ Math.pow( (pos_y - (double)(getFirstPathGround()[1])), 2.0 ) );
	}
	public double getDistanceObserved64(double pos_x, double pos_y) {
		if( getPathSize() == 0) return 0;
		return Math.sqrt( Math.pow( (pos_x - (double)(getFirstPathObserved()[0])), 2.0 ) 
						+ Math.pow( (pos_y - (double)(getFirstPathObserved()[1])), 2.0 ) );
	}

	public void moveRandom(int i) {
		Random rnd = new Random();
		moveTo(rnd.nextInt(8));
	}
	
	// far04 changed uncommented
	public void moveHillClimbing() {
		
		// System.out.println("far moveHillClimbing called");
		if( isStuck ) {
			moveTo(6);
			if(--stuckCount <= 0) isStuck = false;
			return;
		}

		double presentDistance, d=999999999;
		Random rnd = new Random();
		int direction = 8;    
		boolean stuck = true;

		presentDistance = getDistanceGround(getGroundTruthX(), getGroundTruthY());

		for( int i=0; i<8; i++ ) {
			// random number for zigzag moving
			direction = rnd.nextInt(8);
			switch( direction ) {
			case 0: d = getDistanceGround(getGroundTruthX()-1, getGroundTruthY()-1); 	break;
			case 1: d = getDistanceGround(getGroundTruthX()-1, getGroundTruthY()); 		break;
			case 2:	d = getDistanceGround(getGroundTruthX()-1, getGroundTruthY()+1); 	break;
			case 3: d = getDistanceGround(getGroundTruthX(), getGroundTruthY()-1); 		break;
			case 4: d = getDistanceGround(getGroundTruthX(), getGroundTruthY()+1); 		break;
			case 5: d = getDistanceGround(getGroundTruthX()+1, getGroundTruthY()-1); 	break;
			case 6: d = getDistanceGround(getGroundTruthX()+1, getGroundTruthY()); 		break;
			case 7: d = getDistanceGround(getGroundTruthX()+1, getGroundTruthY()+1); 	break;
			}
			if( d < presentDistance && chkValidMove(direction)) { stuck = false; break; }
		}
		if( chkValidMove(direction) ) moveTo(direction);

		// if vehicle is hijacked we need to move observed location as well
		if (isHijacked){
			direction=0;
			d = 999999999;
			presentDistance = getDistanceGround(getGroundTruthX(), getGroundTruthY());
			// presentDistance = getDistanceObserved(getX(), getY());
			for( int i=0; i<8; i++ ) {
				switch( i ) {
				case 0: d = getDistanceObserved(getX()-1, getY()-1); 	break;
				case 1: d = getDistanceObserved(getX()-1, getY()); 		break;
				case 2:	d = getDistanceObserved(getX()-1, getY()+1); 	break;
				case 3: d = getDistanceObserved(getX(), getY()-1); 		break;
				case 4: d = getDistanceObserved(getX(), getY()+1); 		break;
				case 5: d = getDistanceObserved(getX()+1, getY()-1); 	break;
				case 6: d = getDistanceObserved(getX()+1, getY()); 		break;
				case 7: d = getDistanceObserved(getX()+1, getY()+1); 	break;
				}
				if( d < presentDistance && chkValidMove(direction)) { stuck = false; break; }
			}
			if(chkValidMove(direction)) {
				moveObservedTo(direction);
			}
			// if the real UAV position is out of the border
			else {}
			
			// System.out.println("OBSERVED x = "+getX()+"  y = "+getY());
			// System.out.println("GROUND x = "+getGroundTruthX()+"  y = "+getGroundTruthY());
		}
		
		if( stuck ) {
			stuckCount++;
			if( stuckCount >= 5 ) isStuck = true;
		}    	
		else moveTo(direction);
	}
	
	public void moveBestFirst() {
		
		// System.out.println("moveBestFirst called");
		int direction=0;
		double d = 999999999, bestDistance = 999999999;

		for( int i=0; i<8; i++ ) {    		
			switch( i ) {
			case 0: d = getDistanceGround(getGroundTruthX()-1, getGroundTruthY()-1); 	break;
			case 1: d = getDistanceGround(getGroundTruthX()-1, getGroundTruthY()); 		break;
			case 2:	d = getDistanceGround(getGroundTruthX()-1, getGroundTruthY()+1); 	break;
			case 3: d = getDistanceGround(getGroundTruthX(), getGroundTruthY()-1); 		break;
			case 4: d = getDistanceGround(getGroundTruthX(), getGroundTruthY()+1); 		break;
			case 5: d = getDistanceGround(getGroundTruthX()+1, getGroundTruthY()-1); 	break;
			case 6: d = getDistanceGround(getGroundTruthX()+1, getGroundTruthY()); 		break;
			case 7: d = getDistanceGround(getGroundTruthX()+1, getGroundTruthY()+1); 	break;
			}
			if( d < bestDistance ) {
				bestDistance = d; direction = i;
			}
		}
		if( chkValidMove(direction) ) moveTo(direction);

		// if vehicle is hijacked we need to move observed location as well
		if (isHijacked) {
			direction=0;
			d = 999999999;
			bestDistance = 999999999;
			for( int i=0; i<8; i++ ) {
				switch( i ) {
				case 0: d = getDistanceObserved(getX()-1, getY()-1); 	break;
				case 1: d = getDistanceObserved(getX()-1, getY()); 		break;
				case 2:	d = getDistanceObserved(getX()-1, getY()+1); 	break;
				case 3: d = getDistanceObserved(getX(), getY()-1); 		break;
				case 4: d = getDistanceObserved(getX(), getY()+1); 		break;
				case 5: d = getDistanceObserved(getX()+1, getY()-1); 	break;
				case 6: d = getDistanceObserved(getX()+1, getY()); 		break;
				case 7: d = getDistanceObserved(getX()+1, getY()+1); 	break;
				}
				if( d < bestDistance ) {
					bestDistance = d; direction = i;
				}
			}
			if(chkValidMove(direction)) moveObservedTo(direction);
			
			// /*
			System.out.println("UAV index = "+index);
			System.out.println("OBSERVED   x = "+getX()+"  y = "+getY());
			System.out.println("GROUND     x = "+getGroundTruthX()+"  y = "+getGroundTruthY());
			System.out.println("O Path Size  = "+observedPath.size()+"  G Path Size = "+groundTruthPath.size());
			if(observedPath.size() > 0)
				System.out.println("O Path First = "+observedPath.getFirst()[0]+" "+observedPath.getFirst()[1]);
			else
				System.out.println("O Path Frist NONE");
			if(groundTruthPath.size() > 0)
				System.out.println("G Path First = "+groundTruthPath.getFirst()[0]+" "+groundTruthPath.getFirst()[1]);
			else
				System.out.println("G Path Frist NONE");
			System.out.println("\n");
			// */
		}
		
		// print position information for debugging
		/*
		else {
			System.out.println(index+" OBSERVED x = "+getX()+"  y = "+getY());
			System.out.println(index+" GROUND   x = "+getGroundTruthX()+"  y = "+getGroundTruthY());
		}
		*/
	}

	public void moveTo(int direction) {
		switch (direction) {
		case 0:	// up-left
			setGroundTruthX(getGroundTruthX()-MySpeed.VELOCITY); 
			setGroundTruthY(getGroundTruthY()-MySpeed.VELOCITY); break;
		case 1: // up
			setGroundTruthX(getGroundTruthX()-MySpeed.VELOCITY); break;
		case 2: // up-right
			setGroundTruthX(getGroundTruthX()-MySpeed.VELOCITY); 
			setGroundTruthY(getGroundTruthY()+MySpeed.VELOCITY); break;
		case 3: // left
			setGroundTruthY(getGroundTruthY()-MySpeed.VELOCITY); break;
		case 4: // right
			setGroundTruthY(getGroundTruthY()+MySpeed.VELOCITY); break;
		case 5: // down-left
			setGroundTruthX(getGroundTruthX()+MySpeed.VELOCITY); 
			setGroundTruthY(getGroundTruthY()-MySpeed.VELOCITY); break;
		case 6: // down
			setGroundTruthX(getGroundTruthX()+MySpeed.VELOCITY); break;
		case 7: // down-right
			setGroundTruthX(getGroundTruthX()+MySpeed.VELOCITY); 
			setGroundTruthY(getGroundTruthY()+MySpeed.VELOCITY); break;
		default:
			break;
		}
		payloadCheck(getGroundTruthX(), getGroundTruthY());
	}

	public void moveObservedTo(int direction) {
		switch (direction) {
		case 0:	// up-left
			setObservedX(getX()-MySpeed.VELOCITY); 
			setObservedY(getY()-MySpeed.VELOCITY); break;
		case 1: // up
			setObservedX(getX()-MySpeed.VELOCITY); break;
		case 2: // up-right
			setObservedX(getX()-MySpeed.VELOCITY); 
			setObservedY(getY()+MySpeed.VELOCITY); break;
		case 3: // left
			setObservedY(getY()-MySpeed.VELOCITY); break;
		case 4: // right
			setObservedY(getY()+MySpeed.VELOCITY); break;
		case 5: // down-left
			setObservedX(getX()+MySpeed.VELOCITY); 
			setObservedY(getY()-MySpeed.VELOCITY); break;
		case 6: // down
			setObservedX(getX()+MySpeed.VELOCITY); break;
		case 7: // down-right
			setObservedX(getX()+MySpeed.VELOCITY); 
			setObservedY(getY()+MySpeed.VELOCITY); break;
		default:
			break;
		}
		payloadCheck(getX(), getY()); //TODO(iantay) is this correct
	}

	private void payloadCheck(int pos_x, int pos_y) {		
		if(getPathSize()!=0 && positionCheck(pos_x, pos_y)) {
			if(getPathSize()==1 && target!=null) {
				if(isHijacked) return;
				// VEHICLE ARRIVED TO ITS GOAL WHERE THE PLACE IS THE ONE OF UNASSIGNED_TARGETS
				if( getPayload() == Vehicle.PAYLOAD_COM ) { 
					setStatus(MyGame.STATUS_VEHICLE_PENDING);
				}
				else {
					setStatus(MyGame.STATUS_VEHICLE_PENDING);
					String msg = "Vehicle [" + index + "] has reached its target.";
					PanelMsgBoard.Msg(msg);
				}
				lsnr.EVT_Vehicle_ArrivesToTarget(index, getTarget().getName(), getTarget().getPos()[0], getTarget().getPos()[1]);
			}
			lsnr.Hide_Popup(this);
			removeFirstPath();
		}
	}
	
	//far06 Move exact TODO implement
	public void movePrecise() {
        //Get Cartesian distance to goal
        double s64DeltaX = getGroundTruthX64() - (double)(getFirstPathGround()[0]);
        double s64DeltaY = getGroundTruthY64() - (double)(getFirstPathGround()[1]);
        //Direction angle
        //Angle is measured from North, CCW
        //Range: [-pi, +pi]
        double s64OldAngle = 0;
        double s64NewAngle = 0;
        double s64AngleDiff = 0;
        double s64AngleInc = Math.PI/250.0; // original 1000 or 2000
        //Check proximity
        if (Math.abs(s64DeltaX) <= MySpeed.VELOCITY64) { s64DeltaX = 0; }
        if (Math.abs(s64DeltaY) <= MySpeed.VELOCITY64) { s64DeltaY = 0; }
        //Calculate velocity angle
        //Angle is measured from east, CCW
        if ((s64DeltaX != 0) || (s64DeltaY != 0)) {
            double pi = Math.PI;
            s64NewAngle = Math.atan2(s64DeltaY,s64DeltaX);
            s64OldAngle = s64GtAngle;
            s64AngleDiff = s64NewAngle - s64GtAngle;
            s64AngleDiff=(s64AngleDiff > pi)? (s64AngleDiff-2.0*pi):(s64AngleDiff);
            s64AngleDiff=(s64AngleDiff < -pi)? (s64AngleDiff+2.0*pi):(s64AngleDiff);
            if (s64AngleDiff > 2*s64AngleInc && s64AngleDiff < pi) {
                s64NewAngle = s64GtAngle + s64AngleInc;
            }
            else if (s64AngleDiff < -2*s64AngleInc && s64AngleDiff > -pi) {
                s64NewAngle = s64GtAngle - s64AngleInc;
            }
            s64GtAngle = s64NewAngle;
        }
        else {
            s64GtAngle = 0;
        }
        
        setGroundTruthX64(getGroundTruthX64() - Math.cos(s64GtAngle)*MySpeed.VELOCITY64);
        setGroundTruthY64(getGroundTruthY64() - Math.sin(s64GtAngle)*MySpeed.VELOCITY64);
        // payloadCheck((int)(getGroundTruthX64()), (int)(getGroundTruthY64()));
        payloadCheck((int)(getX64()), (int)(getY64()));
        
        // if(getIndex() == 1) System.out.println("X = "+getX64()+" Y = "+getY64());

        if (isHijacked) {
            //Get Cartesian distance to goal
            double s64ObsDeltaX = getX64() - (double)(getFirstPathObserved()[0]);
            double s64ObsDeltaY = getY64() - (double)(getFirstPathObserved()[1]);
            //Direction angle
            //Angle is measured from North, CCW
            //Range: [-pi, +pi]
            double s64ObsAngle = 0;
            
            //Check proximity
            if (Math.abs(s64ObsDeltaX) <= MySpeed.VELOCITY64) { s64ObsDeltaX = 0; }
            if (Math.abs(s64ObsDeltaY) <= MySpeed.VELOCITY64) { s64ObsDeltaY = 0; }
            //Calculate velocity angle
            //Angle is measured from east, CCW
            if ((s64ObsDeltaX != 0) || (s64ObsDeltaY != 0)) {
                s64ObsAngle = Math.atan2(s64ObsDeltaY,s64ObsDeltaX);
            }
            else {
                s64ObsAngle = 0;
            }
            
            setObservedX64(getX64() - Math.cos(s64ObsAngle)*MySpeed.VELOCITY64);
            setObservedY64(getY64() - Math.sin(s64ObsAngle)*MySpeed.VELOCITY64);
            setObsAngle64(s64ObsAngle);
            
            payloadCheckHacked((int)getGroundTruthX64(), (int)getGroundTruthY64());
            /*
    		System.out.println("UAV index = "+index);
    		System.out.println("OBSERVED   x = "+getX64()+"  y = "+getY64());
    		System.out.println("GROUND     x = "+getGroundTruthX64()+"  y = "+getGroundTruthY64());
    		System.out.println("O Path Size  = "+observedPath.size()+"  G Path Size = "+groundTruthPath.size());
    		if(observedPath.size() > 0)
    			System.out.println("O Path First = "+observedPath.getFirst()[0]+" "+observedPath.getFirst()[1]);
    		else
    			System.out.println("O Path Frist NONE");
    		if(groundTruthPath.size() > 0)
    			System.out.println("G Path First = "+groundTruthPath.getFirst()[0]+" "+groundTruthPath.getFirst()[1]);
    		else
    			System.out.println("G Path Frist NONE");
    		System.out.println("\n");
    		*/
        }
	}
	
    private void payloadCheckHacked(int pos_x, int pos_y) {
		if(getGroundPathSize()!=0 && GroundPositionCheck(pos_x, pos_y)) {
			removeGroundFirstPath();
		}
    }
	
	private boolean positionCheck (int pos_x, int pos_y) {
		if((pos_x>=getFirstPathObserved()[0]-1) && (pos_x<=getFirstPathObserved()[0]+1)
				&& (pos_y>=getFirstPathObserved()[1]-1) && (pos_y<=getFirstPathObserved()[1]+1))
			return true;
		else return false;
	}
	
	private boolean GroundPositionCheck (int pos_x, int pos_y) {
		if((pos_x>=getFirstPathGround()[0]-1) && (pos_x<=getFirstPathGround()[0]+1)
				&& (pos_y>=getFirstPathGround()[1]-1) && (pos_y<=getFirstPathGround()[1]+1))
			return true;
		else return false;
	}

	public synchronized boolean chkValidMove(int direction) {
		switch (direction) {
		case 0:	// up-left
			if( (getGroundTruthX() > 0 && getGroundTruthY() > 0) && 
					(chkValidPosition(getGroundTruthX()-MySpeed.VELOCITY, getGroundTruthY()-MySpeed.VELOCITY)) ) 
				return true;
			return false; 
		case 1: // up
			if( (getGroundTruthX() > 0) && 
					(chkValidPosition(getGroundTruthX()-MySpeed.VELOCITY, getGroundTruthY())) ) 
				return true;
			return false;
		case 2: // up-right
			if( (getGroundTruthX() > 0 && getGroundTruthY() < MySize.height-1) && 
					(chkValidPosition(getGroundTruthX()-MySpeed.VELOCITY, getGroundTruthY()+MySpeed.VELOCITY)) ) 
				return true;
			return false;
		case 3: // left
			if( (getGroundTruthY() > 0 ) &&
					( chkValidPosition(getGroundTruthX(), getGroundTruthY()-MySpeed.VELOCITY)) ) 
				return true;
			return false;
		case 4: // right
			if( (getGroundTruthY() < MySize.height-1) && 
					( chkValidPosition(getGroundTruthX(), getGroundTruthY()+MySpeed.VELOCITY)) )
				return true;
			return false;
		case 5: // down-left
			if( (getGroundTruthX() < MySize.width-1 && getGroundTruthY() > 0) &&
					( chkValidPosition(getGroundTruthX()+MySpeed.VELOCITY, getGroundTruthY()-MySpeed.VELOCITY)) )
				return true;
			return false;
		case 6: // down
			if( (getGroundTruthX() < MySize.width-1 ) &&
					( chkValidPosition(getGroundTruthX()+MySpeed.VELOCITY, getGroundTruthY())) )
				return true;
			return false;
		case 7: // down-right
			if( (getGroundTruthX() < MySize.width-1 && getGroundTruthY() < MySize.height-1 ) &&
					( chkValidPosition(getGroundTruthX()+MySpeed.VELOCITY, getGroundTruthY()+MySpeed.VELOCITY)) )
				return true;
			return false;
		default:
			return false;
		}
	}

	public synchronized boolean chkValidPosition(int width, int height) {
		if( type==Vehicle.TYPE_UUV ) {    		
			if( getMap().getCellType(width, height)==MyGame.LAND) return false;    		
		}
		return true;
	}

	public void chkHazardArea() {
		double damage = 0;
		double d;
		int[] hazard_pos;
		for(int i=0; i<map.getListHazard().size(); i++ ) {
			hazard_pos = map.getListHazard().get(i);
            d = Math.sqrt( 
                    Math.pow( (double)(s64XposGdTruth - hazard_pos[0]), 2.0 ) + 
                    Math.pow( (double)(s64YposGdTruth - hazard_pos[1]), 2.0 ) )
                    * (double)MySize.SIZE_CELL;
            if(d <= MySize.SIZE_HAZARD_1_PXL ) damage += 50/d;
			else if(d < MySize.SIZE_HAZARD_2_PXL && d > MySize.SIZE_HAZARD_1_PXL) damage += 30;
			else damage += 0;

			if( d < 50d && d > 45d )  
				lsnr.EVT_Vehicle_Damaged(getIndex(), hazard_pos[0], hazard_pos[1]); 

		}
		// We don't decrease the speed of a vehicle anymore
		// setBuffer(damage);	
		vDamage += (double)(damage)/50; // originally 100, could be changed to 50?
		lsnr.Vehicle_Damaged_By_Hazard_Area_From_Vehicle(this);
	}

	private void updateVisibility(int x, int y) {
		int[] target_pos;
		Target t;    	
		int w = Math.round(MySize.SIZE_TARGET_PXL / MySize.SIZE_CELL / 2);
		int d = Math.round(MySize.SIZE_UAV_COMM_PXL/ MySize.SIZE_CELL / 3);

		for( int i=0; i < getMap().getListAssignedTarget().size(); i++ ) {
			setTarget(null);
			target_pos = getMap().getListAssignedTarget().get(i).getPos();    
			t = getMap().getListAssignedTarget().get(i);		
			if( !t.isVisible() ) {
				if( (x-d<=target_pos[0]+w)&&(x+d>=target_pos[0]-w)&&(y-d<=target_pos[1]+w)&&(y+d>=target_pos[1]-w) )  {
					t.setVisible(true);
					lsnr.EVT_Target_BecameVisible(t.getName(), t.getPos());
					break;
				}
			}
		}
	}

	public void COM_Payload() {
		updateVisibility(getGroundTruthX(), getGroundTruthY());
		getMap().unassignTarget(new int[]{getGroundTruthX(), getGroundTruthY()});
		lsnr.Target_Become_Visible_From_Vehicle(this);
		setStatus(MyGame.STATUS_VEHICLE_MOVING);
		lsnr.EVT_Payload_EngagedAndFinished_COMM(index, getTarget().getName());
	}

	public synchronized void hijack(String hackData) throws IllegalArgumentException{
		// hackData comes in form "NEW_X_TARGET NEW_Y_TARGET"
		// setObservedX(getX());
		// setObservedY(getY());
		setObservedX64(getX64());
		setObservedY64(getY64());
		String[] coordStrings = hackData.split(" ");
		if (hackData == null) throw new IllegalArgumentException("Null hackData");
		if (coordStrings.length != 2) throw new IllegalArgumentException("Wrong number of coordinates in hackdata, must be 2");
		int xCoord, yCoord;
		try {
			xCoord = Integer.parseInt(coordStrings[0]);
		}
		catch (NumberFormatException e) {
			System.out.println("Uncaught number format exception on x-coordinate from AttackEngine");
			return;
		}
		try {
			yCoord = Integer.parseInt(coordStrings[1]);
		}
		catch (NumberFormatException e2) {
			System.out.println("Uncaught number format exception on y-coordinate from AttackEngine");
			return;
		}
		// if attack position is "0 0"
		// it will be considered as fake attack
		if (xCoord == 0 && yCoord == 0){
			System.out.println("Fake attack launched.");
			lsnr.EVT_Hack_Launch_Fake(index);
			return;
		}
		
		// real attack if attack position is NOT "0 0"
		isHijacked = true;
		
		System.out.println("Launching hack with smarter attacker");
		lsnr.EVT_Hack_Launch(index, xCoord, yCoord);
		lsnr.EVT_Generate_Ghost_Mission(this);
		
		int[] hackCoords = new int[]{xCoord, yCoord};
		observedPath = new LinkedList<int[]>(groundTruthPath);
		
		CreateSmarterGoundPath();
		
		// groundTruthPath.clear();
		// groundTruthPath.add(hackCoords);
		
		// System.out.println("Hack complete, getX currently returns " + getX());		
		// groundTruthPath.addFirst(hackCoords);
		// groundTruthPath.set(1, hackCoords);
		//    	else observedPath.set(0, hackCoords); // Just overwrite next item on path;
		//    	groundTruthPath = observedPath; // swap path and hackpath
		//    	observedPath = temp;
		//    	System.out.println("Finished replacing original path with hacked path");
		//    	System.out.println("Next element on hacked path : ");
		//    	System.out.println(groundTruthPath.getFirst()[0] + ", " + groundTruthPath.getFirst()[1]);
	}

	public void endHijack() {
		isHijacked = false;
	}
	
	public void CreateSmarterGoundPath() {
		groundTruthPath.clear();
		HackLocation = new int[]{getX(), getY()};
		int[] end_point;
		int sign = 0;
		while(sign == 0) sign = ThreadLocalRandom.current().nextInt(-1, 1+1);
		HackAngle = sign*ThreadLocalRandom.current().nextInt(30, 60)/180.0*Math.PI;
		// System.out.println("ANGLE = "+HackAngle+" SIGN = "+sign);

		for(int i=0; i<observedPath.size(); i++) {
			int[] point = CreateMatchedPoint(HackLocation[0], HackLocation[1], observedPath.get(i)[0], observedPath.get(i)[1], HackAngle);
			groundTruthPath.add(point);
			// System.out.println("INIT POINT = "+HackLocation[0]+" "+HackLocation[1]);
			// System.out.println("OBS  POINT = "+observedPath.get(i)[0]+" "+observedPath.get(i)[1]);
			// System.out.println("NEW  POINT = "+point[0]+" "+point[1]);
		}

		if(observedPath.size() == 0) end_point = GenerateNewEndPoint(HackLocation[0], HackLocation[1]);
		else end_point = GenerateEndPoint(HackLocation[0], HackLocation[1], observedPath.getLast()[0], observedPath.getLast()[1], HackAngle);
		groundTruthPath.add(end_point);
		// System.out.println("END  POINT = "+end_point[0]+" "+end_point[1]);
	}
	
	public int[] CreateMatchedPoint(double x0, double y0, double x1, double y1, double angle) {
		double theta = Math.atan2((x1-x0), (y1-y0));
		double length = Math.hypot((x1-x0), (y1-y0));
		
		double new_theta = theta + angle;
		if(new_theta > 2*Math.PI) new_theta -= 2*Math.PI;
		if(new_theta < -2*Math.PI) new_theta += 2*Math.PI;
		double x2 = x0 + Math.sin(new_theta)*length;
		double y2 = y0 + Math.cos(new_theta)*length;
		
		// System.out.println("ANGLE = "+theta+" NEW ANGLE = "+new_theta);
		int[] new_point = new int[]{(int)x2, (int)y2};
		return new_point;
	}
	
	public int[] GenerateEndPoint(double x0, double y0, double x1, double y1, double angle) {
		double theta = Math.atan2((x1-x0), (y1-y0));		
		double new_theta = theta + angle;
		if(new_theta > 2*Math.PI) new_theta -= 2*Math.PI;
		if(new_theta < -2*Math.PI) new_theta += 2*Math.PI;
		double x2 = x0 + Math.sin(new_theta)*1000;
		double y2 = y0 + Math.cos(new_theta)*1000;
		int[] new_point = new int[]{(int)x2, (int)y2};
		return new_point;
	}
	
	public int[] GenerateNewEndPoint(double x0, double y0) {
		int[] new_point;
		if(x0 < 245) {
			if(y0 < 245) new_point = new int[]{-100, -100};
			else new_point = new int[]{-100, 600};
		}
		else {
			if(y0 < 245) new_point = new int[]{600, -100};
			else new_point = new int[]{600, 600};
		}
		return new_point;
	}
}