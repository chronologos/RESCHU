package reschu.game.model;
 
import java.awt.Color;  
import java.util.*; 
import java.awt.event.*;
  
import javax.swing.Timer; 
 
import reschu.constants.*;
import reschu.database.DBWriter;
import reschu.game.controller.GUI_Listener;
import reschu.game.controller.Reschu;
import reschu.game.utils.FileReader;
import reschu.game.view.FrameEnd;
import reschu.game.view.FrameStart;
import reschu.game.view.PanelMsgBoard;

public class Game implements Runnable, ActionListener
{	    
    static public int TIME_TOTAL_GAME = 10 * 60 * MySpeed.SPEED_CLOCK ; 
    
    private double PROBABILITY_TARGET_VISIBILITY; // The higher, the more visible target    
    
    private int nTargetAreaTotal = (Reschu.tutorial()) ? MyGame.nTARGET_AREA_TOTAL_TUTORIAL : MyGame.nTARGET_AREA_TOTAL; 
    final private int[] DB_BY_PIXEL 
    	= new int[]{480,480,470,470,470, 470,460,460,450,450,
    				450,440,440,430,430, 430,420,410,410,410,
    				410,410,410,410,410, 410,410,420,420,420,
    				430,430,430,430,430, 430,430,420,420,420,
    				420,420,420,420,420, 420,430,430,440,440,
    				450,450,460,460,460, 460,470,470,480,480,
    				480,490,490,490,490, 500,500,500,510,510,
    				510,510,510,520,520, 520,520,520,520,530,
    				530,540,550,550,560, 560,570,570,580,580,
    				590,590,590,600};
    
    private int[] DB = new int[MySize.height];

    static public Calendar cal = Calendar.getInstance();
    
    private VehicleList vehicle_list;
    private PayloadList payload_list;
    
    public Map map;
    public int ex_pos_x, ex_pos_y;
    private Vehicle v; 
    private GUI_Listener lsnr;
    private Timer tmr_clock;    
    private int elapsedTime;
    private Vehicle currentPayloadVehicle;
    private boolean vehicleColorFlashFlag = true;
    private DBWriter dbWriter;
    private StructTargetNamePool[] targetNamePool;
    private boolean[] targetVisibilityPool;
    private int targetVisibilityPool_index;
    private int score;
    
    private Random rnd = new Random(); 
    
    public synchronized int getElapsedTime() {return elapsedTime;}
	
    public Game(GUI_Listener l, int scenario) { 
    	if( Reschu.train() ) Game.TIME_TOTAL_GAME *= 10; 
    	
    	if( Reschu._database ) {
	        new Thread(new Runnable() {
	            public void run() {
	            	dbWriter = new DBWriter();
	            	dbWriter.CreateTable(Reschu._username);
	            }
	        }).start();
    	}
    	
        lsnr = l;     		
        FrameStart frmStart = new FrameStart(lsnr);        
		frmStart.setSize(400,300);
		frmStart.setLocation(300,300);
		frmStart.setAlwaysOnTop(true);
		frmStart.setVisible(true);	 
		 
        rnd.setSeed( getSeedNum(scenario) ); 
    	setProbability_Target_Visibility(scenario);
    	
    	PanelMsgBoard.Msg("Game Started");
        tmr_clock = new Timer(MySpeed.SPEED_TIMER, this);
    	for(int i=0; i<DB.length; i++) DB[i] = DB_BY_PIXEL[Math.round(i/5)]/MySize.SIZE_CELL;
    	
        vehicle_list = new VehicleList(this);
        //@change-removed passing random object to PayloatList() 2008-04-01
        payload_list = new PayloadList(); 
        
        map = new Map(MySize.width, MySize.height, this, lsnr);
        elapsedTime = 0;
                
        targetNamePool = new StructTargetNamePool[nTargetAreaTotal];
        for(int i=0; i<nTargetAreaTotal; i++) targetNamePool[i] = new StructTargetNamePool(""+(char)(65+i));
        
        targetVisibilityPool_index = 0;
        targetVisibilityPool = new boolean[nTargetAreaTotal];
        setTargetVisibility();
        
        setMap();
        
        // normalize randomizer
        if (Reschu.tutorial()) {
        	for (int i = 0; i < 21; i++) rnd.nextInt(1);  
        	map.setHazardArea(rnd);
        	map.setTargetArea_TEMPORARY_FOR_TUTORIAL_BY_CARL(rnd);
        }
        else {    
	        map.setHazardArea(rnd);
	        try {
	        	map.setTargetArea(rnd);
	        } catch(UserDefinedException e) {
	        	e.printStackTrace();
	        }
        }
        
        setVehicle(scenario); 
        setPayload(); 
    }
    
