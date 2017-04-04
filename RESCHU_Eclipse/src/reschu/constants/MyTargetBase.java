package reschu.constants;

public class MyTargetBase {
	final static public int[][] TargetBase = {{40, 452, 1, 7, 8, 9},
											  {89, 47, 0, 12, 13, 14},
											  {137, 32, -1, 12, 13, 14},
											  {341, 445, 2, 13, 14, 15},
											  {146, 143, -2, 1, 2, 3},
											  {122, 82, -2, 3, 4, 5},
											  {396, 438, 3, 14, 15, 16},
											  {159, 183, 3, 20, 21, 22},
											  {102, 161, -1, 1, 2, 3},
											  {180, 259, 3, 12, 13, 14},
											  {366, 377, 3, 17, 18, 19},
											  {103, 213, 2, 10, 11, 12},
											  {183, 338, 3, 9, 10, 11},
											  {264, 465, 2, 13, 14, 15},
											  {101, 258, 3, 11, 12, 13},
											  {32, 427, 2, 7, 8, 9},
											  {188, 429, 3, 15, 16, 17},
											  {36, 377, 0, 15, 16, 17},
											  {296, 353, 3, 12, 13, 14},
											  {38, 310, 3, 19, 20, 21},
											  {249, 430, 3, 22, 23, 24},
											  {78, 251, 3, 14, 15, 16},
											  {68, 218, 3, 7, 8, 9},
											  {66, 159, 3, 14, 15, 16},
											  {52, 100, 3, 13, 14, 15}};
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