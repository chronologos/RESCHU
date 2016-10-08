package reschu.game.model;


public class StructSelectedPoint {
	private Vehicle v;
	private int[] point;
	private int idx;
	
	StructSelectedPoint(Vehicle v, int x, int y, int i) {
		this.v = v;
		point = new int[]{x, y};
		idx = i;
	}
	public Vehicle getV() { return v; }
	public int getX() { return point[0]; } 
	public int getY() { return point[1]; }
	public int getIdx() { return idx; }
}
