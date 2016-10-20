package reschu.game.view;
import reschu.game.model.UAV;

public class UAVMonitor {
	//private UAV uav;
	private Prototype prototype;
	private UAV activeUAV;	
	private boolean displayEnabled;
	private int zoomLevel;
	
	// Initialize UAVMonitor with Prototype object
	public UAVMonitor(Prototype proto) {
		prototype = proto;
		displayEnabled = false;
		zoomLevel = 1;
	}
	
	public void enableUAVFeed(UAV uav) {
		displayEnabled = true;
		activeUAV = uav;
	}
	
	public void disableUAVFeed(UAV uav) {
		displayEnabled = false;
		activeUAV = null;
	}
	
	// pass x and y coordinates to prototype
	public void passCoords() {
		if (!displayEnabled || activeUAV == null) return;
		prototype.setX(activeUAV.getX());
		prototype.setY(activeUAV.getY());
	}
	
	// pass direction to prototype based on vector to next way coordinate in UAV's path
	public void setVelocity() {
		if (!displayEnabled || activeUAV == null) return;
		int[] nextPoint = activeUAV.getFirstPath();
		int currentX = activeUAV.getX();
		int currentY = activeUAV.getY();
		int nextX = nextPoint[0];
		int nextY = nextPoint[1];
		int xOffset = nextX - currentX;
		int yOffset = nextY - currentY;
		int xDir = xOffset > 0 ? 1 : xOffset < 0 ? -1 : 0;
		int yDir = yOffset > 0 ? 1 : yOffset < 0 ? -1 : 0;
		//int dirCode = yDir * 3 + xDir;
		prototype.setXDirection(xDir);
		prototype.setYDirection(yDir);
	}
	
	// Check if panning will cause
	public boolean applyPan() {
		
	}
	
	// Pass zoom command from GUI through PanelPayload (or PanelPayloadControls) to UAV
	public void setZoom(int level) {
		zoomLevel = level;
		prototype.setZoom(level);
	}
	
	public int getZoom() {
		return zoomLevel;
	}
	
	
}
