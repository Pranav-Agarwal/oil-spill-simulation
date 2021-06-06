package edu.neu.csye6200.absim;

import java.util.Timer;
import java.util.logging.Logger;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;

/**
 * Class that handles the simulation's interactions with the UI
 */
public class ABcontroller extends ABApp{
	
	private static Logger log = Logger.getLogger(ABcontroller.class.getName());       
	private ABsimulator sim;        //Simulation Object that is a child of TimerTask
	private Timer timer;            //Timer object that schedules the simulation
	
    /**
     * Constructor called when default map size is to be used
     */
	ABcontroller(){
		super(50);
		timer = new Timer();
	}
	
    /**
     * Constructor called when custom map size is to be used
     */
	ABcontroller(int mapSize){
		super(mapSize);
		timer = new Timer();
	}
	
	@Override
	public void showHelp() {
		helpFrame.setVisible(true);
	}
	
    /**
     * Refresh the UI Panel showing the map information
     */
	public void refreshMapInformation() {
		((JLabel)components.get("timeElapsedLabel")).setText(Integer.toString(ABmap.instance().getTimeElapsed()));
		((JLabel)components.get("oilInMapLabel")).setText(Integer.toString(ABmap.instance().getOilInMap()));
		((JLabel)components.get("oilInStorageLabel")).setText(Integer.toString(ABmap.instance().getOilInStorage()));
		((JProgressBar)components.get("oilCleanedProgressBar")).setValue(100-((ABmap.instance().getOilInMap()*100)/ABmap.instance().initialOilCount));
		((JLabel)components.get("fuelUsedLabel")).setText(Integer.toString(ABmap.instance().getFuelUsed()));
		((JLabel)components.get("simCompletedLabel")).setText(Boolean.toString(ABmap.instance().isDone()));
	}
	
    /**
     * Refresh the UI Panel showing the selected Boat's information
     */
	@SuppressWarnings("unchecked")
	public void refreshBoatInformation() {
		ABoat selected=null;
		for(ABoat b : ABmap.instance().boats) {
			if(((JComboBox<String>)components.get("nameComboBox")).getSelectedItem()==b.name) {
				b.select();
				selected=b;
				break;
			}
		}
		((JLabel)components.get("boatPositionLabel")).setText("["+selected.posX+","+selected.posY+"]");
		((JLabel)components.get("boatStateLabel")).setText(selected.state.name());
		int f,mf,l,ml;
		f = selected.fuel;
		mf = selected.maxFuel;
		if(selected instanceof ACleaningBoat) {
			((JLabel)components.get("boatTypeLabel")).setText("Cleaning Boat");
			l = selected.load;
			ml = ((ACleaningBoat) selected).maxLoad;
			((JLabel)components.get("boatLoadLabel")).setText(l+"/"+ml);
			((JProgressBar)components.get("boatLoadProgressBar")).setValue((l*100)/ml);
		}
		else if(selected instanceof ACollectorBoat) {
			((JLabel)components.get("boatTypeLabel")).setText("Collector Boat");
			l = selected.load;
			ml = ((ACollectorBoat) selected).maxLoad;
			((JLabel)components.get("boatLoadLabel")).setText(l+"/"+ml);
			((JProgressBar)components.get("boatLoadProgressBar")).setValue((l*100)/ml);
		}
		else{
			((JLabel)components.get("boatTypeLabel")).setText("Refuel Boat");
			((JLabel)components.get("boatLoadLabel")).setText("NA");
			((JProgressBar)components.get("boatLoadProgressBar")).setValue(0);
		}
		((JLabel)components.get("boatFuelLabel")).setText(f+"/"+mf);
		((JProgressBar)components.get("boatFuelProgressBar")).setValue((f*100)/mf);
	}
	
	/**
     * Initializes the simulation
     */
	private void initialize() {
		setInitializationParams();
		switchFrontEnd();
	}
	
    /**
     * Obtain the initialization params from the UI Panel
     */
	private void setInitializationParams() {
		int spreadRate = 130-((JSlider)components.get("spreadSlider")).getValue();
		int intensity = ((JSlider)components.get("intensitySlider")).getValue();
		int[] boatParams = new int[8];
		boatParams[0] = (int) ((JSpinner)components.get("cleanerSpinner")).getValue();
		boatParams[1] = (int) ((JSpinner)components.get("cleanerFuelSpinner")).getValue();
		boatParams[2] = (int) ((JSpinner)components.get("cleanerLoadSpinner")).getValue();
		boatParams[3] = (int) ((JSpinner)components.get("collectorSpinner")).getValue();
		boatParams[4] = (int) ((JSpinner)components.get("collectorFuelSpinner")).getValue();
		boatParams[5] = (int) ((JSpinner)components.get("collectorLoadSpinner")).getValue();
		boatParams[6] = (int) ((JSpinner)components.get("refuelSpinner")).getValue();
		boatParams[7] = (int) ((JSpinner)components.get("refuelFuelSpinner")).getValue();
		log.info("Simulation initializing with spread rate "+ spreadRate + " and intensity " + intensity);
		simulateInitialConditions(spreadRate,boatParams,3,intensity);
	}
	
