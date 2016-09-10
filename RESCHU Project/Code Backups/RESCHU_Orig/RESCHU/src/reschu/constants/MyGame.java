package reschu.constants;
 
public class MyGame { 
	final static public String VERSION_INFO = "RESCHU VER 1.0.0";	
    
    final static public int LAND = 0;
    final static public int SEASHORE = 1;
    final static public int SEA = 2;
	
	final static public int STATUS_VEHICLE_STASIS  = 0;
	final static public int STATUS_VEHICLE_MOVING  = 1;
	final static public int STATUS_VEHICLE_PENDING = 2;
	final static public int STATUS_VEHICLE_PAYLOAD = 3;
 
    final static public int nHAZARD_AREA = 14;
    final static public int nHAZARD_AREA_TUTORIAL = 3;
    
    final static public int nTARGET_AREA_LAND = 4;
    final static public int nTARGET_AREA_LAND_TUTORIAL = 7;
    final static public int nTARGET_AREA_SHORE = 3; 
    final static public int nTARGET_AREA_COMM = 0;   
    final static public int nTARGET_AREA_TOTAL = nTARGET_AREA_LAND + nTARGET_AREA_SHORE + nTARGET_AREA_COMM;
    final static public int nTARGET_AREA_TOTAL_TUTORIAL = nTARGET_AREA_LAND_TUTORIAL + nTARGET_AREA_SHORE + nTARGET_AREA_COMM;
    
}

