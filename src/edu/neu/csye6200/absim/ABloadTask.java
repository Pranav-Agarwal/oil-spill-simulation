package edu.neu.csye6200.absim;

import java.util.logging.Logger;

/**
 * task that calls it's assigned boat's load function
 */
public class ABloadTask extends ABtask{
	
	private Logger log = Logger.getLogger(ABloadTask.class.getName());

	ABloadTask(ABoat assignedBoat) {
		super(assignedBoat);
	}

	/**
	 * calls the load function and returns true if the task
	 * is completed.
	 */
	@Override
	public boolean run() {
		return boat.load();
	}
	
}
