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

package org.openintents.tools.simulator.controller.telnet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.openintents.tools.simulator.model.telnet.addons.GPSAddonModel;
import org.openintents.tools.simulator.view.telnet.addons.GPSAddonView;

/**
 * GPSAddonController keeps the behaviour of the gps add-on (listeners, etc.)
 * 
 * Gps add-on sets emulator gps position (via telnet communication).
 * 
 * @author Peli
 * 
 */
public class GPSAddonController {
	// GPS variables
	@SuppressWarnings("unused")
	private GPSAddonView mView;
	@SuppressWarnings("unused")
	private GPSAddonModel mModel;

	public GPSAddonController(final GPSAddonModel model, final GPSAddonView view) {
		this.mModel = model;
		this.mView = view;

		JButton gpsButton = view.getGpsButton();
		gpsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.sendGPS(view.getLongitude(), view.getLatitude(),
						view.getAltitude());
			}
		});
	}
}
