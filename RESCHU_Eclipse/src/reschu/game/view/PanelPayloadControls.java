package reschu.game.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*; 

import java.io.IOException;
import java.net.*; 

import reschu.constants.MyURL;
import reschu.game.controller.GUI_Listener;

public class PanelPayloadControls extends JPanel implements ActionListener 
{
	private static final long serialVersionUID = -3327951582205927390L;  
    private GUI_Listener listener;
    JButton btnUp, btnDown, btnCW, btnCCW, btnZoomIn, btnZoomOut;
    JButton btnSubmit;
    private URL urlImgZoomIn, urlImgZoomOut; //,urlImgUp, urlImgDown, urlImgCW, urlImgCCW;
    private String fileImgZoomIn, fileImgZoomOut;
    double origin_time;
    
    /** Creates a new instance of PanelPayloadControls */
    public PanelPayloadControls(GUI_Listener e, String strTitle, double o) {
        origin_time = o;
        String pan_prefix = "Pictures/pan/"; //HERE
        
//        try { urlImgUp = new URL(url_prefix+"pan_up.gif");
//            btnUp = new JButton(new ImageIcon(urlImgUp));
//        } catch (MalformedURLException urle) {
//            btnUp = new JButton("UP");}
//        try {
//            urlImgDown = new URL(url_prefix+"pan_down.gif");
//            btnDown = new JButton(new ImageIcon(urlImgDown));
//        } catch (MalformedURLException urle) {
//            btnDown = new JButton("DOWN");}
//        try {
//            urlImgCW = new URL(url_prefix+"pan_right.gif");
//            btnCW = new JButton(new ImageIcon(urlImgCW));
//        } catch (MalformedURLException urle) {
//            btnCW = new JButton("CW");}
//        try {
//            urlImgCCW = new URL(url_prefix+"pan_left.gif");
//            btnCCW = new JButton(new ImageIcon(urlImgCCW));
//        } catch (MalformedURLException urle) {
//            btnCCW = new JButton("CCW");}
        
            fileImgZoomIn = pan_prefix + "zoom_in.gif";
            btnZoomIn = new JButton(new ImageIcon(fileImgZoomIn));
            /*	{
            btnZoomIn = new JButton("+");}	*/
        
            fileImgZoomOut = pan_prefix+"zoom_out.gif";
            btnZoomOut = new JButton(new ImageIcon(fileImgZoomOut));
            /*	{
            btnZoomOut = new JButton("-");   }	*/
        
        btnSubmit = new JButton("OK");
        btnSubmit.setEnabled(false);
        /*
        setLayout(new GridLayout(6,1));        
        add(btnUp); 		btnUp.addActionListener(this);
        add(btnDown); 		btnDown.addActionListener(this);
        add(btnCW); 		btnCW.addActionListener(this);
        add(btnCCW); 		btnCCW.addActionListener(this);
        add(btnZoomIn); 	btnZoomIn.addActionListener(this);
        add(btnZoomOut);	btnZoomOut.addActionListener(this);
        */ 
        setLayout(new GridLayout(2,1));
        add(btnZoomIn); 	btnZoomIn.addActionListener(this);
        // add(btnSubmit);
        // btnSubmit.addActionListener(this);
        add(btnZoomOut);	btnZoomOut.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnUp)		listener.panUpSelected(); 
        if(e.getSource() == btnDown) 	listener.panDownSelected();
        if(e.getSource() == btnCW)   	listener.rotateCWSelected();
        if(e.getSource() == btnCCW)		listener.rotateCCWSelected();
        if(e.getSource() == btnZoomIn)	listener.zoomIn();
        if(e.getSource() == btnZoomOut)	listener.zoomOut();
        if(e.getSource() == btnSubmit)  listener.submitPayload();
    }
    
    public void enableSubmit(boolean enable) { btnSubmit.setEnabled(enable); }
    public void setListener(GUI_Listener l) { listener = l; }
    
}