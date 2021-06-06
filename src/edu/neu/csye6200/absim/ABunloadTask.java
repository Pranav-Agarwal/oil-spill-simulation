package edu.neu.csye6200.absim;

import java.util.logging.Logger;

/**
 * task that calls it's assigned boat's unload function
 */
public class ABunloadTask extends ABtask {
	
	private Logger log = Logger.getLogger(ABunloadTask.class.getName());

	ABunloadTask(ABoat assignedBoat) {
		super(assignedBoat);
	}

	/**
	 * calls the unload function and returns true if the task
	 * is completed.
	 */
	@Override
	public boolean run() {
		return boat.unload();
	}

}
