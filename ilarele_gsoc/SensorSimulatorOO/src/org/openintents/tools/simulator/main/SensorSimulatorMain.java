/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 * 
 * Copyright (C) 2008-2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.tools.simulator.main;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.SensorSimulator;
import org.openintents.tools.simulator.SimulatorInstances;
import org.openintents.tools.simulator.TelnetSimulator;

/**
 * Main class of our sensor simulator. This class creates a Frame that is filled
 * by SensorSimulator class, creates a menu bar and panel for tabs.
 * 
 * @author Josip Balic
 */
public class SensorSimulatorMain extends JPanel implements WindowListener,
		ChangeListener, ItemListener {

	private static final long serialVersionUID = -5990997086225010821L;
	// command strings

	// variable that holds running instances of Sensor Simulators
	public static SimulatorInstances simulatorInstances = new SimulatorInstances();

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("SensorSimulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create the menu bar: File Help
		JMenuBar myMenuBar = new JMenuBar();
		myMenuBar.setPreferredSize(new Dimension(200, 30));
		JMenu menuFile = new JMenu("File");
		myMenuBar.add(menuFile);
		JMenu menuHelp = new JMenu("Help");
		myMenuBar.add(menuHelp);

		// Exit menu
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuFile.add(menuItemExit);

		// Help Online menu
		JMenuItem menuItemHelpOnline = new JMenuItem("Online Help");
		menuItemHelpOnline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					if (desktop.isSupported(Desktop.Action.BROWSE)) {
						URI uri;
						try {
							uri = new URI(
									Global.HELP_SENSOR_SIMULATOR_DESCRIPTION_URL);
							desktop.browse(uri);
						} catch (URISyntaxException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		menuHelp.add(menuItemHelpOnline);

		// Create TabbedPane
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(Global.WIDTH, Global.HEIGHT));
		SensorSimulator simulator = new SensorSimulator();
		TelnetSimulator telnet = new TelnetSimulator();

		// add instance of this simulator to SensorSimulatorInstances
		setFirstSimulatorInstance(simulator);
		JScrollPane simulatorScroll = new JScrollPane(simulator.view);
		simulatorScroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		JScrollPane telnetScroll = new JScrollPane(telnet.view);
		telnetScroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// add Simulator, Telnet and Recording tabs
		tabbedPane.addTab("Sensor Simulator", simulatorScroll);
		tabbedPane.addTab("Telnet Simulator", telnetScroll);

		// add tabs
		frame.add(tabbedPane);

		// add menu
		frame.setJMenuBar(myMenuBar);

		// show frame
		frame.setVisible(true);
		frame.pack();
	}

	/**
	 * Method that adds instance of our first simulator tab to
	 * SimulatorInstances
	 * 
	 * @param sensorSimulator
	 *            , SensorSimulator instance we want to add.
	 */
	private static void setFirstSimulatorInstance(SensorSimulator simulator) {
		simulatorInstances.addSimulator(simulator);
	}

	/** Add a listener for window events. */
	void addWindowListener(Window w) {
		w.addWindowListener(this);
	}

	// React to window events.
	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	/**
	 * Main method of SensorSimulatorMain class.
	 * 
	 * @param args
	 *            , String[] arguments used to run this GUI.
	 */
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}

				createAndShowGUI();
			}
		});
	}

	/**
	 * This method is invoked when action happens.
	 * 
	 * @param e
	 *            , ActionEvent that generated action.
	 */
	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void stateChanged(ChangeEvent arg0) {
	}

	public void itemStateChanged(ItemEvent arg0) {
	}
}
