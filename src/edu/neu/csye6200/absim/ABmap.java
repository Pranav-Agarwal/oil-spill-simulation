package edu.neu.csye6200.absim;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.neu.csye6200.absim.ABoat.State;


/**
 * Singleton class that contains the state of the simulation map
 * the singleton pattern has been modified slightly to accept the size parameter during initialization.
 * It is also a JPanel object that contains the ABcell Jpanels.
 */
public class ABmap extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ABmap.class.getName());
	private static ABmap instance;             //single instance of this class
	public static HashMap<String,BufferedImage> icons = new HashMap<String,BufferedImage>() ;  //shared HashMap of loaded icons
	public ABcell[][] cells;                   //grid of ABcell objects
	public int size;                           //side length of map square grid
	public HashMap<String,int[]> dumpStations; //HashMap of dump stations. each dumpStation is represented by an int[]
	public ArrayList<ABoat> boats;             //list of boats in map
	public int spreadRate;                     //spread rate of oil in map
	public int initialOilCount=0;              //stores the starting amount of oil in the map
	
	/* Statistics about the current map state */	
	private int oilInMap=0;                    //stores the current amount of oil in the map
	private int fuelUsed=0;                    //stores the total fuel used by boats so far
	private int oilInStorage=0;                //stores the total oil collected and deposited to storage so far
	private int timeElapsed=0;                 //simulated ticks occured since simulation start
	private boolean isDone = false;

	static {
		/* Loads all icons and places them in the HashMap when this class is initialized. */
		try {
			icons.put("fuelStation",ImageIO.read(ABcell.class.getResource("/edu/neu/csye6200/absim/icons/fuelStation.png")));
			icons.put("dumpStation",ImageIO.read(ABcell.class.getResource("/edu/neu/csye6200/absim/icons/dumpStation.png")));
			icons.put("oilStorage",ImageIO.read(ABcell.class.getResource("/edu/neu/csye6200/absim/icons/oilStorage.png")));
			icons.put("boat",ImageIO.read(ABcell.class.getResource("/edu/neu/csye6200/absim/icons/boat.png")));
		} catch (IOException e) {
			log.info("could not read icon");
			e.printStackTrace();
		}
	}
	
	private ABmap(int size) {
		this.size=size;
		dumpStations = new HashMap<String,int[]>();
		boats = new ArrayList<ABoat>();
		cells = new ABcell[size][size];
		for(int row=0;row<size;row++) {
			for(int col=0;col<size;col++) {
				cells[row][col] = new ABcell(row,col);
				add(cells[row][col]);
			}
		}
		/* Sets icons for predefined special cells in the map */
		cells[0][5].image = icons.get("fuelStation");
		cells[0][3].image = icons.get("oilStorage");
		cells[0][0].image = icons.get("dumpStation");
		cells[0][size-1].image = icons.get("dumpStation");
		cells[size-1][0].image = icons.get("dumpStation");
		cells[size-1][size-1].image = icons.get("dumpStation");
		cells[size/2][0].image = icons.get("dumpStation");
		cells[0][size/2].image = icons.get("dumpStation");
		cells[size-1][size/2].image = icons.get("dumpStation");
		cells[size/2][size-1].image = icons.get("dumpStation");
	}
	
	/**
	 * creates the single instance of this class
	 */
	public static void init(int size) {
		if(instance==null) instance = new ABmap(size);
	}
	
	/**
	 * returns the singleton ABmap object
	 */
	public static ABmap instance() {
		return instance;
	}
	
	/**
	 * Adds landmasses to the map
	 */
	public void addLand() {
		for (int row=2;row<size-2;row++) {
			for(int col=2;col<size-2;col++) {
				if (Math.random()<=0.003) {    //0.3 % chance of converting an ocean cell to a land 'seed' cell
					spreadLand(100,row,col);
				}
			}
		}
		log.info("Landmasses added to map");
	}
	
	/**
	 * recursively spread land from a land 'seed' cell to create a larger, more realistic landmass
	 */
	private void spreadLand(double spreadChance,int row,int col) {
		/* checks if cell already has land, or is at edges, or spread chance is too low */
		if(cells[row][col].getOilCount()==-1 || spreadChance<=10 || row<=1 || row>=size-2 || col<=1 || col >=size-2) return;
		cells[row][col].setOilCount(-1);
		double spreadMin = 0.2;
		double spreadMax = 0.5;
		/* calls for spreading to all neighbors with a random, but diminishing chance to spread */
		spreadLand(spreadChance*(Math.random()*(spreadMax-spreadMin)+spreadMin),row+1,col);
		spreadLand(spreadChance*(Math.random()*(spreadMax-spreadMin)+spreadMin),row+1,col-1);
		spreadLand(spreadChance*(Math.random()*(spreadMax-spreadMin)+spreadMin),row+1,col+1);
		spreadLand(spreadChance*(Math.random()*(spreadMax-spreadMin)+spreadMin),row-1,col);
		spreadLand(spreadChance*(Math.random()*(spreadMax-spreadMin)+spreadMin),row-1,col+1);
		spreadLand(spreadChance*(Math.random()*(spreadMax-spreadMin)+spreadMin),row-1,col-1);
		spreadLand(spreadChance*(Math.random()*(spreadMax-spreadMin)+spreadMin),row,col+1);
		spreadLand(spreadChance*(Math.random()*(spreadMax-spreadMin)+spreadMin),row,col-1);
	}
	
	/**
	 * converts 'countOfSpills' random cells into oil cells in the centre 30% of the map.
	 * countOfSpills currently hardcoded to 3.
	 */
	public void createOilSpill(int countOfSpills,int intensityOfSpill,int centerRow,int centerCol) {
		for(int i=0;i<countOfSpills;i++) {
			double randomMultiplierRow = Math.random()*(1.3-0.7)+0.7;
			double randomMultiplierCol = Math.random()*(1.3-0.7)+0.7;
			double randomMultiplierIntensity = (Math.random()*(1.2-0.8)+0.8)*300;
			ABcell t=null;
			int k=-1;
			int timeout = 0;
			/* checks if randomly selected cell is a landmass, if so looks for another cell (until timeout of 50 tries is reached) */
			while(k==-1 && timeout<50) {
				timeout++;
				t = cells[(int)(centerRow*randomMultiplierRow)][(int)(centerCol*randomMultiplierCol)];
				k = t.getOilCount();
			}
			if (timeout<50) t.setOilCount((int)(intensityOfSpill*randomMultiplierIntensity));
			else log.severe("could not find a place to create an oil spill");
		}
		findOilInMap();                //refreshes the oil count in the map
		initialOilCount = oilInMap;    //sets initial oil count variable to this value.
		log.info("Oil spill created on map");
	}

	/**
	 * looks for oil cells in the map and spreads it to it's neighbors if they have atleast 'spreadDelta' less oil.
	 */
	public void spreadOil(int spreadDelta) {
		double multiplier=0.1;      //what percent of current oil to spread to neighbors
		int currentOil=0;
		int temp=0;
		for (int row=1;row<size-1;row++) {
			for(int col=1;col<size-1;col++) {
				currentOil = cells[row][col].getOilCount();
				temp=0;
				if(currentOil>0) {
					int t = 0;
					
					/* adds oil to neighbors and subtracts it from itself*/
					temp = cells[row+1][col].getOilCount();
					if(temp>=0 && currentOil-temp>spreadDelta) {
						t = (int)(currentOil*multiplier) ;
						cells[row+1][col].setOilCount(temp+t);
						currentOil-=t;
						cells[row][col].setOilCount(currentOil);
					}
					temp = cells[row+1][col+1].getOilCount();
					if(temp>=0 && currentOil-temp>spreadDelta) {
						t = (int)(currentOil*multiplier) ;
						cells[row+1][col+1].setOilCount(temp+t);
						currentOil-=t;
						cells[row][col].setOilCount(currentOil);
					}
					temp = cells[row+1][col-1].getOilCount();
					if(temp>=0 && currentOil-temp>spreadDelta) {
						t = (int)(currentOil*multiplier) ;
						cells[row+1][col-1].setOilCount(temp+t);
						currentOil-=t;
						cells[row][col].setOilCount(currentOil);
					}
					temp = cells[row-1][col].getOilCount();
					if(temp>=0 && currentOil-temp>spreadDelta) {
						t = (int)(currentOil*multiplier) ;
						cells[row-1][col].setOilCount(temp+t);
						currentOil-=t;
						cells[row][col].setOilCount(currentOil);
					}
					temp = cells[row-1][col+1].getOilCount();
					if(temp>=0 && currentOil-temp>spreadDelta) {
						t = (int)(currentOil*multiplier) ;
						cells[row-1][col+1].setOilCount(temp+t);
						currentOil-=t;
						cells[row][col].setOilCount(currentOil);
					}
					temp = cells[row-1][col-1].getOilCount();
					if(temp>=0 && currentOil-temp>spreadDelta) {
						t = (int)(currentOil*multiplier) ;
						cells[row-1][col-1].setOilCount(temp+t);
						currentOil-=t;
						cells[row][col].setOilCount(currentOil);
					}
					temp = cells[row][col+1].getOilCount();
					if(temp>=0 && currentOil-temp>spreadDelta) {
						t = (int)(currentOil*multiplier) ;
						cells[row][col+1].setOilCount(temp+t);
						currentOil-=t;
						cells[row][col].setOilCount(currentOil);
					}
					temp = cells[row][col-1].getOilCount();
					if(temp>=0 && currentOil-temp>spreadDelta) {
						t = (int)(currentOil*multiplier) ;
						cells[row][col-1].setOilCount(temp+t);
						currentOil-=t;
						cells[row][col].setOilCount(currentOil);
					}

				}
			}
		}
	}
	
	/**
	 * sums up the total oil in the map and updates the oilInMap class variable.
	 */
	public void findOilInMap() {
		int t=0;
		for (int row=1;row<size-1;row++) {
			for(int col=1;col<size-1;col++) {
				int temp = cells[row][col].getOilCount();
				if(temp>0) t+=temp;		
				}	
		}
		oilInMap = t;
	}

	/**
	 * calls the update method of all boats in the map.
	 * Also updates the map statistics and spreads oil if appropriate.
	 */
	public void updateMap() {
		
		timeElapsed+=1;
		
		/*spreads oil every 'spreadRate' simulation ticks*/
		if(timeElapsed%spreadRate==0) spreadOil(50);
		
		/*calls the update method of every boat in the map*/
		for (ABoat b : boats) {
			b.update();
		}
		
		/*updates the oil remaining in the map*/
	    findOilInMap();
	    isDone = checkIfDone();
	}
	
	/**
	 * @return the timeElapsed
	 */
	public int getTimeElapsed() {
		return timeElapsed;
	}

	/**
	 * @param timeElapsed the timeElapsed to set
	 */
	public void setTimeElapsed(int timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	/**
	 * @return the fuelUsed
	 */
	public int getFuelUsed() {
		return fuelUsed;
	}

	/**
	 * @param fuelUsed the fuelUsed to set
	 */
	public void setFuelUsed(int fuelUsed) {
		this.fuelUsed = fuelUsed;
	}

	/**
	 * @return the oilInStorage
	 */
	public int getOilInStorage() {
		return oilInStorage;
	}

	/**
	 * @param oilInStorage the oilInStorage to set
	 */
	public void setOilInStorage(int oilInStorage) {
		this.oilInStorage = oilInStorage;
	}
	
	/**
	 * @return the oilInMap
	 */
	public int getOilInMap() {
		return this.oilInMap;
	}

	/**
	 * @param oilInMap the oilInMap to set
	 */
	public void setOilInMap(int oilInMap) {
		this.oilInMap = oilInMap;
	}
	
	/**
	 * Checks if simulation is completed (all boats in IDLE state)
	 */
	private boolean checkIfDone() {
		for(ABoat b : ABmap.instance().boats) {
			if (b.state!=State.IDLE) return false;
		}
		if(oilInMap==0)return true;    //makes sure this does not return true at the very start of the sim when all boats are IDLE
		else return false;
	}
	
	/**
	 * @param isDone the isDone to set
	 */
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
	
	/**
	 * @return the isDone
	 */
	public boolean isDone() {
		return this.isDone;
	}
	

}
	
