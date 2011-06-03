package org.openintents.tools.simulator.view.telnet.addons;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.telnet.addons.GPSAddonModel;

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

	public void fillPane(JPanel gpsPanel, GridBagConstraints c3) {
		JLabel gpsLongitudeLabel = new JLabel("GPS Longitude: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		gpsPanel.add(gpsLongitudeLabel, c3);

		c3.gridx = 1;
		gpsPanel.add(gpsLongitudeText, c3);

		JLabel gpsLongitudeUnitLabel = new JLabel("degress", JLabel.LEFT);
		c3.gridx = 2;
		gpsPanel.add(gpsLongitudeUnitLabel, c3);

		JLabel gpsLatitudeLabel = new JLabel("GPS Latitude: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		gpsPanel.add(gpsLatitudeLabel, c3);

		c3.gridx = 1;
		gpsPanel.add(gpsLatitudeText, c3);

		JLabel gpsLatitudeUnitLabel = new JLabel("degress", JLabel.LEFT);
		c3.gridx = 2;
		gpsPanel.add(gpsLatitudeUnitLabel, c3);

		JLabel gpsAltitudeLabel = new JLabel("GPS Altitude: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		gpsPanel.add(gpsAltitudeLabel, c3);

		c3.gridx = 1;
		gpsPanel.add(gpsAltitudeText, c3);

		JLabel gpsAltitudeUnitLabel = new JLabel("meters", JLabel.LEFT);
		c3.gridx = 2;
		gpsPanel.add(gpsAltitudeUnitLabel, c3);

		JLabel gpsLisNameLabel = new JLabel("LIS name: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		gpsPanel.add(gpsLisNameLabel, c3);

		lisName = new JTextField(10);
		c3.gridx = 1;
		gpsPanel.add(lisName, c3);

		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;

		gpsPanel.add(gpsButton, c3);

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
}
