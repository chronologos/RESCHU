package reschu.game.view;
 
import java.awt.*; 
import java.util.LinkedList;

import javax.swing.*; 
import javax.swing.border.*;

import reschu.constants.*;
import reschu.game.model.Game;
import reschu.game.model.Vehicle;
import reschu.game.model.VehicleList;

import info.clearthought.layout.TableLayout;

public class PanelTimeLine extends JPanel {
	private static final long serialVersionUID = 7243933752530455674L; 
	private VehicleIcon[] pnlVehicleIcon;
	private VehicleTime[] pnlVehicleTime;	
	private TimeText pnlTimeText;
	private RemainingTime pnlRemainingTime;
	private TitledBorder bdrTitle;
	//private int number_of_row, row_height;
	private int current_time;
	
	public PanelTimeLine(Game g, VehicleList vl) {		 
		// double size[][] = {{30, 5, TableLayout.FILL, 5}, {10, 3, 24, 3, 24, 3, 24, 3, 24, 3, 24, 3, 24, 6}};
		double size[][] = {{30, 5, TableLayout.FILL, 5}, {10, 3, 20, 3, 20, 3, 20, 3, 20, 3, 20, 3, 20, 3, 24, 3}};
		bdrTitle = BorderFactory.createTitledBorder("Time Line");
		setBorder(bdrTitle);  
		setLayout(new TableLayout(size));
		
		pnlVehicleIcon = new VehicleIcon[vl.size()];
		pnlVehicleTime = new VehicleTime[vl.size()];		
		pnlTimeText = new TimeText();
		pnlRemainingTime = new RemainingTime(g);

		//number_of_row = vl.size() + 2;
		//row_height = this.getHeight() / number_of_row;
		
		add(pnlTimeText, "2,0");
		
		for(int i=0; i<vl.size(); i++) {
			pnlVehicleIcon[i] = new VehicleIcon(vl.getVehicle(i));
			pnlVehicleTime[i] = new VehicleTime(vl.getVehicle(i));
			add(pnlVehicleIcon[i], "0,"+2*(i+1));
			add(pnlVehicleTime[i], "2,"+2*(i+1));
		}
		add(pnlRemainingTime, "2,14");
	}
	
	public void refresh(int milliseconds) {
		repaint();
		current_time = milliseconds;
		pnlTimeText.refresh(current_time);
		pnlRemainingTime.refresh(current_time);
		for(int i=0; i<pnlVehicleTime.length; i++) {
			pnlVehicleIcon[i].chkEngageEnabled();
			pnlVehicleTime[i].refresh(current_time);
		}
	} 
}

class TimeText extends JPanel { 
	private static final long serialVersionUID = 6191134278259404128L;
	private int p1, p2, p3, p4, p5;
	//private int current_time;
	//private int t1, t2, t3, t4, t5;
		
	public TimeText() {}
	public void paint ( Graphics g ) {
		/*
		for( int i=1; i<=30; i++ )
			if( (current_time+i) % 30 == 0 ) { 
				t1 = current_time+i;
				t2 = t1 + 30;
				t3 = t1 + 60;
				t4 = t1 + 90;
				t5 = t1 + 120;
				break;
			}
		 */		
		// p1 = getWidth() / 120; 
		// p2 = 30*getWidth() / 120; 
		// p3 = 60*getWidth() / 120; 
		// p4 = 90*getWidth() / 120;
		// p5 = 119*getWidth() / 120;
		p1 = 1;
		p2 = 59*getWidth() / 240; 
		p3 = 117*getWidth() / 240; 
		p4 = 175*getWidth() / 240;
		p5 = 233*getWidth() / 240;
		
		g.setColor(new Color(238,238,238));
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.BLACK);
		g.setFont(MyFont.fontSmallBold);
		/*
		g.drawString(setTimeFormat(t1), p1-10, 10);
		g.drawString(setTimeFormat(t2), p2-10, 10);
		g.drawString(setTimeFormat(t3), p3-10, 10);
		g.drawString(setTimeFormat(t4), p4-10, 10);
		g.drawString(setTimeFormat(t5), p5-10, 10);
		*/
		g.drawString("T(s)", p1, 10);
		g.drawString("T+120", p2-20, 10);
		g.drawString("T+240", p3-20, 10);
		g.drawString("T+360", p4-20, 10);
		g.drawString("T+480", p5-25, 10);
	}
	
	public void refresh(int milliseconds) {
		/*
		current_time = milliseconds / 1000;	
		
		for( int i=0; i<=30; i++ ) 
			if( (current_time+i) % 30 == 0 ) {
				p1 = i      *getWidth() / 120; 
				p2 = (i+30) *getWidth() / 120; 
				p3 = (i+60) *getWidth() / 120; 
				p4 = (i+90) *getWidth() / 120; 				
				p5 = (i+119)*getWidth() / 120;
				break;
			}
		*/
		repaint();
	}
	
	public String setTimeFormat(int time) {
		String time_min = "" + time/60;
		String time_sec = "" + time%60;
				
		if(time_min.length() == 1) time_min = "0" + time_min;
		if(time_sec.length() == 1) time_sec = "0" + time_sec;
		
		return time_min + ":" + time_sec;
	}
}

