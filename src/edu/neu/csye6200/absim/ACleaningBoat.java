package edu.neu.csye6200.absim;

import java.util.logging.Logger;

/**
 * Autonomous boat that seeks out and collects oil from the map
 */
public class ACleaningBoat extends ABoat{
	
	private static Logger log = Logger.getLogger(ACleaningBoat.class.getName());
	private int homeX;        //X location of this boat's dumping station
	private int homeY;        //Y location of this boat's dumping station
	
	public ACleaningBoat(String name,int homeX,int homeY,int maxFuel,int maxLoad){
		super(name);
		this.homeX = homeX;
		this.homeY = homeY;
		this.maxFuel = maxFuel;
		this.maxLoad = maxLoad;
		fuel = maxFuel;
		load=0;
		ABmap.instance().dumpStations.put(id,new int[] {homeX,homeY,0,0});  //add this boat's dumping station to ABmap's HashMap
		tasks.push(new ABmoveTask(this,homeX,homeY));      //push the initial task to the boat - move to it's dumping station
		log.info("Cleaning boat "+name+" with ID: "+id+" was created");
	}
	
	/**
	 * Called every simulation tick
	 */
	@Override
	public void update() {
		/* if boat does not have fuel empty task stack and ask for refuel */
		if(!hasFuel) {
			
			/* signifies refuelling has been completed */
			if(fuel==maxFuel) hasFuel=true;
			
			tasks.clear();
			state = State.OUT_OF_FUEL;
		}
		else {
			/* if boat does not have cargo space then empty task stack, move to dump station and unload
			 * NOTE - tasks are pushed in reverse order since it is a stack */
			if(!hasSpace) {
				tasks.clear();
				tasks.push(new ABunloadTask(this));
				tasks.push(new ABmoveTask(this,homeX,homeY));
				hasSpace = true;
			}
			/* if boat does not have any pending task in stack*/
			if(tasks.empty()) {
				/* find a cell with oil, move to it and begin a cleaning operation.
				 * If returned value of findOil is [0,0], this means no cell was found
				 * and the boat defaults to going to it's dump station, unloading and waiting.*/
				int[] t = findTarget();                  
				if(t[0] == 0 && t[1] == 0) {                                       
					tasks.push(new ABunloadTask(this));
					tasks.push(new ABmoveTask(this,homeX,homeY));
				}
				else {
					tasks.push(new ABloadTask(this));
					tasks.push(new ABmoveTask(this,t[0],t[1]));
				}
			}
			/* if boat does have a pending task in stack, call it's run method.
			 * if a task's run() returns true, it is complete. Pop it from the stack in that case*/
			else {
				if(tasks.peek().run()) tasks.pop();
			}
		}
	}
	
	/**
	 * Select an oil cell to clean
	 */
	protected int[] findTarget() {
		double dist = 99999999;
		double t;
		double m1 = 1;
		double m2 = 1;
		int[] ans = new int[2];
		for (int row=1;row<ABmap.instance().size-1;row++) {
			for(int col=1;col<ABmap.instance().size-1;col++) {
				/* search heuristic for selecting an oil cell is
				 * t = (distance from boat to cell) - (distance from center to cell)*m1 + (distance from boat's dumping station to cell)*m2
				 * the cell with least t is selected. */
				t = getDist(row,col,posX,posY) - (getDist(row,col,ABmap.instance().size/2,ABmap.instance().size/2))*m1 + getDist(row,col,homeX,homeY)*m2;
				if(ABmap.instance().cells[row][col].getBoats().size()==0 && ABmap.instance().cells[row][col].getOilCount()>0 && t<dist) {
					dist=t;
					ans[0]=row;
					ans[1]=col;
				}
			}
		}
		return ans;
	}
	
	/**
	 * transfers oil from all 8 cells surrounding the boat to cargo
	 */
	@Override
	public boolean load() {
		state = State.WORKING;
		ABcell[] neighbours = new ABcell[9];
		neighbours[0] = ABmap.instance().cells[posX][posY];
		/* check for and skip appropriate cells if at edges of map*/
		if(posX>0 && posY>0) neighbours[1] = ABmap.instance().cells[posX-1][posY-1];
		else neighbours[1]=null;
		if(posX>0) neighbours[2] = ABmap.instance().cells[posX-1][posY];
		else neighbours[2]=null;
		if(posX>0 && posY<ABmap.instance().size-1) neighbours[3] = ABmap.instance().cells[posX-1][posY+1];
		else neighbours[3]=null;
		if(posY<ABmap.instance().size-1) neighbours[4] = ABmap.instance().cells[posX][posY+1];
		else neighbours[4]=null;
		if(posX<ABmap.instance().size-1 && posY<ABmap.instance().size-1) neighbours[5] = ABmap.instance().cells[posX+1][posY+1];
		else neighbours[5]=null;
		if(posX<ABmap.instance().size-1) neighbours[6] = ABmap.instance().cells[posX+1][posY];
		else neighbours[6]=null;
		if(posX<ABmap.instance().size-1 && posY>0) neighbours[7] = ABmap.instance().cells[posX+1][posY-1];
		else neighbours[7]=null;
		if(posY>0) neighbours[8] = ABmap.instance().cells[posX][posY-1];
		else neighbours[8]=null;
		
		/* remove oil from neighbor and add it to the boat. Uses up 30 fuel per cell cleaned*/
		for(ABcell neighbour : neighbours) {
			if (!(neighbour==null)&& neighbour.getOilCount()>0) {
				
				/*this is to avoid overflow if oil in neighbour is greater than remaining space*/
				int temp = Math.min(neighbour.getOilCount(), maxLoad-load);
				load+=temp;
				neighbour.setOilCount(neighbour.getOilCount()-temp);
				fuel-=30;
				
				/* check if fuel has run out or cargo capacity has been reached*/
				if(fuel<30) {
					hasFuel=false;
					break;
				}
				if(load>=maxLoad) {
					hasSpace=false;
					break;
				}
			}
		}
		return true;
	}
	
	/**
	 * Transfers oil from cargo to it's dump station
	 */
	@Override
	public boolean unload() {
		state = State.WORKING;
		int unloadPerTick = 1000;
		
		/*this is to avoid negative load if load left in boat is less than unloadPerTick*/
		int temp = Math.min(unloadPerTick, load);
		
		load-=temp;
		ABmap.instance().dumpStations.get(id)[2]+=temp;  //dumpstation[2] is that dumpStation's current oil count
		if(load==0) {
			state = State.IDLE;
			return true;  //task completed
		}
		else return false; //task needs to be continued next update tick
	}
	
}
