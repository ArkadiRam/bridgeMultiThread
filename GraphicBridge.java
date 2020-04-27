import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class GraphicBridge extends JFrame   {
	
	/** 
	 * @author Arkadzi Zaleuski
	 * Data: 24 stycznia 2020 r.
	 */
	private static final long serialVersionUID = 1L;
	
	class Panel extends JPanel{
		/**
		 * 
		 */
		
		private static final long serialVersionUID = 1L;
		Font bigFont = new Font("Segoe Script", Font.BOLD, 22);
		
		public Panel() {
			super();
		}
	
		
		public void paintComponent(Graphics g) {
			
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, 50, 800);
			g.fillRect(450, 0, 70, 800);
			g.setColor(Color.DARK_GRAY);
			g.fillRect(50, 0, 100, 800);
			g.fillRect(350, 0, 100, 800);
			g.setColor(Color.MAGENTA);
			g.fillRect(150, 0, 50, 800);
			g.fillRect(300, 0, 50, 800);
			g.setColor(Color.cyan);
			g.fillRect(200, 0,100 , 800);	
			g.setFont(bigFont);
			g.setColor(Color.WHITE);
			Graphics2D g2 = (Graphics2D)g;
			AffineTransform at = new AffineTransform();
	        at.setToRotation(Math.toRadians(-90));
	        g2.setTransform(at);
	        for(int i=1;i<=6;i++) {
			g.drawString("Parking", -120*i, 40);
			g.drawString("Road", -120*i, 120);
			g.drawString("Gate", -120*i,180);
			g.drawString("Bridge", -120*i, 260);
			g.drawString("Gate", -120*i, 330);
			g.drawString("Road", -120*i, 410);
			g.drawString("Parking", -120*i, 470);
	        }
	        at.setToRotation(Math.toRadians(0));
	        g2.setTransform(at);
	       synchronized(bridge.allBuses) {
	        	for(Bus bus: bridge.allBuses) {
	        		try {
						bus.visualModel.draw(g2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }	
	        }
		}
		}
			
	public NarrowBridge bridge;
	private Panel panel = new Panel();
	Thread animation = new Thread(new Runnable() {
		public  void run() {
			while(true) {
				synchronized(bridge.allBuses) {
				for(Bus bus:bridge.allBuses) {
					bus.visualModel.setX((bus.visualModel.getX()+bus.visualModel.getSpeed()));
				}
				}
				panel.repaint();
			
			
			try {

				Thread.sleep(16);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		});
	public Panel getPanel() {
		return panel;
	}
	JMenuBar bar = new JMenuBar();
	JMenu info = new JMenu("Info");
	JMenuItem item = new JMenuItem("Autor");
	public GraphicBridge(NarrowBridge bridge) {
		super("Bridge");
		setSize(520,800);
		setResizable(false);
		bar.add(info);
		info.add(item);
		item.addActionListener((action)->{
			JOptionPane.showMessageDialog(this, "Autor: Arkadzi Zaleuski\n Index: 250929\n Data: 24 stycznia 2020");
		});
		setJMenuBar(bar);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setContentPane(panel);
		this.setLocation(420,0);
		setVisible(true);
		this.bridge = bridge;
	}	
}