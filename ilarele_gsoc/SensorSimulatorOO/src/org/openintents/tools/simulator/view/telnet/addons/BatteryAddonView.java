package org.openintents.tools.simulator.view.telnet.addons;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.openintents.tools.simulator.model.telnet.addons.BatteryAddonModel;

public class BatteryAddonView extends JPanel {
	private static final long serialVersionUID = -5834092002279084857L;

	// TelnetSimulations variables
	private JSlider batterySlider;

	// Battery variables
	private JCheckBox batteryPresence;
	private JCheckBox batteryAC;
	private JComboBox batteryStatusList;
	private JComboBox batteryHealthList;

	private final String[] batteryStatus = { "unknown", "charging",
			"discharging", "not-charging", "full" };
	private final String[] batteryHealth = { "unknown", "good", "overheat",
			"dead", "overvoltage", "failure" };

	// Batter file variables
	private JButton batteryEmulation;
	private JButton batteryNext;
	private JFileChooser fileChooser;
	private JButton openButton;

	public BatteryAddonView(BatteryAddonModel model) {
		batterySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		batterySlider.setMajorTickSpacing(10);
		batterySlider.setMinorTickSpacing(5);
		batterySlider.setPaintTicks(true);
		batterySlider.setPaintLabels(true);
		batterySlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		batteryPresence = new JCheckBox("Is Present");
		batteryPresence.setSelected(model.isPresent());

		batteryAC = new JCheckBox("AC plugged");
		batteryAC.setSelected(model.isBatteryAC());

		batteryStatusList = new JComboBox(batteryStatus);
		batteryStatusList.setSelectedItem(model.getStatus());

		batteryHealthList = new JComboBox(batteryHealth);
		batteryHealthList.setSelectedItem(model.getHealth());

		batteryNext = new JButton("Next time event");

		fileChooser = new JFileChooser();
		openButton = new JButton("Open a File");

		batteryEmulation = new JButton("Emulate Battery");
	}

	public JSlider getBatterySlider() {
		return batterySlider;
	}

	public JCheckBox getBatteryPresence() {
		return batteryPresence;
	}

	public JCheckBox getBatteryAC() {
		return batteryAC;
	}

	public JComboBox getBatteryStatusList() {
		return batteryStatusList;
	}

	public JComboBox getBatteryHealthList() {
		return batteryHealthList;
	}

	public void fillFileEmulationPane(JPanel batteryFilePanel) {
		batteryFilePanel.add(openButton, BorderLayout.PAGE_START);
		batteryFilePanel.add(batteryEmulation, BorderLayout.WEST);
		batteryFilePanel.add(batteryNext, BorderLayout.EAST);
	}

	public JButton getBatteryEmulation() {
		return batteryEmulation;
	}

	public JButton getBatteryNext() {
		return batteryNext;
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	public JButton getOpenFileButton() {
		return openButton;
	}
}
