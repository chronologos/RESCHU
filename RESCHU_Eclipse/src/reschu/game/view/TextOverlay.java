package reschu.game.view;
import java.awt.Font;
import java.awt.Graphics2D;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.awt.Overlay;
import com.jogamp.opengl.util.awt.TextRenderer;

public class TextOverlay implements GLEventListener {
	private Overlay overlay;
	private TextRenderer trP14;    
	private TextRenderer trB12;
	private TextRenderer trB17;
	private TextRenderer trB20;
	private TextRenderer trB24;
	private int image_x = 0;
	private int image_y = 0;
	private int x_dist = 0;
	private int y_dist = 0;
	private int max_x = 200;
	private int max_y = 200;
	private int zoom_count = 3; //far01 zooming

	public static TextRenderer zoom_bar;
	
	// for zooming functions
	public void setZoomLevel(int iZoomlevel) {
		zoom_count = iZoomlevel;
	}

	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.setSwapInterval(0);
		overlay = new Overlay(drawable);
		trP14 = new TextRenderer(new Font("SansSerif", Font.PLAIN, 14), true, false);
		trB12 = new TextRenderer(new Font("SansSerif", Font.BOLD, 12), true, false); 
		trB17 = new TextRenderer(new Font("SansSerif", Font.BOLD, 17), true, false); 
		trB20 = new TextRenderer(new Font("SansSerif", Font.BOLD, 20), true, false);
		trB24 = new TextRenderer(new Font("SansSerif", Font.BOLD, 24), true, false);
	}

	// modified display function by Mahmoud
	@Override
	public void display(GLAutoDrawable drawable) {
		Graphics2D g2d = overlay.createGraphics();
		trB24.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB24.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB24.draw("_", drawable.getSurfaceWidth() / 2 - 25, drawable.getSurfaceHeight() / 2 + 5);
		trB24.draw("_", drawable.getSurfaceWidth() / 2 + 18, drawable.getSurfaceHeight() / 2 + 5);
		trB24.draw("|", drawable.getSurfaceWidth() / 2 - 1, drawable.getSurfaceHeight() / 2 - 25);
		trB24.draw("|", drawable.getSurfaceWidth() / 2 - 1, drawable.getSurfaceHeight() / 2 + 18);
		trB24.endRendering();

		trP14.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trP14.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trP14.draw("|      |     |     |     |     |     |     |      |", 
				drawable.getSurfaceWidth() / 4 - 5, 10 + drawable.getSurfaceHeight() * 4 / 5);
		trP14.endRendering();

		trB20.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB20.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB20.draw("|", (int) (-8 + drawable.getSurfaceWidth() / 2 + (drawable.getSurfaceWidth() / 4) * ((image_x + x_dist) / max_x)), 
				5 + drawable.getSurfaceHeight() * 4 / 5);
		trB20.endRendering();

		trB12.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB12.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 - 12);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 13);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 40);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 61);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 83);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 105);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 128);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 151);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 172);
		trB12.endRendering();

		trB20.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB20.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB20.draw("__", 17 + drawable.getSurfaceWidth() * 5 / 6, (int) (drawable.getSurfaceHeight() / 2 + (drawable.getSurfaceHeight() / 4) * ((image_y + y_dist) / max_y)));
		trB20.endRendering();

		trB17.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB17.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB17.draw("[+]", (drawable.getSurfaceWidth() * 1 / 10) + 22, drawable.getSurfaceHeight() / 4 + 140);
		trB17.draw("_", (drawable.getSurfaceWidth() * 1 / 10) + 28, drawable.getSurfaceHeight() / 4 + 133);
		trB17.draw("|", (drawable.getSurfaceWidth() * 1 / 10) + 25, drawable.getSurfaceHeight() / 4 + 120);
		trB17.draw("|", (drawable.getSurfaceWidth() * 1 / 10) + 25, drawable.getSurfaceHeight() / 4 + 105);
		trB17.draw("|", (drawable.getSurfaceWidth() * 1 / 10) + 25, drawable.getSurfaceHeight() / 4 + 90);
		trB17.draw("|", (drawable.getSurfaceWidth() * 1 / 10) + 25, drawable.getSurfaceHeight() / 4 + 75);

		trB17.draw("|", (drawable.getSurfaceWidth() * 1 / 10) + 35, drawable.getSurfaceHeight() / 4 + 120);
		trB17.draw("|", (drawable.getSurfaceWidth() * 1 / 10) + 35, drawable.getSurfaceHeight() / 4 + 105);
		trB17.draw("|", (drawable.getSurfaceWidth() * 1 / 10) + 35, drawable.getSurfaceHeight() / 4 + 90);
		trB17.draw("|", (drawable.getSurfaceWidth() * 1 / 10) + 35, drawable.getSurfaceHeight() / 4 + 75);
		trB17.draw("_", (drawable.getSurfaceWidth() * 1 / 10) + 28, drawable.getSurfaceHeight() / 4 + 74);
		trB17.draw("[-]", (drawable.getSurfaceWidth() * 1 / 10) + 22, drawable.getSurfaceHeight() / 4 + 50);
		trB17.endRendering();

		trB20.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB20.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB20.draw("__", (drawable.getSurfaceWidth() * 1 / 10) + 20, drawable.getSurfaceHeight() / 4 + 75 + (int) (30 / 3) * zoom_count); //far01 zooming

		trB20.endRendering();
	}
	
	/*
	@Override
	public void display(GLAutoDrawable drawable) {
		Graphics2D g2d = overlay.createGraphics();
		trB24.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB24.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB24.draw("_", drawable.getSurfaceWidth() / 2 - 25, drawable.getSurfaceHeight() / 2 + 5);
		trB24.draw("_", drawable.getSurfaceWidth() / 2 + 18, drawable.getSurfaceHeight() / 2 + 5);
		trB24.draw("|", drawable.getSurfaceWidth() / 2 - 1, drawable.getSurfaceHeight() / 2 - 25);
		trB24.draw("|", drawable.getSurfaceWidth() / 2 - 1, drawable.getSurfaceHeight() / 2 + 18);
		trB24.endRendering();

		trP14.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trP14.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trP14.draw("|      |      |      |      |      |      |      |      |", 
				drawable.getSurfaceWidth()/4-10, drawable.getSurfaceHeight()*9/10);
		trP14.endRendering();

		trB20.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB20.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		// trB20.draw("|", (int) (-8 + drawable.getSurfaceWidth() / 2 + (drawable.getSurfaceWidth() / 4) * ((image_x + x_dist) / max_x)), 5 + drawable.getSurfaceHeight() * 4 / 5);
		trB20.endRendering();

		trB12.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB12.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 - 19);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 8);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 36);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 62);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 89);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 116);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 143);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 170);
		trB12.draw("__", drawable.getSurfaceWidth() * 9 / 10, drawable.getSurfaceHeight() / 4 + 197);
		trB12.endRendering();

		trB20.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB20.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		// trB20.draw("__", 17 + drawable.getSurfaceWidth() * 5 / 6, (int) (drawable.getSurfaceHeight() / 2 + (drawable.getSurfaceHeight() / 4) * ((image_y + y_dist) / max_y)));
		trB20.endRendering();

		trB17.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB17.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		trB17.draw("[+]", (drawable.getSurfaceWidth() * 0 / 10) + 41, drawable.getSurfaceHeight() / 4 + 176);
		trB17.draw("_", (drawable.getSurfaceWidth() * 0 / 10) + 47, drawable.getSurfaceHeight() / 4 + 169);
		
		trB17.draw(" 5 |", (drawable.getSurfaceWidth() * 0 / 10) + 25, drawable.getSurfaceHeight() / 4 + 156);
		trB17.draw(" 4 |", (drawable.getSurfaceWidth() * 0 / 10) + 25, drawable.getSurfaceHeight() / 4 + 141);
		trB17.draw(" 3 |", (drawable.getSurfaceWidth() * 0 / 10) + 25, drawable.getSurfaceHeight() / 4 + 126);
		trB17.draw(" 2 |", (drawable.getSurfaceWidth() * 0 / 10) + 25, drawable.getSurfaceHeight() / 4 + 111);
		trB17.draw(" 1 |", (drawable.getSurfaceWidth() * 0 / 10) + 25, drawable.getSurfaceHeight() / 4 + 96);
		trB17.draw(" 0 |", (drawable.getSurfaceWidth() * 0 / 10) + 25, drawable.getSurfaceHeight() / 4 + 81);
		trB17.draw("-1 |", (drawable.getSurfaceWidth() * 0 / 10) + 24, drawable.getSurfaceHeight() / 4 + 66);
		trB17.draw("-2 |", (drawable.getSurfaceWidth() * 0 / 10) + 24, drawable.getSurfaceHeight() / 4 + 51);
		trB17.draw("-3 |", (drawable.getSurfaceWidth() * 0 / 10) + 24, drawable.getSurfaceHeight() / 4 + 36);
		trB17.draw("-4 |", (drawable.getSurfaceWidth() * 0 / 10) + 24, drawable.getSurfaceHeight() / 4 + 21);
		trB17.draw("-5 |", (drawable.getSurfaceWidth() * 0 / 10) + 24, drawable.getSurfaceHeight() / 4 + 6);
		
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 156);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 141);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 126);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 111);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 96);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 81);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 66);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 51);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 36);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 21);
		trB17.draw("|", (drawable.getSurfaceWidth() * 0 / 10) + 55, drawable.getSurfaceHeight() / 4 + 6);
		
		trB17.draw("_", (drawable.getSurfaceWidth() * 0 / 10) + 47, drawable.getSurfaceHeight() / 4 + 5);
		trB17.draw("[-]", (drawable.getSurfaceWidth() * 0 / 10) + 42, drawable.getSurfaceHeight() / 4 - 15);
		trB17.endRendering();

		trB20.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		trB20.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		// trB20.draw("__", (drawable.getSurfaceWidth() * 1 / 10) + 20, drawable.getSurfaceHeight() / 4 + 75 + (int) (60 / 3) * zoom_count); // TODO(restore zoom count)
		// trB20.draw("__", (drawable.getSurfaceWidth() * 1 / 10) + 20, drawable.getSurfaceHeight() / 4 + 75);

		trB20.endRendering();
	}
	*/

	@Override
	public void dispose(GLAutoDrawable arg0) {
		overlay = null;
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {}

	/*
	// can be called every cycle in Reschu
	public void update() {
		System.out.println("1");
	}
	*/
	
	/*
	public static void paintZoomBar() {
		// trB17.draw("_", (drawable.getSurfaceWidth() * 0 / 10) + 47, drawable.getSurfaceHeight() / 4 + 5);
	}
	*/
}
