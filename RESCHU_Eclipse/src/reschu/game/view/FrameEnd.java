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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import reschu.constants.*;
import reschu.game.controller.GUI_Listener;
import reschu.game.model.Game;

public class FrameEnd extends JFrame {
	private static final long serialVersionUID = 1490485040395748916L;
	private GUI_Listener lsnr;
	private Game game;
	private TitledBorder bdrTitle;
	private JButton btnEnd;
	private ImageIcon imgIcon;
	private JLabel lblHAL;
	private JLabel lblThank, lblDamage, lblTask, lblAttack, lblTotal;
	
	private int total_damage;
	private int total_task;
	private int total_attack;
	private int final_score;
	
	public int GetTotalDamage() {
		return total_damage;
	}
	
	public int GetTotalTask() {
		return total_task;
	}
	
	public int GetTotalAttack() {
		return total_attack;
	}
	
	public int GetFinalScore() {
		return final_score;
	}
	
	public FrameEnd(GUI_Listener l, Game g) {
		super("RESCHU Security-Aware");
		setAlwaysOnTop(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { 
				System.exit(0);
			}
		});
		setLayout(new GridLayout(0,1));
		setResizable(false);
		
		// lsnr = l;
		game = g;
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
		
		total_damage = game.getVehicleList().getTotalDamage();
		total_task = game.GetTotalTask();
		total_attack = game.GetDetectedAttack();
		final_score = (100-total_damage+total_task+total_attack);
		
		lblThank  = new JLabel("Thank you for your participation!");
		lblDamage = new JLabel("Total UAV damage is "+total_damage);
		lblTask   = new JLabel("Total tasks done is "+total_task);
		lblAttack = new JLabel("Total detect attack "+total_attack);
		lblTotal  = new JLabel("Your Total score is "+final_score);
        		
		btnEnd = new JButton("EXIT");
		btnEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if( e.getSource() == btnEnd )
					System.exit(0);}
		});
		
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
		pnl.add(lblHAL);
		pnl.add(lblThank);
		pnl.add(lblDamage);
		pnl.add(lblTask);
		pnl.add(lblAttack);
		pnl.add(lblTotal);
		pnl.add(Box.createGlue());
		pnl.add(btnEnd);
		lblHAL.setAlignmentX(CENTER_ALIGNMENT);
		lblThank.setAlignmentX(CENTER_ALIGNMENT);
		lblDamage.setAlignmentX(CENTER_ALIGNMENT);
		lblTask.setAlignmentX(CENTER_ALIGNMENT);
		lblAttack.setAlignmentX(CENTER_ALIGNMENT);
		lblTotal.setAlignmentX(CENTER_ALIGNMENT);
		btnEnd.setAlignmentX(CENTER_ALIGNMENT);
		add(pnl);
	}
}