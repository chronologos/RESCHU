package reschu.game.view;

import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;  


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import org.jdesktop.animation.timing.*;
import org.jdesktop.animation.timing.interpolation.*;

import reschu.game.controller.GUI_Listener;
import reschu.game.model.Game;
import reschu.game.model.Payload;
import reschu.game.model.PayloadList;
import reschu.game.model.UAV;
import reschu.game.model.Vehicle;
import reschu.game.utils.Utils;
import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
/**
 * @author Carl Nehme
 * Code modified by yale. 
 */
public class PanelPayload extends MyCanvas implements GLEventListener {

	private static final long serialVersionUID = -6487171440210682586L; 
	private static final boolean GL_DEBUG = false;
	private static final boolean USE_POPUP = false;

	private static final GLU glu = new GLU();
	private GLJPanel glCanvas;
	private TextureRenderer animRenderer;
	private Animator changing_view;
	private Animator changing_x;
	private Animator changing_y;

	private TextRenderer trP14;    
	private TextRenderer trB12;
	private TextRenderer trB17;
	private TextRenderer trB20;
	private TextRenderer trB24;

	private Random rnd = new Random();
	private BufferedImage img;
	private Game g;
	private GUI_Listener lsnr;
	private PayloadList payload_list;
	private java.awt.event.MouseEvent mouseEvt;
	private int GL_width,  GL_height;

	private final int bogus_pxl_width_and_height = 600;
	private int pxl_width = bogus_pxl_width_and_height;	// the width of payload image
	private int pxl_height = bogus_pxl_width_and_height;// the height of payload image

	private float bezierAlpha = 1f;
	private float image_x;
	private float image_y;
	private float new_x_off;
	private float new_y_off;
	private float x_dist;
	private float y_dist;
	private float camera_x;
	private float camera_y;
	private float camera_height;

	private double zoom_angle_off;
	private double rotate_angle;
	private double CAMERA_ANGLE;

	private int zoom_count; 
	private Vehicle v;
	private Payload curPayload;
	private float x_limit = (float) rnd.nextInt(10);
	private float y_limit = (float) rnd.nextInt(10);
	//    private boolean penalize;
	private boolean enabled = false;
	private boolean correct = false;
	private boolean screenBlackedAfterPayloadDone;
	private JPopupMenu popMenu;
	private JMenuItem mnuSubmit, mnuCancel;

	private double min_x, max_x, min_y, max_y;
	private boolean rbtnClicked = false;
	private int clickedX,  clickedY;

	private int viewport[] = new int[4];
	private double mvmatrix[] = new double[16];
	private double projmatrix[] = new double[16];
	private double wcoord[] = new double[4];
	private double wcoord1[] = new double[4];
	private double wcoord2[] = new double[4];
	private double wcoord3[] = new double[4];

	private int x_direction = 2;
	private int y_direction = 2;

	private FloatBuffer frameBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer(); 
	private Boolean Image_Loading;
	private float flash; 

	private JButton btnSubmit, btnCancel;

	private UAVMonitor uavMonitor;

	// Prototype instance variables
	private int xDirection = 1;
	private int yDirection = 0;
	
	private float centreX = 0;
	private float centreY = 0;

	//private BufferedImage backingImage;
	private int backingImgWidth;
	private int backingImgHeight;
	private Texture CurrentTexture;
	//private Animator a;
	public Animator a;
	public int numCalls;
	public long startTime;
	public static final int TILE_LENGTH = 1000;
	public static final int VIEWPORT_LENGTH = 400;
	public static final int OVERLAP_LENGTH = 450;
	public static final int SPEED = 1; // no.of pixels moved in each call to display, leads to movement speed of roughly 500 pixels/sec
	private Map<String, Texture> subTextures;
	private int tileX = 0;
	private int tileY = 0;
	private int xPos = 0;
	private int yPos = 0;
	private float zoomLevel = 1;
	private int corners = 0;
	private Transition t;
	private MapTileCreator tiler;
	private String tileFileDir;

	public PanelPayload(GUI_Listener e, String strTitle, GLJPanel payloadCanvas, Game g, String tileFileDir, int imageHeight, int imageWidth) {
		if( GL_DEBUG ){ 
			System.out.println("GL: PanelPayload created");
		}
		lsnr = e;
		glCanvas = payloadCanvas;
		this.g = g; 
		payload_list = g.getPayloadList();
		Image_Loading = false;
		flash = 0;         
		glEnabled(true);
		setPopup();
		//makeVibrateThread();  // TO BE RESTORED
		backingImgHeight = imageHeight;
		backingImgWidth = imageWidth;
		this.tileFileDir = tileFileDir;
		glCanvas.setSize(VIEWPORT_LENGTH, VIEWPORT_LENGTH);
	}

	public void setUAVMonitor(UAVMonitor uavMonitor) {
		this.uavMonitor = uavMonitor;
	}