class VehicleIcon extends JPanel {
	private static final long serialVersionUID = -5961320475456746793L;
	private Vehicle v;
	private Color vColor;
	private boolean colorFlag;
	
	public VehicleIcon(Vehicle v) { 
		this.v = v;
		vColor = MyColor.COLOR_VEHICLE;
	}
	
	public void paint( Graphics g ) {  	  
		if( v.getType() == "UAV" ) {
	    	g.setColor(Color.BLACK);
	        g.drawArc(5, 0, MySize.SIZE_VEHICLE_WIDTH_TMS_PXL, MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL, 0, 180);
	        g.drawLine(5, MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL/2, MySize.SIZE_VEHICLE_WIDTH_TMS_PXL+5, MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL/2);
	    	g.setColor(vColor);
	        g.fillArc(5, 0, MySize.SIZE_VEHICLE_WIDTH_TMS_PXL, MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL, 0, 180);
	        paintString((Graphics2D)g, MySize.SIZE_VEHICLE_WIDTH_TMS_PXL/2+3, MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL/2-5, Color.black, MyFont.fontSmallBold, Integer.toString(v.getIndex()));
	   	} else if( v.getType() == "UUV") {
	    	g.setColor(Color.BLACK);
	        g.drawArc(5, -MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL/2, MySize.SIZE_VEHICLE_WIDTH_TMS_PXL, MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL, 180, 180);	        
	        g.drawLine(5,0,MySize.SIZE_VEHICLE_WIDTH_TMS_PXL+5,0);
	    	g.setColor(vColor);
	     	g.fillArc(5, -MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL/2, MySize.SIZE_VEHICLE_WIDTH_TMS_PXL, MySize.SIZE_VEHICLE_HEIGHT_TMS_PXL, 180, 180);
	     	paintString((Graphics2D)g, MySize.SIZE_VEHICLE_WIDTH_TMS_PXL/2+3, 14, Color.black, MyFont.fontSmallBold, Integer.toString(v.getIndex()));
	   	}
	}
	
	public void paintString(Graphics2D g, int x, int y,  Color color, Font font, String str) {    
	    	g.setColor(color); 
	    	g.setFont(font);
	    	g.drawString(str, x , y );
	}
	
	public void chkEngageEnabled() {		
		if( v.getStatus() == MyGame.STATUS_VEHICLE_PENDING ) {  
			colorFlag = !colorFlag; 
			if( colorFlag ) vColor = MyColor.COLOR_VEHICLE; 
			else vColor = MyColor.COLOR_VEHICLE_PENDING;
		}
		else { vColor = MyColor.COLOR_VEHICLE; }
	}
}

class VehicleTime extends JPanel {
	private static final long serialVersionUID = 4435452374430336399L;
	private Vehicle v;
	private int p0, p1, p2, p3, p4, p5, p6, p7, p8;
    protected LinkedList<int[]> pathList = new LinkedList<int[]>(); // TUPLE<DISTANCE/VELOCITY, PATH_TYPE> PATH_TYPE={WP:0, GP:1, GP(COMM):2}  
	
