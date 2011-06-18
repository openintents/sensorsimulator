package org.openintents.tools.simulator.help;

import java.awt.Container;

import javax.swing.JFrame;

import org.openintents.tools.simulator.view.sensor.sensors.SensorView;

public class HelpWindow extends JFrame {
	private static final long serialVersionUID = -3465061620684511665L;

	public HelpWindow(SensorView view) {
		super();
		setTitle("Information - " + view.getModel().getName());
		setSize(250, 250);
		setLocation(300, 200);
		Container contentPane = getContentPane();
		contentPane.add(view.getHelpPanel());
	}
}
