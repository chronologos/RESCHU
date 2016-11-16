package reschu.tutorial;

import reschu.app.AppMain;
import reschu.game.controller.Reschu;
   
public class Tutorial {
	private TutorialModel scenario;
	
	public Tutorial(AppMain main){		
		switch( Reschu._scenario ) {
			case 1: scenario = new Scenario1(main); break;
			case 2: scenario = new Scenario2(main); break;
			case 3: scenario = new Scenario3(main); break;
			case 4: scenario = new Scenario4(main); break; 
		}		 	
	}		
	
	public void tick() { 
		scenario.tick();
	}
	
	public void event(int type, int vIdx, String target) {
		scenario.checkEvent(type, vIdx, target);
	}
} 


