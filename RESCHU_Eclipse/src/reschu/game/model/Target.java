package reschu.game.model;

public class Target {  
	final static public String MISSION_SHORE = "SHORE";
	final static public String MISSION_LAND = "LAND";
	
	private int[] pos;
	private String mission; // SHORE, LAND
	private String type;  
	private boolean done;
	private String name;
	private boolean visible;
	
	private int req_zoom;
	private int task_ans;
	 
	Target() {}
	Target(String s, int[] p, String m, String t, boolean v, int r, int a) {
		name = s;
		pos = p;
		mission = m;
		type = t;
		done = false;
		visible = true;
		// visible = v;
		// for counting task data base
		req_zoom = r;
		task_ans = a;
	}
	Target(String s, int[] p, String m, String t, boolean v) {
		name = s;
		pos = p;
		mission = m;
		type = t;
		done = false;
		visible = v;
		req_zoom = 3;
		task_ans = 0;
	}
	
	public void setPos(int[] p) { pos = p; }
	public void setMission(String m) { mission = m; }
	public void setType(String t) { type = t; }	
	public void setName(String i) { name = i; }	
	
	public int[] getPos() {return pos;}
	public String getMission() {return mission;}
	public String getType() {return type;}
	public String getName() {return name;}
	
    public void setDone() {done = true;}
    public boolean isDone() {return done;}
    
    public void setVisible(boolean v) {visible = v;}
    public boolean isVisible() {return visible;}
    
    public int getRequireZoom() {return req_zoom;}
    public int getTaskAnswer() {return task_ans;}
    
    static public boolean isTargetType(String s) {
    	if( s.equals(MISSION_LAND) || s.equals(MISSION_SHORE) ) return true;
    	return false;
    }
}
