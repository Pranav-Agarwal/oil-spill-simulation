package edu.neu.csye6200.absim;

import java.util.logging.Logger;

/**
 * Abstract parent class for a task that a boat executes
 */
public abstract class ABtask {
	
	private Logger log = Logger.getLogger(ABtask.class.getName());
	private static long ctr = 0;
	
	public long id;                       //task id
	public ABoat boat = null;            //boat that this task is assigned to
	
	ABtask(ABoat assignedBoat){
		id = ctr++;
		this.boat = assignedBoat;
		
	}
	
	/**
	 * Override this method to define behaviour when a task is executed
	 */
	public abstract boolean run();
}
