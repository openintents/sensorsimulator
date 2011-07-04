package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openintents.tools.simulator.model.sensor.sensors.BarcodeReaderModel;
import org.openintents.tools.simulator.model.sensor.sensors.SensorModel;
import org.openintents.tools.simulator.view.sensor.JTextFieldLimit;

public class BarcodeReaderView extends SensorView {

	private static final long serialVersionUID = 2384391181800708327L;

	public BarcodeReaderView(SensorModel model) {
		super(model);
	}

	// Barcode
	private JTextField mBarcodeReaderText;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		// Barcode (13 numbers)
		// Panel for our barcode reader
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Settings"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		GridBagConstraints layout = new GridBagConstraints();
		layout.gridwidth = 3;
		layout.gridx = 0;
		layout.gridy = 0;

		JLabel label = new JLabel("Barcode: ", JLabel.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;
		resultPanel.add(label, layout);

		mBarcodeReaderText = new JTextField(13);
		mBarcodeReaderText.setDocument(new JTextFieldLimit(13));
		mBarcodeReaderText.setText(""
				+ ((BarcodeReaderModel) model).getBarcode());
		layout.gridx = 1;
		resultPanel.add(mBarcodeReaderText, layout);

		resultPanel.add(label, layout);
		return resultPanel;
	}

	public String getBarcode() {
		return mBarcodeReaderText.getText();
	}

	@Override
	protected JPanel getSensorSpecificHelp() {
		// TODO Auto-generated method stub
		return null;
	}
}