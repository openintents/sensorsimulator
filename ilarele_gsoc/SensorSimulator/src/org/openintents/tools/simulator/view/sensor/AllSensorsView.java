package org.openintents.tools.simulator.view.sensor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.view.sensor.sensors.SensorView;

public class AllSensorsView extends JScrollPane {
	private static final long serialVersionUID = -5966939252818311988L;
	private ArrayList<SensorView> mSensors;
	private JComboBox mPhoneChooser;
	private HashMap<String, PhoneSensors> mPhoneSensors;

	public AllSensorsView(ArrayList<SensorView> view) {
		mSensors = view;
		setPreferredSize(new Dimension(
				(int) (Global.W_FRAME * Global.SENSOR_SPLIT_RIGHT),
				Global.H_CONTENT + Global.H_BUTTONS));
		JPanel insidePanel = new JPanel(new GridBagLayout());
		getViewport().add(insidePanel);

		GridBagConstraints l = new GridBagConstraints();
		l.fill = GridBagConstraints.HORIZONTAL;
		l.anchor = GridBagConstraints.NORTHWEST;

		l.gridx = 0;
		l.gridy = 0;
		l.gridwidth = 1;
		l.gridheight = 1;
		mPhoneChooser = getPhoneChooser();
		JPanel chooserPanel = new JPanel();
		chooserPanel.setBorder(BorderFactory
				.createTitledBorder("Choose Device"));
		chooserPanel.add(mPhoneChooser);
		insidePanel.add(chooserPanel, l);

		l.gridwidth = 2;
		l.weightx = 0.5;
		l.gridx = 0;
		l.gridy = 1;
		l.gridheight = 2;
		JPanel mBasicOrientationPanel = getBasicOrientationSensors();
		insidePanel.add(mBasicOrientationPanel, l);

		l.gridx = 0;
		l.gridy += 2;
		l.gridheight = 3;
		JPanel mExtendedOrientationPanel = getExtendedOrientationSensors();
		insidePanel.add(mExtendedOrientationPanel, l);

		l.gridx = 2;
		l.gridy = 1;
		l.gridheight = 3;
		JPanel mEnvironmentPanel = getEnvironmentSensors();
		insidePanel.add(mEnvironmentPanel, l);

		l.gridx = 2;
		l.gridy += 3;
		l.gridheight = 1;
		JPanel mOthersPanel = getOtherSensors();
		insidePanel.add(mOthersPanel, l);
	}

	private JComboBox getPhoneChooser() {
		mPhoneSensors = new HashMap<String, PhoneSensors>();

		// read from file
		Scanner scn;
		try {
			scn = new Scanner(new File(Global.FILE_CONFIG_PHONE));
			scn.useDelimiter(";|\n");
			while (scn.hasNext()) {
				PhoneSensors phone = new PhoneSensors();

				phone.name = scn.next();
				phone.sensors = new ArrayList<String>();
				String[] list = scn.next().split(",");
				for (String sensor : list) {
					phone.sensors.add(sensor.trim());
				}
				mPhoneSensors.put(phone.name, phone);
			}

			JComboBox result = new JComboBox(mPhoneSensors.keySet().toArray());
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	private JPanel getOtherSensors() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		TitledBorder border = BorderFactory.createTitledBorder("Other Sensors");
		result.setBorder(border);

		ArrayList<SensorView> localSensors = mSensors;
		SensorView temperature = localSensors
				.get(SensorModel.POZ_BARCODE_READER);
		result.add(temperature.getEnabled());

		return result;
	}

	private JPanel getEnvironmentSensors() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		TitledBorder border = BorderFactory
				.createTitledBorder("Environment Sensors");
		result.setBorder(border);

		ArrayList<SensorView> localSensors = mSensors;
		SensorView temperature = localSensors.get(SensorModel.POZ_TEMPERATURE);
		result.add(temperature.getEnabled());

		SensorView light = localSensors.get(SensorModel.POZ_LIGHT);
		result.add(light.getEnabled());

		SensorView proximity = localSensors.get(SensorModel.POZ_PROXIMITY);
		result.add(proximity.getEnabled());

		SensorView pressure = localSensors.get(SensorModel.POZ_PRESSURE);
		result.add(pressure.getEnabled());

		return result;
	}

	private JPanel getExtendedOrientationSensors() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		TitledBorder border = BorderFactory
				.createTitledBorder("Extended Orientation");
		result.setBorder(border);

		ArrayList<SensorView> localSensors = mSensors;
		SensorView linearAccelerometer = localSensors
				.get(SensorModel.POZ_LINEAR_ACCELERATION);
		result.add(linearAccelerometer.getEnabled());

		SensorView gravity = localSensors.get(SensorModel.POZ_GRAVITY);
		result.add(gravity.getEnabled());

		SensorView rotation = localSensors.get(SensorModel.POZ_ROTATION);
		result.add(rotation.getEnabled());

		SensorView gyroscope = localSensors.get(SensorModel.POZ_GYROSCOPE);
		result.add(gyroscope.getEnabled());
		return result;
	}

	private JPanel getBasicOrientationSensors() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		TitledBorder border = BorderFactory
				.createTitledBorder("Basic Orientation");
		result.setBorder(border);

		ArrayList<SensorView> localSensors = mSensors;
		SensorView accelerometer = localSensors
				.get(SensorModel.POZ_ACCELEROMETER);
		result.add(accelerometer.getEnabled());

		SensorView magneticField = localSensors
				.get(SensorModel.POZ_MAGNETIC_FIELD);
		result.add(magneticField.getEnabled());

		SensorView orientation = localSensors.get(SensorModel.POZ_ORIENTATION);
		result.add(orientation.getEnabled());

		return result;
	}

	public JComboBox getSensorsComboBox() {
		return mPhoneChooser;
	}

	public HashMap<String, PhoneSensors> getPhoneSensors() {
		return mPhoneSensors;
	}

	public ArrayList<SensorView> getSensorsView() {
		return mSensors;
	}
}
