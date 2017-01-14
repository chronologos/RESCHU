package reschu.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.io.FileNotFoundException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import reschu.game.controller.GUI_Listener;
import reschu.game.controller.Reschu;
import reschu.game.view.UAVMonitor;
import reschu.userinput.UserInputEvent;
import reschu.userinput.UserInputListener;

import reschu.game.view.PanelMsgBoard;

public class AttackNotificationEngine {//implements UserInputListener {

	public static final String ATTACK_NOTIFICATIONS_FILENAME = "AttackFile.txt";
	// attackfile should specify attack in the format: vehicle number,attack time 
	// where vehicle number is int and attack time is time in seconds after game starts
	public Map<Integer, Integer> hackData; // Map Vehicles to the notification times
	public Map<String, Integer> timerToVehicle; // Map Timers to Vehicles
	private GUI_Listener lsnr; // Reschu.java
	//private Reschu reschu;
	private boolean hackPaneOpen; // track as instance variable so that new pane can close old one
	private JOptionPane hackPane;
	private FileWriter logFile;
	
	public AttackNotificationEngine(GUI_Listener l) throws FileNotFoundException {
	//public AttackNotificationEngine(Reschu l) throws FileNotFoundException {
		lsnr = l;
		//reschu = l;
		System.out.println("attack notifications engine loaded");
		File attackFile = new File(ATTACK_NOTIFICATIONS_FILENAME);
		BufferedReader br = new BufferedReader(new FileReader(attackFile)); 
		timerToVehicle = new HashMap<String, Integer>();
		hackData = new HashMap<Integer, Integer>();
		String line;  
		Timer nextTimer;
		String nextTime;

		//this.logFile = logFile;
		
		
		int delay;
		int vehicle;

		String action;


		class Hack extends TimerTask {	
			String timerName;
			public Hack(String timerName) {
				this.timerName = timerName;
			}

			@Override 
			public void run() {
				//Integer hackTime = hackData.get(timerName);
				int vehicle = timerToVehicle.get(timerName);
				int hackTime = hackData.get(vehicle);
				try {
					//vehicleList.getVehicle(vehicle).hijack(hackData.get(vehicle));
					System.out.println("Launching hack warning");
					launchHackWarning(vehicle);
				}
				catch(IllegalArgumentException e) {
					System.out.println("Hack data file has illegal hack coordinates");
				}
			}			
		};



		try {
			while ((line = br.readLine()) != null){  
				if (!line.startsWith("//")){
					String[] attackParams = line.split(",");
					action = attackParams[0];
					if (!action.equals("NOTIFY")){
						continue;
					}
					vehicle = Integer.parseInt(attackParams[1]);
					delay = Integer.parseInt(attackParams[2]);
					//hackData.put(vehicle, location);
					hackData.put(vehicle, delay);
					String timerName = "HackTimer" + vehicle;// TEMP : IN FUTURE, SAME VEHICLE CAN APPEAR IN MULTIPLE LINES
					nextTimer = new Timer(timerName);
					nextTimer.schedule(new Hack(timerName), delay);
					timerToVehicle.put(timerName, vehicle);
					//attackTimers.add(nextTimer);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("Illegal non-numeric values in hacking input file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Illegal non-numeric values in hacking input file");
			e.printStackTrace();
		} 
		//System.out.printf("%d, %d", delay, vehicle);		
		//attackTimer = new Timer(delay, taskPerformer);
		//attackTimer.setRepeats(false);
		//attackTimer.start();
	}

	@SuppressWarnings("deprecation")
	public void launchHackWarning(int VehicleID) {
		// Launch warning 
		if (hackPaneOpen && hackPane != null){
			System.out.println("Closing existing jpane");
			//hackPane.getRootPane().setVisible(false);
			hackPane.setVisible(false);
		}
		Object[] options = {"Investigate", "Ignore"};
		hackPane = new JOptionPane(); 
		JDialog optionDialog = new JDialog();
		optionDialog.
//		
//		 JOptionPane             pane = new JOptionPane(message, messageType,
//                 optionType, icon,
//                 options, initialValue);
//
//pane.setInitialValue(initialValue);
//pane.setComponentOrientation(((parentComponent == null) ?
//getRootFrame() : parentComponent).getComponentOrientation());
//
//int style = styleFromMessageType(messageType);
//JDialog dialog = pane.createDialog(parentComponent, title, style);
//
//pane.selectInitialValue();
//dialog.show();
//dialog.dispose();


		hackPaneOpen = true;
		PanelMsgBoard.Msg("Vehicle ["+VehicleID+"] might be hacked."); 
		int selectedValue = JOptionPane.showOptionDialog(hackPane, "Vehicle " + VehicleID + " seems to be malfunctioning. Please contact Mahmoud for details.", "Security Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
		//JDialog dialog = hackPane.createDialog("title");
		//dialog.addC
		 lsnr.EVT_Hack_Notification_Launch(VehicleID);
		 
		
		System.out.println("elfar:" + selectedValue);
		if (selectedValue == 0) {
			
			lsnr.EVT_Hack_Notification_Investigate(VehicleID);
			
			lsnr.activateUAVFeed(VehicleID);
			lsnr.Vehicle_Selected_From_pnlMap(VehicleID);
			lsnr.EVT_VSelect_Map_LBtn(VehicleID); 
			System.out.println("blahblah");
		}
		else {
			System.out.println("Burden alert");
			lsnr.EVT_Hack_Notification_Ignore(VehicleID);
			
		}
		hackPaneOpen = false;
	}
	
	/*
	public void UserInput(UserInputEvent e) {
		if (e.t)
		System.out.println("User ignored hack alert");
	}
	*/

}
