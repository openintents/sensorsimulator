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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
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
	private static final long serialVersionUID = -7248177045429153977L;

	// variable that holds running instances of Sensor Simulators
	public SimulatorInstances simulatorInstances = new SimulatorInstances();

	private SensorSimulator mSimulator;
	private TelnetSimulator mTelnet;
	private JLabel mStatusBar;

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("SensorSimulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Global.ICON_SENSOR_SIMULATOR_PATH));
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel menuBar = new JPanel();
		JPanel contentPanel = new JPanel();
		mStatusBar = new JLabel();
		JPanel statusPanel = new JPanel();

		fillMainPanel(menuBar, contentPanel, frame);
		fillStatusPanel(statusPanel, frame);

		menuBar.setBorder(new EmptyBorder(Global.BORDER_VSIZE,
				Global.BORDER_HSIZE, Global.BORDER_VSIZE, Global.BORDER_HSIZE));
		mainPanel.add(menuBar, BorderLayout.NORTH);
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		mainPanel.add(statusPanel, BorderLayout.SOUTH);

		mainPanel.setPreferredSize(new Dimension(Global.W_FRAME + 150,
				Global.H_FRAME));
		// show frame
		frame.add(mainPanel);
		frame.setVisible(true);
		frame.pack();
	}

	private void fillStatusPanel(JPanel statusPanel, JFrame frame) {
		statusPanel.setLayout(new GridLayout(1, 1));
		statusPanel.setDoubleBuffered(false);
		statusPanel.add(mStatusBar);
		statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
	}

	/**
	 * Creates the main panel, with a menu bar and a container panel.
	 * 
	 * @param menubar
	 * @param contentPanel
	 * @param frame
	 * 
	 * @return menu bar panel
	 */
	private void fillMainPanel(JPanel menuBar, JPanel contentPanel, JFrame frame) {
		contentPanel.setLayout(new BorderLayout());
		// menu
		menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));

		// sensor simulator button
		JButton simulatorButton = createSimulatorButton(contentPanel, frame);
		menuBar.add(simulatorButton);

		// telnet simulator button
		JButton telnetButton = createTelnetButton(contentPanel, frame);
		menuBar.add(telnetButton);

		// settings button
		JButton settingsButton = createSettingsButton(contentPanel, frame);
		menuBar.add(Box.createHorizontalGlue());
		settingsButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		menuBar.add(settingsButton);

		// help button
		JButton helpButton = createHelpButton();
		helpButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		menuBar.add(helpButton);
	}

	private JButton createSettingsButton(final JPanel contentPanel,
			final JFrame frame) {
		JButton menuButton = new JButton(Global.MENU_SETTINGS);
		SpringLayout layout = new SpringLayout();
		final JPanel settingsPanel = new JPanel(layout);
		settingsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Global.COLOR_ENABLE_BLUE),
				BorderFactory.createEmptyBorder(Global.BORDER_HSIZE,
						Global.BORDER_HSIZE, Global.BORDER_HSIZE,
						Global.BORDER_HSIZE)));
		settingsPanel.add(mSimulator.view.getSettingsPanel());

		menuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contentPanel.removeAll();
				contentPanel.add(settingsPanel);
				printStatus("Settings");
				contentPanel.validate();
				contentPanel.repaint();
			}
		});
		return menuButton;
	}

	private JButton createTelnetButton(final JPanel contentPanel,
			final JFrame frame) {
		JButton menuButton = new JButton(Global.MENU_CONSOLE);
		mTelnet = new TelnetSimulator(this);
		final JScrollPane telnetScroll = new JScrollPane(mTelnet.view);
		telnetScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		menuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contentPanel.removeAll();
				contentPanel.add(telnetScroll);
				printStatus("Telnet Simulator");
				contentPanel.validate();
				contentPanel.repaint();
			}
		});
		return menuButton;
	}

	private JButton createSimulatorButton(final JPanel contentPanel,
			final JFrame frame) {
		JButton menuButton = new JButton(Global.MENU_SENSOR_SIMULATOR);
		mSimulator = new SensorSimulator(this);

		// add instance of this simulator to SensorSimulatorInstances
		setFirstSimulatorInstance(mSimulator);

		final JScrollPane simulatorScroll = new JScrollPane(mSimulator.view);
		simulatorScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// first, sensor simulator is selected
		contentPanel.add(simulatorScroll);
		printStatus("Sensor Simulator");
		menuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contentPanel.removeAll();
				contentPanel.add(simulatorScroll);
				printStatus("Sensor Simulator");
				contentPanel.repaint();
				frame.pack();
			}
		});
		return menuButton;
	}

	private JButton createHelpButton() {
		// Help Online menu
		JButton menuButton = new JButton(Global.MENU_HELP);
		menuButton.addActionListener(new ActionListener() {
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
		return menuButton;
	}

	/**
	 * Method that adds instance of our first simulator tab to
	 * SimulatorInstances
	 * 
	 * @param sensorSimulator
	 *            , SensorSimulator instance we want to add.
	 */
	private void setFirstSimulatorInstance(SensorSimulator simulator) {
		simulatorInstances.addSimulator(simulator);
	}

	/** Add a listener for window events. */
	void addWindowListener(Window w) {
		w.addWindowListener(this);
	}

	/**
	 * Main method of SensorSimulatorMain class.
	 * 
	 * @param args
	 *            , String[] arguments used to run this GUI.
	 */
	public static void main(String[] args) {
		try {
			// set nimbus theme
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

			// modify some colors from the theme
			UIManager.put("control", Global.BACK);
			UIManager.put("text", Global.TEXT);
			UIManager.put("scrollbar", Global.TEXT);
			UIManager.put("nimbusBlueGrey", Global.BUTTON);
			UIManager.put("nimbusBase", Global.TAB);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		final SensorSimulatorMain mainSensorSimulator = new SensorSimulatorMain();
		new Global().initGlobal();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainSensorSimulator.createAndShowGUI();
			}
		});
	}

	/**
	 * Called to set status in the program status bar.
	 * 
	 * @param status
	 */
	public void printStatus(String status) {
		mStatusBar.setText(" " + status);
	}

	/**
	 * This method is invoked when action happens.
	 * 
	 * @param e
	 *            , ActionEvent that generated action.
	 */
	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
	}

	// React to window events.
	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
