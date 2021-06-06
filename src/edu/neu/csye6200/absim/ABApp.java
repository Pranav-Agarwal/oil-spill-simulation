package edu.neu.csye6200.absim;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Superclass of the application that contains all the UI components
 */
public abstract class ABApp implements ActionListener {
	
	private static Logger log = Logger.getLogger(ABApp.class.getName()); 
	protected JFrame frame;                          //top level container of the app
	protected JFrame helpFrame;                      //container showing the help information
	protected HashMap<String,Component> components;  //HashMap of components that provides an easy interface to take input from them
	
	public ABApp(int mapSize) {
		
		try { 
			Handler handler = new FileHandler("Simulation_Log.log");      //add a file handler to the logger
			Logger.getLogger("").addHandler(handler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("Application is starting with map size " + mapSize);
		components = new HashMap<String,Component>();
		ABmap.init(mapSize);
		initGUI();
		showUI();
	}
 
    /**
     * A convenience method that uses the Swing dispatch threat to show the UI.
     * This prevents concurrency problems during component initialization.
     */
    public void showUI() {
    	
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	frame.setVisible(true); // The UI is built, so display it;
            }
        });
    	
    }
    
    /**
     * Shut down the application
     */
    public void exit() {
    	frame.dispose();
    	helpFrame.dispose();
    	System.exit(0);
    }

    /**
     * Override this method to show an About Dialog
     */
    public abstract void showHelp();
    
	/**
	 * Initialize the Graphical User Interface.
	 * Additionally, relevant components are also added to the HashMap here.
	 */
    public void initGUI() {
    	
		/* Initialize the Main Application Frame */
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("Oil Spill Simulation");
		frame.setSize(new Dimension(1212, 953));
		frame.getContentPane().setLayout(null);

		frame.getContentPane().add(ABmap.instance());
		ABmap.instance().setBounds(10, 11, 900, 900);
		ABmap.instance().setLayout(new GridLayout(ABmap.instance().size,ABmap.instance().size));
		
		JPanel initPanel = new JPanel();
		initPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		initPanel.setBounds(931, 11, 253, 443);
		frame.getContentPane().add(initPanel);
		initPanel.setLayout(null);
		components.put("initPanel", initPanel);
		
		JLabel lblNewLabel = new JLabel("Initialization");
		lblNewLabel.setFont(new Font("Calibri", Font.BOLD, 14));
		lblNewLabel.setBounds(77, 21, 131, 25);
		initPanel.add(lblNewLabel);
				
		JLabel lblNewLabel_1_1 = new JLabel("Spill Spread Rate");
		lblNewLabel_1_1.setBounds(77, 57, 130, 14);
		initPanel.add(lblNewLabel_1_1);
		
		JSlider spreadSlider = new JSlider();
		spreadSlider.setValue(80);
		spreadSlider.setMinorTickSpacing(10);
		spreadSlider.setPaintTicks(true);
		spreadSlider.setSnapToTicks(true);
		spreadSlider.setMajorTickSpacing(10);
		spreadSlider.setMinimum(30);
		spreadSlider.setBounds(51, 75, 142, 14);
		initPanel.add(spreadSlider);
		components.put("spreadSlider", spreadSlider);
		
		JLabel lblNewLabel_1 = new JLabel("Spill Intensity");
		lblNewLabel_1.setBounds(77, 100, 96, 14);
		initPanel.add(lblNewLabel_1);
		
		JSlider intensitySlider = new JSlider();
		intensitySlider.setPaintTicks(true);
		intensitySlider.setSnapToTicks(true);
		intensitySlider.setValue(300);
		intensitySlider.setMinorTickSpacing(50);
		intensitySlider.setMajorTickSpacing(50);
		intensitySlider.setMinimum(200);
		intensitySlider.setMaximum(800);
		intensitySlider.setBounds(51, 120, 142, 14);
		initPanel.add(intensitySlider);
		components.put("intensitySlider", intensitySlider);
		
		JLabel lblNewLabel_1_1_2 = new JLabel("Cleaning Boats");
		lblNewLabel_1_1_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1_1_2.setBounds(41, 148, 95, 14);
		initPanel.add(lblNewLabel_1_1_2);
		
		JLabel lblNewLabel_3_2 = new JLabel("Count");
		lblNewLabel_3_2.setBounds(146, 148, 46, 14);
		initPanel.add(lblNewLabel_3_2);
		
		JSpinner cleanerSpinner = new JSpinner();
		cleanerSpinner.setModel(new SpinnerNumberModel(3, 1, 8, 1));
		cleanerSpinner.setBounds(190, 145, 40, 20);
		initPanel.add(cleanerSpinner);
		components.put("cleanerSpinner", cleanerSpinner);
		
		JLabel lblNewLabel_3 = new JLabel("Max Fuel");
		lblNewLabel_3.setBounds(37, 176, 70, 14);
		initPanel.add(lblNewLabel_3);
		
		JSpinner cleanerFuelSpinner = new JSpinner();
		cleanerFuelSpinner.setModel(new SpinnerNumberModel(5000, 1000, 20000, 1000));
		cleanerFuelSpinner.setBounds(103, 173, 80, 20);
		initPanel.add(cleanerFuelSpinner);
		components.put("cleanerFuelSpinner", cleanerFuelSpinner);
		
		JLabel lblNewLabel_3_1 = new JLabel("Max Load");
		lblNewLabel_3_1.setBounds(37, 207, 70, 14);
		initPanel.add(lblNewLabel_3_1);
		
		JSpinner cleanerLoadSpinner = new JSpinner();
		cleanerLoadSpinner.setModel(new SpinnerNumberModel(3000, 1000, 10000, 1000));
		cleanerLoadSpinner.setBounds(103, 204, 80, 20);
		initPanel.add(cleanerLoadSpinner);
		components.put("cleanerLoadSpinner", cleanerLoadSpinner);
		
		JLabel lblNewLabel_1_1_3 = new JLabel("Collector Boats");
		lblNewLabel_1_1_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1_1_3.setBounds(37, 242, 99, 14);
		initPanel.add(lblNewLabel_1_1_3);
		
		JLabel lblNewLabel_3_2_1 = new JLabel("Count");
		lblNewLabel_3_2_1.setBounds(146, 242, 46, 14);
		initPanel.add(lblNewLabel_3_2_1);
		
		JSpinner collectorSpinner = new JSpinner();
		collectorSpinner.setModel(new SpinnerNumberModel(1, 1, 3, 1));
		collectorSpinner.setBounds(190, 239, 40, 20);
		initPanel.add(collectorSpinner);
		components.put("collectorSpinner", collectorSpinner);
		
		JLabel lblNewLabel_3_3 = new JLabel("Max Fuel");
		lblNewLabel_3_3.setBounds(37, 270, 70, 14);
		initPanel.add(lblNewLabel_3_3);
		
		JSpinner collectorFuelSpinner = new JSpinner();
		collectorFuelSpinner.setModel(new SpinnerNumberModel(5000, 1000, 10000, 1000));
		collectorFuelSpinner.setBounds(103, 267, 80, 20);
		initPanel.add(collectorFuelSpinner);
		components.put("collectorFuelSpinner", collectorFuelSpinner);
		
		JLabel lblNewLabel_3_1_1 = new JLabel("Max Load");
		lblNewLabel_3_1_1.setBounds(37, 301, 70, 14);
		initPanel.add(lblNewLabel_3_1_1);
		
		JSpinner collectorLoadSpinner = new JSpinner();
		collectorLoadSpinner.setModel(new SpinnerNumberModel(50000, 10000, 100000, 10000));
		collectorLoadSpinner.setBounds(103, 298, 80, 20);
		initPanel.add(collectorLoadSpinner);
		components.put("collectorLoadSpinner", collectorLoadSpinner);
		
		JLabel lblNewLabel_1_1_4 = new JLabel("Refuel Boats");
		lblNewLabel_1_1_4.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1_1_4.setBounds(41, 329, 82, 14);
		initPanel.add(lblNewLabel_1_1_4);
		
		JLabel lblNewLabel_3_2_1_1 = new JLabel("Count");
		lblNewLabel_3_2_1_1.setBounds(146, 323, 46, 14);
		initPanel.add(lblNewLabel_3_2_1_1);
		
		JSpinner refuelSpinner = new JSpinner();
		refuelSpinner.setModel(new SpinnerNumberModel(2, 1, 3, 1));
		refuelSpinner.setBounds(190, 323, 40, 20);
		initPanel.add(refuelSpinner);
		components.put("refuelSpinner", refuelSpinner);
		
		JLabel lblNewLabel_3_3_1 = new JLabel("Max Fuel");
		lblNewLabel_3_3_1.setBounds(41, 357, 70, 14);
		initPanel.add(lblNewLabel_3_3_1);
		
		JSpinner refuelFuelSpinner = new JSpinner();
		refuelFuelSpinner.setModel(new SpinnerNumberModel(15000, 10000, 50000, 5000));
		refuelFuelSpinner.setBounds(103, 354, 80, 20);
		initPanel.add(refuelFuelSpinner);
		components.put("refuelFuelSpinner", refuelFuelSpinner);
		
		JButton Init = new JButton("Initialize");
		Init.addActionListener(this);
		Init.setBounds(84, 396, 89, 23);
		initPanel.add(Init);
		
		JPanel controlPanel = new JPanel();
		controlPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		controlPanel.setVisible(false);
		controlPanel.setBounds(930, 465, 254, 185);
		frame.getContentPane().add(controlPanel);
		controlPanel.setLayout(null);
		components.put("controlPanel", controlPanel);
		
		JLabel lblControl = new JLabel("Control");
		lblControl.setFont(new Font("Calibri", Font.BOLD, 14));
		lblControl.setBounds(99, 11, 131, 25);
		controlPanel.add(lblControl);
		
		JButton Start = new JButton("Play");
		Start.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 12));
		Start.setBounds(73, 47, 100, 23);
		Start.setActionCommand("play");
		controlPanel.add(Start);
		Start.addActionListener(this);
		
		JSlider speedSlider = new JSlider();
		speedSlider.setValue(2);
		components.put("speedSlider", speedSlider);
		speedSlider.setPaintTicks(true);
		speedSlider.setSnapToTicks(true);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setMinimum(1);
		speedSlider.setMaximum(4);
		speedSlider.setBounds(56, 93, 134, 26);
		controlPanel.add(speedSlider);
		
		JButton speedBtn = new JButton("Change Speed");
		speedBtn.addActionListener(this);
		speedBtn.setBounds(51, 130, 150, 23);
		controlPanel.add(speedBtn);
		speedBtn.setActionCommand("changeSpeed");
		
		
		JPanel generalInfoPanel = new JPanel();
		generalInfoPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		generalInfoPanel.setVisible(false);
		generalInfoPanel.setBounds(215, 478, 253, 208);
		frame.getContentPane().add(generalInfoPanel);
		generalInfoPanel.setLayout(null);
		components.put("generalInfoPanel", generalInfoPanel);
		
		JLabel lblMapInformation = new JLabel("Map Information");
		lblMapInformation.setFont(new Font("Calibri", Font.BOLD, 14));
		lblMapInformation.setBounds(70, 11, 131, 25);
		generalInfoPanel.add(lblMapInformation);
		
		JLabel lblNewLabel_2 = new JLabel("Time Elapsed :");
		lblNewLabel_2.setBounds(42, 47, 82, 14);
		generalInfoPanel.add(lblNewLabel_2);
		
		JLabel timeElapsedLabel = new JLabel("0");
		timeElapsedLabel.setBounds(153, 47, 46, 14);
		generalInfoPanel.add(timeElapsedLabel);
		components.put("timeElapsedLabel", timeElapsedLabel);
		
		JLabel lblNewLabel_2_2 = new JLabel("Oil in Map :");
		lblNewLabel_2_2.setBounds(42, 72, 82, 14);
		generalInfoPanel.add(lblNewLabel_2_2);
		
		JLabel oilInMapLabel = new JLabel("0");
		oilInMapLabel.setBounds(153, 72, 46, 14);
		generalInfoPanel.add(oilInMapLabel);
		components.put("oilInMapLabel", oilInMapLabel);
		
		JLabel lblNewLabel_2_3 = new JLabel("Oil In Storage :");
		lblNewLabel_2_3.setBounds(42, 97, 82, 14);
		generalInfoPanel.add(lblNewLabel_2_3);
		
		JLabel oilInStorageLabel = new JLabel("0");
		oilInStorageLabel.setBounds(153, 97, 46, 14);
		generalInfoPanel.add(oilInStorageLabel);
		components.put("oilInStorageLabel", oilInStorageLabel);
		
		JLabel lblNewLabel_2_4 = new JLabel("Fuel Used :");
		lblNewLabel_2_4.setBounds(42, 147, 82, 14);
		generalInfoPanel.add(lblNewLabel_2_4);
		
		JLabel fuelUsedLabel = new JLabel("0");
		fuelUsedLabel.setBounds(153, 147, 46, 14);
		generalInfoPanel.add(fuelUsedLabel);
		components.put("fuelUsedLabel", fuelUsedLabel);
		
		JLabel lblNewLabel_2_4_1 = new JLabel("Sim Completed:");
		lblNewLabel_2_4_1.setBounds(42, 172, 102, 14);
		generalInfoPanel.add(lblNewLabel_2_4_1);
		
		JLabel simCompletedLabel = new JLabel("false");
		simCompletedLabel.setBounds(153, 172, 46, 14);
		generalInfoPanel.add(simCompletedLabel);
		components.put("simCompletedLabel", simCompletedLabel);
		
		JLabel lblNewLabel_2_3_2 = new JLabel("Oil Cleaned :");
		lblNewLabel_2_3_2.setBounds(42, 122, 82, 14);
		generalInfoPanel.add(lblNewLabel_2_3_2);
		
		JProgressBar oilCleanedProgressBar = new JProgressBar();
		oilCleanedProgressBar.setForeground(Color.GREEN);
		oilCleanedProgressBar.setBackground(Color.WHITE);
		oilCleanedProgressBar.setBounds(134, 122, 89, 14);
		generalInfoPanel.add(oilCleanedProgressBar);
		components.put("oilCleanedProgressBar", oilCleanedProgressBar);
		
		JPanel boatInfoPanel = new JPanel();
		boatInfoPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		boatInfoPanel.setVisible(false);
		boatInfoPanel.setBounds(521, 526, 253, 141);
		frame.getContentPane().add(boatInfoPanel);
		boatInfoPanel.setLayout(null);
		components.put("boatInfoPanel", boatInfoPanel);
		
		JLabel lblBoatInformation = new JLabel("Boat Information");
		lblBoatInformation.setFont(new Font("Calibri", Font.BOLD, 14));
		lblBoatInformation.setBounds(79, 11, 131, 25);
		boatInfoPanel.add(lblBoatInformation);
		
		JLabel lblNewLabel_2_1 = new JLabel("Name :");
		lblNewLabel_2_1.setBounds(51, 47, 82, 14);
		boatInfoPanel.add(lblNewLabel_2_1);
		
		JComboBox<String> nameComboBox = new JComboBox<String>();
		nameComboBox.setBounds(126, 43, 105, 22);
		boatInfoPanel.add(nameComboBox);
		components.put("nameComboBox", nameComboBox);
		
		JLabel lblNewLabel_2_2_1 = new JLabel("Type :");
		lblNewLabel_2_2_1.setBounds(51, 72, 82, 14);
		boatInfoPanel.add(lblNewLabel_2_2_1);
		
		JLabel boatTypeLabel = new JLabel("");
		boatTypeLabel.setBounds(126, 72, 105, 14);
		boatInfoPanel.add(boatTypeLabel);
		components.put("boatTypeLabel", boatTypeLabel);
		
		JLabel lblNewLabel_2_3_1 = new JLabel("Position :");
		lblNewLabel_2_3_1.setBounds(51, 97, 82, 14);
		boatInfoPanel.add(lblNewLabel_2_3_1);
		
		JLabel boatPositionLabel = new JLabel("");
		boatPositionLabel.setBounds(126, 97, 46, 14);
		boatInfoPanel.add(boatPositionLabel);
		components.put("boatPositionLabel", boatPositionLabel);
		
		JLabel lblNewLabel_2_4_2 = new JLabel("State :");
		lblNewLabel_2_4_2.setBounds(51, 122, 90, 14);
		boatInfoPanel.add(lblNewLabel_2_4_2);
		
		JLabel boatStateLabel = new JLabel("");
		boatStateLabel.setBounds(126, 122, 84, 14);
		boatInfoPanel.add(boatStateLabel);
		components.put("boatStateLabel", boatStateLabel);
		
		JLabel lblNewLabel_2_4_2_1 = new JLabel("Fuel : ");
		lblNewLabel_2_4_2_1.setBounds(51, 156, 82, 14);
		boatInfoPanel.add(lblNewLabel_2_4_2_1);
		
		JProgressBar boatFuelProgressBar = new JProgressBar();
		boatFuelProgressBar.setForeground(Color.GREEN);
		boatFuelProgressBar.setBackground(Color.WHITE);
		boatFuelProgressBar.setBounds(126, 156, 89, 14);
		boatInfoPanel.add(boatFuelProgressBar);
		components.put("boatFuelProgressBar", boatFuelProgressBar);
		
		JLabel boatFuelLabel = new JLabel("");
		boatFuelLabel.setBounds(126, 171, 89, 14);
		boatInfoPanel.add(boatFuelLabel);
		components.put("boatFuelLabel", boatFuelLabel);
		
		JLabel lblNewLabel_2_4_2_1_1 = new JLabel("Load :");
		lblNewLabel_2_4_2_1_1.setBounds(51, 196, 82, 14);
		boatInfoPanel.add(lblNewLabel_2_4_2_1_1);
		
		JProgressBar boatLoadProgressBar = new JProgressBar();
		boatLoadProgressBar.setForeground(Color.GREEN);
		boatLoadProgressBar.setBackground(Color.WHITE);
		boatLoadProgressBar.setBounds(126, 196, 89, 14);
		boatInfoPanel.add(boatLoadProgressBar);
		components.put("boatLoadProgressBar", boatLoadProgressBar);
		
		JLabel boatLoadLabel = new JLabel("");
		boatLoadLabel.setBounds(126, 211, 89, 14);
		boatInfoPanel.add(boatLoadLabel);
		components.put("boatLoadLabel", boatLoadLabel);
		
		JPanel appControlPanel = new JPanel();
		appControlPanel.setBackground(UIManager.getColor("Button.background"));
		appControlPanel.setBounds(931, 825, 253, 88);
		frame.getContentPane().add(appControlPanel);
		appControlPanel.setLayout(null);
		
		JButton exitButton = new JButton("Exit");
		exitButton.setBounds(139, 34, 91, 23);
		appControlPanel.add(exitButton);
		exitButton.addActionListener(this);
		exitButton.setActionCommand("exit");
		
		JButton helpButton = new JButton("Help");
		helpButton.setBounds(27, 34, 91, 23);
		appControlPanel.add(helpButton);
		helpButton.addActionListener(this);
		helpButton.setActionCommand("help");
		
		/* Initialize the HELP Frame */
		helpFrame = new JFrame();
		helpFrame.setResizable(false);
		helpFrame.setTitle("About");
		helpFrame.setSize(new Dimension(500, 551));
		helpFrame.getContentPane().setLayout(null);
		helpFrame.setVisible(false);
		
		JLabel lblNewLabel_4 = new JLabel("Oil Spill Simulation");
		lblNewLabel_4.setBounds(179, 11, 130, 24);
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		helpFrame.getContentPane().add(lblNewLabel_4);
		
		JTextPane txtpnDaDadaAsdsa = new JTextPane();
		txtpnDaDadaAsdsa.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtpnDaDadaAsdsa.setBackground(SystemColor.menu);
		txtpnDaDadaAsdsa.setText("This program simulates the spread and cleaning of an oil spill in an ocean area. The initial quantity of oil spilled and the rate of spread can be adjusted during initialization. There are 3 types of boats in the simulation. Cleaning boats seek out oil cells and collect the oil, which they then drop to a dump station unique to them when their cargo hold is full. Collector boats go from dump station to dump station collecting oil and then depositing it in a single central oil storage. Refuel boats seek out boats that are out of fuel and refuel them. They take fuel from a single shared Refuel station on the map. The quantity of boats of each type and their fuel and load capacities can be set during initialization. count of Cleaning boats can range from 1-8 and are automatically assigned dump stations evenly spread throughout the edges of the map.\r\n\r\nAfter initialization, press Play to begin it. You can then pause it using the same button, and use the slider and associated Set Speed button to change the simulation speed. The Map information panel shows general world stats and the Boat information panel shows stats for a particular boat, that can be changed via the combo box.\r\n\r\nLegend of colors/icons - \r\n\r\n\r\n\r\n\r\n\r\n\r\n");
		txtpnDaDadaAsdsa.setEditable(false);
		txtpnDaDadaAsdsa.setBounds(22, 53, 452, 329);
		helpFrame.getContentPane().add(txtpnDaDadaAsdsa);
		
		JLabel lbl1 = new JLabel("New label");
		lbl1.setIcon(new ImageIcon(ABmap.icons.get("fuelStation")));
		lbl1.setBounds(49, 403, 15, 14);
		helpFrame.getContentPane().add(lbl1);
		
		JLabel lbl2 = new JLabel("New label");
		lbl2.setIcon(new ImageIcon(ABmap.icons.get("dumpStation")));
		lbl2.setBounds(49, 428, 15, 14);
		helpFrame.getContentPane().add(lbl2);
		
		JLabel lbl3 = new JLabel("New label");
		lbl3.setIcon(new ImageIcon(ABmap.icons.get("oilStorage")));
		lbl3.setBounds(49, 453, 15, 14);
		helpFrame.getContentPane().add(lbl3);
		
		JLabel lbl4 = new JLabel("New label");
		lbl4.setIcon(new ImageIcon(ABmap.icons.get("boat")));
		lbl4.setBounds(49, 478, 15, 14);
		helpFrame.getContentPane().add(lbl4);
		
		JLabel lblNewLabel_6 = new JLabel("Refuel Station");
		lblNewLabel_6.setBounds(74, 403, 95, 14);
		helpFrame.getContentPane().add(lblNewLabel_6);
		
		JLabel lblNewLabel_6_1 = new JLabel("Dump Station");
		lblNewLabel_6_1.setBounds(74, 428, 95, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_1);
		
		JLabel lblNewLabel_6_2 = new JLabel("Oil Storage");
		lblNewLabel_6_2.setBounds(74, 453, 95, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_2);
		
		JLabel lblNewLabel_6_3 = new JLabel("Boat");
		lblNewLabel_6_3.setBounds(74, 478, 95, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_3);
		
		JLabel lbl5 = new JLabel("");
		lbl5.setBounds(190, 403, 15, 14);
		lbl5.setOpaque(true);
		lbl5.setBackground(new Color(235,61,61));
		helpFrame.getContentPane().add(lbl5);
		
		JLabel lbl6 = new JLabel("");
		lbl6.setIcon(null);
		lbl6.setBounds(190, 428, 15, 14);
		lbl6.setOpaque(true);
		lbl6.setBackground(new Color(255,255,255));
		helpFrame.getContentPane().add(lbl6);

		JLabel lbl7 = new JLabel("");
		lbl7.setBounds(190, 453, 15, 14);
		lbl7.setOpaque(true);
		lbl7.setBackground(new Color(97,51,20));
		helpFrame.getContentPane().add(lbl7);
		
		JLabel lbl8 = new JLabel("");
		lbl8.setBounds(190, 478, 15, 14);
		lbl8.setOpaque(true);
		lbl8.setBackground(new Color(219,208,53));
		helpFrame.getContentPane().add(lbl8);
		
		JLabel lblNewLabel_6_4 = new JLabel("Selected Boat");
		lblNewLabel_6_4.setBounds(215, 403, 95, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_4);
		
		JLabel lblNewLabel_6_5 = new JLabel("Cleaning Boat");
		lblNewLabel_6_5.setBounds(215, 428, 95, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_5);
		
		JLabel lblNewLabel_6_6 = new JLabel("Collector Boat");
		lblNewLabel_6_6.setBounds(215, 453, 95, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_6);
		
		JLabel lblNewLabel_6_7 = new JLabel("Refuel Boat");
		lblNewLabel_6_7.setBounds(215, 478, 95, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_7);
		
		JLabel lblNewLabel_6_4_1 = new JLabel("Oil");
		lblNewLabel_6_4_1.setBounds(360, 403, 85, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_4_1);
		
		JLabel lbl9 = new JLabel("");
		lbl9.setBounds(335, 403, 15, 14);
		lbl9.setOpaque(true);
		lbl9.setBackground(new Color(0,0,0));
		helpFrame.getContentPane().add(lbl9);
		
		JLabel lblNewLabel_6_4_2 = new JLabel("Land");
		lblNewLabel_6_4_2.setBounds(360, 428, 85, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_4_2);
		
		JLabel lbl10 = new JLabel("");
		lbl10.setBounds(335, 428, 15, 14);
		lbl10.setOpaque(true);
		lbl10.setBackground(new Color(59,117,49));
		helpFrame.getContentPane().add(lbl10);
		
		JLabel lblNewLabel_6_4_3 = new JLabel("Water");
		lblNewLabel_6_4_3.setBounds(360, 453, 85, 14);
		helpFrame.getContentPane().add(lblNewLabel_6_4_3);
		
		JLabel lbl11 = new JLabel("");
		lbl11.setBounds(335, 453, 15, 14);
		lbl11.setOpaque(true);
		lbl11.setBackground(new Color(55,114,196));
		helpFrame.getContentPane().add(lbl11);
    }
    
 
}