package reschu.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.HashMap;

public class AttackEngine {
	public static final String ATTACK_FILENAME = "AttackFile.txt";
	// USAGE NOTE
	// AttackFile.txt should specify attack in the format: vehicle number,attack time 
	// where vehicle number is int and attack time is time in milliseconds after game starts
	
	public Map<Integer, String> hackData; // Map Vehicles to their hacked coordinates
	public Map<String, Integer> timerToVehicle; // Map Timers to Vehicles

	public AttackEngine(final VehicleList vehicleList) throws FileNotFoundException {
		String line, location, action;
		Timer nextTimer;
		int delay;
		int vehicle;
		
		File attackFile = new File(ATTACK_FILENAME);
		BufferedReader br = new BufferedReader(new FileReader(attackFile)); 
		timerToVehicle = new HashMap<String, Integer>();
		hackData = new HashMap<Integer, String>();
		
		class Hack extends TimerTask {	
			String timerName;
			public Hack(String timerName) {
				this.timerName = timerName;
			}

			@Override 
			public void run() {
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
			// Parse AttackFile
			// NOTIFY,VEH_NO,TIME 
			// ATTACK,VEH_NO,TIME,NEW_X_TARGET NEW_Y_TARGET
			while ((line = br.readLine()) != null){  
				if (!line.startsWith("//")){
					String[] attackParams = line.split(",");
					action = attackParams[0];
					if (!action.equals("ATTACK")){
						continue;
					}
					vehicle = Integer.parseInt(attackParams[1]) - 1;
					delay = Integer.parseInt(attackParams[2]);
					location = attackParams[3];
					hackData.put(vehicle, location);
					String timerName = "HackTimer" + vehicle;// TEMP : IN FUTURE, SAME VEHICLE CAN APPEAR IN MULTIPLE LINES
					nextTimer = new Timer(timerName);
					nextTimer.schedule(new Hack(timerName), delay);
					timerToVehicle.put(timerName, vehicle);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("Illegal non-numeric values in hacking input file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Illegal non-numeric values in hacking input file");
			e.printStackTrace();
		} 
	}
}
