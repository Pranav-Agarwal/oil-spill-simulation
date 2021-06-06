package edu.neu.csye6200.absim;

import java.util.Stack;
import java.util.logging.Logger;

/**
 * Abstract parent autonomous boat class
 */
public abstract class ABoat {
	
	private static Logger log = Logger.getLogger(ABoat.class.getName());
	public static enum State {IDLE,MOVING,WORKING,OUT_OF_FUEL}; //enumerator of possible boat states
	static int ctr=0;                    //counter for boat ID
	public String id;                    //boat ID
	public int load;                     //current boat cargo load
	public int maxLoad;                  //maximum cargo capacity of boat
	public String name;                  //boat name
	public int fuel;                     //current boat fuel
	public int maxFuel;                  //maximum fuel capacity of boat
	public Stack<ABtask> tasks;          //stack of tasks pending to be done by this boat
	public int posX;                     //X coordinate of this boat
	public int posY;                     //Y coordinate of this boat
	public boolean hasFuel = true;       //true if boat needs refuelling
	public boolean hasSpace = true;      //true if boat need unloading
	public State state;                  //current state of the Boat
	private boolean isSelected = true;   //true if the Boat's information is currently being shown in the UI
	
	public ABoat(String name) {
		this.id = Integer.toString(ctr++);
		this.name = name;
		tasks = new Stack<ABtask>();
		this.posX = 1;
		this.posY = 1;
		state = State.IDLE;
		ABmap.instance().cells[posX][posY].addBoat(this);
	}
	
	/**
	 * Utility function to find the manhattan distance between 2 points on the grid
	 */
	protected int getDist(int x1,int y1, int x2, int y2) {
		return Math.abs(x1-x2)+Math.abs(y1-y2);
	}
	
	/**
	 * move this boat to the passed in location
	 * and tell the cells in the old location and new location
	 * to add/remove it from their boat list respectively.
	 * Also deduct 10 fuel
	 */
	public void move(int posX,int posY) {
		state = State.MOVING;
		ABmap.instance().cells[posX][posY].addBoat(this);
		ABmap.instance().cells[this.posX][this.posY].removeBoat(this);
		this.posX = posX;
		this.posY = posY;
		fuel-=10;
		if (fuel<10) hasFuel = false;
	}
	
	
	/**
	 * return the boat's selection status
	 */
	public boolean isSelected() {
		return this.isSelected;
	}
	
	/**
	 * sets this boat's isSelected to true and all other boats
	 * to false to ensure only one boat is selected at a time.
	 * The cells these boats are also refreshed since the background 
	 * color of a cell depends on whether it has a selected boat.
	 */
	public void select() {
		for(ABoat b: ABmap.instance().boats) {
			if (b.isSelected) {
				b.isSelected = false;
				ABmap.instance().cells[b.posX][b.posY].refresh();
			}
		}
		this.isSelected = true;
		ABmap.instance().cells[posX][posY].refresh();
	}
	
	/**
	 * Override this to provide a method for the boat that will be called every simulation tick
	 */
	abstract void update();

	/**
	 * Override this to provide an implementation for unloading the boat
	 */
	protected abstract boolean unload();
	
	/**
	 * Override this to provide an implementation for loading the boat
	 */
	protected abstract boolean load();
	
	/**
	 * Override this to provide an implementation for finding a destination to
	 * move to and work upon by using a search heuristic
	 */
	protected abstract Object findTarget();
	
}