	/**
     * add landmasses to the map, create initial oil spill and add boats
     */
	private void simulateInitialConditions(int spreadRate,int[] boatParams,int oilSpillCount, int oilSpillIntensity) {
		int center = ABmap.instance().size/2;
		ABmap.instance().addLand();
		ABmap.instance().createOilSpill(oilSpillCount, oilSpillIntensity, center, center);
		ABmap.instance().spreadRate = spreadRate;
		
		for(int i=0;i<50;i++) {
			ABmap.instance().spreadOil(200);
		}
		
		int[][] positions = {{1,1},{ABmap.instance().size-2,ABmap.instance().size-2},{ABmap.instance().size-2,1},{1,ABmap.instance().size-2},{1,center},{ABmap.instance().size-2,center},{center,1},{center,ABmap.instance().size-2}};
		
		for (int i=0;i<boatParams[3];i++) {
			ABmap.instance().boats.add(new ACollectorBoat("Collector "+(char)(65+i),boatParams[4],boatParams[5]));
		}
		for (int i=0;i<boatParams[0];i++) {
			ABmap.instance().boats.add(new ACleaningBoat("Cleaner "+(char)(65+i),positions[i][0],positions[i][1],boatParams[1],boatParams[2]));
		}
		for (int i=0;i<boatParams[6];i++) {
			ABmap.instance().boats.add(new ARefuelBoat("Refueller "+(char)(65+i),boatParams[7]));
		}
		log.info("Initialization complete");
	}
	
	/**
     * Changes the Front End UI from the initialization panel to the simulation view
     */
	@SuppressWarnings("unchecked")
	private void switchFrontEnd() {
		for(ABoat b : ABmap.instance().boats) {
			((JComboBox<String>)components.get("nameComboBox")).addItem(b.name);
		}
		((JPanel)components.get("initPanel")).setVisible(false);
		((JPanel)components.get("controlPanel")).setBounds(931, 11, 253, 185);
		((JPanel)components.get("controlPanel")).setVisible(true);
		((JPanel)components.get("generalInfoPanel")).setBounds(931, 230, 253, 208);
		((JPanel)components.get("generalInfoPanel")).setVisible(true);
		((JPanel)components.get("boatInfoPanel")).setBounds(931, 450, 253, 252);
		((JPanel)components.get("boatInfoPanel")).setVisible(true);
	}
	
    /**
     * Begin the simulation 
     */
	private void startSimulation() {
		log.info("Simulation started");
		sim = new ABsimulator(this);
		timer.schedule(sim, 100, 33);
	}

    /**
     * Pause/Play the simulation
     */
	private void playSimulation() {
		sim.flipPlaying();
	}
	
    /**
     * gets the speed setting from the slider and updates the simulation speed
     */
	private void changeSpeed() {
		int t = ((JSlider)components.get("speedSlider")).getValue();
		sim.setRunSpeed(t);
	}


    /**
     * Performs the respective method call based on the ActionEvent generated from the frontend buttons
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equalsIgnoreCase("Initialize")) {
			log.info("initialize button pressed");
			initialize();		
		}
		else if (e.getActionCommand().equalsIgnoreCase("exit")) {
			log.info("exit button pressed");
			exit();
		}
		else if (e.getActionCommand().equalsIgnoreCase("help")) {
			log.info("help button pressed");
			showHelp();	
		}
		else if( e.getActionCommand().equalsIgnoreCase("play")){
			log.info("play/pause button pressed");
			if (sim==null) startSimulation();                                                             //Create new simulation if doesn't exist
			if(((JButton) e.getSource()).getText()=="Play")  ((JButton) e.getSource()).setText("Pause");  //Flips the text on the button to represent it's new function
			else ((JButton) e.getSource()).setText("Play");
		    playSimulation();  
		}
		else if( e.getActionCommand().equalsIgnoreCase("changeSpeed")){
			log.info("change speed button pressed");
			if (sim!=null) changeSpeed();
		}
		
	}
}
