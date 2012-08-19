/*
 * Copyright (C) 2008 - 2011 OpenIntents.org
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

package org.openintents.tools.simulator.view.telnet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.telnet.TelnetSimulatorModel;
import org.openintents.tools.simulator.view.telnet.addons.BatteryAddonView;
import org.openintents.tools.simulator.view.telnet.addons.GPSAddonView;

/**
 * TelnetSimulatorView keeps the GUI for TelnetSimulator.
 * 
 * TelnetSimulator simulates, using a telnet communication with the emulator,
 * the battery level and the gps position.
 * 
 * @author Peli
 */
public class TelnetSimulatorView extends JPanel {
	private static final long serialVersionUID = -8959530416109263396L;
	private JButton telnetSocketButton;
	// Text fields and button for Telnet socket port
	private JTextField telnetSocketText;

	private BatteryAddonView batteryAddonView;
	private GPSAddonView gpsAddonView;

	public TelnetSimulatorView(TelnetSimulatorModel model) {
		gpsAddonView = new GPSAddonView(model.getGpsAddon());
		batteryAddonView = new BatteryAddonView(model.getBatteryAddon());
		setLayout(new BorderLayout());
		// split [left|right]
		JPanel connectPanel = fillConnectPanel(model);
		JTabbedPane rightPanel = fillRightPanel(model);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				connectPanel, rightPanel);
		splitPane.setResizeWeight(Global.SENSOR_SPLIT_LEFT);
		add(splitPane);

		// TODO down split for output
	}

	private JTabbedPane fillRightPanel(TelnetSimulatorModel model) {
		JTabbedPane rightPanel = new JTabbedPane();

		rightPanel.addTab("GPS", fillGPSAddonView());
		rightPanel.addTab("Battery", fillBatteryAddonView());

		return rightPanel;
	}

	private JPanel fillBatteryAddonView() {
		// battery
		JPanel batterySimulationsPanel = new JPanel(new BorderLayout());
		batterySimulationsPanel.setBorder(BorderFactory
				.createTitledBorder("Battery"));

		JPanel batteryCapacityPanel = new JPanel(new BorderLayout());

		JLabel batteryLabel = new JLabel("Charged", SwingConstants.CENTER);
		batteryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		batteryCapacityPanel.add(batteryLabel, BorderLayout.LINE_START);
		batteryCapacityPanel.add(batteryAddonView.getBatterySlider(),
				BorderLayout.CENTER);

		// add batteryCapacityPanel
		batterySimulationsPanel.add(batteryCapacityPanel, BorderLayout.NORTH);

		// Now add a scrollable panel with more controls and GridBagLayout
		JPanel telnetSettingsPanel = new JPanel(new GridBagLayout());
		// define GridBagConstraints
		// Create ScrollPane for simulations through telnet connection
		JScrollPane telnetSettingsScrollPane = new JScrollPane(
				telnetSettingsPanel);
		telnetSettingsScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// Now add neccesary things for battery simulation
		JPanel batteryPanel = new JPanel();
		batteryPanel
				.setLayout(new BoxLayout(batteryPanel, BoxLayout.PAGE_AXIS));

		batteryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Battery"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		batteryPanel.add(batteryAddonView.getBatteryPresence());

		batteryPanel.add(batteryAddonView.getBatteryAC());

		telnetSettingsPanel.add(batteryPanel);

		// Now add neccesary things for battery simulation
		JPanel batteryStatusPanel = new JPanel();
		batteryStatusPanel.setLayout(new BoxLayout(batteryStatusPanel,
				BoxLayout.PAGE_AXIS));

		batteryStatusPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Status"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		// Create the combo box, select item at index 4.
		batteryStatusPanel.add(batteryAddonView.getBatteryStatusList());

		telnetSettingsPanel.add(batteryStatusPanel);

		// Now add neccesary things for battery simulation
		JPanel batteryHealthPanel = new JPanel();
		batteryHealthPanel.setLayout(new BoxLayout(batteryHealthPanel,
				BoxLayout.PAGE_AXIS));

		batteryHealthPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Health"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		// Create the combo box, select item at index 4.

		batteryHealthPanel.add(batteryAddonView.getBatteryHealthList());

		telnetSettingsPanel.add(batteryHealthPanel);

		// Now add neccesary things for battery simulation
		JPanel batteryFilePanel = new JPanel(new BorderLayout());

		batteryFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Load from file"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		// create everything need for battery emulation from file
		batteryAddonView.fillFileEmulationPane(batteryFilePanel);
		telnetSettingsPanel.add(batteryFilePanel);

		// add telnetSettingsScrollPane to telnetSimulationsPanel
		batterySimulationsPanel.add(telnetSettingsScrollPane,
				BorderLayout.CENTER);

		// add telnetSimulationsPanel to rightPanel
		return batterySimulationsPanel;
	}

	private Component fillGPSAddonView() {
		JPanel gpsPanel = gpsAddonView.getPanel();
		return gpsPanel;
	}

	private JPanel fillConnectPanel(TelnetSimulatorModel model) {
		// telnet JLabel, text field and button

		JPanel connectPanel = new JPanel();
		connectPanel.setPreferredSize(new Dimension(
				(int) (Global.W_FRAME * (Global.SENSOR_SPLIT_LEFT - 0.03)),
				Global.H_CONTENT));
		// connectPanel.setLayout(new BoxLayout(connectPanel,
		// BoxLayout.X_AXIS));
		// first row
		Font fontNotify = new Font("SansSerif", Font.BOLD, 12);
		JLabel telnetSocketLabel = new JLabel("Telnet socket port",
				SwingConstants.LEFT);
		telnetSocketLabel.setFont(fontNotify);
		connectPanel.add(telnetSocketLabel);

		telnetSocketText = new JTextField(5);
		telnetSocketText.setText("" + model.getPort());
		connectPanel.add(telnetSocketText);

		// second row
		telnetSocketButton = new JButton("Connect");
		telnetSocketButton.setFont(fontNotify);
		connectPanel.add(telnetSocketButton);

		return connectPanel;
	}

	/**
	 * Get telnet socket port number.
	 * 
	 * @return String containing port number.
	 */
	public int getTelnetPort() {
		String s = telnetSocketText.getText();
		int port = 0;
		try {
			port = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return port;
	}

	public BatteryAddonView getBatteryAddon() {
		return batteryAddonView;
	}

	public GPSAddonView getGpsAddon() {
		return gpsAddonView;
	}

	public JButton getTelnetPortButton() {
		return telnetSocketButton;
	}
}
