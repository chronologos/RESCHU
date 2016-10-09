package reschu.game.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Timer;

public class AttackEngine {
	private Timer attackTimer;
	private Timer attackEndTimer;
	public static final String ATTACK_FILENAME = "AttackFile.txt";
	public int delay; // REQ-INI-0004
	public int vehicle; //TODO, support multi-vehicle attacks
	// attackfile should specify attack in the format: vehicle number,attack time 
	// where vehicle number is int and attack time is time in milliseconds after game starts
	public AttackEngine(final VehicleList vehicleList) throws FileNotFoundException {
		System.out.println("attack engine loaded");
		File attackFile = new File(ATTACK_FILENAME);
		BufferedReader br = new BufferedReader(new FileReader(attackFile)); 
		String line = null;  
		try {
			while ((line = br.readLine()) != null){  
				if (!line.startsWith("//")){
					String[] attackParams = line.split(",");
					vehicle = Integer.parseInt(attackParams[0]);
					delay = Integer.parseInt(attackParams[1]);
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.printf("[AttackEngine.java] %d, %d %n", delay, vehicle);
		ActionListener hijackVehicle = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				vehicleList.getVehicle(vehicle).hijack();
			}
		};
		ActionListener endHijackVehicle = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				vehicleList.getVehicle(vehicle).endHijack();
			}
		};
		
		attackTimer = new Timer(delay, hijackVehicle);
		attackEndTimer = new Timer(delay*2, endHijackVehicle);
		attackTimer.setRepeats(false);
		attackEndTimer.setRepeats(false);
		attackTimer.start();
		attackEndTimer.start();

	}
}
