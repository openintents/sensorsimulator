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

/**
 * Help Window for sensors.
 * 
 * @author ilarele
 * 
 */
public class HelpWindow extends JFrame {
	private static final long serialVersionUID = -3465061620684511665L;

	public HelpWindow(SensorView view) {
		super();
		setTitle("Information - " + view.getModel().getName());
		setSize(Global.WIDTH_HELP, Global.HEIGHT_HELP);
		setLocation(150, 50);
		Container contentPane = getContentPane();
		contentPane.add(view.getHelpPanel());

		KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = -2757256936195546496L;

			// close the frame when the user presses escape
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc,
				"esc");
		getRootPane().getActionMap().put("esc", escapeAction);
	}
}
