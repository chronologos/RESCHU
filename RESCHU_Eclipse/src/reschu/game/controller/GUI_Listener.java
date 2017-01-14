package reschu.game.controller;

import reschu.game.model.Payload;
import reschu.game.model.Vehicle;




public interface GUI_Listener {
    
    /** Creates a new instance of Gui_Listener */
    public void vehicleLocationChanged();
    
    public void clockTick(int milliseconds);
    
    public void gameStart();
    public void gameEnd();
    
    // Events from pnlPayloadControl
    public void panUpSelected();    
    public void panDownSelected();    
    public void rotateCWSelected();    
    public void rotateCCWSelected();    
    public void zoomIn();    
    public void zoomOut();
    public void submitPayload();	// For T3
        
    public void showMessageOnTopOfMap(String msg, int duration);
    
    // Events From pnlPayload
    public void Payload_Finished_From_pnlPayload(Vehicle v);
    public void Payload_Assigned_From_pnlPayload(Vehicle v, Payload p);
    public void Payload_Graphics_Update(); // For T3
    public void Payload_Submit(boolean submit);	// For T3 
    
    // Events From Vehicle
    //public void Vehicle_Reached_Target_From_Vehicle(Vehicle v);
    public void Vehicle_Damaged_By_Hazard_Area_From_Vehicle(Vehicle v);
    public void Target_Become_Visible_From_Vehicle(Vehicle v);
    public void Hide_Popup(Vehicle v);
    
    // Events From pnlMap
    public void Vehicle_Selected_From_pnlMap(int idx);
    public void Vehicle_Engage_From_pnlMap(Vehicle v);
    public void Vehicle_Unselected_From_pnlMap();
       
    // Events From pnlControl
    public void Vehicle_Selected_From_pnlControl(int idx); 
    public void Vehicle_Unselected_From_pnlControl();
    public void Vehicle_Goal_From_pnlControl(Vehicle v);
    public void Vehicle_WP_Add_From_pnlControl(Vehicle v);
    public void Vehicle_WP_Del_From_pnlControl(Vehicle v);
    public void Vehicle_Engage_From_pnlControl(Vehicle v);
    
    // Events from AttackNotification
	public void activateUAVFeed(int idx);

    // Database
    public void EVT_WP_AddWP_Start(int vIdx);	 
    public void EVT_WP_AddWP_End(int vIdx, int mouseCoordX, int mouseCoordY);  
    public void EVT_WP_MoveWP_Start(int vIdx, int mouseCoordX, int mouseCoordY);  
    public void EVT_WP_MoveWP_End(int vIdx, int mouseCoordX, int mouseCoordY); 
    public void EVT_WP_DeleteWP_Start(int vIdx); 
    public void EVT_WP_DeleteWP_End(int vIdx, int mouseCoordX, int mouseCoordY); 
    public void EVT_WP_AddWP_Cancel(int vIdx);
    public void EVT_GP_SetGP_by_System(int vIdx, String targetName);  
    public void EVT_GP_SetGP_Start(int vIdx);  
    public void EVT_GP_SetGP_End_Assigned(int vIdx, int mouseCoordX, int mouseCoordY, String targetName); 
    public void EVT_GP_SetGP_End_Unassigned(int vIdx, int mouseCoordX, int mouseCoordY);  
    public void EVT_GP_ChangeGP_Start(int vIdx, int mouseCoordX, int mouseCoordY, String targetName);  
    public void EVT_GP_ChangeGP_End_Assigned(int vIdx, int mouseCoordX, int mouseCoordY, String targetName);  
    public void EVT_GP_ChangeGP_End_Unassigned(int vIdx, int mouseCoordX, int mouseCoordY); 
    public void EVT_Target_Generated(String targetName, int[] targetPos, boolean visibility);  
    public void EVT_Target_BecameVisible(String targetName, int[] targetPos);  
    public void EVT_Target_Disappeared(String targetName, int[] targetPos); 
    public void EVT_Payload_EngagedAndFinished_COMM(int vIdx, String targetName);  
    public void EVT_Payload_Engaged(int vIdx, String targetName);  
    public void EVT_Payload_Finished_Correct(int vIdx, String targetName);  
    public void EVT_Payload_Finished_Incorrect(int vIdx, String targetName);  
    public void EVT_Vehicle_Damaged(int vIdx, int haX, int haY);	//  Not gonna use this.
    public void EVT_Vehicle_SpeedDecreased(int vIdx, int curSpeed);  
    public void EVT_Vehicle_ArrivesToTarget(int vIdx, String targetname, int x, int y);  
    public void EVT_Vehicle_IntersectHazardArea(int vIdx, int[] threat);
    public void EVT_Vehicle_EscapeHazardArea(int vIdx);
    public void EVT_HazardArea_Generated(int[] pos);  
    public void EVT_HazardArea_Disappeared(int[] pos);  
    public void EVT_System_GameStart();
    public void EVT_System_GameEnd();    

    /**
     * For Yves
     */
    public void EVT_VSelect_Map_LBtn(int vIdx);
    public void EVT_VSelect_Map_RBtn(int vIdx);
    public void EVT_VSelect_Tab(int vIdx);
    public void EVT_VSelect_Tab_All();
    
    /**
     * For Hack Simulation
     */
    public void EVT_Hack_Launch(int vIdx, int xCoord, int yCoord);
    public void EVT_Hack_Launch_Fake(int vIdx);
    public void EVT_Hack_Notification_Launch(int vIdx);
    public void EVT_Hack_Notification_Ignore(int vIdx);
    public void EVT_Hack_Notification_Investigate(int vIdx);
}
