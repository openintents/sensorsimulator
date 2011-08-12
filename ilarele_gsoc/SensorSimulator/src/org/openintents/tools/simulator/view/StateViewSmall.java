package org.openintents.tools.simulator.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.StateModel;

public class StateViewSmall extends JPanel {
	private static final long serialVersionUID = 2245603710509545258L;
	private StateModel mModel;
	private JButton mDeleteBtn;
	private JButton mAddBtn;
	private JButton mEditBtn;

	public StateViewSmall(StateModel stateModel) {
		mModel = stateModel;
		fillLayout();
	}

	private void fillLayout() {
		setLayout(new BorderLayout());

		// main panel
		JPanel mainPanel = fillMainPanel();
		add(mainPanel, BorderLayout.CENTER);

		// left buttons
		JPanel btnPanel = fillBtnPanel();
		add(btnPanel, BorderLayout.EAST);
	}

	private JPanel fillBtnPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		mDeleteBtn = new JButton(Global.ICON_DELETE);
		mEditBtn = new JButton(Global.ICON_EDIT);
		mAddBtn = new JButton(Global.ICON_ADD);
		panel.add(mDeleteBtn);
		panel.add(mEditBtn);
		panel.add(mAddBtn);
		return panel;
	}

	private JPanel fillMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		DevicePanel devicePanel = new DevicePanel(Global.W_DEVICE_SMALL,
				Global.H_DEVICE_SMALL, mModel);
		panel.add(devicePanel, BorderLayout.CENTER);
		panel.setMinimumSize(devicePanel.getPreferredSize());
		return panel;
	}

	public JButton getDeleteBtn() {
		return mDeleteBtn;
	}

	public JButton getEditBtn() {
		return mEditBtn;
	}

	public JButton getAddBtn() {
		return mAddBtn;
	}
}