    public void setListener(GUI_Listener l){ lsnr = l; }
    public DBWriter getDBWriter() {return dbWriter; }
    
    private int getSeedNum(int scenario) {
    	if( Reschu.tutorial() || Reschu.train() ) {
	    	switch( scenario ) {
		    	case 1:  
		    	case 2: 
		    	case 3: 
		    	case 4: 
		    		return 50; 
		    	default: return 0;
	    	}
    	}
    	else {
    		switch( scenario ) {
    		/* @changed 2008-06-29 Carl
    		 * 
		    	case 1: return 10; 
		    	case 2: return 20;
		    	case 3: return 30;
		    	case 4: return 40; 
		    	default: return 0;
		    */
				case 1:
				case 2:
				case 3:
				case 4:
					return 10;
				default:
					return 0;
    		}
    	}
    }
    private void setProbability_Target_Visibility(int scenario) {
    	switch( scenario ) {
			case 1: PROBABILITY_TARGET_VISIBILITY = 1; break;
			case 2: PROBABILITY_TARGET_VISIBILITY = 1; break;
			case 3: PROBABILITY_TARGET_VISIBILITY = 0.5; break;
			case 4: PROBABILITY_TARGET_VISIBILITY = 0.7; break;  
    		default: PROBABILITY_TARGET_VISIBILITY = 1; break;
    	}
    }
    public void setVehicle(int scenario) {
        try {
        	switch( scenario ) {
	        	case 1:
		        	vehicle_list.addVehicle(1, Vehicle.TYPE_UAV, "Fire Scout A", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(2, Vehicle.TYPE_UAV, "Fire Scout B", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(3, Vehicle.TYPE_UAV, "Fire Scout C", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(4, Vehicle.TYPE_UAV, "Fire Scout D", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);            
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(5, Vehicle.TYPE_UAV, "Fire Scout E", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	break;
	        	case 2:
		        	vehicle_list.addVehicle(1, Vehicle.TYPE_UAV, "Fire Scout A", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(2, Vehicle.TYPE_UUV, "Talisman A", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);            
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(3, Vehicle.TYPE_UAV, "Fire Scout B", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(4, Vehicle.TYPE_UUV, "Talisman B", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(5, Vehicle.TYPE_UAV, "Fire Scout C", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	break;
	        	case 3:
		        	vehicle_list.addVehicle(1, Vehicle.TYPE_UAV, "Fire Scout A", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(2, Vehicle.TYPE_UAV, "Global Hawk A", Vehicle.PAYLOAD_COM, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);            
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(3, Vehicle.TYPE_UAV, "Fire Scout B", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(4, Vehicle.TYPE_UAV, "Global Hawk B", Vehicle.PAYLOAD_COM, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(5, Vehicle.TYPE_UAV, "Fire Scout C", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		            break;	        		
	        	case 4:
		        	vehicle_list.addVehicle(1, Vehicle.TYPE_UAV, "Fire Scout A", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(2, Vehicle.TYPE_UUV, "Talisman A", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);            
		        	vehicle_list.addVehicle(3, Vehicle.TYPE_UAV, "Global Hawk", Vehicle.PAYLOAD_COM, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(4, Vehicle.TYPE_UAV, "Fire Scout B", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	if( !Reschu.tutorial() ) vehicle_list.addVehicle(5, Vehicle.TYPE_UUV, "Talisman B", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		            break;
	        	case 5:
	        		vehicle_list.addVehicle(1, Vehicle.TYPE_UAV, "Fire Scout A", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(2, Vehicle.TYPE_UUV, "Talisman A", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);            
		        	vehicle_list.addVehicle(3, Vehicle.TYPE_UAV, "Fire Scout B", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(4, Vehicle.TYPE_UAV, "Fire Scout C", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(5, Vehicle.TYPE_UUV, "Talisman B", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(6, Vehicle.TYPE_UAV, "Fire Scout D", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		            break;
	        	case 6:
	        		vehicle_list.addVehicle(1, Vehicle.TYPE_UAV, "Fire Scout A", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(2, Vehicle.TYPE_UUV, "Talisman A", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);            
		        	vehicle_list.addVehicle(3, Vehicle.TYPE_UAV, "Fire Scout B", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(4, Vehicle.TYPE_UAV, "Fire Scout C", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(5, Vehicle.TYPE_UUV, "Talisman B", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(6, Vehicle.TYPE_UAV, "Fire Scout D", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(7, Vehicle.TYPE_UAV, "Fire Scout E", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(8, Vehicle.TYPE_UUV, "Talisman", Vehicle.PAYLOAD_ISR, 1000/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);            
		        	vehicle_list.addVehicle(9, Vehicle.TYPE_UAV, "Fire Scout F", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(10, Vehicle.TYPE_UAV, "Fire Scout G", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		        	vehicle_list.addVehicle(11, Vehicle.TYPE_UAV, "Fire Scout H", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
		       		vehicle_list.addVehicle(12, Vehicle.TYPE_UAV, "Fire Scout", Vehicle.PAYLOAD_ISR, 500/MySpeed.SPEED_CONTROL, rnd, map, lsnr, this);
	        		break;
	 	        default:
		        	vehicle_list.addVehicle(1, Vehicle.TYPE_UAV, Vehicle.TYPE_UAV+"_1", "ISR", 500, rnd, map, lsnr, this);
	            	break;
        	}
            
        } catch (UserDefinedException e) {e.printStackTrace();}
    }
    
    public void setPayload() { 
    	int lineNum=-1, idx;
    	String vec_type, mis_type, stmt;
    	int[] loc;
    	String fileURL;
    	
    	if( Reschu.train() || Reschu.tutorial()) 
    		fileURL = MyURL.URL_PAYLOAD_INFO_TRAIN;
    	else
    		fileURL = MyURL.URL_PAYLOAD_INFO;
    	
    	FileReader fr = new FileReader(fileURL);
    	String aLine = "";
    	fr.openFile();
    	
    	while( (aLine = fr.readLineByLine()) != null ) {
    		lineNum++;
    		String[] a = aLine.split("#");
	    	if(a.length==6) {	    		
	    		if( !FileReader.isNumber(a[0].trim()) || !FileReader.isNumber(a[3].trim()) || !FileReader.isNumber(a[4].trim()) ) {
	    			System.err.println("ERROR: Failed to load payload at line (" + lineNum + "), check the numbers.");
	    			continue;
	    		}
	    		if( !Vehicle.isVehicleType(a[1].trim()) ) {
	    			System.err.println("ERROR: Failed to load payload at line (" + lineNum + "), check the vehicle type ("+a[1].trim()+").");
	    			continue;
	    		}
	    		if( !Target.isTargetType(a[2].trim()) ) {
	    			System.err.println("ERROR: Failed to load payload at line (" + lineNum + "), check the mission type ("+a[2].trim()+").");
	    			continue;
	    		}
	    		
	    		idx = Integer.parseInt(a[0].trim());
	    		vec_type = a[1].trim();
	    		mis_type = a[2].trim();
	    		loc = new int[]{Integer.parseInt(a[3].trim()), Integer.parseInt(a[4].trim())};	    		
	    		stmt = a[5].trim();	    		
	    			    		
	    		payload_list.addPayload(idx, vec_type, mis_type, loc, stmt);
	    		//System.out.println(idx +", " + vec_type +", " +mis_type +", " +loc[0] +", " +loc[1] + ", " +stmt);
	    	} else {
	    		System.err.println("ERROR: Failed to load payload at line (" + lineNum + "), check the delimeter.");
	    	}
    	}
    	fr.closeFile();
    }
    
    private void setMap() {
        for(int i=0; i<MySize.height; i++) {
        	for(int j=0; j<DB[i]; j++) map.setCellType(j, i, MyGame.LAND);
        	map.setCellType(DB[i], i, MyGame.SEASHORE);
        	for(int j=DB[i]+1; j<MySize.width; j++) map.setCellType(j, i, MyGame.SEA);
        }
    }     
    
    public void run() {    	
    	AutoTargetAssignAll();
        tmr_clock.start();
        
        if( !Reschu._database ) return; 
        
        if( Reschu.expermient() ) {
            lsnr.EVT_System_GameStart();
            
        	// below DBwrites are different from writing to the user's table.
        	// we record the login info to the USER table, 
        	// which contains infos of all the users
	        getDBWriter().UserTable_SetGameStart(Reschu._username);
	        getDBWriter().UserTable_SetTime(Reschu._username);
        }  
    }
    
    public boolean isRunning() {
    	return tmr_clock.isRunning();
    }
    
    public void stop() {
    	tmr_clock.stop();    	
    }
    
    public VehicleList getVehicleList(){ return vehicle_list; }    
    public PayloadList getPayloadList(){ return payload_list; }
    
    public void vehicle_location_change(){ lsnr.vehicle_location_changed(); }
 
    public Vehicle Vechicle_Location_Check(int x, int y)
    {
    	for( int i=0; i<vehicle_list.size(); i++ ) {
    		int v_x = vehicle_list.getVehicle(i).getX();
    		int v_y = vehicle_list.getVehicle(i).getY();
    		int w = Math.round(MySize.SIZE_VEHICLE_WIDTH_PXL/MySize.SIZE_CELL);
    		int h = Math.round(MySize.SIZE_VEHICLE_HEIGHT_PXL/MySize.SIZE_CELL);
    		for( int j=-w; j<w; j++)
    			for( int k=-h; k<h; k++)
    				if( v_x == x+j && v_y == y+k) return vehicle_list.getVehicle(i);
    	}
    	return null;    		
    }
    
    public StructSelectedPoint Vehicle_Goal_Check(int x, int y)
    {
    	Vehicle v;
    	int w = Math.round(MySize.SIZE_WAYPOINT_PXL/MySize.SIZE_CELL);
    	for( int i=0; i<vehicle_list.size(); i++ ) {
    		v = vehicle_list.getVehicle(i);
    		if( v.hasGoal() ) {
        		int w_x = v.getPath().get(v.getPathSize()-1)[0];
        		int w_y = v.getPath().get(v.getPathSize()-1)[1];
        		for( int m=-w; m<w; m++)        
        			for( int n=-w; n<w; n++)
        				if( w_x == x+m && w_y == y+n) return new StructSelectedPoint(v, w_x, w_y, 0); // 0 = no meaning    			
    		}
    	}
    	return null;
    }
        
    public StructSelectedPoint Vehicle_Waypoint_Check(int x, int y)
    {
    	Vehicle v;
    	int w = Math.round(MySize.SIZE_WAYPOINT_PXL/MySize.SIZE_CELL);
    	for( int i=0; i<vehicle_list.size(); i++ ) {
    		v = vehicle_list.getVehicle(i);
    		if( v.getPath().size() > 1) {    			    			
    			for( int j=0; j<v.getPath().size()-1; j++) {
        			int w_x = v.getPath().get(j)[0];
        			int w_y = v.getPath().get(j)[1];
        			for( int m=-w; m<w; m++)        
        				for( int n=-w; n<w; n++)
        					if( w_x == x+m && w_y == y+n) return new StructSelectedPoint(v, w_x, w_y, j);
    			}
    		}
    	}
    	return null;
    }
    
    public void addScore(int i) { score += i; }
    public int getScore() { return score; }
    
    public Vehicle getCurrentPayloadVehicle() { return currentPayloadVehicle; }
    public void setCurrentPayloadVehicle(Vehicle v) { currentPayloadVehicle = v; }
    public void clearCurrentPayloadVehicle() { currentPayloadVehicle = null; }
    
    private void AutoTargetAssignAll() 
    {
    	Vehicle v;
    	for( int i=0; i<vehicle_list.size(); i++ ) {
    		v = vehicle_list.getVehicle(i);
    		AutoTargetAssign(v);	
    	} 
    } 
    
    public void AutoTargetAssign(Vehicle v) 
    {
    	if( v.getPath().size() == 0 && map.getAvailableTarget() > 0 ) {    			
			Target target = null;
			
			if( v.getType() == Vehicle.TYPE_UUV ) {
				for(int i=0; i<map.getListUnassignedTarget().size(); i++) {
					if( map.getListUnassignedTarget().get(i).getType()==v.getType() && map.getListUnassignedTarget().get(i).isVisible() ) {
						target = map.getListUnassignedTarget().get(i);
						v.addGoal(target.getPos()[0], target.getPos()[1]);
						if( elapsedTime != 0 ) lsnr.EVT_GP_SetGP_by_System(v.getIndex(), target.getName());
						break;
					}
				} 
			}
			else if( v.getPayload() == Vehicle.PAYLOAD_COM) { 
				for(int i=0; i<map.getListUnassignedTarget().size(); i++) {
					if( !map.getListUnassignedTarget().get(i).isVisible() ) {
						target = map.getListUnassignedTarget().get(i); 
						v.addGoal(target.getPos()[0], target.getPos()[1]);
						if( elapsedTime != 0 ) lsnr.EVT_GP_SetGP_by_System(v.getIndex(), target.getName());
						break;
					}
				}
			}
			else {
				for(int i=0; i<map.getListUnassignedTarget().size(); i++) {
					if( map.getListUnassignedTarget().get(i).isVisible() ) {
						target = map.getListUnassignedTarget().get(i);
						v.addGoal(target.getPos()[0], target.getPos()[1]);
						if( elapsedTime != 0 ) lsnr.EVT_GP_SetGP_by_System(v.getIndex(), target.getName());
						break;
					}
				} 
			}
    	}	
    } 
    
    public void actionPerformed(ActionEvent e) {
    	elapsedTime += MySpeed.SPEED_TIMER;
    	if( elapsedTime >= Game.TIME_TOTAL_GAME ) {
    		stop(); // stops a timer
            
    		if( Reschu._database ) {
	            getDBWriter().ScoreTable_SetScore(Reschu._username, getScore());
	            getDBWriter().UserTable_SetGameFinish(Reschu._username);
            }
	    	
    		lsnr.Game_End();    		
    		
    		// TEMPORARY SOLUTION
            FrameEnd frmEnd = new FrameEnd(lsnr);
            frmEnd.setSize(400,300);
            frmEnd.setLocation(300,300);
            frmEnd.setVisible(true);	
    	}
    	    	
    	vehicleColorFlashFlag = !vehicleColorFlashFlag;
    	
        for( int i = 0; i < vehicle_list.size(); i++) {
        	v = vehicle_list.getVehicle(i); 
            if( v.getPath().size() != 0 && (elapsedTime % v.getVelocity() == 0) ) {v.moveHillClimbing();}
        }
        vehicle_location_change();

        // Update pnlControl's "ENGAGE" button
        if( elapsedTime % MySpeed.SPEED_CLOCK == 0 ) lsnr.Clock_Tick(elapsedTime);
        
        // Pending Vehicle's Flashing Color
        if( vehicleColorFlashFlag ) MyColor.COLOR_VEHICLE_PENDING = new Color(128,224,255,255);
        else MyColor.COLOR_VEHICLE_PENDING = new Color(228,124,155,255);
          
        // Update Hazard Area
        int haUpdateSpeed = (Reschu.tutorial()) ? MySpeed.SPEED_CLOCK_HAZARD_AREA_UPDATE_TUTORIAL 
        										: MySpeed.SPEED_CLOCK_TARGET_AREA_UPDATE;
        if( elapsedTime % haUpdateSpeed == 0 ) { map.delHazardArea(rnd, 1); map.setHazardArea(rnd); }
        
        // Update Targets
        try {
        	if( elapsedTime % MySpeed.SPEED_CLOCK_TARGET_AREA_UPDATE == 0 ) { 
        		map.garbageTargetCollect(); 
        		map.setTargetArea(rnd); 
        	}
        } catch (UserDefinedException ex) {
        	ex.printStackTrace();
        }
        
        // Auto Target Assign
        // Problem - Should avoid when a vehicle's status is set to PENDING
        //if( elapsedTime % MySpeed.SPEED_CLOCK_AUTO_TARGET_ASSIGN_UPDATE == 0) { AutoTargetAssignAll(); }
        
        // Check Vehicle - Hazard Area
        if( elapsedTime % MySpeed.SPEED_CLOCK_DAMAGE_CHECK == 0 ) for(int i=0; i<vehicle_list.size(); i++) vehicle_list.getVehicle(i).chkHazardArea();
    }
    
    public void setTargetUsed(String name, boolean isUsed) {
    	for( int i=0; i<nTargetAreaTotal; i++ ) 
    		if( targetNamePool[i].getName() == name ) targetNamePool[i].setUsed(isUsed);
    }
    
    public String getEmptyTargetName() {
    	for( int i=0; i<nTargetAreaTotal; i++ )
    		if( !targetNamePool[i].isUsed() ) {
    			targetNamePool[i].setUsed(true);
    			return targetNamePool[i].getName();
    		}
    	return "X";	// MAKE SURE THIS NEVER HAPPENS!! IT SHOULDN'T BE HAPPEN!
    }
    
    private void setTargetVisibility() {    	
    	int nVisibleTarget = (int)Math.round(nTargetAreaTotal * PROBABILITY_TARGET_VISIBILITY);
    	int nInvisibleTarget = nTargetAreaTotal - nVisibleTarget; 
    	int factor = 2;
    	for(int i=0; i<nTargetAreaTotal; i++ ) {
    		if( nInvisibleTarget > 0 && i % factor == 0 ) {
    			targetVisibilityPool[i] = false;
    			nInvisibleTarget--;
    		}
    		else targetVisibilityPool[i] = true; 
    	}    	
    }
    
    public boolean getTargetVisibility() {
    	if( targetVisibilityPool_index+1 == nTargetAreaTotal ) targetVisibilityPool_index = 0;
    	else targetVisibilityPool_index++;
    	return targetVisibilityPool[targetVisibilityPool_index]; 
    }
    
    public static double getDistance(double x1, double y1, double x2, double y2) {
    	return Math.sqrt( Math.pow((double)(x2 - x1), 2.0) + Math.pow((double)(y2 - y1), 2.0) );
    } 
    public static double getDistance(int x1, int y1, int x2, int y2) {
    	return Math.sqrt( Math.pow((double)(x2 - x1), 2.0) + Math.pow((double)(y2 - y1), 2.0) );
    }
}