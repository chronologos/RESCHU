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
	private int[] prevTargetPos;

	public static final int PAN_SPEED = 5;
	// Initialize UAVMonitor with Prototype object
	public UAVMonitor(PanelPayload proto) {
		prototype = proto;
		displayEnabled = true;
		zoomLevel = 1;
		prevTargetPos = new int[2];
	}

	public void enableUAVFeed(Vehicle uav) {
		displayEnabled = true;
		System.out.println("uav feed enabled.");
		if (uav != activeUAV){
			System.out.println("ACTIVE UAV CHANGED TO " + activeUAV);
			zoomLevel = 1; // zoom should be per-vehicle state
			panning = false;
			xDistToPan = 0;
			yDistToPan = 0;
			xPanOffset = 0;
			yPanOffset = 0;
			prototype.resetCenterX();
			prototype.needToRotate = true;
			prototype.fetchImage(prototype.scaleMapXToViewport(uav.getGroundTruthX()), prototype.scaleMapYToViewport(uav.getGroundTruthY()));
			prototype.needToRecenter = true;
		}
		activeUAV = uav;
		//if (activeUAV.getPathSize() > 0) {
			System.out.println("Recalculating rotation for new UAV");
			setRotation();
			if (activeUAV.getPathSize() > 0) {
				prevTargetPos = activeUAV.getFirstPath();
			}
		//}
	}

	public void disableUAVFeed() {
		displayEnabled = false;
		activeUAV = null;
	}

	// pass x and y coordinates to prototype
	public void setCoords() {
		if (!displayEnabled || activeUAV == null){
			return;
		}
		int xAdded = 0;
		int yAdded = 0;
		if (panning) {
			// do not allow panning more than VIEWPORT_LENGTH/2
			if ((xPanOffset < PanelPayload.VIEWPORT_LENGTH/2 && xDistToPan >= 0) || (xPanOffset > - PanelPayload.VIEWPORT_LENGTH/2 && xDistToPan <= 0)) {			
				if (xDistToPan > 0) {
					if (xDistToPan >= PAN_SPEED) { 
						xDistToPan -= PAN_SPEED;
						xAdded += PAN_SPEED;
					}
					else {
						xAdded += xDistToPan;
						xDistToPan = 0;
					}
				}
				else if (xDistToPan < 0) {
					if (xDistToPan <= - PAN_SPEED) {
						xDistToPan += PAN_SPEED;
						xAdded -= PAN_SPEED;
					}
					else {
						xAdded -= xDistToPan;
						xDistToPan = 0;
					}
				}
			}
			if ((yPanOffset < PanelPayload.VIEWPORT_LENGTH/2 && yDistToPan >= 0) || (yPanOffset > - PanelPayload.VIEWPORT_LENGTH/2 && yDistToPan <= 0)) {
				if (yDistToPan > 0) {
					if (yDistToPan >= PAN_SPEED) {
						yDistToPan -= PAN_SPEED;
						yAdded += PAN_SPEED;
					}
					else {
						yAdded += yDistToPan;
						yDistToPan = 0;
					}
				}
				else if (yDistToPan < 0) {
					if (yDistToPan <= - PAN_SPEED) {
						yDistToPan += PAN_SPEED;
						yAdded -= PAN_SPEED;
					}
					else {
						yAdded -= yDistToPan;
						yDistToPan = 0;
					}
				}
			}
			xPanOffset += xAdded;
			yPanOffset += yAdded;
			if (xDistToPan == 0 && yDistToPan == 0) panning = false;
		}
		
		if (xDistToPan != 0) {
			System.out.println("Applying panning in x-direction of " + (xAdded > 0 ? 1 : - 1));
			prototype.setDisplayX(xAdded/zoomLevel);
			System.out.println("Adding x-panning of " + xAdded);
		}
		else {
			System.out.println("No horizontal panning, move vertically");
			prototype.setDisplayX(0); // If no horizontal panning required, displayX speed must be 0
		}
		
		if (yDistToPan != 0) {
			System.out.println("Applying panning in y-direction of " + (yAdded > 0 ? 1 : - 1));
			prototype.setDisplayY(yAdded/zoomLevel);
			System.out.println("Added y-panning of " + yAdded);
		}
		
		else {
			System.out.println("No vertical panning");
			prototype.setDisplayY(0);
		}
		
		if (activeUAV.getPathSize() > 0) {
			System.out.println("Incrementing display Y to -1 for northward movement");
			prototype.setDisplayY((int)Math.max(-PAN_SPEED, prototype.getDisplayY() -1)); // Limit max upward speed to PAN_SPEED
	
			int[] currentTargetPos = activeUAV.getFirstPath();
			if (currentTargetPos[0] != prevTargetPos[0] || currentTargetPos[1] != prevTargetPos[1]) {
				System.out.println("Detected change in waypoint!");
				setRotation();
				prevTargetPos = currentTargetPos;
				prototype.needToRotate = true;
			}
		
		}
		else {
			if (yDistToPan == 0) {
				System.out.println("No waypoint and no panning, setting Y to 0");
				prototype.unsetDisplayY();
			}
			
		}
		
		//prototype.setX(activeUAV.getGroundTruthX() + xPanOffset/zoomLevel);
		//prototype.setY(activeUAV.getGroundTruthY() + yPanOffset/zoomLevel);
		prototype.setX(activeUAV.getGroundTruthX());
		prototype.setY(activeUAV.getGroundTruthY());
		
		if (xPanOffset != 0) {
			//prototype.applyPanX(xPanOffset/zoomLevel);
			prototype.applyPanX((float)xAdded/zoomLevel);
		}
		if (yPanOffset != 0) {
			//prototype.applyPanY(yPanOffset/zoomLevel);
			prototype.applyPanY((float)yAdded/zoomLevel);
		}

		
		prototype.moveCenter();

		/*
		if (activeUAV.getPathSize() > 0) {
			int[] currentTargetPos = activeUAV.getFirstPath();
			if (currentTargetPos[0] != prevTargetPos[0] || currentTargetPos[1] != prevTargetPos[1]) {
				System.out.println("Detected change in waypoint!");
				setRotation();
				prevTargetPos = currentTargetPos;
				prototype.needToRotate = true;
			}
		}
		*/
	}

	// pass direction to prototype based on vector to next way coordinate in UAV's path
	public void setVelocity() {
		if (!displayEnabled || activeUAV == null) return;
		if (activeUAV.getPathSize() == 0){
			prototype.setXDirection(0);
			prototype.setYDirection(0);
			prototype.unsetDisplayY();
			System.out.println("Setting y velocity to 0");
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
		if (activeUAV.getPathSize() == 0) {
			System.out.println("This UAV has reached its target, setting rotation angle to 0 for north-facing");
			prototype.setRotateAngle(0);
		}
		
		System.out.println("Applying rotation!");
		
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
		prototype.setRotateAngle((float)angleToNorth);
	}

	// Check if panning will cause	
	public void applyPan(int x, int y) {
		// check if enabled
		if (!displayEnabled || activeUAV == null) return;
		xDistToPan = x - PanelPayload.VIEWPORT_LENGTH/2;
		yDistToPan = y - PanelPayload.VIEWPORT_LENGTH/2;
		panning = true;
	}

	public boolean isEnabled(){
		return displayEnabled;
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
