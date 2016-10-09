package reschu.tutorial;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.sun.opengl.impl.mipmap.Image;

import reschu.app.AppMain;

public abstract class TutorialModel {
	final static protected int HORIZONTAL = 0;
	final static protected int VERTICAL = 1;
	//protected String URL_PREFIX = Game.URL_PREFIX;
	protected String URL_PREFIX = "http://web.mit.edu/~nehme/www/TutorialImages/";
	private int cur_state = 0;
	private int duration = 0;
	protected AppMain main;
	
	TutorialModel(AppMain main) {
		this.main = main;
	}
	
	// to avoid potential race condition
	synchronized protected void setState(int state) { cur_state = state; }
	synchronized protected int getState() {return cur_state; }
	synchronized protected void setDuration(int sec) { duration = sec; }
	synchronized protected int getDuration() { return duration; }
	
	
	/**
	 * Increments current state for dialog.
	 */
	protected void nextDialog() { setState(getState()+1); }
	
	/**
	 * Creates a label which contains an image and message
	 * 
	 * @param fileName the name of image file
	 * @param message message that you want to show
	 * @return a image label
	 * @throws IOException 
	 */
	protected JLabel makePicPanel(String fileName, String message) throws IOException {	
		/*
		BufferedReader br = new BufferedReader(new FileReader("TutorialImageCounter.txt"));
		
		String line = br.readLine();
		int count = Integer.parseInt(line);
		count = count +1;
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("TutorialImageCounter.txt",false)));
		out.println(Integer.toString(count));
		out.close();
		br.close();
		*/
		ImageIcon imgIcon;	
		JLabel label = new JLabel();
		String filelocation = "Pictures/Tutorial/Scenario";
		String scenarionum = fileName.substring(0,1);
		filelocation = filelocation + scenarionum + "/";
		imgIcon = new ImageIcon(filelocation + fileName); //HERE
		//img = imgIcon.getImage();
		label = new JLabel(message, imgIcon, SwingConstants.LEFT);
		
		//label.setVerticalTextPosition(SwingConstants.BOTTOM);			
		label.setIconTextGap(10); // in pixel scale
		label.setUI(new MultiLineLabelUI());
		label.setBorder( new EtchedBorder() );		
        
      /*  BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = bi.createGraphics();
       g2.drawImage(img, 0, 0, null);
        g2.dispose();
        
        try {
			ImageIO.write(bi, "png", new File("3_" + line + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
        return label;
	}
	
	/**
	 * Shows "correct" message dialog, and moves on to the next dialog state.
	 */
	protected void correct() { 
		setDuration(1);
		nextDialog();		
	} 
	/**
	 * Shows "incorrect" message dialog, it doesn't move to the next dialog.
	 */
	
	protected void incorrect() {
		JOptionPane.showMessageDialog(null, "incorrect!");
	}
	
	/**
	 * Checks whether the given statement is correct.
	 * @param statement 
	 */
	protected void checkCorrect(boolean statement) {
		if(statement) correct();
		//else incorrect();
	}
	/**
	 * If remaining time is equal to zero, then pops up a dialog with current state
	 * If not, decrease the time by one second.
	 */
	protected void tick() {
		if( getDuration() == 0 )
			showDialog();
		else
			setDuration(getDuration()-1);
	}
	
	/**
	 * Resets the current applet with TRAIN_MODE
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	protected void Restart_Reschu() throws NumberFormatException, IOException {
		main.Restart_Reschu();
	}
	
	protected void showDialog() {}
	protected void checkEvent(int type, int vIdx, String target) {}
	
}
