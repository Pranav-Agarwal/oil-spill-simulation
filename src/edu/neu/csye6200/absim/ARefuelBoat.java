package edu.neu.csye6200.absim;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ARefuelBoat extends ABoat{
	
	private Logger log = Logger.getLogger(ARefuelBoat.class.getName());
	
	/* location of the shared refuel station */
	static int refuelStationX = 1;
	static int refuelStationY = 5;
	
	/* shared list of all boats targeted for refuelling by a refuel boat 
	 * this is maintained to avoid 2 refuel boats trying to refuel the same boat*/
	static ArrayList<ABoat> targetedBoats = new ArrayList<ABoat>();
	
	ABoat targetBoat=null;  //boat that this boat has targeted for refuelling

	/* NOTE - this type of boat does not use the load variables
	 * as it does not have any cargo */
	public ARefuelBoat(String name,int maxFuel) {
		super(name);
		this.maxFuel = maxFuel;
		fuel = maxFuel;
		log.info("Refuel boat "+name+" with ID: "+id+" was created");
	}

	@Override
	/**
	 * Called every simulation tick
	 */
	void update() {
		/* if boat does not enough fuel return to the refuel station and refuel itself. 
		 * keeps 1000 fuel as emergency reserve to travel to the refuel station*/
		if(!hasFuel) { 
			
			/*remove the target boat from the shared list as the task has been cancelled
			 * this allows another refuel boat to target it instead*/
			if(targetedBoats.contains(targetBoat)) targetedBoats.remove(targetBoat);
			
			tasks.clear();
			tasks.push(new ABloadTask(this));
			tasks.push(new ABmoveTask(this,refuelStationX,refuelStationY));
			hasFuel=true;  //set this to true before refuelling has been completed to allow it to proceed with the refuelling task
		}
		else {
			/* if boat does not have any pending task in stack*/
			if(tasks.empty()) {
				/* find a boat waiting for refuel, move to it then refuel it.
				 *  If not found go to the refuel station,refuel itself and wait. */
				ABoat b = findTarget();
				if(b==null) {
					tasks.push(new ABloadTask(this));
					tasks.push(new ABmoveTask(this,refuelStationX,refuelStationY));
					return;
				}
				targetBoat = b;
				tasks.push(new ABunloadTask(this));
				tasks.push(new ABmoveTask(this,b.posX,b.posY));
			}
			/* if boat does have a pending task in stack, call it's run method.
			 * if a task's run() returns true, it is complete. Pop it from the stack in that case*/
			else {
				if(tasks.peek().run()) tasks.pop();
			}
		}
	}
	
	/**
	 * find the closest boat that is out of fuel and is not
	 * in the shared targeted list
	 */
	protected ABoat findTarget() {
		ABoat ans = null;
		/* search heuristic - find the closest boat that is out of fuel.*/
		int t=ABmap.instance().size*3;
		for(ABoat b : ABmap.instance().boats) {
			if(b.state == State.OUT_OF_FUEL && !targetedBoats.contains(b)){
				if(getDist(b.posX,b.posY,posX,posY)<t) {
					ans = b;
				}
			}
		}
		if(ans!=null) targetedBoats.add(ans);
		return ans;
	}
	
	/**
	 * Transfer fuel from refuel boat to target boat
	 */
	@Override
	protected boolean unload() {
		state = State.WORKING;
		int refuelRate = 300;
		
		/*this is to avoid fuel overflow if fuel left to fill is less than refuel rate
		 * also avoids fuel of the refuel boat dipping below 1000*/
		int t = Math.min(Math.min(refuelRate,(targetBoat.maxFuel-targetBoat.fuel)),fuel-1000);
		
		targetBoat.fuel+=t;
		fuel-=t;
		if(targetBoat.maxFuel == targetBoat.fuel || fuel<=1000) {
			targetedBoats.remove(targetBoat); //remove boat from shared targeted list
			if (fuel<=1000) hasFuel = false;
			return true;  //task completed
		}
		else return false; //task needs to be continued next update tick
	}

	/**
	 * Transfer fuel from refuel station to refuel boat
	 */
	@Override
	protected boolean load() {
		state = State.WORKING;
		int refuelRate = 1000;
		
		/*this is to avoid fuel overflow if fuel left to fill is less than refuel rate*/
		int t = Math.min(refuelRate,maxFuel-fuel);
		
		fuel+=t;
		ABmap.instance().setFuelUsed(ABmap.instance().getFuelUsed()+t);
		if(maxFuel == fuel) {
			state = State.IDLE;
			return true;
		}
		else return false;
	}

}
