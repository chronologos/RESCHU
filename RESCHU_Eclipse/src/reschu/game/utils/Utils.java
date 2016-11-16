package reschu.game.utils;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class Utils {
	/**
	 * Returns true if this MouseEvent is LEFT CLICK
	 */
	public static boolean isLeftClick(MouseEvent m_ev) {
        if ((m_ev.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
            return true;
        }
        return false;
    }
	
	/**
	 * Returns true if this MouseEvent is RIGHT CLICK
	 */
    public static boolean isRightClick(MouseEvent m_ev) {
        if ((m_ev.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
            return true;
        }
        return false;
    }
    
    /**
     * Translate degrees to radians
     * As now, this is used only in PanelPayload
     */
	public static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }
	
	/**
	 * returns min number
	 */
	public static int min(int a, int b, int c) {
		if( a < b ) return Math.min(a, c);
		else return Math.min(b, c);
	}
	
	/**
	 * returns max number
	 */
	public static int max(int a, int b, int c) {
		if( a > b ) return Math.max(a, c);
		else return Math.max(b, c);
	}	
	/**
	 * returns min number
	 */
	public static int min(int a, int b, int c, int d, int e) {
		int x = Math.min(a, b);
		int y = Math.min(c, d);
		return Math.min(Math.min(x, y), e);
	}
	
	/**
	 * returns max number
	 */
	public static int max(int a, int b, int c, int d, int e) {
		int x = Math.max(a, b);
		int y = Math.max(c, d);
		return Math.max(Math.max(x, y), e);
	} 
}
