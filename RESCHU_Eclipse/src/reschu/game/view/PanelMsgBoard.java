package reschu.game.view;

import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*; 
import javax.swing.border.TitledBorder;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import reschu.game.controller.Reschu;

public class PanelMsgBoard extends JPanel implements ActionListener
{	
	private static final long serialVersionUID = -6444398994914980642L;
	private static final String DATE_FORMAT_NOW = "HH:mm:ss";

	private GridBagLayout grid_bag_layout = new GridBagLayout();
	private static JTextArea txtMsgBoard = new JTextArea(5,5);
	private static JTextField txtChat = new JTextField(1);
	private static JLabel lblChat = new JLabel(">Msg: ");
	private JScrollPane scrollPane;
	private JButton btnSend = new JButton("SEND");
	
	private Reschu reschu;
	
	public PanelMsgBoard() {	
		TitledBorder bdrTitle = BorderFactory.createTitledBorder("Message");
		this.setBorder(bdrTitle);
		
		GridBagConstraints gbc = new GridBagConstraints();

		txtMsgBoard.setEditable(false);
		txtMsgBoard.setLineWrap(true);
		
		scrollPane = new JScrollPane(txtMsgBoard);
		scrollPane.setAutoscrolls(true);
		scrollPane.remove(scrollPane.getHorizontalScrollBar());		
		
		txtChat.addActionListener(this);
		btnSend.addActionListener(this);
		
		this.setLayout(grid_bag_layout);				 
		this.insert_grid(gbc, scrollPane, 0, 0, 3, 1, 1.0, 1.0, 0); this.add(scrollPane); 
		this.insert_grid(gbc, lblChat, 0, 1, 1, 1, 0.0, 0.0, 0); this.add(lblChat);
		this.insert_grid(gbc, txtChat, 1, 1, 1, 1, 1.0, 0.0, 0); this.add(txtChat);
		this.insert_grid(gbc, btnSend, 2, 1, 1, 1, 0.0, 0.0, 0); this.add(btnSend);
	}
	
	public void setReschuInstance(Reschu r) {
		reschu = r;
	}
	
	public static void Msg(String msg) {
		//String timestamp = Calendar.HOUR + ":" + Calendar.MINUTE + ":" + Calendar.SECOND;		
		txtMsgBoard.setText(txtMsgBoard.getText() + Now() + "   " + msg + "\n");
	}
	
	private static String Now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
	
    private void insert_grid(GridBagConstraints gbc, Component cmpt,
    		int x, int y, int width, int height, double percent_x, double percent_y, int ins) {
    	
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = percent_x;
        gbc.weighty = percent_y;
        gbc.insets = new Insets(ins, ins, ins, ins);
        grid_bag_layout.setConstraints(cmpt, gbc);
    }
    
    public void actionPerformed(ActionEvent e) {
    	if( e.getSource() == btnSend || e.getSource() == txtChat ) {
    		Msg("(Operator) " + txtChat.getText()); txtChat.setText("");
    	}
    	if (e.getSource() == btnSend) {
    		reschu.Payload_Finished_From_Msg();
    	}
    }
}
