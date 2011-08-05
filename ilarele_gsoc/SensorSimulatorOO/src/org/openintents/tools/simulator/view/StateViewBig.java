package org.openintents.tools.simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.StateModel;

/**
 * Contains extended view of a state. User can change sensors values by
 * importing the current state. - roll/pitch/yaw/move the device (similar to the
 * default device) - using quick sensor settings - using every sensor specific
 * settings
 * 
 * @author ilarele
 * 
 */
public class StateViewBig extends JPanel {
	private static final long serialVersionUID = 2245403710809545258L;
	private JButton mImportState;
	private DevicePanel mDevicePanel;
	private JTextArea mSensorsValues;
	private StateModel mModel;
	private StateViewSmall mSmallView;

	public StateViewBig(StateModel stateModel, StateViewSmall smallView) {
		this.mModel = stateModel;
		this.mSmallView = smallView;
		initLayout(stateModel);
	}

	private void initLayout(StateModel stateModel) {
		setLayout(new BorderLayout());

		// device view
		mDevicePanel = new DevicePanel(Global.DEVICE_WIDTH_BIG,
				Global.DEVICE_HEIGHT_BIG, stateModel);
		add(mDevicePanel, BorderLayout.CENTER);

		// down panel
		SpringLayout downLayout = new SpringLayout();
		JPanel downPanel = new JPanel(downLayout);
		// sensors values
		String values = stateModel.getSensorsValues();
		mSensorsValues = new JTextArea();
		JScrollPane scrollPaneSensorData = new JScrollPane(mSensorsValues);
		scrollPaneSensorData
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mSensorsValues.setText(values);
		downPanel.add(mSensorsValues);

		// import current state button
		mImportState = new JButton("Import Current State");
		downPanel.add(mImportState);

		downLayout.putConstraint(SpringLayout.WEST, mSensorsValues, 5,
				SpringLayout.WEST, downPanel);
		downLayout.putConstraint(SpringLayout.WEST, mImportState, 5,
				SpringLayout.WEST, downPanel);

		downLayout.putConstraint(SpringLayout.NORTH, mSensorsValues, 5,
				SpringLayout.NORTH, downPanel);
		downLayout.putConstraint(SpringLayout.SOUTH, downPanel, 5,
				SpringLayout.SOUTH, mImportState);
		downLayout.putConstraint(SpringLayout.NORTH, mImportState, 5,
				SpringLayout.SOUTH, mSensorsValues);

		downPanel.setPreferredSize(new Dimension(mSensorsValues
				.getPreferredSize().width,
				mSensorsValues.getPreferredSize().height + 60));
		add(downPanel, BorderLayout.SOUTH);
	}

	public JButton getImportStateButton() {
		return mImportState;
	}

	public void refreshFromModel() {
		mDevicePanel.repaint();
		mSensorsValues.setText(mModel.getSensorsValues());
		mSmallView.repaint();
	}
}
