package org.openintents.tools.simulator.view.telnet;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.telnet.TelnetSimulatorModel;
import org.openintents.tools.simulator.view.telnet.addons.BatteryAddonView;
import org.openintents.tools.simulator.view.telnet.addons.GPSAddonView;

public class TelnetSimulatorView extends JPanel {
	private static final long serialVersionUID = -8959530416109263396L;
	private JButton telnetSocketButton;
	// Text fields and button for Telnet socket port
	private JTextField telnetSocketText;
	private BatteryAddonView batteryAddonView;
	private GPSAddonView gpsAddonView;

	public TelnetSimulatorView(TelnetSimulatorModel model) {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// add-ons
		batteryAddonView = new BatteryAddonView(model.getBatteryAddon());
		gpsAddonView = new GPSAddonView(model.getGpsAddon());

		// add telnet JLabel, text field and button
		JPanel connectPanel = new JPanel(new GridLayout(1, 0));
		Font fontNotify = new Font("SansSerif", Font.BOLD, 12);

		JLabel telnetSocketLabel = new JLabel("Telnet socket port", JLabel.LEFT);
		telnetSocketLabel.setFont(fontNotify);
		telnetSocketLabel.setForeground(Global.NOTIFY_COLOR);
		connectPanel.add(telnetSocketLabel);

		telnetSocketText = new JTextField(5);
		telnetSocketText.setText("" + model.getPort());
		connectPanel.add(telnetSocketText);

		telnetSocketButton = new JButton("Connect");
		telnetSocketButton.setFont(fontNotify);
		telnetSocketButton.setForeground(Global.NOTIFY_COLOR);
		connectPanel.add(telnetSocketButton);

		c.gridx = 0;
		c.gridy = 0;
		add(connectPanel, c);

		// add neccesary things for GPS emulation
		JPanel gpsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 2;
		c3.gridx = 0;
		c3.gridy = 0;


		gpsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("GPS"), BorderFactory
				.createMatteBorder(5, 5, 5, 5, Global.BORDER_COLOR)));
		
		gpsAddonView.fillPane(gpsPanel, c3);

		// GPS
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		add(gpsPanel, c);

		// battery
		JPanel batterySimulationsPanel = new JPanel(new BorderLayout());
		batterySimulationsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Battery"), BorderFactory
				.createMatteBorder(5, 5, 5, 5, Global.BORDER_COLOR)));

		
		JPanel batteryCapacityPanel = new JPanel(new BorderLayout());

		JLabel batteryLabel = new JLabel("Charged", JLabel.CENTER);
		batteryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		batteryCapacityPanel.add(batteryLabel, BorderLayout.LINE_START);
		batteryCapacityPanel.add(batteryAddonView.getBatterySlider(),
				BorderLayout.CENTER);

		// add batteryCapacityPanel
		batterySimulationsPanel.add(batteryCapacityPanel, BorderLayout.NORTH);

		// Now add a scrollable panel with more controls and GridBagLayout
		JPanel telnetSettingsPanel = new JPanel(new GridBagLayout());
		// define GridBagConstraints
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.anchor = GridBagConstraints.NORTHWEST;
		// Create ScrollPane for simulations through telnet connection
		JScrollPane telnetSettingsScrollPane = new JScrollPane(
				telnetSettingsPanel);
		telnetSettingsScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// Now add neccesary things for battery simulation
		JPanel batteryPanel = new JPanel();
		batteryPanel
				.setLayout(new BoxLayout(batteryPanel, BoxLayout.PAGE_AXIS));

		batteryPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Battery"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		batteryPanel.add(batteryAddonView.getBatteryPresence());

		batteryPanel.add(batteryAddonView.getBatteryAC());

		c2.gridy++;
		telnetSettingsPanel.add(batteryPanel, c2);

		// Now add neccesary things for battery simulation
		JPanel batteryStatusPanel = new JPanel();
		batteryStatusPanel.setLayout(new BoxLayout(batteryStatusPanel,
				BoxLayout.PAGE_AXIS));

		batteryStatusPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Status"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		// Create the combo box, select item at index 4.
		batteryStatusPanel.add(batteryAddonView.getBatteryStatusList());

		c2.gridy++;
		telnetSettingsPanel.add(batteryStatusPanel, c2);

		// Now add neccesary things for battery simulation
		JPanel batteryHealthPanel = new JPanel();
		batteryHealthPanel.setLayout(new BoxLayout(batteryHealthPanel,
				BoxLayout.PAGE_AXIS));

		batteryHealthPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Health"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		// Create the combo box, select item at index 4.

		batteryHealthPanel.add(batteryAddonView.getBatteryHealthList());

		c2.gridy++;
		telnetSettingsPanel.add(batteryHealthPanel, c2);

		// Now add neccesary things for battery simulation
		JPanel batteryFilePanel = new JPanel(new BorderLayout());

		batteryFilePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Load from file"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		// create everything need for battery emulation from file
		batteryAddonView.fillFileEmulationPane(batteryFilePanel);
		c2.gridy++;
		telnetSettingsPanel.add(batteryFilePanel, c2);

		// add telnetSettingsScrollPane to telnetSimulationsPanel
		batterySimulationsPanel.add(telnetSettingsScrollPane,
				BorderLayout.CENTER);

		// add telnetSimulationsPanel to rightPanel
		c.gridx = 3;
		c.gridy = 1;
		add(batterySimulationsPanel, c);

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
