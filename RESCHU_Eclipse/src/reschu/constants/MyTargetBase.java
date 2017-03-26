package reschu.constants;

public class MyTargetBase {
	final static public int[][] TargetBase = {{50, 50, 0, 0},
											  {100, 100, 0, 0},
											  {150, 150, 0, 0},
											  {200, 200, 0, 0},
											  {250, 250, 0, 0},
											  {300, 300, 0, 0},
											  {350, 350, 0, 0},
											  {400, 400, 0, 0},
											  {450, 450, 0, 0},
											  {0, 450, 0, 0},
											  {50, 450, 0, 0},
											  {100, 450, 0, 0},
											  {150, 450, 0, 0},
											  {200, 450, 0, 0},
											  {250, 450, 0, 0},
											  {300, 450, 0, 0},
											  {350, 450, 0, 0},
											  {400, 450, 0, 0}};
	static public int TargetIndex = 0;
	
	static public int[][] getTargetBase() {
		return TargetBase;
	}
	
	static public int[] getTargetInfo(int i) {
		return TargetBase[i];
	}
	
	static public int getTargetBaseSize() {
		return TargetBase.length;
	}
	
	static public void addTargetIndex() {
		TargetIndex ++;
	}
	
	static public int getTargetIndex() {
		return TargetIndex;
	}
	
	static public void resetTargetIndex() {
		TargetIndex = 0;
	}
}