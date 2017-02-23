/**
 * UAVMonitor provides the logic that allows for the display of vehicle video feeds
 * based on the selected vehicle. It also handles panning and zooming.
 * @authors Adithya Raghunathan, Ian Tay
 */
package reschu.game.view;
import reschu.game.model.UAV;
import reschu.game.model.Vehicle;

public class UAVMonitor {
	private PanelPayload panelpayload;
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
		panelpayload = proto;
		// Disable initial video feed, changed from true to false
		displayEnabled = false; //far01 Disable initial video feed
		zoomLevel = 1;
		prevTargetPos = new int[2];
	}

	public void enableUAVFeed(Vehicle uav) {
		displayEnabled = true;
		// System.out.println("uav feed enabled.");
		if (uav != activeUAV){
			System.out.println("Active UAV changed to UAV " + uav.getIndex());
			zoomLevel = 1; // zoom should be per-vehicle state
			panning = false;
			xDistToPan = 0;
			yDistToPan = 0;
			xPanOffset = 0;
			yPanOffset = 0;
			panelpayload.resetCenterX();
			panelpayload.needToRotate = true;
			panelpayload.fetchImage(panelpayload.scaleMapXToViewport(uav.getGroundTruthX64()), panelpayload.scaleMapYToViewport(uav.getGroundTruthY64()));
			panelpayload.needToRecenter = true;
		}
		activeUAV = uav;
		/*
		if (activeUAV.getPathSize() > 0) {
			System.out.println("Recalculating rotation for UAV "+activeUAV.getIndex());
			setRotation();
			if (activeUAV.getPathSize() > 0) {
				prevTargetPos = activeUAV.getFirstPathGround();
			}
		}
		*/
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
			// System.out.println("Applying panning in x-direction of " + (xAdded > 0 ? 1 : - 1));
			panelpayload.setDisplayX(((float)(xAdded))/((float)(zoomLevel)));
			// System.out.println("Adding x-panning of " + xAdded);
		}
		else {
			// System.out.println("No horizontal panning, move vertically");
			panelpayload.setDisplayX(0); // If no horizontal panning required, displayX speed must be 0
		}
		
		if (yDistToPan != 0) {
			// System.out.println("Applying panning in y-direction of " + (yAdded > 0 ? 1 : - 1));
			panelpayload.setDisplayY(((float)(yAdded))/((float)(zoomLevel)));
			// System.out.println("Added y-panning of " + yAdded);
		}
		
		else {
			// System.out.println("No vertical panning");
			panelpayload.setDisplayY(0);
		}
		
		if (activeUAV.getPathSize() > 0) {
			// System.out.println("Incrementing display Y to -1 for northward movement");
			panelpayload.setDisplayY((int)Math.max(-PAN_SPEED, panelpayload.getDisplayY() -1)); // Limit max upward speed to PAN_SPEED
	
			int[] currentTargetPos = activeUAV.getFirstPathGround();
			// if (currentTargetPos[0] != prevTargetPos[0] || currentTargetPos[1] != prevTargetPos[1]) {
				// System.out.println("Detected change in waypoint!");
				setRotation();
				prevTargetPos = currentTargetPos;
				panelpayload.needToRotate = true;
			// }
		}
		else {
			if (yDistToPan == 0) {
				// System.out.println("No waypoint and no panning, setting Y to 0");
				panelpayload.unsetDisplayY();
			}
		}
		
		panelpayload.setX((float)activeUAV.getGroundTruthX64());
		panelpayload.setY((float)activeUAV.getGroundTruthY64());
		
		if (xPanOffset != 0) {
			panelpayload.applyPanX((double)xAdded/zoomLevel);
		}
		if (yPanOffset != 0) {
			panelpayload.applyPanY((double)yAdded/zoomLevel);
		}
		
		panelpayload.moveCenter();
	}

	// pass direction to prototype based on vector to next way coordinate in UAV's path
	public void setVelocity() {
		if (!displayEnabled || activeUAV == null) return;
		if (activeUAV.getPathSize() == 0){
			panelpayload.setXDirection(0);
			panelpayload.setYDirection(0);
			panelpayload.unsetDisplayY();
			// System.out.println("Setting y velocity to 0");
			return;
		}
		int[] nextPoint = activeUAV.getFirstPathGround();
		int currentX = activeUAV.getGroundTruthX();
		int currentY = activeUAV.getGroundTruthY();
		int nextX = nextPoint[0];
		int nextY = nextPoint[1];
		int xOffset = nextX - currentX;
		int yOffset = nextY - currentY;
		int xDir = xOffset > 0 ? 1 : xOffset < 0 ? -1 : 0;
		int yDir = yOffset > 0 ? 1 : yOffset < 0 ? -1 : 0;
		panelpayload.setXDirection(xDir);
		panelpayload.setYDirection(yDir);
	}

	// Determine angle to next waypoint and provide rotation angle accordingly
	public void setRotation() {
		if (!displayEnabled || activeUAV == null) return;		
		if (activeUAV.getPathSize() == 0) {
			System.out.println("This UAV has reached its target, setting rotation angle to 0 for north-facing");
			panelpayload.setRotateAngle(0);
		}
		
		/*
		else {
			System.out.println("Applying rotation");
			
			int[] nextLocation = activeUAV.getFirstPathGround();
			double xDelta = nextLocation[0] - activeUAV.getGroundTruthX();
			double yDelta = nextLocation[1] - activeUAV.getGroundTruthY();
			yDelta *= -1;
	
			System.out.println("xDelta: " + xDelta);
			System.out.println("yDelta: " + yDelta);
			
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
			System.out.println("Angle for UAV to rotate " + angleToNorth);
			panelpayload.setRotateAngle((float)angleToNorth);
		}
		*/
		
		double angleToNorth = activeUAV.getGtAngle64();
		angleToNorth *= 180;
		angleToNorth /= Math.PI;
		panelpayload.setRotateAngle((float)(angleToNorth-90.0));
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
