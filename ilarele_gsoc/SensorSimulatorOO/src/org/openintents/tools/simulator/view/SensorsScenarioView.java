package org.openintents.tools.simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.controller.StateControllerBig;
import org.openintents.tools.simulator.controller.StateControllerSmall;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;

public class SensorsScenarioView extends JPanel {
	private static final long serialVersionUID = -5566737606706780206L;
	private SensorsScenarioModel mModel;
	private JButton mCreateNewBtn;
	private JButton mLoadBtn;
	private JButton mRecordBtn;
	private JButton mSaveBtn;
	private JButton mPlayBtn;
	private JCheckBox mLoop;
	private JButton mStopBtn;
	private JButton mPauseBtn;
	private ArrayList<StateViewSmall> mStatesViewSmall;
	private JPanel mScenarioPanel;
	private JPanel mRightPanel;
	private StateViewBig mCurrentBigView;
	private JFormattedTextField mStartState;
	private JFormattedTextField mStopState;

	public SensorsScenarioView(SensorsScenarioModel model) {
		this.mModel = model;
		mStatesViewSmall = new ArrayList<StateViewSmall>();
		fillLayout();
	}

	private void fillLayout() {
		setLayout(new GridLayout());
		JPanel leftPanel = fillLeftPanel();
		JPanel rightPanel = fillRightPanel();

		JSplitPane splitPaneHorizontal = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPaneHorizontal.setResizeWeight(Global.SCENARIO_SPLIT_LEFT);
		add(splitPaneHorizontal);
	}

	private JPanel fillRightPanel() {
		mRightPanel = new JPanel(new BorderLayout());
		return mRightPanel;
	}

	private JPanel fillLeftPanel() {
		SpringLayout layout = new SpringLayout();
		JPanel leftPanel = new JPanel(layout);
		// fill component panels
		JPanel topButtons = fillTopButtonsPanel();
		JPanel scenario = fillScenarioPanel();
		JPanel flowControl = fillFlowControlPanel();

		leftPanel.add(topButtons);

		JScrollPane scrollScenario = new JScrollPane(scenario);
		scrollScenario.setPreferredSize(new Dimension((int) (Global.WIDTH
				* Global.SENSOR_SPLIT_RIGHT * Global.SCENARIO_SPLIT_LEFT),
				(int) (Global.HEIGHT * Global.SENSOR_SPLIT_UP) - 70));
		leftPanel.add(scrollScenario);

		leftPanel.add(flowControl);

		// set layout constraints
		// north + south
		layout.putConstraint(SpringLayout.NORTH, topButtons, 10,
				SpringLayout.NORTH, leftPanel);
		layout.putConstraint(SpringLayout.NORTH, scrollScenario, 10,
				SpringLayout.SOUTH, topButtons);
		layout.putConstraint(SpringLayout.NORTH, flowControl, 10,
				SpringLayout.SOUTH, scrollScenario);
		layout.putConstraint(SpringLayout.SOUTH, leftPanel, 10,
				SpringLayout.SOUTH, flowControl);

		// east + west
		layout.putConstraint(SpringLayout.WEST, topButtons, 10,
				SpringLayout.WEST, leftPanel);
		layout.putConstraint(SpringLayout.WEST, scrollScenario, 10,
				SpringLayout.WEST, leftPanel);
		layout.putConstraint(SpringLayout.WEST, flowControl, 10,
				SpringLayout.WEST, leftPanel);

		layout.putConstraint(SpringLayout.EAST, topButtons, -10,
				SpringLayout.EAST, leftPanel);
		layout.putConstraint(SpringLayout.EAST, scrollScenario, -10,
				SpringLayout.EAST, leftPanel);
		layout.putConstraint(SpringLayout.EAST, flowControl, -10,
				SpringLayout.EAST, leftPanel);
		return leftPanel;
	}

	private JPanel fillFlowControlPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		// start/stop frames + loop
		JPanel settingsPanel = new JPanel(new GridLayout(1, 0));

