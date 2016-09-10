package reschu.constants;

public class MySize {
	
	final static public int SIZE_CELL = 2;
	final static public int SIZE_HALF_CELL = SIZE_CELL / 2;
	final static public int MAP_HEIGHT_PXL = 940;
	final static public int MAP_WIDTH_PXL = 810;
	final static public int OFFSET_HEIGHT = 20;
	final static public int OFFSET_WIDTH = 10;

	final static public int height = MAP_HEIGHT_PXL / SIZE_CELL - OFFSET_HEIGHT;
    final static public int width =  MAP_WIDTH_PXL / SIZE_CELL - OFFSET_WIDTH;
    
    final static public int SIZE_RULER 					= 10;
    final static public int SIZE_VEHICLE_WIDTH_PXL 		= 25;
    final static public int SIZE_VEHICLE_HEIGHT_PXL 	= 50;
    final static public int SIZE_VEHICLE_WIDTH_TMS_PXL 	= 20;
    final static public int SIZE_VEHICLE_HEIGHT_TMS_PXL = 40;
    final static public int SIZE_WAYPOINT_PXL 			= 10;
    final static public int SIZE_TARGET_PXL 			= 30;
    final static public int SIZE_HAZARD_1_PXL 			= 20;
    final static public int SIZE_HAZARD_2_PXL 			= 40;
    final static public int SIZE_HAZARD_3_PXL 			= 60;
    final static public int SIZE_HIGHLIGHT_PXL 			= 50;
    final static public int SIZE_UAV_COMM_PXL			= 60; 
    
}