	public VehicleTime(Vehicle v) { this.v = v;}
	public void paint( Graphics g ) {
		g.clearRect(0, 0, getWidth(), getHeight());
		// g.setColor(MyColor.COLOR_VEHICLE_TIMELINE);
		g.setColor(MyColor.COLOR_VEHICLE);
		g.fillRect(0, 0, getWidth(), getHeight());
	
		// Vertical Line
		/*
		p1 = getWidth() / 120;
		p2 = 30*getWidth() / 120;
		p3 = 60*getWidth() / 120;
		p4 = 90*getWidth() / 120;
		p5 = 119*getWidth() / 120;
		*/
		p0 = 1;
		p1 = 30*getWidth() / 240;
		p2 = 59*getWidth() / 240;
		p3 = 88*getWidth() / 240;
		p4 = 117*getWidth() / 240;
		p5 = 146*getWidth() / 240;
		p6 = 175*getWidth() / 240;
		p7 = 204*getWidth() / 240;
		p8 = 233*getWidth() / 240;
		
		g.setColor(new Color(100,100,100));
		g.drawLine(0, 0, getWidth()-1, 0);
		g.drawLine(0, getHeight()-1, getWidth()-1, getHeight()-1);
		g.drawLine(p0, 0, p0, getHeight());
		g.drawLine(p1, 0, p1, getHeight());
		g.drawLine(p2, 0, p2, getHeight());
		g.drawLine(p3, 0, p3, getHeight());
		g.drawLine(p4, 0, p4, getHeight());
		g.drawLine(p5, 0, p5, getHeight());
		g.drawLine(p6, 0, p6, getHeight());
		g.drawLine(p7, 0, p7, getHeight());
		g.drawLine(p8, 0, p8, getHeight());

		for( int i=0; i<pathList.size(); i++ ) 
			if( pathList.get(i)[0] < 120 ){
				if(pathList.get(i)[1] == 0 ) g.setColor(new Color(0,0,255,200));
				else if(pathList.get(i)[1] == 1) g.setColor(MyColor.COLOR_TARGET_OCCUPIED);
				else g.setColor(MyColor.COLOR_TARGET_COMM_OCCUPIED);
				if( v.getTarget() != null && pathList.get(i)[1] != 0) {
					g.fillRect(pathList.get(i)[0]*getWidth()/120, 0, 11, getHeight());
					g.setColor(Color.white); g.setFont(MyFont.fontSmallBold);
					g.drawString(v.getTarget().getName(), pathList.get(i)[0]*getWidth()/120+2, getHeight()/2+4);
				}
				else g.fillRect(pathList.get(i)[0]*getWidth()/120, 0, 3, getHeight());
			}
	}
	
	public void setPathList() {
		pathList.clear(); 
		int flag;
		for(int i=-1; i<v.getPathSize()-1; i++) { 
			 if( i==-1 ) {
				 if( i == v.getPathSize()-2 ) flag = 1; else flag = 0;		 
				 pathList.add(new int[]{
						 (int)Math.round(
								 getD(v.getPath().get(0)[0], v.getPath().get(0)[1], v.getX(), v.getY()) * (v.getVelocity()/200.0) ), flag});
			 }
			 else {
				 if( i == v.getPathSize()-2 ) flag = 1; else flag = 0;
				 pathList.add(new int[]{pathList.getLast()[0]+
						 (int)Math.round(
								 getD(v.getPath().get(i+1)[0], v.getPath().get(i+1)[1], v.getPath().get(i)[0], v.getPath().get(i)[1]) * (v.getVelocity()/200.0) ), flag});
			 }
		} 
	}
	private double getD(int x1, int y1, int x2, int y2) { return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2)); }
	public void refresh(int milliseconds) { setPathList(); repaint(); }
}

class RemainingTime extends JPanel { 
	private static final long serialVersionUID = -9112337331887745204L;
	private int current_time = 0;
	private int remaining_time = 0;
    public RemainingTime(Game g) {
		JLabel lblBlank = new JLabel("  ");
		this.add(lblBlank);
	}
	public void paint( Graphics g ) {	
		
		Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
		g.setColor(new Color(238,238,238));
		g.fillRect(0,0,getWidth(),getHeight());
		
		g.setColor(new Color(100,100,100,255));
		g.drawRect(0,0,getWidth()-1,getHeight()-1);
		g.setColor(new Color(150,150,150,100));
		g.fillRect(0,0,current_time*getWidth()/MySpeed.TOTAL_SECOND,getHeight());
		g.setColor(new Color(0,0,0,255));
		g.drawString("REMAINS     " + setTimeFormat(remaining_time), getWidth()/3, getHeight()/2+5);
		
		/*
		g.setColor(Color.BLACK); 
		g.setFont(MyFont.fontBold);
		g.drawString("CURRENT [ " + setTimeFormat(current_time) + "] / REMAINS [ " + setTimeFormat(remaining_time) + " ]", 0, getHeight()/2+5);
		*/
	}
	
	public void refresh(int milliseconds) {
		current_time = milliseconds/1000;
		remaining_time = Game.TIME_TOTAL_GAME/1000 - current_time;		
		repaint();
	}
	
	public String setTimeFormat(int time) {
		String time_min = "" + time/60;
		String time_sec = "" + time%60;
				
		if(time_min.length() == 1) time_min = "0" + time_min;
		if(time_sec.length() == 1) time_sec = "0" + time_sec;
		
		return time_min + ":" + time_sec;
	}
}