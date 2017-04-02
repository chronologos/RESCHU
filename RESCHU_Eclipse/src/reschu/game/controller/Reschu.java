package reschu.game.controller;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.border.TitledBorder; 

import reschu.app.AppMain;
import reschu.constants.*;
import reschu.game.model.AttackEngine;
import reschu.game.model.AttackNotificationEngine;
import reschu.game.model.Game;
import reschu.game.model.Payload;
import reschu.game.model.Target;
import reschu.game.model.UserDefinedException;
import reschu.game.model.Vehicle;
import reschu.game.utils.SituationAwareness;
import reschu.game.utils.WAVPlayer;
import reschu.game.view.MyCanvas;
import reschu.game.view.PanelControl;
import reschu.game.view.PanelMap;
import reschu.game.view.PanelMsgBoard;
import reschu.game.view.PanelPayload;
import reschu.game.view.PanelPayloadControls;
import reschu.game.view.PanelTimeLine;
import reschu.game.view.TextOverlay;
import reschu.game.view.UAVMonitor;
import reschu.tutorial.Tutorial;
import info.clearthought.layout.TableLayout;

import java.util.Random;

public class Reschu extends JFrame implements GUI_Listener {
	public Random generator = new Random(System.currentTimeMillis());
	public int randint = generator.nextInt(1000);
	public String randstr = Integer.toString(randint);
	private static final long serialVersionUID = -6078272171985479839L;
	private static final String DATE_FORMAT_NOW = "HH:mm:ss:S";

	public static String _username;
	public static int _scenario;		
	public static int _gamemode;		// the game has several modes. see reschu.constant.MyGameMode
	public static boolean _database; 	// if set to false, we don't write to database

	public JPanel pnlMapContainer, pnlPayloadContainer;
	public PanelControl pnlControl;
	public MyCanvas payload_canvas;
	public PanelPayload pnlPayload; 
	public PanelMap pnlMap;
	public PanelPayloadControls pnlPayloadControls; 
	public PanelMsgBoard pnlMsgBoard;
	public PanelTimeLine pnlTimeLine;
	public UAVMonitor uavMonitor;
	public AttackNotificationEngine attackNotificationEngine;
	public AttackEngine attackEngine;
	public TextOverlay payloadTextOverlay; //far01 pass zoom level to textoverlay

	public Game game;
	private double origin_time;
	private int zoomLevel; //far01
	private TitledBorder bdrTitle;  
	private Tutorial tutorial; 
	public String filename;
	private Random rnd = new Random();

	/** Interactive Tutorial Mode? */
	public static boolean tutorial() { return _gamemode == MyGameMode.TUTORIAL_MODE; }
	// return if scenario is low taskload
	public static boolean low_taskload() {return _scenario == 1;}
	/** Training Mode? */
	public static boolean train() { return _gamemode == MyGameMode.TRAIN_MODE; }
	/** Replay Mode? */
	public static boolean replay() { return _gamemode == MyGameMode.REPLAY_MODE; }
	/** Experiment Mode? */
	public static boolean expermient() {
		return ( _gamemode == MyGameMode.ADMINISTRATOR_MODE || _gamemode == MyGameMode.USER_MODE );
	}

