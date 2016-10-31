/**
 * UAVMonitor provides the logic that allows for the display of vehicle video feeds
 * based on the selected vehicle. It also handles panning and zooming.
 * @authors Adithya Raghunathan, Ian Tay
 */
package reschu.game.view;
import reschu.game.model.UAV;
import reschu.game.model.Vehicle;

public class UAVMonitor {
	private PanelPayload prototype;
	private Vehicle activeUAV;
	private boolean displayEnabled;
	private int zoomLevel;

	private boolean panning;
	private int xDistToPan;
	private int yDistToPan;
	private int xPanOffset;
	private int yPanOffset;

	public static final int PAN_SPEED = 1;
	// Initialize UAVMonitor with Prototype object
	public UAVMonitor(PanelPayload proto) {
		prototype = proto;
		displayEnabled = true; // TODO (kill nerdo)
		zoomLevel = 1;
	}

	public void enableUAVFeed(Vehicle uav) {
		displayEnabled = true;
		
		if (uav != activeUAV){
			zoomLevel = 1; // zoom should be per-vehicle state
			panning = false;
			xDistToPan = 0;
			yDistToPan = 0;
			xPanOffset = 0;
			yPanOffset = 0;
		}
		activeUAV = uav;
	}

	public void disableUAVFeed(UAV uav) {
		displayEnabled = false;
		activeUAV = null;
	}

	// pass x and y coordinates to prototype
	public void setCoords() {
		if (!displayEnabled || activeUAV == null){
			return;
		}
		//System.out.println("Panning command received");
		
		//System.out.println("X coordinate" + activeUAV.getX() + "Y coordinate" + activeUAV.getY());
		int xAdded = 0;
		int yAdded = 0;
		if (panning) {
			// do not allow panning more than VIEWPORT_LENGTH/2
			if ((xPanOffset < PanelPayload.VIEWPORT_LENGTH/2 && xDistToPan >= 0) || (xPanOffset > - PanelPayload.VIEWPORT_LENGTH/2 && xDistToPan <= 0)) {			
				if (xDistToPan > 0) {
					xDistToPan -= PAN_SPEED;
					xAdded += PAN_SPEED;
				}
				else if (xDistToPan < 0) {
					xDistToPan += PAN_SPEED;
					xAdded -= PAN_SPEED;
				}
			}
			if ((yPanOffset < PanelPayload.VIEWPORT_LENGTH/2 && yDistToPan >= 0) || (yPanOffset > - PanelPayload.VIEWPORT_LENGTH/2 && yDistToPan <= 0)) {
				if (yDistToPan > 0) {
					yDistToPan -= PAN_SPEED;
					yAdded += PAN_SPEED;
				}
				else if (yDistToPan < 0) {
					yDistToPan += PAN_SPEED;
					yAdded -= PAN_SPEED;
				}
			}
			xPanOffset += xAdded;
			yPanOffset += yAdded;
			if (xDistToPan == 0 && yDistToPan == 0) panning = false;
		}
		prototype.setX(activeUAV.getGroundTruthX() + xPanOffset/zoomLevel);
		prototype.setY(activeUAV.getGroundTruthY() + yPanOffset/zoomLevel);
	}

	// pass direction to prototype based on vector to next way coordinate in UAV's path
	public void setVelocity() {
		if (!displayEnabled || activeUAV == null) return;
		if (activeUAV.getPathSize() == 0){
			prototype.setXDirection(0);
			prototype.setYDirection(0);
			return;
		}
		int[] nextPoint = activeUAV.getFirstPath();
		int currentX = activeUAV.getGroundTruthX();
		int currentY = activeUAV.getGroundTruthY();
		int nextX = nextPoint[0];
		int nextY = nextPoint[1];
		int xOffset = nextX - currentX;
		int yOffset = nextY - currentY;
		int xDir = xOffset > 0 ? 1 : xOffset < 0 ? -1 : 0;
		int yDir = yOffset > 0 ? 1 : yOffset < 0 ? -1 : 0;
		prototype.setXDirection(xDir);
		prototype.setYDirection(yDir);
	}

	// Determine angle to next waypoint and provide rotation angle accordingly
	public void setRotation() {
		if (!displayEnabled || activeUAV == null) return;
		int[] nextLocation = activeUAV.getFirstPath();
		double xDelta = nextLocation[0] - activeUAV.getX();
		double yDelta = nextLocation[1] - activeUAV.getY();
		
		yDelta *= -1;
		
		System.out.println("xDelta: " + xDelta);
		System.out.println("yDelta : " + yDelta);
		
		
		
		double angleToNorth;
		if (yDelta == 0) {
			angleToNorth = xDelta > 0 ? Math.PI/2 : - Math.PI/2; 
		}
		else if (yDelta > 0) {
			angleToNorth = Math.atan(xDelta/yDelta);
		}
		else {
			double posYDelta = -yDelta;
			if (xDelta >= 0) {
				angleToNorth = Math.PI - Math.atan(xDelta/posYDelta);
			}
			else {
				
				angleToNorth = Math.PI + Math.atan(xDelta/yDelta);
			}
		}
		angleToNorth *= 180;
		angleToNorth /= Math.PI;
		System.out.println("Angle for ship to rotate " + angleToNorth);
		prototype.setRotateAngle((float)-angleToNorth);
	}
	
	// Check if panning will cause	
	public void applyPan(int x, int y) {
		// check if enabled
		if (!displayEnabled || activeUAV == null) return;
		xDistToPan = x - PanelPayload.VIEWPORT_LENGTH/2;
		yDistToPan = y - PanelPayload.VIEWPORT_LENGTH/2;
		panning = true;
	}

	// Pass zoom command from GUI through PanelPayload (or PanelPayloadControls) to UAV
	/*
	public void setZoom(int level) {
		zoomLevel = level;
		prototype.setZoom(level);
	}
	
	public int getZoom() {
		return zoomLevel;
	}
	*/

}
