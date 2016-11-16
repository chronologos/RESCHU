package reschu.tutorial;

import java.awt.HeadlessException;
import java.io.IOException;

import javax.swing.JOptionPane;

import reschu.app.AppMain;
import reschu.constants.MyDB;

public class Scenario2 extends TutorialModel {
	
	Scenario2(AppMain main) {
		super(main);
	}
	
       protected void showDialog() {
               switch(getState()) {
                   case 0:
				try {
					JOptionPane.showMessageDialog(null,
                                       makePicPanel("2_0.png",
                                                       ""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               //Intro: Explains the Purpose of the game
                               setDuration(1);
                               nextDialog();
                               break;
                   
                       case 1:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_1.png",
					                           ""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               //Intro: Explains the Purpose of the game
                               setDuration(1);
                               nextDialog();
                               break;
                       case 2:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_2.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                           //Step_2: Explains the Interface Elements
                           setDuration(3);
                           nextDialog();
                           break;
                       case 3:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_3.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               //Step_1: Explains the Map elements
                               setDuration(3);
                               nextDialog();
                               break;
                       case 4:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_4.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               //Step_3: Explains How to change a vehicle's destination
                               setDuration(25);
                               break;
                       case 5:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_5.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               //Step_4: Explains How to add a waypoint
                               setDuration(1);
                               nextDialog();
                               break;
                       case 6:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_6.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               //Step_5: Explains how to move a waypoint
                               setDuration(25);
                               break;
                       case 7:
                               JOptionPane.showMessageDialog(null, "Good Job!");
                               setDuration(1);
                               nextDialog();
                               break;        
                       case 8:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_7.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(25);
                               break;
                       case 9:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_8.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(1);
                               nextDialog();
                               break;
                       case 10:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_9.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(25);
                               break;
                       case 11:
                               JOptionPane.showMessageDialog(null, "Good Job!");
                               setDuration(2);
                               nextDialog();
                               break;
                       case 12:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_10.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(25);
                               break;
                       case 13:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_11.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(1);
                               nextDialog();
                               break;
                       case 14:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_12.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(25);
                               break;        
                       case 15:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_13.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(500);
                               break;
                       case 16:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_14.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               //Explains the payload window and mission location
                               setDuration(20);
                               break;
                       case 17:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_15.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(4);
                               nextDialog();
                               break;
                        case 18:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_16.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(30);
                               break;        
                       case 19:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_17.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(3);
                               nextDialog();
                               break;        
                       case 20:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_18.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(50);
                               main.TutorialFinished();
                               break; 
                       case 21:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_19.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(0);
                               nextDialog();
                               break;
                       case 22:
				try {
					JOptionPane.showMessageDialog(null,
					           makePicPanel("2_20.png",""));
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                               setDuration(0);
                               nextDialog();
                               break;        
                       case 23:
                               //SESSION ENDED
				try {
					main.Restart_Reschu();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                               break;        
                       default:
                               break;
               }
       }

       protected void checkEvent(int type, int vIdx, String target) {
               switch( getState() ) {
                       case 4:
                               checkCorrect(type==MyDB.GP_CHANGE_END_ASSIGNED && vIdx==1 && target.equals("D"));
                               break;
                       case 6:
                               checkCorrect(type==MyDB.GP_CHANGE_END_ASSIGNED && vIdx==1 && target.equals("E"));
                               break;        
                       case 8:
                               checkCorrect(type==MyDB.WP_ADD_END && vIdx == 1);
                               break;
                       case 10:
                               checkCorrect(type==MyDB.WP_MOVE_END && vIdx ==1);
                               break;
                       case 12:
                               checkCorrect(type==MyDB.WP_ADD_END && vIdx ==1);
                               break;
                       case 14:
                               checkCorrect(type==MyDB.WP_DELETE_END && vIdx == 1);
                               break;
                       case 15:
                    	   	   checkCorrect(type==MyDB.VEHICLE_ARRIVES_TO_TARGET && vIdx == 1);
                    	   	   break;
                       case 16:
                               checkCorrect(type==MyDB.PAYLOAD_ENGAGED && vIdx == 1);
                               break;
                       case 18:
                               checkCorrect( (type==MyDB.PAYLOAD_FINISHED_CORRECT || type==MyDB.PAYLOAD_FINISHED_INCORRECT) && vIdx ==1);
                               break;
                       case 20:
                               checkCorrect( (type==MyDB.PAYLOAD_FINISHED_CORRECT || type==MyDB.PAYLOAD_FINISHED_INCORRECT) && vIdx ==2);
                               break;        
                       default:
                               break;
               }
       }
}
