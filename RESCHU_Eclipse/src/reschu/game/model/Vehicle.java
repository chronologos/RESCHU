package reschu.game.model;

import java.util.LinkedList;
import java.util.Random;

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
	private Target target;
	private LinkedList<int[]> groundTruthPath = new LinkedList<int[]>();
	private Map map;
	private int index;
	private int velocity; 
	private GUI_Listener lsnr;
	private int status;
	private double vDamage;
	private Game g;
	//    private int velocity_buffer;
	//    private int velocity_scale;
	private int stuckCount;
	private boolean isStuck;
	private boolean intersect;
	private boolean isHijacked;

	private LinkedList<int[]> observedPath;

	/**
	 * Set the position of this vehicle (synchronized)
	 */
	public synchronized void setPos(int x, int y) { setGroundTruthX(x); setGroundTruthY(y); }

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
	public synchronized LinkedList<int[]> getGroundTruthPath() { return groundTruthPath;} 
 

	/**
	 * Add a waypoint to the path of this vehicle  (synchronized)
	 */
	public synchronized void addPath(int idx, int[] e) { groundTruthPath.add(idx, e);}

	/**
	 * Add a waypoint to the last path of this vehicle (synchronized)
	 */
	public synchronized void addPathLast(int[] e) { groundTruthPath.addLast(e); }

	/**
	 * Set a path of this vehicle  (synchronized)
	 */
	public synchronized void setPath(int idx, int[] e) { groundTruthPath.set(idx, e); };

	/**
	 * Get the size of a path of this vehicle  (synchronized)
	 */
	public synchronized int getPathSize() {return groundTruthPath.size(); }

	/**
	 * Get a coordinate of a waypoint of this vehicle  (synchronized)
	 */
	public synchronized int[] getPathAt(int idx) {return groundTruthPath.get(idx);}

	/**
	 * Remove a waypoint in the path of this vehicle  (synchronized)
	 */
	public synchronized void removePathAt(int idx) {groundTruthPath.remove(idx);}

	/**
	 * Get a coordinate at the first path of this vehicle  (synchronized)
	 */
	public synchronized int[] getFirstPathGround() {return groundTruthPath.getFirst(); }
	public synchronized int[] getFirstPathObserved() {return observedPath.getFirst();}

	/**
	 * Get a coordinate at the last path of this vehicle  (synchronized)
	 */
	public synchronized int[] getLastPath() {return groundTruthPath.getLast(); }

	/**
	 * Remove the first waypoint of path of this vehicle  (synchronized)
	 */
	public synchronized void removeFirstPath() {groundTruthPath.removeFirst(); }

	/**
	 * Returns a map that this vehicle is assigned to 
	 */
	public synchronized Map getMap() { return map; }

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
		setGroundTruthX(0); setGroundTruthY(0); 
		setTarget(null); 
		this.g = g;
		map=m; 
		setStatus(MyGame.STATUS_VEHICLE_STASIS); 
		vDamage = 0;
		//    	velocity_scale = MySpeed.SPEED_TIMER;
		//    	velocity_buffer = 0;
		stuckCount = 0;
		isStuck = false;
		intersect = false;
	}

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
					//PanelMsgBoard.Msg("Vehicle ["+index+"] is assigned to a target type ["+target.getMission()+"]");
					PanelMsgBoard.Msg("Vehicle ["+index+"] has been assigned to a target.");
					getMap().assignTarget(new int[]{x, y});
					assigned = true;
					break;
				}
				else if(getPayload()!=Vehicle.PAYLOAD_COM && boundaryCheck(x, y, target_pos) ) {
					//2008-04-05
					//UAV to grey target   ��You cannot assign a UAV to a grey target, please reassign��
					lsnr.showMessageOnTopOfMap("You cannot assign a UAV to a grey target, please reassign " + type + " " + index, 5);
				}
			}
			else {
				if( getPayload()!=Vehicle.PAYLOAD_COM && boundaryCheck(x, y, target_pos) ) {
					x = target_pos[0]; y = target_pos[1];
					if(type==Vehicle.TYPE_UUV && getMap().getListUnassignedTarget().get(i).getMission() != "SHORE") {
						//2008-04-05
						//UUV to land target (grey or red) ��You cannot assign a UUV to a land target, please reassign��
						lsnr.showMessageOnTopOfMap("You cannot assign a UUV to a land target, please reassign " + type + " " + index, 5);
						break;
					}
					setTarget(getMap().getListUnassignedTarget().get(i)); 
					PanelMsgBoard.Msg("Vehicle ["+index+"] has been assigned to a target.");
					getMap().assignTarget(new int[]{x, y});
					assigned = true;
					break;	
				}
				else if( getPayload()==Vehicle.PAYLOAD_COM && boundaryCheck(x, y, target_pos) ) {
					//2008-04-05
					//HALE to red target ��You cannot assign a HALE to a red target, please reassign��
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

	public  void changeGoal(int[] ex_goal, int x, int y) {
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
					PanelMsgBoard.Msg("Vehicle ["+index+"] has been assigned to a target.");
					getMap().assignTarget(new int[]{x, y});
					assigned = true;
					break;
				}
				else if(getPayload()!=Vehicle.PAYLOAD_COM && boundaryCheck(x, y, new_target_pos) ) {
					//2008-04-05
					//UAV to grey target   ��You cannot assign a UAV to a grey target, please reassign��
					lsnr.showMessageOnTopOfMap("You cannot assign a UAV to a grey target, please reassign " + type + " " + index, 5);
				}
			}
			else {
				if( getPayload()!=Vehicle.PAYLOAD_COM && boundaryCheck(x, y, new_target_pos) ) {
					x = new_target_pos[0]; y = new_target_pos[1];
					if(type==Vehicle.TYPE_UUV && getMap().getListUnassignedTarget().get(i).getMission() != "SHORE") {
						//2008-04-05
						//UUV to land target (grey or red) ��You cannot assign a UUV to a land target, please reassign��
						lsnr.showMessageOnTopOfMap("You cannot assign a UUV to a land target, please reassign " + type + " " + index, 5);
						break; 
					}
					setTarget(getMap().getListUnassignedTarget().get(i)); 
					PanelMsgBoard.Msg("Vehicle ["+index+"] has been assigned to a target.");
					getMap().assignTarget(new int[]{x, y});
					assigned = true;
					break;	
				}
				else if( getPayload()==Vehicle.PAYLOAD_COM && boundaryCheck(x, y, new_target_pos) ) {
					//2008-04-05
					//HALE to red target ��You cannot assign a HALE to a red target, please reassign��
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
		for( int i=0; i<getPathSize()-1; i++ ) 
			if( getPathAt(i)[0] == x && getPathAt(i)[1] == y ) {
				removePathAt(i);

			}
	}

	public void delWaypoint(int[] coordinate) {
		for( int i=0; i<getPathSize()-1; i++ ) 
			if( getPathAt(i) == coordinate ) {
				removePathAt(i);
			}	
	}

	public void changeWaypoint( int ex_x, int ex_y, int new_x, int new_y ) {
		if( new_x < 0 || new_x > MySize.width || new_y < 0 || new_y > MySize.height ) return;

		for( int i=0; i<getPathSize()-1; i++ )
			if( getPathAt(i)[0] == ex_x && getPathAt(i)[1] == ex_y ) {
				getPathAt(i)[0] = new_x; getPathAt(i)[1] = new_y;
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

	public void moveRandom(int i) {
		Random rnd = new Random();
		moveTo(rnd.nextInt(8));
	}

	/*
	public void moveHillClimbing() { 
		if( isStuck ) {
			moveTo(6);
			if(--stuckCount <= 0) isStuck = false;
			return;
		}

		double presentDistance, d=999999999;
		Random rnd = new Random();
		int direction = 8;    
		boolean stuck = true;

		presentDistance = getDistance(getGroundTruthX(), getGroundTruthY());

		for( int i=0; i<8; i++ ) {
			direction = rnd.nextInt(8);
			switch( direction ) {
			case 0: d = getDistance(getGroundTruthX()-1, getGroundTruthY()-1); 	break;
			case 1: d = getDistance(getGroundTruthX()-1, getGroundTruthY()); 		break;
			case 2:	d = getDistance(getGroundTruthX()-1, getGroundTruthY()+1); 	break;
			case 3: d = getDistance(getGroundTruthX(), getGroundTruthY()-1); 		break;
			case 4: d = getDistance(getGroundTruthX(), getGroundTruthY()+1); 		break;
			case 5: d = getDistance(getGroundTruthX()+1, getGroundTruthY()-1); 	break;
			case 6: d = getDistance(getGroundTruthX()+1, getGroundTruthY()); 		break;
			case 7: d = getDistance(getGroundTruthX()+1, getGroundTruthY()+1); 	break;
			}
			if( d < presentDistance && chkValidMove(direction)) { stuck = false; break; }
		}
		if( stuck ) {
			stuckCount++;
			if( stuckCount >= 5 ) isStuck = true;
		}    	
		else moveTo(direction);
	}
	*/
	
	public void moveBestFirst() {
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
		if (isHijacked){
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
			if( chkValidMove(direction) ) moveObservedTo(direction);
		}
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
		if( getPathSize()!=0 && (pos_x==getFirstPathObserved()[0] && pos_y==getFirstPathObserved()[1])) {
			if( getPathSize() == 1 && target != null) {
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
		int damage = 0;
		double d;
		int[] hazard_pos;
		for(int i=0; i<map.getListHazard().size(); i++ ) {
			hazard_pos = map.getListHazard().get(i);
			d = Math.sqrt( 
					Math.pow( (double)(xPosGroundTruth - hazard_pos[0]), 2.0 ) + 
					Math.pow( (double)(yPosGroundTruth - hazard_pos[1]), 2.0 ) )
					* MySize.SIZE_CELL;
			if(d <= MySize.SIZE_HAZARD_1_PXL ) damage += 50;
			else if(d < MySize.SIZE_HAZARD_2_PXL && d > MySize.SIZE_HAZARD_1_PXL) damage += 30;
			else damage += 0;

			if( d < 50d && d > 45d )  
				lsnr.EVT_Vehicle_Damaged(getIndex(), hazard_pos[0], hazard_pos[1]); 

		}
		// We don't decrease the speed of a vehicle anymore
		//setBuffer(damage);	
		vDamage += (double)(damage)/100;
		lsnr.Vehicle_Damaged_By_Hazard_Area_From_Vehicle(this);
	}

	//    private void setBuffer(int i) {
	//    	velocity_buffer += i;
	//    	if( velocity_buffer >= velocity_scale ) { 
	//    		velocity += velocity_scale; 
	//    		velocity_buffer -= velocity_scale;
	//    		lsnr.EVT_Vehicle_SpeedDecreased(index, velocity);
	//    	}
	//    }

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
		isHijacked = true;
		if (hackData == null) throw new IllegalArgumentException("Null hackData");
		String[] coordStrings = hackData.split(" ");
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
		int[] hackCoords = new int[]{xCoord, yCoord};
		observedPath = new LinkedList<int[]>(groundTruthPath);
		groundTruthPath.clear();
		groundTruthPath.add(hackCoords);
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


}
