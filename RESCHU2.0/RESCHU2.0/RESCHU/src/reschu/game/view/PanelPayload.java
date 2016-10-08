package reschu.game.view;

import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 
import java.awt.image.BufferedImage;
import java.net.URL;
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
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

import org.jdesktop.animation.timing.*;
import org.jdesktop.animation.timing.interpolation.*;

import reschu.game.controller.GUI_Listener;
import reschu.game.model.Game;
import reschu.game.model.Payload;
import reschu.game.model.PayloadList;
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
    
    public PanelPayload(GUI_Listener e, String strTitle, GLJPanel payload_canvas, Game g) {
    	if( GL_DEBUG ) System.out.println("GL: PanelPayload created");
    
    	lsnr = e;
    	glCanvas = payload_canvas;
        this.g = g;
        
        payload_list = g.getPayloadList();
        Image_Loading = false;
        flash = 0; 
        
        glEnabled(false);
     	
        setPopup();
        initTextRenenders();
        makeVibrateThread();  
        
        glCanvas.setLayout(null); 
    }

    private void initTextRenenders() {
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
    public void init(GLAutoDrawable drawable) {
    	if( GL_DEBUG ) System.out.println("GL: init called");        
    	GL2 gl = drawable.getGL().getGL2();
        gl.setSwapInterval(0);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);         
        
        initAnimRenderer();      
        updateAnimRenderer();
    }
	/**
	 * Called by the drawable to initiate OpenGL rendering by the client.
	 */
    public void display(GLAutoDrawable drawable) {
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
        camera_pers(gl);
        lsnr.Payload_Graphics_Update();
        // Indicate the GL that display doesn't have to be called again and again
        // because it is already blacked out.
        if( !isEnabled() ) screenBlackedAfterPayloadDone = true;
    }
    /**
     *  Called by the drawable during the first repaint after the component has been resized.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    	if( GL_DEBUG ) System.out.println("GL: reshape called");
        GL_width = width;
        GL_height = height;
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, GL_width, GL_height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        double aspectRatio = (double) GL_width / (double) GL_height;
        glu.gluPerspective(CAMERA_ANGLE + zoom_angle_off, aspectRatio, 100, 2500.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW); 
    } 
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
    public synchronized boolean isEnabled() { return enabled; }
    private synchronized void glEnabled(boolean b) { enabled = b; } 

    /**
     * Called by MyCanvas
     * Sets the local variable rbtnClicked.
     */
    public void setClicked(boolean c) {rbtnClicked = c;}
    
    public void reset_variables() {
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
    public void set_payload(Vehicle v) throws IOException {
        this.v = v;        
        final String type = v.getType(); 
        final String mission = v.getTarget().getMission();

        // if the previous image is not flushed yet,
        if (img != null) img.flush();

        // get the current payload of this vehicle
        curPayload = payload_list.getPayload(type, mission); // this line
        Image_Loading = true;
       
      
     
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
        if (Utils.isRightClick(m_ev)) { 
            clickedX = m_ev.getX();
            clickedY = m_ev.getY();
            rbtnClicked = true;
//            showPopup(getParent(), m_ev.getXOnScreen()+10, m_ev.getYOnScreen()+10);
            showPopup(getParent(), m_ev.getX()+10, m_ev.getY()+10);            
            lsnr.Payload_Submit(true); // T3
        	
        } else if (Utils.isLeftClick(m_ev)) {
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
    public void r_c_2() {
        if (changing_view != null && changing_view.isRunning()) return;        

        if (v.getType() == Vehicle.TYPE_UAV) {
            changing_view = PropertySetter.createAnimator(1000, this, "Rotating", rotate_angle, rotate_angle + 30);
        }
        changing_view.setAcceleration(0.4f);
        changing_view.start();
    }
    public void r_c_c_2() {
        if (changing_view != null && changing_view.isRunning()) return;

        if (v.getType() == Vehicle.TYPE_UAV) {
            changing_view = PropertySetter.createAnimator(1000, this, "Rotating", rotate_angle, rotate_angle - 30);
        }
        changing_view.setAcceleration(0.4f);
        changing_view.start();
    }
    public void pan(float x, float y, int time) {
    	if( GL_DEBUG ) System.out.println("GL: pan called");
        changing_x = PropertySetter.createAnimator(time, this, "PanX", image_x, x);
        changing_y = PropertySetter.createAnimator(time, this, "PanY", image_y, y); 
        changing_x.start();
        changing_y.start();
    }
    public void zoom_in() {
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
    }
    public void zoom_out() {
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

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}  
}



        