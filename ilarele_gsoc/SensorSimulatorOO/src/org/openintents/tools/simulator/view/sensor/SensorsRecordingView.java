package org.openintents.tools.simulator.view.sensor;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openintents.tools.simulator.model.sensor.SensorsRecordingModel;

public class SensorsRecordingView extends JPanel {
	private static final long serialVersionUID = -5566737606706780206L;

	public SensorsRecordingView(SensorsRecordingModel model) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel panel1 = new JPanel(new GridLayout(0, 1));
		panel1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0), "Info")));
		panel1.add(new JLabel("- start this application :D"));
		panel1.add(new JLabel("- start the android application from the device and"
				+ " follow the instructions from there"));
		panel1.add(new JLabel("for now, check syso"));
		add(panel1);
	}

}
