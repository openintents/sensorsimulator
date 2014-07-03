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

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.openintents.tools.simulator.Global;

/**
 * About Window.
 * 
 * @author ilarele
 * 
 */
public class AboutWindow extends JFrame {
	private static final long serialVersionUID = -3465061620684511665L;

	public AboutWindow() {
		setTitle("About");
		setSize(Global.WIDTH_ABOUT, Global.HEIGHT_ABOUT);
		setLocation(150, 50);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				Global.ICON_SENSOR_SIMULATOR_PATH));

		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		// about
		contentPane.add(getAboutPanel());

		// help
		JPanel helpPane = getHelpPanel();
		helpPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(helpPane);

		// set exit on esc
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

	private JPanel getHelpPanel() {
		JPanel help = new JPanel();
		JButton helpBtn = new JButton(Global.ICON_HELP);
		helpBtn.setOpaque(false);
		helpBtn.setContentAreaFilled(false);
		helpBtn.setBorderPainted(false);
		helpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.startBrowser(Global.HELP_SENSOR_SIMULATOR_DESCRIPTION_URL);
			}
		});
		help.add(helpBtn);
		return help;
	}

	private JPanel getAboutPanel() {
		JPanel about = new JPanel(new GridLayout(0, 1));
		about.add(new JLabel(
				"Sensor Simulator Version 2.0, Release Candidate 1"));
		about.add(new JLabel("Copyright (C) 2008 - 2011 OpenIntents.org"));

		JLabel linkLabel = new JLabel();
		String linkText = "<u>The source code</u>" + " is available ";
		linkLabel.setText("<html><span style=\"color: #000099;\">" + linkText
				+ "</span></html>");
		linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Global.startBrowser(Global.HELP_OPENINTENTS_REPO);
			}
		});
		about.add(linkLabel);
		about.add(new JLabel("under the Apache License, Version 2.0."));
		return about;
	}
}
