package org.openintents.tools.simulator.view.help;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;

public class HelpWindow extends JFrame {
	private static final long serialVersionUID = -3465061620684511665L;

	public HelpWindow(SensorView view) {
		super();
		setTitle("Information - " + view.getModel().getName());
		setSize(Global.WIDTH_HELP, Global.HEIGHT_HELP);
		setLocation(300, 200);
		Container contentPane = getContentPane();
		contentPane.add(view.getHelpPanel());

		KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = -2757256936195546496L;

			// close the frame when the user presses escape
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc,
				"esc");
		getRootPane().getActionMap().put("esc", escapeAction);
	}
}
