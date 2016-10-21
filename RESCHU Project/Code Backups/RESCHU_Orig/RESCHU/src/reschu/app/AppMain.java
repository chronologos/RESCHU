package reschu.app;

import java.awt.*;  
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import reschu.constants.*;
import reschu.game.controller.Reschu;

public class AppMain implements ActionListener
{
	final private boolean WRITE_TO_DATABASE = false;
	
	private String _username;
	private int _gamemode = MyGameMode.TUTORIAL_MODE;
	private int _scenario;			
	private JFrame _frmLogin;
	private JButton _btnStart;
	private JComboBox _cmbBoxGameMode, _cmbBoxScenario;	 
	private Reschu reschu;
	
	/**
	 * When tutorial is finished, RESCHU automatically restarts in the training mode.
	 */
    public void Restart_Reschu() {
        if( _gamemode == MyGameMode.TUTORIAL_MODE ) {
        	_gamemode = MyGameMode.TRAIN_MODE;
        }
        reschu.Game_End();
        reschu.dispose();
        reschu = null;
        
        initRESCHU(_username, _scenario);
    }
    	
    /**
     * This should NEVER be called if you are NOT in the tutorial mode.
     */
    public void TutorialFinished() {
    }
    
	private void initRESCHU(String username, int scenario) 
	{	  
		// Setting _scenario again seems counter-intuitive here. 
		// Since we are differentiating between administrators and subjects,
		// we need to update the scenario number here again.
		_scenario = scenario;
	    
		// Create an instance of Reschu (JFrame)
		reschu = new Reschu(_gamemode, scenario, _username, this, WRITE_TO_DATABASE);		      
		reschu.pack(); 
		reschu.setVisible(true); 
		reschu.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	private void setFrmLogin() 
	{
		TitledBorder border; 
		ImageIcon imgIcon;
		
		JLabel lblHAL, lblGameMode, lblScenario; 
		
		JPanel pnl = new JPanel();
		JPanel pnlInside = new JPanel();
		
		String[] scenarios = {"None", "Scenario 1", "Scenario 2", "Scenario 3", "Scenario 4"};
		String[] gamemodes = {"Tutorial", "Demo"};
				
		border = BorderFactory.createTitledBorder("");
		
		lblHAL = new JLabel();
		lblGameMode = new JLabel("Mode");
		lblScenario = new JLabel("Scenario"); 
		_btnStart = new JButton("START"); 
		_btnStart.addActionListener(this);		
		_cmbBoxGameMode = new JComboBox(gamemodes);
		_cmbBoxGameMode.addActionListener(this);
		_cmbBoxScenario = new JComboBox(scenarios);	
		_cmbBoxScenario.addActionListener(this);
		
		try {
			imgIcon = new ImageIcon(new URL(MyURL.URL_PREFIX + "HAL.png"));
			lblHAL = null; 
			lblHAL = new JLabel("", imgIcon, JLabel.CENTER);
        } catch (MalformedURLException urle) {}		
 
		_frmLogin = new JFrame("RESCHU");
		_frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frmLogin.setLayout(new GridLayout(0,1));
		_frmLogin.setResizable(false);		
		_frmLogin.add(pnl);		
		_frmLogin.setLocation(300,300);
		_frmLogin.setAlwaysOnTop(true);
		_frmLogin.setVisible(true);        
        
		double sizeMain[][] = {{TableLayout.FILL, 50, 238, 50, TableLayout.FILL}, 
				{10, 194, 130, TableLayout.FILL}};				
		double sizeInside[][] = {{TableLayout.FILL, 60, 10, 140, TableLayout.FILL}, 
				{TableLayout.FILL, 25, 3, 25, 3, 25, 10, 25, TableLayout.FILL}};
					
		pnlInside.setLayout(new TableLayout(sizeInside));
		pnlInside.setBorder(border);
		pnlInside.add(lblGameMode, "1,3");
		pnlInside.add(_cmbBoxGameMode, "3,3");
		pnlInside.add(lblScenario, "1,5");
		pnlInside.add(_cmbBoxScenario, "3,5");	
		pnlInside.add(_btnStart, "1,7, 3,7");

		_btnStart.setEnabled(false);					
		
		pnl.setLayout(new TableLayout(sizeMain));
		pnl.setBorder(border);
		pnl.setBackground(Color.WHITE);		
		pnl.add(lblHAL, "1,1, 3,1");
		pnl.add(pnlInside, "2,2");
 
		_frmLogin.setSize(400,370);
	}

	public void actionPerformed(ActionEvent ev) {
		if( ev.getSource() == _cmbBoxGameMode ) {
			_gamemode = _cmbBoxGameMode.getSelectedIndex();
			switch(_gamemode) {
				case 0: _gamemode = MyGameMode.TUTORIAL_MODE; break;
				case 1: _gamemode = MyGameMode.ADMINISTRATOR_MODE; break;
			}
		}
		if( ev.getSource() == _cmbBoxScenario ) {  
			_scenario = _cmbBoxScenario.getSelectedIndex();
			_btnStart.setEnabled(true);			
		}
		if( ev.getSource() == _btnStart ) {
			initRESCHU(_username, _scenario);							
			_frmLogin.setVisible(false); 		
			_frmLogin.dispose();
		}
	}
	
	static public void main (String argv[])
	{   
		SwingUtilities.invokeLater(new Runnable() {
            public void run () {
    			AppMain app = new AppMain();
    			app.setFrmLogin();
            }
        });			
	}    
}