		JLabel startLabel = new JLabel("Start state");
		mStartState = new JFormattedTextField();
		mStartState.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		mStartState.setValue(1);
		JLabel stopLabel = new JLabel("Stop state");
		mStopState = new JFormattedTextField();
		mStopState.setValue(1);
		settingsPanel.add(startLabel);
		settingsPanel.add(mStartState);
		settingsPanel.add(stopLabel);
		settingsPanel.add(mStopState);
		mLoop = new JCheckBox("Loop");
		settingsPanel.add(mLoop);

		panel.add(settingsPanel);

		// play/pause/stop
		JPanel playPanel = new JPanel(new GridLayout(1, 0));
		mPlayBtn = new JButton("Play");
		playPanel.add(mPlayBtn);
		mPauseBtn = new JButton("Pause");
		playPanel.add(mPauseBtn);
		mStopBtn = new JButton("Stop");
		playPanel.add(mStopBtn);

		panel.add(playPanel);

		return panel;
	}

	private JPanel fillScenarioPanel() {
		mScenarioPanel = new JPanel(new GridLayout(0, 2));
		refreshStates();
		return mScenarioPanel;
	}

	private JPanel fillTopButtonsPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 0));
		mCreateNewBtn = new JButton("Create");
		mCreateNewBtn.setToolTipText("Create Scenario");
		mLoadBtn = new JButton("Load");
		mLoadBtn.setToolTipText("Load Scenario");
		mRecordBtn = new JButton("Record");
		mRecordBtn.setToolTipText("Record Scenario");
		mSaveBtn = new JButton("Save");
		mSaveBtn.setToolTipText("Save Scenario");
		panel.add(mCreateNewBtn);
		panel.add(mLoadBtn);
		panel.add(mRecordBtn);
		panel.add(mSaveBtn);
		return panel;
	}

	public JPanel getHelpWindow() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
				BorderFactory.createTitledBorder(
						BorderFactory.createEmptyBorder(3, 0, 15, 0), "Info")));
		panel.add(new JLabel("- start this application :D"));
		panel.add(new JLabel(
				"- start the android application from the device and"
						+ " follow the instructions from there"));
		panel.add(new JLabel("for now, check syso"));
		return panel;
	}

	public JButton getCreateButton() {
		return mCreateNewBtn;
	}

	public JButton getLoadButton() {
		return mLoadBtn;
	}

	public JButton getRecordButton() {
		return mRecordBtn;
	}

	public JButton getSaveButton() {
		return mSaveBtn;
	}

	public void refreshStates() {
		mStatesViewSmall.clear();
		mScenarioPanel.removeAll();
		ArrayList<StateModel> models = mModel.getStates();
		if (models != null) {
			for (StateModel stateModel : models) {
				addView(stateModel);
			}
		}
	}

	public void removeFromGui(StateViewSmall view) {
		view.setVisible(false);
		mStatesViewSmall.remove(view);
		mScenarioPanel.remove(view);
		if (mCurrentBigView != null) {
			mCurrentBigView.setVisible(false);
			mCurrentBigView = null;
		}
	}

	public void addView(StateModel stateModel) {
		StateViewSmall stateView = new StateViewSmall(stateModel);
		new StateControllerSmall(mModel, this, stateModel, stateView);
		mStatesViewSmall.add(stateView);
		mScenarioPanel.add(stateView);
	}

	public void showBigView(StateModel model, StateViewSmall smallView) {
		mCurrentBigView = new StateViewBig(model, smallView);
		new StateControllerBig(mModel, this, model, mCurrentBigView);
		mRightPanel.removeAll();
		mRightPanel.add(mCurrentBigView, BorderLayout.CENTER);
		repaint();
	}

	public JButton getPlayButton() {
		return mPlayBtn;
	}

	public JButton getStopButton() {
		return mStopBtn;
	}

	public JButton getPauseButton() {
		return mPauseBtn;
	}

	public JFormattedTextField getStartStateTxt() {
		return mStartState;
	}

	public JFormattedTextField getStopStateTxt() {
		return mStopState;
	}

	public boolean isLooping() {
		return mLoop.isSelected();
	}

}
