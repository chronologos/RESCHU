package reschu.constants;

public class MyTargetBase {
	final static public int[][] TargetBase = {{40, 452, 1, 8},
											  {89, 47, 0, 13},
											  {137, 32, -1, 13},
											  {341, 445, 2, 14},
											  {146, 143, -2, 2},
											  {122, 82, -2, 4},
											  {396, 438, 3, 15},
											  {159, 183, 3, 21},
											  {102, 161, -1, 2},
											  {180, 259, 3, 13},
											  {366, 377, 3, 18},
											  {103, 213, 2, 11},
											  {183, 338, 3, 10},
											  {264, 465, 2, 14},
											  {101, 258, 3, 12},
											  {32, 427, 2, 8},
											  {188, 429, 3, 16},
											  {36, 377, 0, 16},
											  {296, 353, 3, 13},
											  {38, 310, 3, 20},
											  {249, 430, 3, 23},
											  {78, 251, 3, 15},
											  {68, 218, 3, 8},
											  {66, 159, 3, 15},
											  {52, 100, 3, 14}
											  };
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