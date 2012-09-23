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

package org.openintents.tools.simulator.view.sensor.sensors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openintents.tools.simulator.model.sensors.BarcodeReaderModel;
import org.openintents.tools.simulator.model.sensors.SensorModel;
import org.openintents.tools.simulator.util.JTextFieldLimit;

/**
 * BarcodeReaderView keeps the GUI of the BarcodeReader add-on.
 * 
 * @author Peli
 * 
 */
public class BarcodeReaderView extends SensorView {

	private static final long serialVersionUID = 2384391181800708327L;
	private BarcodeReaderModel mBarcodeReaderModel;

	public BarcodeReaderView(BarcodeReaderModel model) {
		super(model);
		mBarcodeReaderModel = model;
		setSensorQuickSettingsPanel();
	}

	// Barcode
	private JTextField mBarcodeReaderText;
	private JPanel mSensorQuickPane;

	@Override
	public JPanel fillSensorSpecificSettingsPanel() {
		// Barcode (13 numbers)
		// Panel for our barcode reader
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameters"),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		GridBagConstraints layout = new GridBagConstraints();
		layout.gridwidth = 3;
		layout.gridx = 0;
		layout.gridy = 0;

		JLabel label = new JLabel("Barcode: ", SwingConstants.LEFT);
		layout.gridwidth = 1;
		layout.gridx = 0;
		layout.gridy++;
		resultPanel.add(label, layout);

		mBarcodeReaderText = new JTextField(13);
		mBarcodeReaderText.setDocument(new JTextFieldLimit(13));
		mBarcodeReaderText.setText(""
				+ ((BarcodeReaderModel) mModel).getBarcode());
		layout.gridx = 1;
		resultPanel.add(mBarcodeReaderText, layout);

		mBarcodeReaderText.getDocument().addDocumentListener(
				new SensorDocumentListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						mBarcodeReaderModel.setBarcode(mBarcodeReaderText.getText());
					}
				}));

		resultPanel.add(label, layout);
		return resultPanel;
	}

	@Override
	protected JPanel getSensorSpecificHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	private void setSensorQuickSettingsPanel() {
		mSensorQuickPane = new JPanel();
	}

	@Override
	public JPanel getQuickSettingsPanel() {
		return mSensorQuickPane;
	}

	@Override
	public void update(Observable o, Object arg) {
		mBarcodeReaderText.setText(mBarcodeReaderModel.getBarcode());
	}
}