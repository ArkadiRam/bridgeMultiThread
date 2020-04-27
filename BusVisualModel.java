import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BusVisualModel {
	
	/** 
	 * @author Arkadzi Zaleuski
	 * Data: 24 stycznia 2020 r.
	 */
	private int id= 2;
	private float x=0;
	private int y=0;
	public int destination = 0;
	private float speed = 0;
	public BufferedImage image;
	public BusVisualModel(int x,int y, int id, BusDirection dir) throws IOException  {
	
		this.id = id;
		if(dir == BusDirection.WEST) {
			image = ImageIO.read(new File("C:\\Users\\Arkan\\eclipse-projects\\Treads\\src\\carW.png"));
			this.x =460- x;
			this.y = y;
			this.speed = -1;
		}
			else if(dir == BusDirection.EAST) {
				this.x = x;
				this.y = y;
				this.speed = 1;
				image = ImageIO.read(new File("C:\\Users\\Arkan\\eclipse-projects\\Treads\\src\\car2.png"));
			}
		
		}
	public float getSpeed() {
		return speed;
	}
	public void ride(BusDirection dir,int time,int dist) {
		if(dir == BusDirection.WEST)
		speed =10* (float)-dist/time;
		else speed = 10*(float)dist/time;
	}
	public void stop() {
		speed = 0;
	}
	public float getX()
	{
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public int getY()
	{
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public void draw(Graphics g) throws IOException {
		g.drawImage(this.image,(int)x,y,null);
		g.setColor(Color.BLACK);
		g.drawString(Integer.toString(id), Math.round(x)+13, y+23);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
