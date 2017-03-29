package reschu.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import reschu.constants.MyGame;

import java.util.Map;
import java.util.HashMap;

public class AttackEngine {
	public static final String ATTACK_FILENAME = MyGame.AttackFile;
	// USAGE NOTE
	// AttackFile.txt should specify attack in the format: vIdx number,attack time 
	// where vIdx number is int and attack time is time in milliseconds after game starts
	
	public Map<Integer, String> hackData; // Map vIdxs to their hacked coordinates
	public Map<String, Integer> timerTovIdx; // Map Timers to vIdxs
	public Map<Integer, String> timerToLoc;

	public AttackEngine(final VehicleList vehicle_list) throws FileNotFoundException {
		String line, location, action;
		Timer nextTimer;
		int delay;
		int vIdx;
		
		File attackFile = new File(ATTACK_FILENAME);
		BufferedReader br = new BufferedReader(new FileReader(attackFile)); 
		timerTovIdx = new HashMap<String, Integer>();
		hackData = new HashMap<Integer, String>();
		timerToLoc = new HashMap<Integer, String>();
		
		class Hack extends TimerTask {	
			String timerName;
			Integer delay;
			public Hack(String timerName, Integer delay) {
				this.timerName = timerName;
				this.delay = delay;
			}

			@Override 
			public void run() {
				int vIdx = timerTovIdx.get(timerName);
				String location = timerToLoc.get(delay);
				try {
					if(!(vehicle_list.getVehicle(vIdx).getEngageStatus() || vehicle_list.getVehicle(vIdx).getPath().size()==0)) {
						if(!vehicle_list.getVehicle(vIdx).getHijackStatus() && !vehicle_list.getVehicle(vIdx).getLostStatus()) {
							// vehicle_list.getVehicle(vIdx).hijack(hackData.get(vIdx));
							vehicle_list.getVehicle(vIdx).hijack(location);
						}
					}
					else {
						Timer nTimer = new Timer(timerName);
						nTimer.schedule(new Hack(timerName, delay), 10000);
						System.out.println("Reschedule Hacking for UAV "+(vIdx+1));
					}
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
					vIdx = Integer.parseInt(attackParams[1]) - 1;
					delay = Integer.parseInt(attackParams[2]);
					location = attackParams[3];
					hackData.put(vIdx, location);
					String timerName = "HackTimer" + vIdx; // TEMP: IN FUTURE, SAME vIdx CAN APPEAR IN MULTIPLE LINES
					nextTimer = new Timer(timerName);
					timerToLoc.put(delay, location);
					nextTimer.schedule(new Hack(timerName, delay), delay);
					timerTovIdx.put(timerName, vIdx);
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
