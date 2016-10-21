package reschu.game.utils;

import java.util.LinkedList;
import java.util.List;

import reschu.game.model.Vehicle;


public class SituationAwareness {
	
	/**
	 * Check if any of the vehicle's path intersects with hazard areas
	 * @param v A vehicle
	 * @param t A list of hazard areas
	 * @param d Radius of a hazard area
	 * @return True if intersects
	 */
	public static int checkIntersect(final Vehicle v, final List<int[]> list, final int d) {
		LinkedList<int[]> path = new LinkedList<int[]>();		
		path.addFirst(new int[]{v.getX(), v.getY()});	// add the vehicle's position to the first to the path
		for( int i=0; i<v.getPathSize(); i++ ) 
			path.addLast(v.getPathAt(i));
		
		for( int i=0; i<path.size()-1; i++ ) 
			for( int j=0; j<list.size(); j++ ) 
				if( getDistance(path.get(i), path.get(i+1), list.get(j)) <= 15.0d ) // pixel d doesn't work. so I just hardcode here
				{
					return j;
				}
		return -1;
	}
	
	/**
	 * Computes a distance between a point C and a line segment AB
	 * http://www.codeguru.com/forum/showthread.php?t=194400
	 * @param A  
	 * @param B
	 * @param C
	 * @return
	 */
	private static double getDistance(final int[] A, final int[] B, final int[] C) {
		
		double ax = A[0], ay = A[1], bx = B[0], by = B[1], cx = C[0], cy = C[1]; 
		
		double r_numerator = (cx-ax)*(bx-ax) + (cy-ay)*(by-ay);
		double r_denomenator = (bx-ax)*(bx-ax) + (by-ay)*(by-ay);
		double r = r_numerator / r_denomenator; 
		    
		//double px = ax + r*(bx-ax);
		//double py = ay + r*(by-ay);
		    
		double s = ((ay-cy)*(bx-ax)-(ax-cx)*(by-ay) ) / r_denomenator;

		double distanceLine = Math.abs(s)*Math.sqrt(r_denomenator);
		
		//double xx = px;
		//double yy = py;

		double distanceSegment;
		
		if ( (r >= 0) && (r <= 1) ) {
		    distanceSegment = distanceLine;
		}
		else {
		    double dist1 = (cx-ax)*(cx-ax) + (cy-ay)*(cy-ay);
		    double dist2 = (cx-bx)*(cx-bx) + (cy-by)*(cy-by);
		    if (dist1 < dist2) {
		    	//xx = ax; yy = ay;
				distanceSegment = Math.sqrt(dist1);
			} else {
				//xx = bx; yy = by;
				distanceSegment = Math.sqrt(dist2);		
			}
		}
		//System.out.println("DISTANCE AB-C ("+ax+","+ay+")("+bx+","+by+")-("+cx+","+cy+") = " + distanceSegment);
		return distanceSegment;
	}
}
