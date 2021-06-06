package edu.neu.csye6200.absim;

import java.util.HashMap;
import java.util.logging.Logger;

public class ACollectorBoat extends ABoat{
	
	private Logger log = Logger.getLogger(ACollectorBoat.class.getName());
	
	/* location of the shared Oil Storage */
	static int oilStorageX = 1;
	static int oilStorageY = 3;
	
	private String targetedDump = "";  //which dump is currently being targeted for collection by this boat

	public ACollectorBoat(String name,int maxFuel, int maxLoad) {
		super(name);
		this.maxFuel = maxFuel;
		this.maxLoad = maxLoad;
		fuel = maxFuel;
		load=0;
		log.info("Collector boat "+name+" with ID: "+id+" was created");
	}
	
	/**
	 * Called every simulation tick
	 */
	@Override
	public void update(){
		
		/* if boat does not have fuel empty task stack and ask for refuel */
		if(!hasFuel) {
			
			/* signifies refuelling has been completed */
			if(fuel==maxFuel) hasFuel=true;
			
			/*change the targeted flag of it's target dump station to 0 as the task has been cancelled*/
			if(targetedDump != "") ABmap.instance().dumpStations.get(targetedDump)[3]=0; 
			
			tasks.clear();
			state = State.OUT_OF_FUEL;
		}
		else {
			
			/* if boat does not have cargo space then empty task stack, move to dump station and unload
			 * NOTE - tasks are pushed in reverse order since it is a stack */
			if(!hasSpace) {
				tasks.clear();
				tasks.push(new ABunloadTask(this));
				tasks.push(new ABmoveTask(this,oilStorageX,oilStorageY));
				hasSpace = true;
			}
			
			/* if boat does not have any pending task in stack*/
			if(tasks.empty()) {
				
				/* find a Dump Station with oil, move to it and begin loading it's oil into cargo.
				 * If targetedDump is empty after findDumpStation, no suitable station was found
				 * and the boat defaults to going to the oil storage, unloading and waiting.*/
				int[] t = findTarget();
				if(targetedDump == "") {
					tasks.push(new ABunloadTask(this));
					tasks.push(new ABmoveTask(this,oilStorageX,oilStorageY));
					return;
				}
				ABmap.instance().dumpStations.get(targetedDump)[3]=1;
				tasks.push(new ABloadTask(this));
				tasks.push(new ABmoveTask(this,t[0],t[1]));
			}
			
			/* if boat does have a pending task in stack, call it's run method.
			 * if a task's run returns true, it is complete. Pop it from the stack in that case*/
			else {
				if(tasks.peek().run()) tasks.pop();
			}
		}
	}
	
	/**
	 * find a dump station to take oil from.
	 * the dump station that has the most oil that is currently
	 * not targeted by another collector is selected.
	 */
	protected int[] findTarget(){
		int[] ans = new int[2];
		targetedDump = "";
		int maxOilCount = 0;
		/* iterate through all dumpstations in the hashmap
		 * search heuristic is finding dump station with most oil */
		for (HashMap.Entry<String,int[]> entry : ABmap.instance().dumpStations.entrySet()) {
			int[] dumpStation = entry.getValue();
			if (dumpStation[2]>maxOilCount && dumpStation[3]!=1) {        //dumpstation[3] is whether it is already targeted. 0=No, 1=Yes
				maxOilCount = dumpStation[2];                             //dumpstation[2] is the current oil count in the dumpstation
				ans[0] = dumpStation[0];                                  //dumpstation[0] is the X location of the dump station
				ans[1] = dumpStation[1];                                  //dumpstation[1] is the Y location of the dump station
				targetedDump = entry.getKey();
			}
		}
		return ans;
	}
	
	/**
	 * Transfers oil from cargo to central storage
	 */
	@Override
	public boolean unload(){
		state = State.WORKING;
		int unloadPerTick = 5000;
		
		/*this is to avoid negative load if load left in boat is less than unloadPerTick*/
		int temp = Math.min(unloadPerTick, load);
		
		load-=temp;
		ABmap.instance().setOilInStorage(ABmap.instance().getOilInStorage()+temp);
		if(load==0) {
			state = State.IDLE;
			return true; //task completed
		}
		else return false; //task needs to be continued next update tick
	}
	
	/**
	 * Transfers oil from targeted dump station to cargo
	 */
	@Override
	public boolean load() {
		state = State.WORKING;
		int loadPerTick=5000;
		int[] k = ABmap.instance().dumpStations.get(targetedDump);
		
		/*this is to avoid negative load if load left in dump station is less than loadPerTick*/
		int t = Math.min(k[2],loadPerTick);
		
		k[2]-=t;
		load+=t;
		/*If next load tick will overflow storage or dump station is empty end task*/
		if (load+loadPerTick>maxLoad || k[2]==0) {
			k[3]=0;  //change the targeted flag of this dump station to 0
			if (load+loadPerTick>maxLoad)hasSpace = false;
			return true; //task completed
		}
		return false; //task needs to be continued next update tick
	}

}
