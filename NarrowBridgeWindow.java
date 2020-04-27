import java.awt.BorderLayout;
import java.io.IOException;
import java.sql.Time;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class NarrowBridgeWindow extends JFrame {

	/** 
	 * @author Arkadzi Zaleuski
	 * Data: 24 stycznia 2020 r.
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel = new JPanel();
	JTextArea area = new JTextArea(20,40);
	JScrollPane scroll = new JScrollPane(area);
	String[] boxOptions = {"Ruch bez ograniczen","ruch dwukierunkowy, liczba aut na moście ograniczona","ruch jednokierunkowy, liczba aut na moście ograniczona","przez most przejeżdża zawsze tylko jedno auto. "};
	JSlider slider = new JSlider(JSlider.HORIZONTAL,1000,5000,3000);
	JComboBox<String> box  = new JComboBox<String>(boxOptions);
	NarrowBridge bridge;
	JTextField queueField = new JTextField(25);
	JTextField bridgeField = new JTextField(25);
	JMenuBar bar = new JMenuBar();
	JMenu info = new JMenu("Info");
	JMenuItem item = new JMenuItem("Autor");
	public void initializePanel() {
		
	JLabel boxLabel = new JLabel("Mode");	
	JLabel sliderLabel = new JLabel("Natezenie",SwingConstants.CENTER);	
	
	panel.add(boxLabel);
	
	panel.add(box);
	panel.add(sliderLabel);
	panel.add(slider);
	panel.add(queueField);
	panel.add(bridgeField);
			area.setBounds(20, 20, 600, 400);
			panel.add(scroll,BorderLayout.PAGE_START);
		box.addActionListener((action)->{
			
			if(box.getSelectedItem().equals(boxOptions[0])) {
				bridge.setMode(1);
			}
			else if(box.getSelectedItem().equals(boxOptions[1])){
				bridge.setMode(2);
			}
			else if(box.getSelectedItem().equals(boxOptions[2])){
				bridge.setMode(3);
			}
			else if(box.getSelectedItem().equals(boxOptions[3])){
				bridge.setMode(4);
			}
			
		});
		
	slider.addChangeListener((change)->{
		int a = slider.getValue();
		NarrowBridgeConsole.TRAFFIC=a;
	});	
	slider.setMajorTickSpacing(100);
	slider.setPaintTicks(true);
	
	
	}
	
	public void setBridge(NarrowBridge n) {
		this.bridge = n;
	}
	
	public NarrowBridgeWindow() {
		super("Narrow Bridge");
		setSize(440,520);
		initializePanel();
		this.setContentPane(panel); 
		bar.add(info);
		info.add(item);
		item.addActionListener((action)->{
			JOptionPane.showMessageDialog(this, "Autor: Arkadzi Zaleuski\n Index: 250929\n Data: 24 stycznia 2020");
		});
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		area.setEditable(false);
		setResizable(false);
		this.setJMenuBar(bar);
		setVisible(true);
	}
	
	public static synchronized void main(String[] args) throws IOException {
		NarrowBridgeWindow nbw = new NarrowBridgeWindow();
		NarrowBridge bridge = new NarrowBridge();
		GraphicBridge bridgeGraphical = new GraphicBridge(bridge);
		bridge.setFrame(nbw);
		nbw.setBridge(bridge);
		Bus bus;
		bridgeGraphical.animation.start();
		
		while(true) {
		try {			
			synchronized(bridge.allBuses) {
			bus = new Bus(bridge,nbw);
			}
			new Thread(bus).start();
			bridgeGraphical.getPanel().repaint();
			if(!nbw.isVisible()) {
				System.exit(0);
			}
			Thread.sleep(5200 - NarrowBridgeConsole.TRAFFIC);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
	}
}
	
