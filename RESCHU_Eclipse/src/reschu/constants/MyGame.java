package reschu.constants;

import reschu.game.controller.Reschu;

public class MyGame {
	final static public String VERSION_INFO = "RESCHU VER 2.0.0";	
    
    final static public int LAND = 0;
    final static public int SEASHORE = 1;
    final static public int SEA = 2;
	
	final static public int STATUS_VEHICLE_STASIS   = 0;
	final static public int STATUS_VEHICLE_MOVING   = 1;
	final static public int STATUS_VEHICLE_PENDING  = 2;
	final static public int STATUS_VEHICLE_PAYLOAD  = 3;
	final static public int STATUS_VEHICLE_ATTACKED = 4;
	final static public int STATUS_VEHICLE_LOST 	= 5;
 
    final static public int nHAZARD_AREA = 21; // original 14
    final static public int nHAZARD_AREA_TUTORIAL = 3;
    
    final static public int nTARGET_AREA_LAND = 2; // original 8
    final static public int nTARGET_AREA_LAND_TUTORIAL = 7;
    final static public int nTARGET_AREA_SHORE = 4;
    final static public int nTARGET_AREA_COMM = 0;
    final static public int nTARGET_AREA_MORE = 3;
    final static public int nTARGET_AREA_TOTAL = nTARGET_AREA_LAND + nTARGET_AREA_SHORE + nTARGET_AREA_COMM;
    final static public int nTARGET_AREA_TOTAL_TUTORIAL = nTARGET_AREA_LAND_TUTORIAL + nTARGET_AREA_SHORE + nTARGET_AREA_COMM;
    final static public int nTARGET_AREA_TOTAL_HIGH = nTARGET_AREA_LAND + nTARGET_AREA_SHORE + nTARGET_AREA_MORE;
    
    final static public double MIN_HACK_DISTANCE = 10.0;
    
    // final static public String AttackFile = (Reschu.low_taskload())? "AttackFile_Low.txt" : "AttackFile_High.txt";
    final static public String AttackFile = (Reschu.low_taskload())? ((Reschu.practice_mode())? "AttackFile_Practice_Low.txt" : "AttackFile_Test_Low.txt") :
    																 ((Reschu.practice_mode())? "AttackFile_Practice_High.txt" : "AttackFile_Test_High.txt");
    
    final static public boolean TargetDataBase = true;
    // final static public boolean TargetDataBase = false;
    
    final static public int TOTAL_SECOND = 900; // total time (in second) for one experiment
    // final static public int TOTAL_SECOND = 9000;
}