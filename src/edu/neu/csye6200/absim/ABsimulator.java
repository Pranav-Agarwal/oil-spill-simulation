package edu.neu.csye6200.absim;

import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Simulation object that is executed periodically
 * Runs on it's own thread seperate from UI.
 */
public class ABsimulator extends TimerTask  {
	
	private static Logger log = Logger.getLogger(ABsimulator.class.getName());
	private ABcontroller controller;        //reference to controller for UI components
	private long ctr=0;                     //actual ticks occured since simulation start
	private Boolean isPlaying = false;      //false if app is paused
	private int runSpeed;                   //changes run speed of simulation
	
	public ABsimulator(ABcontroller application) {
		this.controller = application;
		runSpeed=3;   //default
	}
	
	/**
	 * the task to be run periodically
	 */
	@Override
	public void run() {
		if(isPlaying) {                                   //check if paused
			if(ABmap.instance().isDone()) {               //check if completed
				log.info("Simulation completed after " +ctr+" timer ticks");
				log.info("Final statistics - Time Taken: "+ABmap.instance().getTimeElapsed()+", Oil Cleaned: "+ABmap.instance().getOilInStorage()+", Fuel Used: "+ABmap.instance().getFuelUsed());
				cancel();                                 //stop timerTask
			}
			ctr++;
			if(ctr%runSpeed==0) {                         //this simulates run speed. a simulation tick will occur every 'runSpeed' actual ticks
				ABmap.instance().updateMap();             //updates the map to it's new state after a simulation tick
				controller.refreshMapInformation();      //refresh the UI
				controller.refreshBoatInformation();     //refresh the UI
			}
		}
	}
	
	/**
	 * Inverts the isPlaying boolean every time it is called
	 */
	public void flipPlaying() {
		isPlaying = !isPlaying;
		log.info("Simulation was played/paused");
	}
	
	/**
	 * Changes the run speed of the app. Input 's' can range from 1 to 4
	 */
	public void setRunSpeed(int s) {
		runSpeed = 5-s;
		log.info("Simulation run speed was changed to "+s);
	}

}
