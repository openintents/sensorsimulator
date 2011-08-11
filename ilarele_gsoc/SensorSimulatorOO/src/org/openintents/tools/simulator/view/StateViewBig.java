package org.openintents.tools.simulator.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
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
		mModel = stateModel;
		mSmallView = smallView;
		initLayout(stateModel);
	}

	private void initLayout(StateModel stateModel) {
		setLayout(new FlowLayout());

		JPanel leftPanel = new JPanel(new BorderLayout());
		// device view
		mDevicePanel = new DevicePanel(Global.W_DEVICE_BIG,
				Global.H_DEVICE_BIG, stateModel);
		leftPanel.add(mDevicePanel, BorderLayout.CENTER);
		// import current state button
		mImportState = new JButton("Copy Current State", Global.ICON_COPY);
		leftPanel.add(mImportState, BorderLayout.SOUTH);
		add(leftPanel, BorderLayout.CENTER);

		// sensors values
		String values = stateModel.getSensorsValues();
		mSensorsValues = new JTextArea();
		JScrollPane scrollPaneSensorData = new JScrollPane(mSensorsValues);
		scrollPaneSensorData
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		mSensorsValues.setText(values);
		add(mSensorsValues, BorderLayout.EAST);

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
