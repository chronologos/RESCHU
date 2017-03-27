package reschu.constants;

public class MyTargetBase {
	final static public int[][] TargetBase = {{40, 452, 1, 8},
											  {32, 427, 2, 8},
											  {36, 377, 0, 16},
											  {38, 310, 3, 20},
											  {78, 251, 3, 15},
											  {68, 218, 3, 8},
											  {66, 159, 3, 15},
											  {52, 100, 3, 14},
											  {89, 47, 0, 13},
											  {137, 32, -1, 13},
											  {122, 82, -2, 4},
											  {102, 161, -1, 2},
											  {103, 213, 2, 11},
											  {101, 258, 3, 12}};
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