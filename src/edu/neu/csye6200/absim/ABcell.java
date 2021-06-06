package edu.neu.csye6200.absim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * JPanel Object that contains the properties of a cell in the map and displays it
 */
public class ABcell extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ABcell.class.getName());
	private int oilCount;                                                                      //oil in this cell
	private ArrayList<ABoat> boats;                                                            //boats currently in this cell
	public int x;                                                                              //x coordinate of this cell
	public int y;                                                                              //y coordinate of this cell
	public BufferedImage image = null;                                                         //icon of this cell
	private Color bgColor = new Color(0,0,0);                                                  //background color of this cell

	public ABcell(int x, int y){
		oilCount=0;
		boats=new ArrayList<ABoat>();
		this.x=x;
		this.y=y;
		refresh();
	}
	
	/**
	 * sets this cells background color and icon depending on the oilcount and boats list in this cell
	 */
	public void refresh() {
		if(boats.size()==0){
			image = null;
			if(oilCount>0) {
				int bgColorVal = (int)(150-((oilCount/10)*1.5));       //sets a shade of grey depending on how much oil it holds
				if(bgColorVal<0) bgColorVal=0;                         //makes sure a very large oil count does not overflow rgb range
				bgColor = new Color(bgColorVal,bgColorVal,bgColorVal);
			}
			else if (oilCount==0) bgColor = new Color(55,114,196);   //water cell
			else if (oilCount<0) bgColor = new Color(59,117,49);     //land cell
		}
		else {
			image = ABmap.icons.get("boat");
			if(boats.get(boats.size()-1) instanceof ACleaningBoat) bgColor = new Color(255,255,255);
			else if(boats.get(boats.size()-1) instanceof ACollectorBoat) bgColor = new Color(97,51,20);
			else if(boats.get(boats.size()-1) instanceof ARefuelBoat) bgColor = new Color(219,208,53);
			for (ABoat b: boats) {
				if(b.isSelected()) bgColor = new Color(235,61,61);  //sets bg color to red if the boat is selected
			}
		}
		setBackground(bgColor);
	}

	/**
	 * @return the oilCount
	 */
	public int getOilCount() {
		return oilCount;
	}

	/**
	 * @param oilCount the oilCount to set
	 */
	public void setOilCount(int oilCount) {
		this.oilCount = oilCount;
		refresh();
	}

	/**
	 * @return the boats in this cell
	 */
	public ArrayList<ABoat> getBoats() {
		return boats;
	}

	/**
	 * adds a boat to this cell
	 */
	public void addBoat(ABoat boat) {
		boats.add(boat);
		refresh();
	}
	
	/**
	 * removes a boat from this cell
	 */
	public void removeBoat(ABoat boat) {
		boats.remove(boat);
		refresh();
	}
	
	 @Override
	 protected void paintComponent(Graphics g) {
	     super.paintComponent(g); // paint the background image and scale it to fill the entire space
	     if(image!=null) g.drawImage(image,0,0,this.getWidth(), this.getHeight(),null);
	 }
	
	
	
}
