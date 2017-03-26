package reschu.constants;

public class MyTargetBase {
	final static public int[][] TargetBase = {{50, 50, 1, 0},
											  {100, 100, 2, 0},
											  {150, 150, 3, 0},
											  {200, 200, 4, 0},
											  {250, 250, 5, 0},
											  {300, 300, 6, 0},
											  {350, 350, 7, 0},
											  {400, 400, 8, 0},
											  {450, 450, 9, 0},
											  {0, 450, 10, 0},
											  {50, 450, 11, 0},
											  {100, 450, 12, 0},
											  {150, 450, 13, 0},
											  {200, 450, 14, 0},
											  {250, 450, 15, 0},
											  {300, 450, 16, 0},
											  {350, 450, 17, 0},
											  {400, 450, 18, 0}};
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