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

package org.openintents.tools.simulator.view.telnet.addons;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.telnet.addons.GPSAddonModel;

/**
 * GPSAddonView keeps the GUI for GPS Addon.
 * 
 * Gps add-on sets emulator gps position (via telnet communication).
 * 
 * @author Peli
 */
public class GPSAddonView extends JPanel {
	private static final long serialVersionUID = -3439159781311851558L;
	// GPS variables
	private JTextField gpsLongitudeText;
	private JTextField gpsLatitudeText;
	private JTextField gpsAltitudeText;
	private JTextField lisName;
	private JButton gpsButton;

	public GPSAddonView(final GPSAddonModel model) {
		gpsLongitudeText = new JTextField(10);
		gpsLongitudeText.setText("" + model.getLongitude());

		gpsLatitudeText = new JTextField(10);
		gpsLatitudeText.setText("" + model.getLatitude());

		gpsAltitudeText = new JTextField(10);
		gpsAltitudeText.setText("" + model.getAltitude());
		gpsButton = new JButton("Send GPS");
	}

	/**
	 * Get Longitude from TextField.
	 * 
	 * @return longitude, float longitude
	 */
	public float getLongitude() {
		String s = gpsLongitudeText.getText();
		float longitude = 0;
		try {
			longitude = Float.parseFloat(s);
		} catch (NumberFormatException e) {
		}
		return longitude;
	}

	/**
	 * Get Latitude for TextField.
	 * 
	 * @return latitude, float latitude
	 */
	public float getLatitude() {
		String s = gpsLatitudeText.getText();
		float latitude = 0;
		try {
			latitude = Float.parseFloat(s);
		} catch (NumberFormatException e) {
		}
		return latitude;
	}

	/**
	 * Get Altitude from TextField.
	 * 
	 * @return altitude, float altitude
	 */
	public float getAltitude() {
		String s = gpsAltitudeText.getText();
		float altitude = 0;
		try {
			altitude = Float.parseFloat(s);
		} catch (NumberFormatException e) {
		}
		return altitude;
	}

	/**
	 * Get LIS Name
	 * 
	 * @return name, String lis name
	 */
	public String getLisName() {
		String name = lisName.getText();
		return name;
	}

	/**
	 * Method used by our LIS emulator to write current Longitude and Latitude
	 * to simulator's TextFields.
	 * 
	 * @param Longitude
	 *            , Double Longitude
	 * @param Latitude
	 *            , Double Latitude
	 */
	public void setGPS(Double Longitude, Double Latitude) {
		gpsLongitudeText.setText(Longitude.toString());
		gpsLatitudeText.setText(Latitude.toString());
	}

	public JButton getGpsButton() {
		return gpsButton;
	}

	public JPanel getPanel() {
		JPanel gpsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints layout = new GridBagConstraints();
		gpsPanel.setBorder(BorderFactory.createTitledBorder("GPS"));
		JLabel gpsLongitudeLabel = new JLabel("GPS Longitude: ", JLabel.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;
		gpsPanel.add(gpsLongitudeLabel, layout);

		layout.gridx = 1;
		gpsPanel.add(gpsLongitudeText, layout);

		JLabel gpsLongitudeUnitLabel = new JLabel("degress", JLabel.LEFT);
		layout.gridx = 2;
		gpsPanel.add(gpsLongitudeUnitLabel, layout);

		JLabel gpsLatitudeLabel = new JLabel("GPS Latitude: ", JLabel.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;
		gpsPanel.add(gpsLatitudeLabel, layout);

		layout.gridx = 1;
		gpsPanel.add(gpsLatitudeText, layout);

		JLabel gpsLatitudeUnitLabel = new JLabel("degress", JLabel.LEFT);
		layout.gridx = 2;
		gpsPanel.add(gpsLatitudeUnitLabel, layout);

		JLabel gpsAltitudeLabel = new JLabel("GPS Altitude: ", JLabel.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;
		gpsPanel.add(gpsAltitudeLabel, layout);

		layout.gridx = 1;
		gpsPanel.add(gpsAltitudeText, layout);

		JLabel gpsAltitudeUnitLabel = new JLabel("meters", JLabel.LEFT);
		layout.gridx = 2;
		gpsPanel.add(gpsAltitudeUnitLabel, layout);

		JLabel gpsLisNameLabel = new JLabel("LIS name: ", JLabel.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;
		gpsPanel.add(gpsLisNameLabel, layout);

		lisName = new JTextField(10);
		layout.gridx = 1;
		gpsPanel.add(lisName, layout);

		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;

		gpsPanel.add(gpsButton, layout);
		return gpsPanel;
	}
}
