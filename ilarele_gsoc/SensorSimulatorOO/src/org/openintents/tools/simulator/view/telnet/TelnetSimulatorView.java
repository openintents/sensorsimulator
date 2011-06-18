package org.openintents.tools.simulator.view.telnet;

import java.awt.BorderLayout;
import java.awt.Component;
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
		gpsAddonView = new GPSAddonView(model.getGpsAddon());
		batteryAddonView = new BatteryAddonView(model.getBatteryAddon());

		// split [left|right]
		JPanel leftPanel = fillLeftPanel(model);
		JTabbedPane rightPanel = fillRightPanel(model);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, rightPanel);
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

	private JPanel fillLeftPanel(TelnetSimulatorModel model) {
		// telnet JLabel, text field and button
		
		JPanel connectPanel = new JPanel();
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
