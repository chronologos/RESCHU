package reschu.game.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseWheelListener;

import com.jogamp.opengl.awt.GLJPanel;

import java.awt.event.MouseWheelEvent;

public class MyCanvas extends GLJPanel { 
	private static final long serialVersionUID = 2481159327202652931L;
	PanelPayload pnlPayload; 
	
    public MyCanvas() { 
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent m_ev) {
            	// forwards mouse events to the payload window 
            	// only when the panel is enabled
            	if( !pnlPayload.isEnabled() ) return;
            	
            	pnlPayload.mouse_click(m_ev); 
            }
        }); 
        addMouseWheelListener(new MouseWheelListener() {   
            public void mouseWheelMoved(MouseWheelEvent m_ev) {
            	// forwards mouse events to the payload window 
            	// only when the panel is enabled
            	if( !pnlPayload.isEnabled() ) return;
            	
            	int notches = m_ev.getWheelRotation();
                pnlPayload.setClicked(false);
                //pnlPayload.getPopMenu().setVisible(false);
            	if( notches < 0 ) pnlPayload.zoom_in();
            	else pnlPayload.zoom_out(); 
            }
        	
        });
    }

    public void addListener(PanelPayload p) {pnlPayload = p;}
}
