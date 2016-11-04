package reschu.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import reschu.game.view.UAVMonitor;

public class AttackNotificationEngine {
	
	public static final String ATTACK_NOTIFICATIONS_FILENAME = "AttackNotificationFile.txt";
	// attackfile should specify attack in the format: vehicle number,attack time 
	// where vehicle number is int and attack time is time in seconds after game starts
	public Map<Integer, Integer> hackData; // Map Vehicles to the notification times
	public Map<String, Integer> timerToVehicle; // Map Timers to Vehicles
	
	private UAVMonitor uavMonitor;
	private VehicleList vehicleList;
	
	public AttackNotificationEngine(final VehicleList vehicleList, UAVMonitor uavMonitor) throws FileNotFoundException {
		System.out.println("attack notifications engine loaded");
		File attackFile = new File(ATTACK_NOTIFICATIONS_FILENAME);
		BufferedReader br = new BufferedReader(new FileReader(attackFile)); 
		timerToVehicle = new HashMap<String, Integer>();
		hackData = new HashMap<Integer, Integer>();
		String line;  
		Timer nextTimer;
		String nextTime;
		
		int delay;
		int vehicle;
		
		this.vehicleList = vehicleList;
		this.uavMonitor = uavMonitor;
		
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
					vehicle = Integer.parseInt(attackParams[0]);
					delay = Integer.parseInt(attackParams[1]);
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
	
	public void launchHackWarning(int VehicleID) {
		// Launch warning 
		Object[] options = {"Investigate", "Ignore"};
		JOptionPane hackPane = new JOptionPane(); 
		int selectedValue = JOptionPane.showOptionDialog(hackPane, "Vehicle " + VehicleID + " seems to be malfunctioning. Please contact Mahmoud for details.", "Security Alert", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
		System.out.println("elfar:" + selectedValue);
		if (selectedValue == 0) {
			uavMonitor.enableUAVFeed(vehicleList.getVehicle(VehicleID));
			System.out.println("blahblah");
		}
		else {
			System.out.println("Burden alert");
		}
		
	}

}
