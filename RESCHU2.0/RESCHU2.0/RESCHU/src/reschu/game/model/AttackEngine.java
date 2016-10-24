package reschu.game.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//import javax.swing.Timer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;

public class AttackEngine {
	//private Timer attackTimer;
	//private List<Timer> attackTimers;
	public static final String ATTACK_FILENAME = "AttackFile.txt";
	//public int delay; // REQ-INI-0004
	//public int vehicle; //TODO, support multi-vehicle attacks
	// attackfile should specify attack in the format: vehicle number,attack time 
	// where vehicle number is int and attack time is time in milliseconds after game starts
	public Map<Integer, String> hackData; // Map Vehicles to their hacked coordinates
	public Map<String, Integer> timerToVehicle; // Map Timers to Vehicles
	
	public AttackEngine(final VehicleList vehicleList) throws FileNotFoundException {
		System.out.println("attack engine loaded");
		File attackFile = new File(ATTACK_FILENAME);
		BufferedReader br = new BufferedReader(new FileReader(attackFile)); 
		timerToVehicle = new HashMap<String, Integer>();
		//attackTimers = new ArrayList<Timer>();
		hackData = new HashMap<Integer, String>();
		String line = null;  
		Timer nextTimer = null;
		String location = null;
		
		int delay;
		int vehicle;
		
		class Hack extends TimerTask {	
			String timerName;
			public Hack(String timerName) {
				this.timerName = timerName;
			}
			
			@Override 
			public void run() {
				String hackLocation = hackData.get(timerName);
				int vehicle = timerToVehicle.get(timerName);
				try {
					vehicleList.getVehicle(vehicle).hijack(hackData.get(vehicle));
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
					location = attackParams[2];
					hackData.put(vehicle, location);
					String timerName = "HackTimer" + vehicle;// TEMP : IN FUTURE, SAME VEHICLE CAN APPEAR IN MULTIPLE LINES
					nextTimer = new Timer(timerName);
					nextTimer.schedule(new Hack(timerName), delay);
					timerToVehicle.put(timerName, vehicle);
					//attackTimers.add(nextTimer);
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("Illegal non-numeric values in hacking input file");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Illegal non-numeric values in hacking input file");
			e.printStackTrace();
		} 
		//System.out.printf("%d, %d", delay, vehicle);		
		//attackTimer = new Timer(delay, taskPerformer);
		//attackTimer.setRepeats(false);
		//attackTimer.start();
		}
}