	private void initTextRenderers() {
		trP14 = new TextRenderer(new Font("SansSerif", Font.PLAIN, 14), true, false);
		trB12 = new TextRenderer(new Font("SansSerif", Font.BOLD, 12), true, false); 
		trB17 = new TextRenderer(new Font("SansSerif", Font.BOLD, 17), true, false); 
		trB20 = new TextRenderer(new Font("SansSerif", Font.BOLD, 20), true, false);
		trB24 = new TextRenderer(new Font("SansSerif", Font.BOLD, 24), true, false);
	}

	/** 
	 * A thread for making the screen vibrate
	 */   
	private void makeVibrateThread() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}

					x_dist += x_direction * ((float) rnd.nextGaussian() + 2); 
					y_dist += y_direction * ((float) rnd.nextGaussian() + 2);

					if (x_direction > 0) {
						if (x_dist > x_limit) {
							x_limit = 6 * (float) rnd.nextInt(3);
							x_direction = -x_direction;
						}
					} else {
						if (x_dist < -x_limit) {
							x_limit = 6 * (float) rnd.nextInt(3);
							x_direction = -x_direction;
						}
					}

					if (y_direction > 0) {
						if (y_dist > y_limit) {
							y_limit = 6 * (float) rnd.nextInt(3);
							y_direction = -y_direction;
						}
					} else {
						if (y_dist < -y_limit) {
							y_limit = 6 * (float) rnd.nextInt(3);
							y_direction = -y_direction;
						}
					}
					flash = (float) (flash + 0.1) % 2;
					//rotate_angle = rotate_angle + 1;
					glCanvas.display();
				}
			}
		}).start();
	}

	/**
	 * Called by the drawable immediately after the OpenGL context is initialized.
	 */
	/*
    public void init(GLAutoDrawable drawable) {

    	System.out.println("Calling init of panel payload's drawable");

    	if( GL_DEBUG ) System.out.println("GL: init called");        
    	GL2 gl = drawable.getGL().getGL2();
        gl.setSwapInterval(0);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);         

        initAnimRenderer();      
        updateAnimRenderer();
    }
	 */


	/**
	 * Called by the drawable to initiate OpenGL rendering by the client.
	 */
	/*
    public void display(GLAutoDrawable drawable) {
    	//glCanvas.display();
    	System.out.println("Calling display of prototype from panel payload");
    	prototype.doDisplay();
    	/*

    	System.out.println("Calling display of the PanelPayload");

    	if( GL_DEBUG ) System.out.println("GL: display called"); 
    	if( !isEnabled() && screenBlackedAfterPayloadDone ) return;

        GL2 gl = drawable.getGL().getGL2();        
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity(); 

        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);

        glu.gluLookAt(camera_x + image_x + x_dist, // eyeX
        		camera_y + image_y + y_dist, // eyeY
        		camera_height, // eyeZ
                image_x + x_dist, // centerX 
                image_y + y_dist, // centerY
                0f, // centerZ
                (float) (Math.sin(Utils.degreesToRadians(rotate_angle))),  // upX
                (float) (Math.cos(Utils.degreesToRadians(rotate_angle))),  // upY
                0.0f); // upZ

        displayAnimRenderer(drawable, viewport[2], viewport[3], image_x, image_y);
        displayText(drawable);

        unproj(gl, (int) (viewport[2] / 2), (int) (viewport[3] / 2), wcoord1);
        unproj(gl, 1, 1, wcoord2);

        double half_width = wcoord1[0] - wcoord2[0];
        min_x = -(pxl_width - 50) + half_width;
        max_x = (pxl_width - 50) - half_width;

        double half_height = wcoord2[1] - wcoord1[1];
        max_y = (pxl_height - 50) - half_height;
        unproj(gl, 1, viewport[3] - 1, wcoord3);
        min_y = -(pxl_height - 50) - (wcoord3[1] - wcoord1[1]);

        image_x = new_x_off;
        image_y = new_y_off;

        // calibrate if the image is off the screen
        if (image_x < min_x) image_x = (float) min_x; 
        if (image_x > max_x) image_x = (float) max_x; 
        if (image_y < min_y) image_y = (float) min_y; 
        if (image_y > max_y) image_y = (float) max_y; 

        if (mouseEvt != null) { 
        	// Move to the mouse clicked position
            unproj(gl, mouseEvt.getX(), mouseEvt.getY(), wcoord); 
            // Check if the clicked position is the correct target position
            setCorrect();

            if (Utils.isLeftClick(mouseEvt)) {
            	hidePopup();

                if (wcoord[0] < min_x) wcoord[0] = min_x;
                if (wcoord[0] > max_x) wcoord[0] = max_x;
                if (wcoord[1] < min_y) wcoord[1] = min_y;
                if (wcoord[1] > max_y) wcoord[1] = max_y;

                int time_factor = 2 * (int)Math.sqrt(
                		(wcoord[0]-image_x) * (wcoord[0]-image_x) 
                		+ (wcoord[1]-image_y) * (wcoord[1]-image_y));
                pan((float) wcoord[0], (float) wcoord[1], time_factor);
            }
            mouseEvt = null;
        }
        //camera_pers(gl);
        lsnr.Payload_Graphics_Update();
        // Indicate the GL that display doesn't have to be called again and again
        // because it is already blacked out.
        if( !isEnabled() ) screenBlackedAfterPayloadDone = true;

    }
	 */
	/**
	 *  Called by the drawable during the first repaint after the component has been resized.
	 */
	/*
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    	if( GL_DEBUG ) System.out.println("GL: reshape called");
        GL_width = width;
        GL_height = height;
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, GL_width, GL_height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        double aspectRatio = (double) GL_width / (double) GL_height;
        //glu.gluPerspective(CAMERA_ANGLE + zoom_angle_off, aspectRatio, 100, 2500.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW); 
    } 
	 */
	/**
	 * Called by the drawable when the display mode or the display device 
	 * associated with the GLAutoDrawable has changed.
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		if( GL_DEBUG ) System.out.println("GL: displayChanged called");
	}

	private void initAnimRenderer() {
		animRenderer = new TextureRenderer(pxl_width, pxl_height, false); 
		animRenderer.setSize(10, 10); 
	}

	private void updateAnimRenderer() {
		if( GL_DEBUG ) System.out.println("GL: updateAnimRenderer called");
		int w = animRenderer.getWidth();
		int h = animRenderer.getHeight();
		Graphics2D g2d = animRenderer.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(img, null, null);
		g2d.dispose();
		animRenderer.markDirty(0, 0, w, h); // to be automatically synchronized with the underlying Texture
	}

	/**
	 * Called by MyCanvas to check if this panel is enabled, that is, 
	 * if there is any payload that is currently shown in the panel 
	 */
	@Override
	public synchronized boolean isEnabled() { return enabled; }
	private synchronized void glEnabled(boolean b) { enabled = b; } 

	/**
	 * Called by MyCanvas
	 * Sets the local variable rbtnClicked.
	 */
	public void setClicked(boolean c) {rbtnClicked = c;}

	public void resetVariables() {
		camera_x = 0;
		image_x = image_y = 0;
		new_x_off = new_y_off = 0;
		x_dist = y_dist = 0;
		rotate_angle = 0;
		zoom_count = 0;
		min_x = max_x = 0;
		min_y = max_y = 0;
		//      penalize = false;
	}

	/**
	 * Called by PanelMap when the user engages a target.
	 * @param v 
	 * @throws IOException 
	 */
	public void setPayload(Vehicle v) throws IOException {
		this.v = v;        
		final String type = v.getType(); 
		final String mission = v.getTarget().getMission();

		// if the previous image is not flushed yet,
		if (img != null) img.flush();

		// get the current payload of this vehicle
		curPayload = payload_list.getPayload(type, mission); // this line
		Image_Loading = true;
		uavMonitor.enableUAVFeed(v);


		/*
        new Thread(new Runnable() {
            public void run() {
                try { 
                	System.out.println("payload reached");
                	img = ImageIO.read(new File(curPayload.getFilename())); // HERE
                } catch (IOException ex) {									
                    ex.printStackTrace();									
                    System.exit(0);
                }
                pxl_width = (type == Vehicle.TYPE_UUV) ? 2000 : img.getWidth();
                pxl_height = img.getHeight();

                if (animRenderer == null) initAnimRenderer();

                // updates animation renderer with this image's size                
                animRenderer.setSize(pxl_width, pxl_height);                
                updateAnimRenderer();

                // set camera
                camera_height = (type == Vehicle.TYPE_UUV) ? 1300 : 2000;
                CAMERA_ANGLE = 30.0;
                zoom_angle_off = 10;
                camera_y = (type == Vehicle.TYPE_UUV) ? 0 : -400;

                // resets all the variables that should be cleaned and display it
                reset_variables();
                glCanvas.display();

                Image_Loading = false;
            }
        }).start();
		 */

		glEnabled(true);
		correct = false;
		lsnr.Payload_Assigned_From_pnlPayload(v, curPayload);
	}

	private void displayText(GLAutoDrawable drawable) {

		if( GL_DEBUG ) System.out.println("GL: displayText called");
		if (Image_Loading) {
			trB24.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
			trB24.setColor(0.9f, 0.9f, 0.9f, flash);
			trB24.draw("INITIATING VIDEO FEED", drawable.getSurfaceWidth() / 2 - 120, drawable.getSurfaceHeight() / 2);
			trB24.endRendering();
		}
		if (isEnabled()) {
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
			trB20.setColor(0.8f, 0.1f, 0.1f, 1f);
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
			trB20.setColor(0.8f, 0.1f, 0.1f, 1f);
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
			trB20.setColor(0.8f, 0.1f, 0.1f, 1f);
			trB20.draw("__", (drawable.getSurfaceWidth() * 1 / 10) + 20, drawable.getSurfaceHeight() / 4 + 75 + (int) (60 / 3) * zoom_count);
			trB20.endRendering();

			if (rbtnClicked) { 
				//System.out.println("CLICKEDX AND CLICKEDY="+clickedX+","+ clickedY);
				//System.out.println("HEIGHT AND WIDTH OF DRAWABLE=" +drawable.getWidth()+","+drawable.getHeight());
				trB24.beginRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
				trB24.setColor(0.1f, 0.1f, 1.0f, 0.9f);
				//System.out.println((x_dist / (pxl_width * 2) * drawable.getWidth())+","+(y_dist / (pxl_height * 2) * drawable.getHeight()));
				//System.out.println(x_dist+","+y_dist);
				double box_center_x = clickedX - (x_dist / (pxl_width * 2)) * drawable.getSurfaceWidth();
				double box_center_y = drawable.getSurfaceHeight() - clickedY - (y_dist / (pxl_height * 2)) * drawable.getSurfaceHeight();
				//System.out.println("BOX X AND Y="+box_center_x+","+ box_center_y);
				trB24.draw("|", (int) box_center_x - 15, (int) box_center_y - 7);
				trB24.draw("|", (int) box_center_x + 9, (int) box_center_y - 7);
				trB24.draw("__", (int) box_center_x - 12, (int) box_center_y + 15);
				trB24.draw("__", (int) box_center_x - 12, (int) box_center_y - 10);
				//textRenderer8.draw("K",(int)box_center_x,(int)box_center_y);
				trB24.endRendering();
			}
		}
		/*
	    if (penalize) {
	    textRenderer.setColor(1.0f, 0.2f, 0.3f, 0.9f);
	    textRenderer.draw("INCORRECT LOCATION", 40, drawable.getHeight() / 2);
	    }
		 */
	}

	private void displayAnimRenderer(GLAutoDrawable drawable, int viewport_x, int viewport_y, float x_off, float y_off) {
		if( GL_DEBUG ) System.out.println("GL: displayAnimRenderer called");    
		if (bezierAlpha == 0f) return;

		GL2 gl = drawable.getGL().getGL2();
		Texture tex = animRenderer.getTexture();
		TextureCoords tc = tex.getImageTexCoords();

		float tx1 = tc.left();
		float ty1 = tc.top();
		float tx2 = tc.right();
		float ty2 = tc.bottom();

		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		tex.bind(gl);
		tex.enable(gl);
		gl.glBegin(GL2.GL_QUADS);

		float rgb = bezierAlpha;
		float corner_x = pxl_width;
		float corner_y = pxl_height;
		gl.glColor4f(rgb, rgb, rgb, rgb);
		gl.glTexCoord2f(tx1, ty1); gl.glVertex3f(-corner_x,  corner_y, 0f);
		gl.glTexCoord2f(tx2, ty1); gl.glVertex3f( corner_x,  corner_y, 0f);
		gl.glTexCoord2f(tx2, ty2); gl.glVertex3f( corner_x, -corner_y, 0f);
		gl.glTexCoord2f(tx1, ty2); gl.glVertex3f(-corner_x, -corner_y, 0f);
		gl.glEnd();

		tex.disable(gl);
	}

	private void camera_pers(GL2 gl) {
		if( GL_DEBUG ) System.out.println("GL: camera_pers called");
		gl.glViewport(0, 0, GL_width, GL_height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		double aspectRatio = (double) GL_width / (double) GL_height;
		glu.gluPerspective(CAMERA_ANGLE + zoom_angle_off, aspectRatio, 100, 2500.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW); 
		lsnr.Payload_Graphics_Update(); 
	}

	private void unproj(GL2 gl, int x, int y, double[] wcoord) {
		if( GL_DEBUG ) System.out.println("GL: unproj called");
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0); 

		/* note viewport[3] is height of window in pixels */
		int realx = (int) x; 				// GL x coord pos
		int realy = viewport[3] - (int) y; 	// GL y coord pos

		gl.glReadPixels(realx, realy, 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, frameBuffer);
		frameBuffer.rewind(); 
		glu.gluUnProject((double) realx, (double) realy, (double) frameBuffer.get(0), //winX,winY,winZ 
				mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
	}

	public void mouse_click(java.awt.event.MouseEvent m_ev) {
		if( GL_DEBUG ) System.out.println("GL(Mouse): " + m_ev.toString());
		clickedX = m_ev.getX();
		clickedY = m_ev.getY();
		if (Utils.isRightClick(m_ev)) { 
			rbtnClicked = true;
			//            showPopup(getParent(), m_ev.getXOnScreen()+10, m_ev.getYOnScreen()+10);
			showPopup(getParent(), m_ev.getX()+10, m_ev.getY()+10);            
			lsnr.Payload_Submit(true); // T3

		} else if (Utils.isLeftClick(m_ev)) {
			uavMonitor.applyPan(clickedX, clickedY);
			rbtnClicked = false; 
		}
		mouseEvt = m_ev;
	}


	/**
	 * Check if the mouse clicked position is correct place 
	 * provided by the current payload.
	 * This function is to be called in display() function.
	 */
	private void setCorrect() {
		int px = curPayload.getLocation()[0];
		int py = curPayload.getLocation()[1];
		int offset = 150;
		if ( (wcoord[0] <= px + offset && wcoord[0] >= px - offset) 
				&& (wcoord[1] <= py + offset && wcoord[1] >= py - offset)) {
			correct = true;
		} else { 
			correct = false;
		}
		if( GL_DEBUG ) System.out.println("GL: setCorrect(" + correct + ") called");
	}

	/**
	 * Called when submit in the popup menu is clicked or the submit button is clicked. 
	 */
	public void checkCorrect() {
		if (correct) {
			PanelMsgBoard.Msg("CORRECT!, SCORE!");
			g.addScore(1);
			lsnr.EVT_Payload_Finished_Correct(v.getIndex(), v.getTarget().getName());
		} else {
			PanelMsgBoard.Msg("INCORRECT!, NO SCORE!");
			lsnr.EVT_Payload_Finished_Incorrect(v.getIndex(), v.getTarget().getName());
			/*
			 * This chuck of code was written for penalizing users when they clicked 
			 * the wrong target. As for now (May 10, 2008) RESCHU just disregards 
			 * the wrong target selection and gets you out. So, there's no more meaning
			 * to penalize.
			 * -yale
        penalize = true;
        new Thread(new Runnable() {
	        public void run() {
		        try {
		        	Thread.sleep(5000);
		        } catch (InterruptedException e) {}
		    	penalize = false;
		    	}
	        }).start();
			 */
		}

		lsnr.Payload_Finished_From_pnlPayload(v);
		lsnr.Payload_Submit(false); // T3
		initAnimRenderer(); 
		glEnabled(false);
		screenBlackedAfterPayloadDone = false;
	}

	// PAYLOAD CAMERA CONTROL
	public double getRotating() { return rotate_angle; }
	public void setRotating(double alpha) { rotate_angle = alpha; }
	public float getPanX() { return new_x_off; }
	public void setPanX(float alpha) { new_x_off = alpha; }
	public float getPanY() { return new_y_off; }
	public void setPanY(float alpha) { new_y_off = alpha; }
	public double getZoom() { return zoom_angle_off; }
	public void setZoom(double alpha) { zoom_angle_off = alpha; }
	/*
    public void r_c_2() {
        if (changing_view != null && changing_view.isRunning()) return;        

        if (v.getType() == Vehicle.TYPE_UAV) {
            changing_view = PropertySetter.createAnimator(1000, this, "Rotating", rotate_angle, rotate_angle + 30);
        }
        changing_view.setAcceleration(0.4f);
        changing_view.start();
    }
	 */
	/*
    public void r_c_c_2() {
        if (changing_view != null && changing_view.isRunning()) return;

        if (v.getType() == Vehicle.TYPE_UAV) {
            changing_view = PropertySetter.createAnimator(1000, this, "Rotating", rotate_angle, rotate_angle - 30);
        }
        changing_view.setAcceleration(0.4f);
        changing_view.start();
    }
	 */
	/*
    public void pan(float x, float y, int time) {
    	if( GL_DEBUG ) System.out.println("GL: pan called");
        changing_x = PropertySetter.createAnimator(time, this, "PanX", image_x, x);
        changing_y = PropertySetter.createAnimator(time, this, "PanY", image_y, y); 
        changing_x.start();
        changing_y.start();
    }
	 */
	public void zoom_in() {

		if (zoomLevel >= 1) {
			zoomLevel ++;
		}
		else {
			zoomLevel = 1/((1/zoomLevel) - 1);
		}
		System.out.println("zoom is" + zoomLevel);
		return;
		/*
    	if (!isEnabled() || zoom_count == 3 || (changing_view != null && changing_view.isRunning())) {
            return;
        }
        zoom_count = zoom_count + 1;
        if (v.getType() == Vehicle.TYPE_UAV) {
            changing_view = PropertySetter.createAnimator(500, this, "Zoom", zoom_angle_off, zoom_angle_off - 5);
        } else if (v.getType() == Vehicle.TYPE_UUV) {
            changing_view = PropertySetter.createAnimator(500, this, "Zoom", zoom_angle_off, zoom_angle_off - 5);
        }
        changing_view.setAcceleration(0.4f);
        changing_view.start();
		 */
	}

	public void zoom_out() {

		if (zoomLevel <= 1) {
			zoomLevel = 1/((1/zoomLevel) + 1);
		}
		else {
			zoomLevel --;
		}
		System.out.println("zoom is" + zoomLevel);
		return;
		/*
        if (!isEnabled() || zoom_count == 0 || (changing_view != null && changing_view.isRunning())) {
            return;
        }
        zoom_count = zoom_count - 1;
        if (v.getType() == Vehicle.TYPE_UAV) {
            changing_view = PropertySetter.createAnimator(500, this, "Zoom", zoom_angle_off, zoom_angle_off + 5);
        } else if (v.getType() == Vehicle.TYPE_UUV) {
            changing_view = PropertySetter.createAnimator(500, this, "Zoom", zoom_angle_off, zoom_angle_off + 5);
        }
        changing_view.setAcceleration(0.4f);
        changing_view.start();
		 */
	}

	// PopupMenu implementation
	public JPopupMenu getPopMenu() { return popMenu; }   
	private void setPopup() {
		if( USE_POPUP ) {
			popMenu = new JPopupMenu();  
			mnuSubmit = new JMenuItem("Submit");
			mnuCancel = new JMenuItem("Cancel");
			mnuSubmit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					rbtnClicked = false;
					hidePopup();
					checkCorrect();
				}
			});
			mnuCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					rbtnClicked = false;
					hidePopup(); 
				}
			});
			popMenu.add(mnuSubmit);
			popMenu.add(mnuCancel);
		}
		else {
			btnSubmit = new JButton("SUBMIT");
			btnSubmit.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							rbtnClicked = false;
							glCanvas.remove(btnSubmit);
							glCanvas.remove(btnCancel);
							checkCorrect();
						}
					});

			btnCancel = new JButton("CANCEL");
			btnCancel.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							rbtnClicked = false;
							glCanvas.remove(btnSubmit);
							glCanvas.remove(btnCancel);
						}
					}); 
		}

	}
	private void showPopup(Component invoker, int x, int y) {
		if( USE_POPUP ) {
			popMenu.show(invoker, x, y);
		}
		else {
			btnSubmit.setBounds(x, y, 80, 20);
			btnCancel.setBounds(x, y+20, 80, 20);
			glCanvas.add(btnSubmit);
			glCanvas.add(btnCancel);
		}
	}
	private void hidePopup() {
		if( USE_POPUP ) {
			popMenu.setVisible(false);    		
		}
		else {
			glCanvas.remove(btnSubmit);
			glCanvas.remove(btnCancel);
		}
	}



	public void setX(int x) {
		xPos = x;
	}

	public void setY(int y) {
		yPos = y;
	}

	public void setXDirection(int xDir) {
		xDirection = xDir;
	}

	public void setYDirection(int yDir) {
		yDirection = yDir;
	}

	public void setZoom(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public void doDisplay() {
		//myCanvas.display();
		//display(myCanvas);
	}


	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		if (t == null) t = new Transition(OVERLAP_LENGTH, VIEWPORT_LENGTH, TILE_LENGTH);

		uavMonitor.setCoords(); // updates the x and y coordinates as necessary
		uavMonitor.setVelocity();

		int tileIncrement = t.nextTile(xPos + (int)((float)VIEWPORT_LENGTH/2), yPos + (int)((float)VIEWPORT_LENGTH/2), xDirection, yDirection, tileX, tileY);

		if (tileIncrement != 0) { // New tile
			System.out.println("Switching tiles! xPos: " + xPos + "; Tile increment: " + tileIncrement);
			if (!getNextImage(tileX, tileY, tileIncrement, drawable, gl)) {
				System.out.println("Nerd Alert! Reversing!");
			}
		}

		if (xPos + TILE_LENGTH >= backingImgWidth - SPEED && xDirection == 1) {
			System.out.println("Hit right end of image, reversing!");
			if (++corners % 4 == 0) {
				xDirection *= -1;
			}
			else {
				xDirection = 0;
			}
			yDirection = 1;
		}

		if (xPos - TILE_LENGTH <= SPEED && xDirection == -1) {
			if (++corners % 4 == 0) {
				xDirection *= -1;
			}
			else {
				xDirection = 0;
			}
			yDirection = -1;
		}

		if (yPos + TILE_LENGTH >= backingImgHeight - SPEED && yDirection == 1) {
			if (++ corners % 4 == 0) {
				yDirection *= -1;
			}
			else {
				yDirection = 0;
			}
			xDirection = -1;
		}

		if (yPos - TILE_LENGTH <= SPEED && yDirection == -1) {
			if (++ corners % 4 == 0) {
				yDirection *= -1;
			}
			else {
				yDirection = 0;
			}
			xDirection = 1;
		}

		//xPos += SPEED * xDirection;
		//yPos += SPEED * yDirection;


		float x1 = (float)(xPos - tileX)/TILE_LENGTH;
		float x2 = x1 + (float)VIEWPORT_LENGTH/TILE_LENGTH;
		float y1 = (float)(yPos - tileY)/TILE_LENGTH;
		float y2 = y1 + (float)VIEWPORT_LENGTH/TILE_LENGTH;
		//if (centreX == 0) centreX = x1 + (x2 - x1)/(2 * zoomLevel);
	    //if (centreY == 0) centreY = y1 + (y2 - y1)/(2 * zoomLevel);
	      centreX = x1 + (x2 - x1)/(2 * zoomLevel);
	      centreY = y1 + (y2 - y1)/(2 * zoomLevel);
		render(drawable, x1, x2, y1, y2, gl);
	}

	private boolean getNextImage(int tileX, int tileY, int tileIncrement, GLAutoDrawable drawable, GL2 gl) {
		tileIncrement += 4;
		int yDelta = (tileIncrement / 3) - 1;
		int xDelta = (tileIncrement % 3) - 1;
		int nextTileX = tileX + xDelta * TILE_LENGTH - xDelta * OVERLAP_LENGTH;
		int nextTileY = tileY + yDelta * TILE_LENGTH - yDelta * OVERLAP_LENGTH;

		System.out.println("Current Tile X: " + this.tileX + "; " + "NextTile X : " + nextTileX);

		if (nextTileX > tiler.getMaxTileX() || nextTileY > tiler.getMaxTileY()) {
			System.out.println("Next tile exceeds last tile, not switching");
			return false;
		}

		int curXTileWidth;
		//compute curXTileWidth rather than using TILE_LENGTH due to last row and column of tiles having different dimensions.
		if (nextTileX + TILE_LENGTH > backingImgWidth){
			curXTileWidth = backingImgWidth - nextTileX;
		}
		else if (nextTileX < 0){
			return false;
		}
		else{
			curXTileWidth = TILE_LENGTH;
		}
		int curYTileHeight;

		if (nextTileY + TILE_LENGTH > backingImgHeight) {
			System.out.println("Next TileY: " + nextTileY + "; Setting next tile height to " + (backingImgHeight - nextTileY));
			curYTileHeight = backingImgHeight - nextTileY;
		}
		else if (nextTileY < 0) {
			return false;
		}
		else {
			curYTileHeight = TILE_LENGTH;
		}
		String imgKey = tiler.coordinateConverter(new int[]{nextTileX, nextTileY, curXTileWidth, curYTileHeight});
		System.out.println("Key for next image: " + imgKey);

		CurrentTexture = subTextures.get(imgKey);
		System.out.println("---");

		this.tileX = nextTileX;
		this.tileY = nextTileY;
		return true;
	}


	private void render(GLAutoDrawable drawable, float x1, float x2, float y1, float y2, GL2 gl) {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_TEXTURE_2D);
		CurrentTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		
		float left = centreX - (x2 - x1)/(2 * zoomLevel);
		float right = centreX + (x2 - x1)/(2 * zoomLevel);
		
		float top = centreY - (y2 - y1)/(2 * zoomLevel);
		float bottom = centreY + (y2 - y1)/(2 * zoomLevel);
		
		//gl.glTexCoord2f(x1+(x2-x1)/zoomLevel, y2 + (y1 - y2)/zoomLevel); // bot right
		gl.glTexCoord2f(right, bottom);
		gl.glVertex3f(1.0f, 1.0f, 0);
		//gl.glTexCoord2f(x1, y2 + (y1 - y2)/zoomLevel); // bot left
		gl.glTexCoord2f(left, bottom);
		gl.glVertex3f(-1.0f, 1.0f, 0);
		//gl.glTexCoord2f(x1, y2); // top left
		gl.glTexCoord2f(left, top);
		gl.glVertex3f(-1.0f, -1.0f, 0);
		//gl.glTexCoord2f(x1 + (x2 - x1)/zoomLevel, y2); //top right
		gl.glTexCoord2f(right, top);
		gl.glVertex3f(1.0f, -1.0f, 0);
		gl.glEnd();
		centreX = (left + right)/2;
		centreY = (top + bottom)/2;
	}


	@Override
	public void dispose(GLAutoDrawable arg0) {
		//method body
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		System.out.println("init() called");


		GL2 gl = arg0.getGL().getGL2();
		tiler = new MapTileCreator(TILE_LENGTH, VIEWPORT_LENGTH, OVERLAP_LENGTH); // Precomputing of tiles is complete
		subTextures = tiler.makeGridFiles(arg0);
		System.out.println("Constructing new textures");
		t = new Transition(OVERLAP_LENGTH, VIEWPORT_LENGTH, TILE_LENGTH);
		String startTileKey = tiler.coordinateConverter(new int[]{0,0, TILE_LENGTH, TILE_LENGTH});
		CurrentTexture = subTextures.get(startTileKey);
		CurrentTexture.enable(gl);
		System.out.println("Initialized and enabled current texture");
		final FPSAnimator animator = new FPSAnimator(arg0, 10);
		animator.start();
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// method body
	}

	public class MapTileCreator {
		private int tileLength;
		private int viewportLength;
		private int overlapLength;
		private int imageHeight;
		private int imageWidth;
		private Map<String, BufferedImage> subImages;


		private int maxTileX = 0;
		private int maxTileY = 0;
		public MapTileCreator(int tileLength, int viewportLength, int overlapLength) {
			this.tileLength = tileLength;
			this.viewportLength = viewportLength;
			this.overlapLength = overlapLength;
			imageHeight = backingImgHeight;
			imageWidth = backingImgWidth;
			subImages = new HashMap<String, BufferedImage>();
		}

		public List<int[]> calculateGrids(){
			// Given tileLength, overlapLength and an image, calculate the coordinates of the tiles required to
			// cover the image. GetSubimage accepts coordinates of the form 
			// (xPixelsFromTopLeft, yPixelsFromTopLeft, Width, Height)
			int currentimageWidth = overlapLength;
			int currentimageHeight = overlapLength;
			int finalColTileWidth = tileLength;
			int finalRowTileLength = tileLength;

			List<Integer> subImgYCoords = new ArrayList<Integer>();
			List<Integer> subImgXCoords = new ArrayList<Integer>();  
			List<int[]> coordinates = new ArrayList<int[]>();

			// tiles usually do not exactly cover image. In last row and column they will be shorter. 
			while (currentimageWidth < imageWidth){
				int nextXCoordinate = currentimageWidth - overlapLength;
				subImgXCoords.add(nextXCoordinate);
				currentimageWidth = nextXCoordinate + tileLength;
				if (currentimageWidth > imageWidth){
					finalColTileWidth = imageWidth - subImgXCoords.get(subImgXCoords.size()-1);
				}	  
			}
			while (currentimageHeight < imageHeight){
				int nextYCoordinate = currentimageHeight - overlapLength;
				subImgYCoords.add(nextYCoordinate);
				currentimageHeight = nextYCoordinate + tileLength;
				if (currentimageHeight > imageHeight){
					finalRowTileLength = imageHeight - subImgYCoords.get(subImgYCoords.size()-1);
				}
			}
			
			for (int i = 0; i < subImgXCoords.size(); i++){
				for (int j = 0; j < subImgYCoords.size(); j++){
					if (i == subImgXCoords.size()-1 && j == subImgYCoords.size()-1){
						int[] coords = {subImgXCoords.get(i), subImgYCoords.get(j), finalColTileWidth, finalRowTileLength};
						coordinates.add(coords);
					}
					else if (i == subImgXCoords.size()-1) {
						int[] coords = {subImgXCoords.get(i), subImgYCoords.get(j), finalColTileWidth, tileLength};
						coordinates.add(coords);
					}
					else if (j == subImgYCoords.size()-1){
						int[] coords = {subImgXCoords.get(i), subImgYCoords.get(j), tileLength, finalRowTileLength};
						coordinates.add(coords);
					}
					else {
						int[] coords = {subImgXCoords.get(i), subImgYCoords.get(j), tileLength, tileLength};
						coordinates.add(coords);
					}
				}
			}
			return coordinates;
		}

		public String coordinateConverter(int[] coords){
			// Converts int[] of xcoord, ycoord, width, height to string suitable for storing in
			// hashmap
			StringBuilder bob = new StringBuilder("");
			for (int i = 0; i<coords.length - 1; i++){
				bob.append(Integer.toString(coords[i]));
				bob.append(" ");
			}
			bob.append(Integer.toString(coords[coords.length - 1]));
			return bob.toString();
		}

		//public Map<String, BufferedImage> makeGrid(){
		/*
		  public Map<String, Texture> makeGrid(GLAutoDrawable drawable) {
			  // Using coordinates from CalculateGrids, Generate hashmap of subimages.
			  List<int[]> allCoords = CalculateGrids();
			  String key = null;
			  BufferedImage subImage = null;
			  Texture subTexture = null;

//			  Map<String, BufferedImage> mySubImages = new HashMap<String, BufferedImage>();
			  Map<String, Texture> mySubTextures = new HashMap<String, Texture>();

			  GL2 gl = drawable.getGL().getGL2();

			  for (int[] coords : allCoords) {
				  key = coordinateConverter(coords);
				  subImage = mainImage.getSubimage(coords[0], coords[1], coords[2], coords[3]);  
				  //mySubImages.put(key, subImage);
				  subTexture = AWTTextureIO.newTexture(gl.getGLProfile(), subImage, true);
				  mySubTextures.put(key, subTexture);
			  }
			  //return mySubImages;
			  return mySubTextures;
		  }
		 */
		// Approach 2 - Partitioning the original image file into multiple files (Runs into out of memory problem...)
		//public Map<String, BufferedImage> makeGridFiles(){
		public Map<String, Texture> makeGridFiles(GLAutoDrawable drawable) {
			// Using coordinates from CalculateGrids, Generate hashmap of subimages.
			List<int[]> allCoords = calculateGrids();
			String key;
			BufferedImage subImage;
			File subImageFile;
			Texture subTexture;

			Map<String, Texture> mySubTextures = new HashMap<String, Texture>();

			GL2 gl = drawable.getGL().getGL2();

			for (int[] coords : allCoords) {
				key = coordinateConverter(coords);
				System.out.println(key);
				subImageFile = new File(tileFileDir + "/" + key + ".jpg");
				try {
					subImage = ImageIO.read(subImageFile);
					subTexture = AWTTextureIO.newTexture(gl.getGLProfile(), subImage, true);
					mySubTextures.put(key, subTexture);
				}
				catch (IOException e) {
					System.out.println("GG I fucked up");
					e.printStackTrace();
					System.exit(1);
				}
			}
			return mySubTextures;
		}


		public int getMaxTileX() {
			return maxTileX;
		}

		public int getMaxTileY() {
			return maxTileY;
		}
	}



}



