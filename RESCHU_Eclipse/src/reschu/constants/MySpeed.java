package reschu.constants;

public class MySpeed {
    final static public int VELOCITY = 1;
	final static public int SPEED_CONTROL = 5;
    final static public int SPEED_TIMER = 5000 / SPEED_CONTROL; // UAV speed, originally 500
    final static public int SPEED_CLOCK = 10000 / SPEED_CONTROL; // total time range, originally 1000
    final static public int SPEED_CLOCK_DAMAGE_CHECK = SPEED_CLOCK * 3;
    final static public int SPEED_CLOCK_HAZARD_AREA_UPDATE = SPEED_CLOCK * 10;
    final static public int SPEED_CLOCK_HAZARD_AREA_UPDATE_TUTORIAL = SPEED_CLOCK * 80;
    final static public int SPEED_CLOCK_TARGET_AREA_UPDATE = SPEED_CLOCK * 5;
    final static public int SPEED_CLOCK_AUTO_TARGET_ASSIGN_UPDATE = SPEED_CLOCK * 15;
}