	/**
	 * Normal constructor for RESCHU.
	 * @param gamemode
	 * @param scenario
	 * @param username
	 * @param main
	 * @param database
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public Reschu(int gamemode, int scenario, String username, AppMain main, boolean database) throws NumberFormatException, IOException {
		super("RESCHU Security-Aware");
		_gamemode = gamemode;
		_scenario = scenario;
		_username = username;
		_database = database;      
		setDefaultCloseOperation(EXIT_ON_CLOSE); 

		if( tutorial() ) tutorial = new Tutorial(main);
		if( train() ) {
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) { 
					if( game.isRunning() ) {
						setVisible(false);
						JOptionPane.showMessageDialog(null,
								"Congratulations! You are now ready to proceed to the main experiment (10 mins in length).", "Message", 1);
						JOptionPane.showMessageDialog(null,
								"Please follow the appropriate link in on our website.", "Message", 1);
					}
				}
			});
		}

		restartPayloadText();
		writeScenarioCount();
		initComponents();

	}    
	public void writeScenarioCount() throws IOException{
		if (!MyLogging.WRITE_TO_DISK){
			return;
		}
		BufferedReader br = new BufferedReader(new FileReader("ScenarioCount.txt"));  

		String line1 = "";
		String line2 = "";
		String line = "";
		for(int i = 0; i <2; i++){
			line = br.readLine();

			if(i == 0){
				line1 = line;
			}
			if(i == 1){
				line2 = line;
			}
		}
		br.close();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ScenarioCount.txt",false)));

		if(_scenario == 4){
			String[] aline1 = line1.split("#");
			String[] aline2 = line2.split("#");
			int cnt1 = Integer.parseInt(aline1[1].trim());
			int cnt2 = Integer.parseInt(aline2[1].trim());
			if((cnt1 > 2 || cnt2 > 2) || (cnt1 == 2 && cnt2 == 0) || (cnt1==2 && cnt2==2)
					){
				cnt1 = 0;
				cnt2 = 0;
			}
			else{
				cnt1++;
			}
			aline1[1] = Integer.toString(cnt1);
			aline2[1] = Integer.toString(cnt2);
			line1 = aline1[0].trim() + "# " + aline1[1] + "#";
			line2 = aline2[0].trim() + "# " + aline2[1] + "#";
			out.println(line1);
			out.println(line2);
			out.close();
		}
		if(_scenario == 6){
			String[] aline1 = line1.split("#");
			String[] aline2 = line2.split("#");
			int cnt1 = Integer.parseInt(aline1[1].trim());
			int cnt2 = Integer.parseInt(aline2[1].trim());
			if((cnt1 > 2 || cnt2 > 2) || (cnt1==2 && cnt2==2)	){
				cnt1 = 0;
				cnt2 = 0;
			}
			else{
				cnt2++;
			}
			aline1[1] = Integer.toString(cnt1);
			aline2[1] = Integer.toString(cnt2);
			line1 = aline1[0].trim() + "# " + aline1[1] + "#";
			line2 = aline2[0].trim() + "# " + aline2[1] + "#";
			out.println(line1);
			out.println(line2);
			out.close();
		}
		else{
			out.println(line1);
			out.println(line2);
			out.close();
		}
	}
	public void restartPayloadText() throws IOException{

		BufferedReader br1 = new BufferedReader(new FileReader("ScenarioCount.txt"));
		String line1 = "";
		Boolean done1 = false;

		while((line1 = br1.readLine()) != null){

			String[] aline1 = line1.split("#");

			if(aline1[0].trim().equals("Scenario4") && aline1[1].trim().equals("2")){
				String nextline = br1.readLine();

				String[] aline2 = nextline.split("#");
				if(aline2[0].trim().equals("Scenario6") && aline2[1].trim().equals("0")
						|| aline2[0].trim().equals("Scenario6") && aline2[1].trim().equals("2")){

					done1 = true;
				}
				else{
					break;
				}
			}
			else{
				break;
			}
		}
		br1.close();
		if(done1){

			BufferedReader br = new BufferedReader(new FileReader("PayloadText.txt"));  
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("PayloadText.txt",false)));
			while ((br.readLine()) != null) {
				out.println("");
			}
			out.close();
			br.close();
		}

	}
	
	private void initComponents() throws NumberFormatException, IOException {  
		double sizeMain[][] = {{TableLayout.FILL, 440, 5, 820+170 /* was 820 only*/, TableLayout.FILL}, 
				{370, 110, TableLayout.FILL, 200}};
		double sizePayload[][] = {{TableLayout.FILL, 0.1}, {TableLayout.FILL}};
		double sizeMap[][] = {{TableLayout.FILL}, {TableLayout.FILL}};

		Container contentPane = getContentPane();
		contentPane.setLayout(new TableLayout(sizeMain));

		game = new Game(this, _scenario);        
		origin_time = System.currentTimeMillis();

		payload_canvas = new MyCanvas();
		pnlPayload = new PanelPayload(this, "PAYLOAD_PANEL", payload_canvas, game,"Pictures/Tiles", 4000, 4000);
		payload_canvas.addListener(pnlPayload);
		payload_canvas.addGLEventListener(pnlPayload);
		
		payloadTextOverlay = new TextOverlay();
		payload_canvas.addGLEventListener(payloadTextOverlay);
		
		uavMonitor = new UAVMonitor(pnlPayload);
		pnlPayload.setUAVMonitor(uavMonitor);

		// Create Each Panel Objects
		pnlMap = new PanelMap(this, game, "MAP_PANEL");
		// make mutual reference between Reschu and PanelMap
		// pnlMap.setRESCHU(this);
		pnlControl = new PanelControl(this, game, "CONTROL_PANEL");
		pnlPayloadControls = new PanelPayloadControls(this, "PAYLOAD_CONTROLS", origin_time);  
		pnlMsgBoard = new PanelMsgBoard();
		pnlMsgBoard.setReschuInstance(this);
		pnlTimeLine = new PanelTimeLine(game, game.getVehicleList());

		// Panel Payload Container Setup
		pnlPayloadContainer = new JPanel();     // and this
		bdrTitle = BorderFactory.createTitledBorder("Payload");
		pnlPayloadContainer.setBorder(bdrTitle);
		pnlPayloadContainer.setLayout(new TableLayout(sizePayload));
		pnlPayloadContainer.add(payload_canvas, 	"0,0");
		pnlPayloadContainer.add(pnlPayloadControls, "1,0");

		// Panel Map Container Setup
		pnlMapContainer = new JPanel();        
		bdrTitle = BorderFactory.createTitledBorder("Map"); 
		pnlMapContainer.setBorder(bdrTitle);
		pnlMapContainer.setLayout(new TableLayout(sizeMap)); 
		pnlMapContainer.add(pnlMap, "0,0");
		pnlPayloadControls.setEnabled(false);
		pnlPayloadControls.setListener(this); 

		add(pnlPayloadContainer, 	"1, 0");  // what is add?
		add(pnlMsgBoard, 			"1, 1");
		add(pnlControl, 			"1, 2");
		add(pnlTimeLine, 			"1, 3");
		add(pnlMapContainer, 		"3, 0, 3, 3");

		// Create PanelMap's double-buffer
		// The new JFrame does not make a back buffer (swing automatically creates it)
		// This has to be commented out if we do not extend JApplet.
		// pnlMap.init_buffer();

		// Disable the map panel until the game starts
		pnlMap.setEnabled(false); 

		//Prepare file for saving

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:S");
		SimpleDateFormat date = new SimpleDateFormat("EEE,MMM d,yyyy");
		String temp_1 = sdf.format(cal.getTime());
		String temp_2 = "Time   / Invoker / Type / vIdx /     log     / Coordinate X / Coordinate Y";
		String test = "logs/" + date.format(cal.getTime()) + "rand" +  randstr +  ".txt";

		//For string
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(test)));
			out.println(temp_1);
			out.println(temp_2);
			out.close();
		} catch (IOException e) {
			System.out.println("Warning: File NOT correctly written to.  Reschu:Write()");
			//exception handling left as an exercise for the reader
		}
	}

	/**
	 * For situation awareness research on RESCHU. 
	 * Checks whether there is any hazard area in between a vehicle's path.
	 * @param vIdx
	 */
	private void checkIntersect(int vIdx) {
		int haIdx = SituationAwareness.checkIntersect(
				game.getVehicleList().getVehicle(vIdx-1), 
				game.map.getListHazard(),  
				MySize.SIZE_HAZARD_3_PXL);
		if( haIdx > 0 ) { 
			if( !game.getVehicleList().getVehicle(vIdx-1).getIntersect() ) { 
				game.getVehicleList().getVehicle(vIdx-1).setIntersect(true);
				EVT_Vehicle_IntersectHazardArea(vIdx, game.map.getListHazard().get(haIdx));
			}
		} else {
			if( game.getVehicleList().getVehicle(vIdx-1).getIntersect() ) { 
				game.getVehicleList().getVehicle(vIdx-1).setIntersect(false);
				EVT_Vehicle_EscapeHazardArea(vIdx);
			}
		}    		
	} 

	//Gui_Listener Interface
	
	@Override
	public void vehicleLocationChanged(){
		// TODO: Do I really want to repaint the whole map here?
		pnlMap.repaint();
	}
	
	@Override
	public void clockTick(int milliseconds) {
		if(tutorial())
			if( milliseconds % 1000 == 0 ) tutorial.tick();
		pnlTimeLine.refresh(milliseconds);
		pnlControl.chkEngageEnabled();

		// This decreases the remaining duration of TextOnTop (the warning msg)
		pnlMap.decreaseTextOnTopTime();
	}
	
	@Override
	public void gameStart() {
		if (!tutorial()) {
			try {
				attackNotificationEngine = new AttackNotificationEngine(this, game);
				attackEngine = new AttackEngine(game.getVehicleList());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		// screen resolution check for Java WebStart. 
		//    	if( getHeight() < MySize.MAP_HEIGHT_PXL ) {
		//    		setVisible(false);
		//    		JOptionPane.showMessageDialog(null,	
		//					"You need a minimum screen resolution of 1280 x 1024. Please try again.", "Message", 1);
		//    		System.exit(0);
		//    	}

		if( train() ) {
			// enables the "close" function when Training mode            
			setDefaultCloseOperation(EXIT_ON_CLOSE); 

			// Welcome message. This should encourage users to play this training mode.
			JOptionPane.showMessageDialog(null,	
					"Welcome to the last step before you proceed to the main experiment. " +
							"You will be able to train on a full-sized team.", "Message", 1);
			JOptionPane.showMessageDialog(null,	
					"Unlike the actual experiment, " +
							"the camera window in the training round will show the same image " +
							"as you engage different targets in order to give you a chance to practice." 
							, "Message", 1);
			JOptionPane.showMessageDialog(null,	
					"Train for as long as you want and close the window " +
							"when you are done to proceed to the main experiment. ", "Message", 1);
		}
		
		new Thread(game).start();
		// enable panels which are initially disabled
		pnlMap.setEnabled(true); 
	}

	@Override
	public void gameEnd() { 
		// PanelMsgBoard.Msg("YOUR TOTAL SCORE: " + game.getScore());
		EVT_System_GameEnd();
		game.stop();
		Thread.currentThread().interrupt();
	}

	/**
	 * Shows a message on the top of the PanelMap
	 * 
	 * @param msg message
	 * @param duration duration of displaying the message (in second)
	 */
	@Override
	public void showMessageOnTopOfMap(String msg, int duration) {
		pnlMap.setTextOnTop(msg, duration);
	}
	@Override
	public void rotateCWSelected(){
		return;
		//pnlPayload.r_c_2();
	}
	@Override
	public void panUpSelected(){
		//pnlPayload.pan_up();
		return;
	}
	@Override
	public void rotateCCWSelected(){
		//pnlPayload.r_c_c_2(); }
		return;
	}
	@Override
	public void panDownSelected(){
		return;
		//pnlPayload.pan_down(); }
	}
	// zoom functions, need to improve
	@Override
	public void zoomIn() {
		zoomLevel = pnlPayload.zoom_in();
		payloadTextOverlay.setZoomLevel(zoomLevel); //far01
		
		if(pnlPayload.getZoomCount() == pnlPayload.ZOOMMAX) {
			zoomMax();
		}
	}
	@Override
	public void zoomOut() {
		zoomLevel = pnlPayload.zoom_out();
		payloadTextOverlay.setZoomLevel(zoomLevel); //far01
		
		if(pnlPayload.getZoomCount() == pnlPayload.ZOOMMIN) {
			zoomMin();
		}
	}
	
	@Override
	public void zoomMax() {
		Write(MyDB.INVOKER_USER, MyDB.ZOOM_MAX, -1, "Clicked Zoom In, reached Zoom Max", -1, -1);
	}
	@Override
	public void zoomMin() {
		Write(MyDB.INVOKER_USER, MyDB.ZOOM_MIN, -1, "Clicked Zoom Out, reached Zoom Min", -1, -1);
	}
	
	@Override
	public void submitPayload() {
		pnlPayload.checkCorrect();
	}
	
	@Override
	public void activateUAVFeed(int idx) {
		uavMonitor.enableUAVFeed(game.getVehicleList().getVehicle(idx));
	}
	
	@Override
	public void Vehicle_Selected_From_pnlMap(int idx) { 
		System.out.println("Vehicle Selected From pnlMap " + idx);
		pnlControl.Show_Vehicle_Status(idx);
	}
	
	@Override
	public void Vechicle_Selected_From_Investigate(int idx) {
		pnlControl.Show_Vehicle_Status(idx+1); // TODO why doesn't it work without +1?
		pnlMap.setSelectedVehicle(game.getVehicleList().getVehicle(idx));
	}
	
	@Override
	public void Vehicle_Unselected_From_pnlMap() {
		pnlMap.setClear();
		pnlMap.setSelectedVehicle(null);
		pnlControl.Show_Vehicle_Status(0);
		uavMonitor.disableUAVFeed();
	}

	// Events From pnlPayload
	/**
	 * Called from PanelPayload when the user finishes the visual task.
	 * When this method is called, the followings are executed.
	 *  1. set the target as "DONE" so that later garbage collected
	 *  2. set the vehicle's status as "STASIS" so that it stops flashing
	 *  3. tell the game to assign a target to this vehicle
	 *  4. clear the game's current payload vehicle.
	 *  5. set the panels enabled
	 *  6. clear the mission info text
	 */
	public void Payload_Finished_From_pnlPayload(Vehicle v) {
		v.getTarget().setDone();
		v.setStatus(MyGame.STATUS_VEHICLE_STASIS);
		game.AutoTargetAssign(v);
		game.clearCurrentPayloadVehicle();
		pnlMap.setEnabled(true);
		pnlControl.setEnabled(true);    	
		pnlPayloadControls.setEnabled(false);
		pnlControl.Update_Vehicle_Payload_Clear(v);
	}
	
	public void Payload_Finished_From_Msg() {
		Vehicle v = pnlMap.selectedVehicle;
		if(v != null) {
			if(v.isEngaged) {
				pnlControl.ClearTaskMsg(pnlMap.selectedVehicle.getIndex());
				Payload_Finished_From_pnlPayload(v);
				v.isEngaged = false;
			}
			else {
				showMessageOnTopOfMap("Please first click \"Engage\" to engage the UAV to a task", 5);
			}
		}
		else {
			showMessageOnTopOfMap("Please fisrt select corresponding UAV and click \"Engage\" to engage the UAV", 5);
		}
	}

	public void Payload_Assigned_From_pnlPayload(Vehicle v, Payload p) {		
		// PanelMsgBoard.Msg("[MISSION(" +v.getIndex()+")] " + p.getStatement());
		pnlControl.Update_Vehicle_Payload(v, p);
	}

	public void Payload_Graphics_Update() {
		// TODO: NEED TO FIND OUT A MORE EFFICIENT WAY TO REPAINT THE PAYLOAD WINDOW!!!
		payload_canvas.repaint();   // and this
	}

	/**
	 * (For T3) Called to enable/disable the submit button in the payloadControl panel 
	 */
	public void Payload_Submit(boolean submit) {
		pnlPayloadControls.enableSubmit(submit);
	}

	// Events From Vehicle
	// public void Vehicle_Reached_Target_From_Vehicle(Vehicle v) {}
	public void Vehicle_Damaged_By_Hazard_Area_From_Vehicle(Vehicle v) { pnlControl.Update_Vehicle_Damage(v); }
	public void Target_Become_Visible_From_Vehicle(Vehicle v) { game.AutoTargetAssign(v); }
	public void Hide_Popup(Vehicle v) {pnlMap.HidePopup(v);}

	// Events From pnlControl
	public void Vehicle_Selected_From_pnlControl(int idx) { pnlMap.setSelectedVehicle(game.getVehicleList().getVehicle(idx));}
	public void Vehicle_Unselected_From_pnlControl() { pnlMap.setClear(); pnlMap.setSelectedVehicle(null);}
	public void Vehicle_Goal_From_pnlControl(Vehicle v) { pnlMap.setClear(); pnlMap.setGoal(v);}
	public void Vehicle_WP_Add_From_pnlControl(Vehicle v) { pnlMap.setClear(); pnlMap.addWP(v);}
	public void Vehicle_WP_Del_From_pnlControl(Vehicle v) { pnlMap.setClear(); pnlMap.delWP(v);}
	
	@Override
	public void Vehicle_Engage_From_pnlMap(Vehicle v) {
		Vehicle_Engage_Task(v);
		EVT_Payload_Engaged_pnlMap(v.getIndex(), v.getTarget().getName());
	}
	
	public void Vehicle_Engage_From_pnlCompact(Vehicle v) { 
		Vehicle_Engage_Task(v);
		EVT_Payload_Engaged_pnlCompact(v.getIndex(), v.getTarget().getName());
	}
	
	public void Vehicle_Engage_From_pnlUAV(Vehicle v) { 
		Vehicle_Engage_Task(v);
		EVT_Payload_Engaged_pnlUAV(v.getIndex(), v.getTarget().getName());
	}
	
	// integrate all engage functions
	public void Vehicle_Engage_Task(Vehicle v) {
		v.isEngaged = true;
		pnlControl.PrintTaskMsg(v.getIndex());
		pnlControl.Show_Vehicle_Status(v.getIndex());
		try {
			Engage(v);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String msg = "Vehicle ["+v.getIndex()+"] has engaged in a counting task.";
		PanelMsgBoard.Msg(msg);
	}
	
	// vehicle home function
	// from compact panel
	public void Vehicle_Go_Home(Vehicle v, int source) throws UserDefinedException {
		// transfer to UAV status window
		pnlControl.Show_Vehicle_Status(v.getIndex());
		// create a dialog
		Object[] options = {"Yes", "No"};
		JOptionPane home_mode = new JOptionPane("Let UAV "+v.getIndex()+" go home", JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, options[0]);
		home_mode.setVisible(true);
		JDialog invest_dialog = home_mode.createDialog(home_mode.getParent(), "Emergent State");
		invest_dialog.setVisible(true);
		// write to log file
		if(source == 0) EVT_Home_From_Compact(v.getIndex(), v.getX(), v.getY());
		if(source == 1) EVT_Home_From_UAV_Panel(v.getIndex(), v.getX(), v.getY());
		if(source == 2) EVT_Home_From_Right_Click(v.getIndex(), v.getX(), v.getY());
		// choose YES or NO
		Object selectedValue = home_mode.getValue();
		invest_dialog.dispose();
		if(selectedValue == "Yes") {
			// add detected attack
			if(source == 0) EVT_Home_From_Compact_Yes(v.getIndex(), v.getX(), v.getY());
			if(source == 1) EVT_Home_From_UAV_Panel_Yes(v.getIndex(), v.getX(), v.getY());
			if(source == 2) EVT_Home_From_Right_Click_Yes(v.getIndex(), v.getX(), v.getY());
			EVT_Vehicle_Deleted(v.getIndex(), v.getX(), v.getY());
			game.HomeFunction(v);
			EVT_Vehicle_Added(v.getIndex(), v.getX(), v.getY());
			EVT_New_Target_Assgined(v.getIndex(), v.getX(), v.getY(), v.getTarget());
		}
		else {
			if(source == 0) EVT_Home_From_Compact_No(v.getIndex(), v.getX(), v.getY());
			if(source == 1) EVT_Home_From_UAV_Panel_No(v.getIndex(), v.getX(), v.getY());
			if(source == 2) EVT_Home_From_Right_Click_No(v.getIndex(), v.getX(), v.getY());
		}
	}
	
	private void Engage(Vehicle v) throws IOException {
		pnlMap.setEnabled(false);
		pnlControl.setEnabled(false);    	
		pnlPayloadControls.setEnabled(true);
		v.setStatus(MyGame.STATUS_VEHICLE_PAYLOAD);
		game.setCurrentPayloadVehicle(v);
		pnlPayload.setPayload(v); // this is the important line
	}

	// DB
	private void Write(int invoker, int type, int vIdx, String log, int X, int Y) {
		if (!MyLogging.WRITE_TO_DISK) {
			return;
		}
		Calendar cal = Calendar.getInstance();
		String temp = Now() + ",   " + invoker  + ",   " + type  + ",   " + vIdx  + ",   " + log  + ",   " + X + ", " + Y;
		// System.out.println(temp);
		SimpleDateFormat date = new SimpleDateFormat("EEE,MMM d,yyyy");
		String test = "logs/" + date.format(cal.getTime()) + "rand" + randstr + ".txt";

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(test,true)));
			out.println(temp);
			out.close();
		} catch (IOException e) {
			System.out.println("Warning: File NOT correctly written to.  Reschu:Write()");
			//exception handling left as an exercise for the reader
		}

		// DB is not used in this demo version. So deleted.
		if( tutorial()) {
			String target = ( log.indexOf("[") >= 0 ) 
					? log.substring(log.indexOf("[")+1, log.indexOf("[")+2)	: "";
					tutorial.event(type, vIdx, target);
		}
	}

	private static String Now() {
		//Needs to be updated to get high resolution timing, to ms
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		//System.out.println(System.currentTimeMillis());
		return sdf.format(cal.getTime());
	}

	public void EVT_WP_AddWP_Start(int vIdx){
		Write(MyDB.INVOKER_USER, MyDB.WP_ADD_START, vIdx, "WP add start", -1, -1);
	}
	public void EVT_WP_AddWP_End(int vIdx, int mouseCoordX, int mouseCoordY){
		checkIntersect(vIdx);
		Write(MyDB.INVOKER_USER, MyDB.WP_ADD_END, vIdx, "WP add end", mouseCoordX, mouseCoordY); 
	}
	public void EVT_WP_AddWP_Cancel(int vIdx) {
		Write(MyDB.INVOKER_USER, MyDB.WP_ADD_CANCEL, vIdx, "WP add canceled", -1, -1); 
	}
	public void EVT_WP_MoveWP_Start(int vIdx, int mouseCoordX, int mouseCoordY){
		Write(MyDB.INVOKER_USER, MyDB.WP_MOVE_START, vIdx, "WP move start", mouseCoordX, mouseCoordY);
	}
	public void EVT_WP_MoveWP_End(int vIdx, int mouseCoordX, int mouseCoordY){
		checkIntersect(vIdx);
		Write(MyDB.INVOKER_USER, MyDB.WP_MOVE_END, vIdx, "WP move end", mouseCoordX, mouseCoordY);
	}
	public void EVT_WP_DeleteWP_Start(int vIdx){
		Write(MyDB.INVOKER_USER, MyDB.WP_DELETE_START, vIdx, "WP delete start", -1, -1);
	}
	public void EVT_WP_DeleteWP_End(int vIdx, int mouseCoordX, int mouseCoordY){
		checkIntersect(vIdx);
		Write(MyDB.INVOKER_USER, MyDB.WP_DELETE_END, vIdx, "WP delete end", mouseCoordX, mouseCoordY);
	}
	public void EVT_GP_SetGP_by_System(int vIdx, String targetName){
		checkIntersect(vIdx);
		Write(MyDB.INVOKER_SYSTEM, MyDB.GP_SET_BY_SYSTEM, vIdx, "Goal set by system; assigned to target[" + targetName + "]", -1, -1);
	}
	public void EVT_GP_SetGP_Start(int vIdx){
		Write(MyDB.INVOKER_USER, MyDB.GP_SET_START, vIdx, "Goal set start", -1, -1);
	}
	public void EVT_GP_SetGP_End_Assigned(int vIdx, int mouseCoordX, int mouseCoordY, String targetName){
		checkIntersect(vIdx);
		Write(MyDB.INVOKER_USER, MyDB.GP_SET_END_ASSIGNED, vIdx, "Goal set end; assigned to target[" + targetName +"]", mouseCoordX, mouseCoordY);
	}
	public void EVT_GP_SetGP_End_Unassigned(int vIdx, int mouseCoordX, int mouseCoordY){
		checkIntersect(vIdx);
		Write(MyDB.INVOKER_USER, MyDB.GP_SET_END_UNASSIGNED, vIdx, "Goal set end. no assign", mouseCoordX, mouseCoordY);
	}
    public void EVT_GP_SetGP_Cancel(int vIdx) {
    	Write(MyDB.INVOKER_USER, MyDB.GP_SET_CANCEL, vIdx, "Goal set canceled", -1, -1); 
    }
	public void EVT_GP_ChangeGP_Start(int vIdx, int mouseCoordX, int mouseCoordY, String targetName){
		Write(MyDB.INVOKER_USER, MyDB.GP_CHANGE_START, vIdx, "Goal change start from Target[" + targetName + "]", mouseCoordX, mouseCoordY);    	
	}
	public void EVT_GP_ChangeGP_End_Assigned(int vIdx, int mouseCoordX, int mouseCoordY, String targetName){    	
		checkIntersect(vIdx);
		Write(MyDB.INVOKER_USER, MyDB.GP_CHANGE_END_ASSIGNED, vIdx, "Goal change end. target[" + targetName + "] assigned", mouseCoordX, mouseCoordY);
	}
	public void EVT_GP_ChangeGP_End_Unassigned(int vIdx, int mouseCoordX, int mouseCoordY){
		checkIntersect(vIdx);
		Write(MyDB.INVOKER_USER, MyDB.GP_CHANGE_END_UNASSIGNED, vIdx, "Goal change end; target unassigned", mouseCoordX, mouseCoordY);
	}
	public void EVT_Target_Generated(String targetName, int[] targetPos, boolean visibility){
		Write(MyDB.INVOKER_SYSTEM, MyDB.TARGET_GENERATED, -1, "Target[" + targetName + "] generated (visibile=" + visibility + ")", targetPos[0], targetPos[1]);
	}
	public void EVT_Target_BecameVisible(String targetName, int[] targetPos){
		Write(MyDB.INVOKER_SYSTEM, MyDB.TARGET_BECAME_VISIBLE, -1, "Target[" + targetName +"] became visible", targetPos[0], targetPos[1]);
	}
	public void EVT_Target_Disappeared(String targetName, int[] targetPos){
		Write(MyDB.INVOKER_SYSTEM, MyDB.TARGET_DISAPPEARED, -1, "Target[" + targetName + "] disappeared" , targetPos[0], targetPos[1]);
	}
	public void EVT_Payload_EngagedAndFinished_COMM(int vIdx, String targetName){
		Write(MyDB.INVOKER_USER, MyDB.PAYLOAD_ENGAGED_AND_FINISHED, vIdx, "Payload Engaged and Finished. COMM", -1, -1);
	}
	public void EVT_Payload_Engaged_pnlMap(int vIdx, String targetName){
		Write(MyDB.INVOKER_USER, MyDB.PAYLOAD_ENGAGED_FROM_PNLMAP, vIdx, "Payload Engaged to Target[" + targetName + "] from Map", -1, -1);
	}
	public void EVT_Payload_Engaged_pnlCompact(int vIdx, String targetName){
		Write(MyDB.INVOKER_USER, MyDB.PAYLOAD_ENGAGED_FROM_PNLCOMPACT, vIdx, "Payload Engaged to Target[" + targetName + "] from Compact Panel", -1, -1);
	}
	public void EVT_Payload_Engaged_pnlUAV(int vIdx, String targetName){
		Write(MyDB.INVOKER_USER, MyDB.PAYLOAD_ENGAGED_FROM_PNLUAV, vIdx, "Payload Engaged to Target[" + targetName + "] from UAV panel", -1, -1);
	}
	public void EVT_Payload_Finished_Correct(int vIdx, String targetName){
		play(WAVPlayer.CORRECT);
		Write(MyDB.INVOKER_USER, MyDB.PAYLOAD_FINISHED_CORRECT, vIdx, "Payload Finished; CORRECT", -1, -1);
	}
	public void EVT_Payload_Finished_Incorrect(int vIdx, String targetName){
		play(WAVPlayer.INCORRECT);
		Write(MyDB.INVOKER_USER, MyDB.PAYLOAD_FINISHED_INCORRECT, vIdx, "Payload Finished. INCORRECT", -1, -1);
	}
	public void EVT_Vehicle_Damaged(int vIdx,int haX, int haY){
		Write(MyDB.INVOKER_SYSTEM, MyDB.VEHICLE_DAMAGED, vIdx, "Damaged with a HazardArea", haX, haY);
	}
	public void EVT_Vehicle_SpeedDecreased(int vIdx, int curSpeed){
		play(WAVPlayer.PENALIZED);
		Write(MyDB.INVOKER_SYSTEM, MyDB.VEHICLE_SPEED_DECREASED, vIdx, "Speed Decreased to (" + curSpeed + ")", -1, -1);
	}
	public void EVT_Vehicle_ArrivesToTarget(int vIdx, String targetName, int x, int y){
		play(WAVPlayer.VEHICLE_ARRIVE);
		Write(MyDB.INVOKER_SYSTEM, MyDB.VEHICLE_ARRIVES_TO_TARGET, vIdx, "UAV ["+vIdx+"] arrives to target ["+targetName+"]", x, y);
	}
	public void EVT_Hacked_Vehicle_Target(int vIdx, String targetName, int x, int y) {
		play(WAVPlayer.VEHICLE_ARRIVE);
		Write(MyDB.INVOKER_SYSTEM, MyDB.HACKED_UAV_ARRIVES_TARGET, vIdx, "Hacked UAV ["+vIdx+"] arrives to target ["+targetName+"]", x, y);
	}
	public void EVT_Vehicle_IntersectHazardArea(int vIdx, int[] threat) {
		Write(MyDB.INVOKER_SYSTEM, MyDB.VEHICLE_INTERSECT_HAZARDAREA, vIdx, "Intersect with a HazardArea", threat[0], threat[1]);
	}
	public void EVT_Vehicle_EscapeHazardArea(int vIdx) {
		Write(MyDB.INVOKER_SYSTEM, MyDB.VEHICLE_ESCAPE_HAZARDAREA, vIdx, "Escape from a HazardArea", -1, -1);
	}
	public void EVT_HazardArea_Generated(int[] pos) {
		for( int vIdx=0; vIdx<game.getVehicleList().size(); vIdx++ ) 
			checkIntersect(vIdx+1);
		// Write(MyDB.INVOKER_SYSTEM, MyDB.HAZARDAREA_GENERATED, -1, "HazardArea Generated", pos[0], pos[1]);
	}
	public void EVT_HazardArea_Disappeared(int[] pos) {
		for( int vIdx=0; vIdx<game.getVehicleList().size(); vIdx++ ) 
			checkIntersect(vIdx+1);
		// Write(MyDB.INVOKER_SYSTEM, MyDB.HAZARDAREA_DISAPPEARED, -1, "HazardArea Disappeared", pos[0], pos[1]);    	
	}
	public void EVT_Correct_Task(int vIdx, String name, String ans, int input) {
		Write(MyDB.INVOKER_USER, MyDB.CORRECT_TASK_ANSWER, vIdx, "Correct! Target "+name+" answer is "+ans+", operator respond is "+input, -1, -1);
	}
	public void EVT_Incorrect_Task(int vIdx, String name, String ans, int input) {
		Write(MyDB.INVOKER_USER, MyDB.INCORRECT_TASK_ANSWER, vIdx, "Incorrect! Target "+name+" answer is "+ans+", operator respond is "+input, -1, -1);
	}
	@Override
	public void EVT_System_GameStart(){
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_START, -1, "Game Start. username=" + _username + ", scenario="+ _scenario, -1, -1); 
	}
	public void EVT_System_GameEnd(){ 
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_END, -1, 
				"Game End. user =" + _username + ". scenario = "+ _scenario, -1, -1);
	}
	public void EVT_RECORD_FINAL_SCORE(int damage, int task, int wrong_task, int attack, int wrong_attack, int lost, int total) {
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_END, -1, 
				"Total UAV damage is   "+damage, -1, -1);
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_END, -1, 
				"Total tasks done is   "+task, -1, -1);
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_END, -1, 
				"Total wrong tasks is  "+wrong_task, -1, -1);
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_END, -1, 
				"Total detected attack "+attack, -1, -1);
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_END, -1,
				"Total wrong detects   "+wrong_attack, -1, -1);
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_END, -1, 
				"Total disappeared UAV "+lost, -1, -1);
		Write(MyDB.INVOKER_SYSTEM, MyDB.SYSTEM_GAME_END, -1, "Your Total score is 100 - "
				+damage+"(damage) + 5*"+task+"(correct task) - 5*"+wrong_task+"(wrong task) + 10*"
				+attack+"(correct attack) - 10*"+wrong_attack+"(wrong attack) - 20*"+lost+"(lost) = "+total, -1, -1);
	}
	
	/**
	 * Yves
	 */
	public void EVT_VSelect_Map_LBtn(int vIdx) {
		Vehicle v = game.getVehicleList().getVehicle(vIdx-1);
		Write(MyDB.INVOKER_USER, MyDB.YVES_VEHICLE_SELECT_MAP_LBTN, vIdx,
				"Vehicle ["+v.getIndex()+"] select map Lbtn and its video feed enabled)", v.getX(), v.getY());
		uavMonitor.enableUAVFeed(v);
	}
	public void EVT_VSelect_Map_RBtn(int vIdx) { 
		Write(MyDB.INVOKER_USER, MyDB.YVES_VEHICLE_SELECT_MAP_RBTN, vIdx, "Vehicle select map Rbtn", -1, -1);
	}
	public void EVT_VSelect_Tab(int vIdx) {
		Write(MyDB.INVOKER_USER, MyDB.YVES_VEHICLE_SELECT_TAB, vIdx, "Vehicle select tab", -1, -1);
	}
	public void EVT_VSelect_Tab_All() { 
		Write(MyDB.INVOKER_USER, MyDB.YVES_VEHICLE_DESELECT_TAB, -1, "Vehicle deselect tab", -1, -1);
	}
	
	public void EVT_Hack_Launch(int vIdx, int xCoord, int yCoord) {
		Write(MyDB.INVOKER_SYSTEM, MyDB.HACK_LAUNCHED, vIdx, "Vehicle Hacked with Smarter Attacker", xCoord, yCoord);
	}
	public void EVT_Hack_Launch_Fake(int vIdx) {
		Write(MyDB.INVOKER_SYSTEM, MyDB.HACK_LAUNCHED_FAKE, vIdx, "Fake Vehicle Hack", -1, -1);
	}

    public void EVT_Hack_Notification_Launch(int vIdx) {
    	Write(MyDB.INVOKER_SYSTEM, MyDB.HACK_NOTIFICATION_LAUNCHED, vIdx, "Hack Notification Launched", -1, -1);
    	game.getVehicleList().getVehicle(vIdx-1).isNotified = true;
    }
    public void EVT_Hack_Notification_Ignore(int vIdx) {
    	Write(MyDB.INVOKER_USER, MyDB.HACK_NOTIFICATION_IGNORED, vIdx, "Hack Notification Ignored", -1, -1);
    }
    public void EVT_Hack_Notification_Investigate(int vIdx) {
    	Write(MyDB.INVOKER_USER, MyDB.HACK_NOTIFICATION_INVESTIGATED, vIdx, "Hack Notification Investigated", -1, -1);
    }
    public void EVT_Hack_Notification_Missed(int vIdx) {
    	Write(MyDB.INVOKER_SYSTEM, MyDB.HACK_NOTIFICATION_MISSED, vIdx, "Hack Notification Missed", -1, -1);
    }
    
    // For Home (Emergency) Mode
    public void EVT_Home_From_Compact(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_COMPACT, vIdx, "Home button clicked from compact panel", xCoord, yCoord);
    }
    public void EVT_Home_From_UAV_Panel(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_UAV_PANEL, vIdx, "Home button clicked from UAV panel", xCoord, yCoord);
    }
    public void EVT_Home_From_Right_Click(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_RIGHT_CLICK, vIdx, "Home button clicked from right click", xCoord, yCoord);
    }
    public void EVT_Home_From_Compact_Yes(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_COMPACT_YES, vIdx, "Home button clicked from compact panel comfirmed", xCoord, yCoord);
    }
    public void EVT_Home_From_Compact_No(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_COMPACT_NO, vIdx, "Home button clicked from compact panel denied", xCoord, yCoord);
    }
    public void EVT_Home_From_UAV_Panel_Yes(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_UAV_PANEL_YES, vIdx, "Home button clicked from UAV panel comfirmed", xCoord, yCoord);
    }
    public void EVT_Home_From_UAV_Panel_No(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_UAV_PANEL_NO, vIdx, "Home button clicked from UAV panel denied", xCoord, yCoord);
    }
    public void EVT_Home_From_Right_Click_Yes(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_RIGHT_CLICK_YES, vIdx, "Home button clicked from right click comfirmed", xCoord, yCoord);
    }
    public void EVT_Home_From_Right_Click_No(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.HOME_FROM_RIGHT_CLICK_NO, vIdx, "Home button clicked from right click denied", xCoord, yCoord);
    }
    public void	EVT_Vehicle_Added(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.VEHICLE_ADDED, vIdx, "Additional UAV "+vIdx+" is added", xCoord, yCoord);
    }
    public void	EVT_Vehicle_Deleted(int vIdx, int xCoord, int yCoord) {
    	Write(MyDB.INVOKER_USER, MyDB.VEHICLE_DELETED, vIdx, "UAV "+vIdx+" is going home (deleted)", xCoord, yCoord);
    }
    public void EVT_New_Target_Assgined(int vIdx, int xCoord, int yCoord, Target t) {
    	Write(MyDB.INVOKER_USER, MyDB.NEW_TARGET_ASSIGNED, vIdx,
    			"UAV "+vIdx+" is assigned to target "+t.getName()+" at the position of "+t.getPos()[0]+", "+t.getPos()[1], xCoord, yCoord);
    }
    // For Ghost Mission
    public void EVT_Generate_Ghost_Mission(Vehicle v) {
    	Write(MyDB.INVOKER_SYSTEM, MyDB.GENERATE_GHOST_MISSION, v.getIndex(),
    			"UAV "+v.getIndex()+" is under attck, generate its ghost mission", v.getX(), v.getY());
    	// game.AddGhostUAV(v);
    }
    public void EVT_ATTACKED_UAV_DISAPPEAR(Vehicle v) {
    	Write(MyDB.INVOKER_SYSTEM, MyDB.ATTACKED_UAV_DISAPPEAR, v.getIndex(),
    			"Attacked UAV "+v.getIndex()+" disappear because its ground truth location is out of border", v.getX(), v.getY());
    }
    public void EVT_UAV_DECIDED_NOT_HACKED(Vehicle v) {
    	Write(MyDB.INVOKER_SYSTEM, MyDB.UAV_NOT_HACKED_DECIDED, v.getIndex(),
    			"UAV "+v.getIndex()+" is considered NOT being attacked from pop menu", v.getX(), v.getY());
    }
    public void EVT_UAV_DECIDED_HACKED(Vehicle v) {
    	Write(MyDB.INVOKER_SYSTEM, MyDB.UAV_HACKED_DECIDED, v.getIndex(),
    			"UAV "+v.getIndex()+" is considered being attacked from pop menu", v.getX(), v.getY());
    	try {
			Vehicle_Go_Home(v, 2);
		} catch (UserDefinedException e) {
			e.printStackTrace();
		}
    }
    // audio player
	private void play(String arg) {
		new WAVPlayer(arg).start();
	}
	
	// main function
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run () {
				try {
					new Reschu(1, 1, "administartor_0", new AppMain(), false).setVisible(true);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}