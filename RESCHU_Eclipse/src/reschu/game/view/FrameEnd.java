package reschu.game.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent; 
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
import javax.swing.border.TitledBorder;

import reschu.constants.*;
import reschu.game.controller.GUI_Listener;


public class FrameEnd extends JFrame {
	private static final long serialVersionUID = 1490485040395748916L;
	//private Gui_Listener lsnr;
	private TitledBorder bdrTitle;
	private JButton btnStart;
	private ImageIcon imgIcon;
	private JLabel lblHAL;  
	
	public FrameEnd(GUI_Listener l) {
		//lsnr = l;
		super("RESCHU Security-Aware");
		
		setAlwaysOnTop(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				System.exit(0); 
			}
		});		
		setLayout(new GridLayout(0,1));
		setResizable(false);
		
		bdrTitle = BorderFactory.createTitledBorder(MyGame.VERSION_INFO);		 
		JPanel pnl = new JPanel();
		pnl.setBorder(bdrTitle);
		pnl.setBackground(Color.WHITE);
		BufferedImage img = null;
		try {
			 img = ImageIO.read(new File("Pictures/HAL/HAL.png"));
        } catch (IOException e) {}	
		imgIcon = new ImageIcon(img);
		lblHAL = new JLabel("", imgIcon, JLabel.CENTER);
        		
		btnStart = new JButton("THANKS FOR YOUR PARTICIPATION");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){ 
				if( e.getSource() == btnStart ) 
					System.exit(0);}
		});		
		pnl.add(lblHAL);
		pnl.add(btnStart);
		add(pnl);
	} 
	
	
	
}
