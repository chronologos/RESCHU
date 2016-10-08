package reschu.game.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel; 
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import reschu.constants.*;
import reschu.database.DBConnectionMgr;
import reschu.database.DBWriter;
import reschu.game.controller.GUI_Listener;
import reschu.game.controller.Reschu;
 
public class FrameStart extends JFrame implements ActionListener {	
	private static final long serialVersionUID = -1014337649743412793L; 
	private GUI_Listener lsnr;
	private TitledBorder bdrTitle, bdrInside;
	private JButton btnStart;
	private ImageIcon imgIcon;
	private JLabel lblHAL;
	private JProgressBar progressBar;
    private Timer tmr_clock;  
    private int cur_progress;  
    private int cur_time;
    final private int TIME_WAIT_MSEC = 10 * 1000;
    final private int TIME_INCREMENT_MSEC = 50;
	
	public FrameStart(GUI_Listener l) {		
		super("RESCHU");
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        tmr_clock = new Timer(TIME_INCREMENT_MSEC, this);
        cur_time = 0;
         
		lsnr = l;
		setAlwaysOnTop(true); 		
		setLayout(new GridLayout(0,1));
		setResizable(false);
		
		bdrTitle = BorderFactory.createTitledBorder("");
		bdrInside = BorderFactory.createTitledBorder("");
		
		JPanel pnl = new JPanel();
		pnl.setBorder(bdrTitle);
		pnl.setBackground(Color.WHITE);
		BufferedImage img = null;
		try {
			  img = ImageIO.read(new File("Pictures/HAL/HAL.png"));
        } catch (IOException e) {}		
		imgIcon = new ImageIcon(img);
		
        lblHAL = new JLabel("", imgIcon, JLabel.CENTER);
        		
		JPanel pnlInside = new JPanel(); 
		pnlInside.setBorder(bdrInside);
		
		progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);        
		
		btnStart = new JButton("START");
		btnStart.setEnabled(false);
		btnStart.addActionListener(this);
		
		pnl.add(lblHAL);
		pnlInside.add(progressBar);
		pnlInside.add(btnStart);
		pnl.add(pnlInside);
		add(pnl);
		
		tmr_clock.start();
	} 
	
	public void actionPerformed(ActionEvent e){
		cur_time += TIME_INCREMENT_MSEC;
		cur_progress = DBConnectionMgr.NumConnections;		
		progressBar.setValue(cur_time/(TIME_WAIT_MSEC/100));
		
		if( cur_time >= TIME_WAIT_MSEC ) {
			if( Reschu._database && cur_progress < DBWriter.NUM_OF_CONNECTION_IN_POOL) return;
			
			tmr_clock.stop(); 
			btnStart.setEnabled(true);	
		}
		
		if( e.getSource() == btnStart ) {
			this.setVisible(false);
			lsnr.Game_Start(); 
		}
		
	} 
}