package reschu.app;

import java.awt.*;  
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import info.clearthought.layout.TableLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import reschu.constants.*;
import reschu.game.controller.Reschu;

public class AppMain implements ActionListener
{
	final private boolean WRITE_TO_DATABASE = false;
	
	private String _username;
	private int _gamemode = MyGameMode.ADMINISTRATOR_MODE;
	private int _scenario;
	private int _practice;
	private JFrame _frmLogin;
	private JButton _btnStart;
	private JComboBox _cmbBoxGameMode, _cmbBoxScenario, _cmbBoxPractice;
	private JTextField _cmbTextUserID;
	private Reschu reschu;
	
	/**
	 * When tutorial is finished, RESCHU automatically restarts in the training mode.
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
    public void Restart_Reschu() throws NumberFormatException, IOException {
        if( _gamemode == MyGameMode.TUTORIAL_MODE ) {
        	_gamemode = MyGameMode.TRAIN_MODE;
        }
        reschu.gameEnd();
        reschu.dispose();
        reschu = null;
        
        initRESCHU(_username, _scenario, _practice);
    }
    	
    /**
     * This should NEVER be called if you are NOT in the tutorial mode.
     */
    public void TutorialFinished() {
    }
    
    // for debugging mode
    public void initRESCHU_public(String username, int scenario, int practice) {
    	_gamemode = 1;
    	try {
			initRESCHU(username,  scenario, practice);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	private void initRESCHU(String username, int scenario, int practice) throws NumberFormatException, IOException 
	{	  
		// Setting _scenario again seems counter-intuitive here. 
		// Since we are differentiating between administrators and subjects,
		// we need to update the scenario number here again.
		_scenario = scenario;
		_practice = practice;
	    
		// Create an instance of Reschu (JFrame)
		reschu = new Reschu(_gamemode, _scenario, _practice, _username, this, WRITE_TO_DATABASE);		      
		reschu.pack(); 
		reschu.setVisible(true); 
		reschu.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	private void setFrmLogin() 
	{
		TitledBorder border; 
		ImageIcon imgIcon;
		
		JLabel lblHAL, lblGameMode, lblScenario, lblPractice, lblUserId;  
		
		JPanel pnl = new JPanel();
		JPanel pnlInside = new JPanel();
		
		// String[] scenarios = {"None", "Scenario 1", "Scenario 2", "Scenario 3", "Scenario 4", "Scenario 5", "Scenario 6"};
		// String[] gamemodes = {"Tutorial", "Demo"};
		String[] scenarios = {"Scenario 1", "Scenario 2"};
		String[] gamemodes = {"Experiment"};
		String[] practices = {"Practice", "Experiment"};
				
		border = BorderFactory.createTitledBorder("");
		
		lblHAL = new JLabel();
		lblUserId = new JLabel("User ID");
		lblGameMode = new JLabel("Mode");
		lblScenario = new JLabel("Scenario");
		lblPractice = new JLabel("Mode");
		_btnStart = new JButton("START"); 
		_btnStart.addActionListener(this);
		_cmbTextUserID = new JTextField();
		_cmbTextUserID.addActionListener(this);
		_cmbBoxGameMode = new JComboBox(gamemodes);
		_cmbBoxGameMode.addActionListener(this);
		_cmbBoxScenario = new JComboBox(scenarios);	
		_cmbBoxScenario.addActionListener(this);
		_cmbBoxPractice = new JComboBox(practices);
		_cmbBoxPractice.addActionListener(this);
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("Pictures/HAL/HAL.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
					
		imgIcon = new ImageIcon(img);  //HERE
		lblHAL = null; 
		lblHAL = new JLabel("", imgIcon, JLabel.CENTER);
		
		_frmLogin = new JFrame("RESCHU");
		_frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frmLogin.setLayout(new GridLayout(0,1));
		_frmLogin.setResizable(false);
		_frmLogin.add(pnl);
		_frmLogin.setLocation(300,300);
		_frmLogin.setAlwaysOnTop(true);
		_frmLogin.setVisible(true);        
        
		double sizeMain[][] = {{TableLayout.FILL, 50, 238, 50, TableLayout.FILL}, 
				{10, 194, 150, TableLayout.FILL}};				
		double sizeInside[][] = {{TableLayout.FILL, 60, 10, 140, TableLayout.FILL}, 
				{TableLayout.FILL, 10, 0, 25, 3, 25, 3, 25, 10, 25, TableLayout.FILL}};
					
		pnlInside.setLayout(new TableLayout(sizeInside));
		pnlInside.setBorder(border);
		// pnlInside.add(lblGameMode, "1,3");
		// pnlInside.add(_cmbBoxGameMode, "3,3");
		pnlInside.add(lblUserId, "1,3");
		pnlInside.add(_cmbTextUserID, "3, 3");
		pnlInside.add(lblScenario, "1,5");
		pnlInside.add(_cmbBoxScenario, "3,5");
		pnlInside.add(lblPractice, "1,7");
		pnlInside.add(_cmbBoxPractice, "3,7");
		pnlInside.add(_btnStart, "1,9, 3,9");

		_btnStart.setEnabled(true);					
		
		pnl.setLayout(new TableLayout(sizeMain));
		pnl.setBorder(border);
		pnl.setBackground(Color.WHITE);		
		pnl.add(lblHAL, "1,1, 3,1");
		pnl.add(pnlInside, "2,2");
 
		_frmLogin.setSize(400, 400);
	}

	public void actionPerformed(ActionEvent ev) {
		if( ev.getSource() == _cmbBoxGameMode ) {
			_gamemode = _cmbBoxGameMode.getSelectedIndex();
			switch(_gamemode) {
				// case 0: _gamemode = MyGameMode.TUTORIAL_MODE; break;
				// case 1: _gamemode = MyGameMode.ADMINISTRATOR_MODE; break;
				case 0: _gamemode = MyGameMode.ADMINISTRATOR_MODE; break;
			}
		}
		if( ev.getSource() == _cmbBoxScenario ) {  
			_scenario = _cmbBoxScenario.getSelectedIndex();
			// _btnStart.setEnabled(true);
		}
		if( ev.getSource() == _cmbBoxPractice ) {  
			_practice = _cmbBoxPractice.getSelectedIndex();
		}
		if( !_cmbTextUserID.getText().equals("") ) {  
			_username = _cmbTextUserID.getText();
		}
		if( ev.getSource() == _btnStart ) {
			try {
				initRESCHU(_username, _scenario, _practice);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}							
			_frmLogin.setVisible(false); 		
			_frmLogin.dispose();
		}
	}
	
	static public void main (String argv[])
	{   
		SwingUtilities.invokeLater(new Runnable() {
            public void run () {
    			AppMain app = new AppMain();
    			// for debugging mode
    			// app.initRESCHU_public("", 1);
    			app.setFrmLogin();
            }
        });			
	}    
}
