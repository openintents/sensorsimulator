package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openintents.tools.simulator.model.sensor.sensors.ProximityModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;

public class ProximityView extends SensorView {
	private static final long serialVersionUID = -13895826746028866L;

	public ProximityView(ProximityModel model) {
		super(model);
	}

	// Proximity
	private JTextField mProximityText;
	private JTextField mProximityRangeText;
	private JCheckBox mBinaryProximity;
	private JRadioButton mProximityNear;
	private JRadioButton mProximityFar;
	private ButtonGroup mProximityButtonGroup;

	@Override
	public JPanel fillSensorSettingsPanel() {
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		ProximityModel proxModel = (ProximityModel) model;
		/*
		 * Settings for the proximity in centimetres: Value FAR corresponds to
		 * the maximum value of the proximity. Value NEAR corresponds to any
		 * value less than FAR.
		 */
		JPanel proximityFieldPane = new JPanel(new GridBagLayout());
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.anchor = GridBagConstraints.NORTHWEST;
		c3.gridwidth = 3;
		c3.gridx = 0;
		c3.gridy = 0;

		JLabel label = new JLabel("Proximity: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		proximityFieldPane.add(label, c3);

		mProximityText = new JTextField(5);
		mProximityText.setText("" + proxModel.getProximity());
		c3.gridx = 1;
		mProximityText.setEnabled(false);
		proximityFieldPane.add(mProximityText, c3);

		label = new JLabel(" cm", JLabel.LEFT);
		c3.gridx = 2;
		proximityFieldPane.add(label, c3);

		label = new JLabel("Maximum range: ", JLabel.LEFT);
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		proximityFieldPane.add(label, c3);

		mProximityRangeText = new JTextField(5);
		mProximityRangeText.setText("" + proxModel.getProximityRange());

		/*
		 * On key press, update the proximity text to reflect the value of the
		 * maximum range if the FAR option is selected, otherwise set the
		 * proximity to any random number less than the maximum range.
		 */
		mProximityRangeText.getDocument().addDocumentListener(
				new DocumentListener() {

					public void changedUpdate(DocumentEvent arg0) {
						updateProximityText();
					}

					public void insertUpdate(DocumentEvent arg0) {
						updateProximityText();
					}

					public void removeUpdate(DocumentEvent arg0) {
						updateProximityText();
					}

					public void updateProximityText() {
						if (mProximityFar.isSelected()) {
							mProximityText.setText(mProximityRangeText
									.getText());
						} else {
							Random r = new Random();
							int currentMaximumRange = Integer
									.parseInt(mProximityRangeText.getText());
							int reduction = r.nextInt(currentMaximumRange);
							int randomNearProximity = currentMaximumRange
									- reduction;
							mProximityText.setText(Integer
									.toString(randomNearProximity));
						}
					}

				});

		c3.gridx = 1;
		proximityFieldPane.add(mProximityRangeText, c3);

		label = new JLabel(" cm", JLabel.LEFT);
		c3.gridx = 2;
		proximityFieldPane.add(label, c3);

		mBinaryProximity = new JCheckBox(SensorModel.BINARY_PROXIMITY);
		mBinaryProximity.setSelected(proxModel.isBinary());
		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;
		proximityFieldPane.add(mBinaryProximity, c3);

		mProximityNear = new JRadioButton("NEAR", proxModel.isNear());
		mProximityFar = new JRadioButton("FAR", !proxModel.isNear());
		mProximityButtonGroup = new ButtonGroup();
		mProximityButtonGroup.add(mProximityFar);
		mProximityButtonGroup.add(mProximityNear);

		mProximityNear.setActionCommand("NEAR");
		mProximityFar.setActionCommand("FAR");

		c3.gridwidth = 1;
		c3.gridx = 0;
		c3.gridy++;

		proximityFieldPane.add(mProximityNear, c3);
		c3.gridx++;
		proximityFieldPane.add(mProximityFar, c3);

		// Proximity panel ends

		// Add proximity panel to settings
		c2.gridx = 0;
		c2.gridwidth = 1;
		c2.gridy++;
		resultPanel.add(proximityFieldPane, c2);
		return resultPanel;
	}

	public double getProximity() {
		return getSafeDouble(mProximityText);
	}

}
