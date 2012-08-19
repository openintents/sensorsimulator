/*
 * Copyright (C) 2011 OpenIntents.org
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

package org.openintents.tools.simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.controller.StateControllerBig;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.view.gui.util.TimeScrollBar;

/**
 * SensorsScenarioView is represented by the "Scenario Simulator" tab.
 * 
 * It contains:
 * 		a detailed view for the selected state for editing
 * 		the scenario items in a grid
 * 		a playbar that covers data regarding dynamic state of the scenario
 * 		buttons for control the actions over the scenario (create, load, save, record)
 *   
 * @author ilarele
 *
 */
public class SensorsScenarioView extends JPanel {
	private static final long serialVersionUID = -5566737606706780206L;
	// related model (model-view-controller)
	private SensorsScenarioModel mModel;

	// view components
	private JButton mCreateNewBtn;
	private JButton mLoadBtn;
	private JButton mRecordBtn;
	private JButton mSaveBtn;
	private JButton mPlayBtn;
	private JButton mStopBtn;
	private JCheckBox mLoop;

	private JPanel mScenarioPanel;
	private JPanel mRightPanel;

	private TimeScrollBar mTimeBar;
	private JScrollBar mHScrollBar;

	private StateViewBig mCurrentBigView;
	private StateViewSmall mCurrentSmallView;

	public SensorsScenarioView(SensorsScenarioModel model) {
		mModel = model;
		fillLayout();
	}

	// layout initialization

	private void fillLayout() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel topButtons = fillTopButtonsPanel();
		topButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(topButtons);

		add(new JSeparator());

		JPanel detailedViewPanel = fillDetailedViewPanel();
		detailedViewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(detailedViewPanel);
		add(new JSeparator());

		JScrollPane scenarioGridScroll = fillScenarioGridPanel();
		add(scenarioGridScroll);

		add(new JSeparator());

		TimeScrollBar timeBar = new TimeScrollBar(mModel);
		timeBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(timeBar);
		mTimeBar = timeBar;

		JPanel flowControlPanel = fillFlowControlPanel();
		flowControlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(flowControlPanel);
	}

	private JPanel fillDetailedViewPanel() {
		mRightPanel = new JPanel(new BorderLayout());
		return mRightPanel;
	}

	private JPanel fillFlowControlPanel() {
		JPanel panel = new JPanel();
		mPlayBtn = new JButton(Global.ICON_PLAY_PAUSE);
		mStopBtn = new JButton(Global.ICON_STOP);
		mLoop = new JCheckBox("Loop");
		panel.add(mPlayBtn);
		panel.add(mStopBtn);
		panel.add(mLoop);
		return panel;
	}

	private JScrollPane fillScenarioGridPanel() {
		mScenarioPanel = new JPanel(new GridLayout(1, 0));
		JScrollPane scrollScenario = new JScrollPane(mScenarioPanel);
		scrollScenario
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollScenario.setPreferredSize(new Dimension(
				(int) (Global.W_FRAME * Global.SENSOR_SPLIT_RIGHT),
				(int) (Global.H_DEVICE_SMALL * 1.5)));
		scrollScenario.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollScenario.setWheelScrollingEnabled(true);
		mHScrollBar = scrollScenario.getHorizontalScrollBar();
		mHScrollBar.setUnitIncrement(Global.W_DEVICE_SMALL / 2);
		mHScrollBar.setBlockIncrement(Global.W_DEVICE_SMALL / 2);
		return scrollScenario;
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
		return panel;
	}

	// GUI operation: add/remove/update
	/**
	 * Removes a (small) view from the scenario list.
	 */
	public void removeView(StateViewSmall view) {
		view.setVisible(false);
		// remove from scenario
		int removedPosition = indexOfView(view);
		mScenarioPanel.remove(removedPosition);
		// take care of the big (detailed) view
		if (mCurrentSmallView != null && mCurrentSmallView.equals(view)) {
			mCurrentBigView.setVisible(false);
			mCurrentBigView = null;
			mCurrentSmallView = null;
		}
		// compute the new number of pixels per scenario position
		mTimeBar.scaleNumberOfPixelsPerPosition();
		refresh();
	}

	public void addView(StateViewSmall stateView) {
		addView(mScenarioPanel.getComponents().length, stateView);
	}

	/**
	 * Adds the view to the scenario, compute pixels per position for mTimeBar
	 * and update scroll position to the new added state.
	 * 
	 * @param position
	 *            of the new added view
	 * @param stateView
	 *            the new view to be add in the scenario
	 */
	public void addView(int position, StateViewSmall stateView) {
		mScenarioPanel.add(stateView, position);
		mTimeBar.scaleNumberOfPixelsPerPosition();
		updateScrollPosition();
		refresh();
	}

	/**
	 * Sets the model for the detailed view.
	 * @param model based on which to create the detail view
	 * @param smallView used to know if we should clean the detailed view
	 * when deleting a certain state. 
	 */
	public void showBigView(StateModel model, StateViewSmall smallView) {
		mCurrentBigView = new StateViewBig(model, smallView, this);
		mCurrentSmallView = smallView;
		// TODO: move controller (like StateControllerSmall is instantiate)
		new StateControllerBig(mModel, this, model, mCurrentBigView);
		mRightPanel.removeAll();
		mRightPanel.add(mCurrentBigView, BorderLayout.CENTER);
		mModel.setCurrentPosition(indexOfView(smallView));
		refresh();
	}

	/**
	 * Updates mHScrollBar value to the current state from the model, taking into account
	 * the number of items in the scenario and pixels per position.
	 */
	public void updateScrollPosition() {
		int scenarioPosition = mModel.getCurrentPosition();
		float scrollPxPerPosition = (((float) mHScrollBar.getMaximum() - mHScrollBar
				.getVisibleAmount()) / mModel.getStates().size());
		if (scenarioPosition == 0) {
			scenarioPosition = -1;
		}
		int newScrollValue = (int) ((scenarioPosition + 1) * scrollPxPerPosition);
		mHScrollBar.setValue(newScrollValue);
		refresh();
	}

	/**
	 * 
	 * @param toSearchView
	 * @return The index of the toSearchView in the scenario.
	 */
	public int indexOfView(StateViewSmall toSearchView) {
		Component[] components = mScenarioPanel.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component view = components[i];
			if (view == toSearchView)
				return i;
		}
		return -1;
	}

	public void addTextToScenario(String text) {
		mScenarioPanel.add(new JLabel(text));
	}

	/**
	 * Checks if a state is the current state (Used to highlight the selected -
	 * current - state).
	 * 
	 * @param toCheckState
	 * @return
	 */
	public boolean isCurrentState(StateViewSmall toCheckState) {
		int viewPos = indexOfView(toCheckState);
		if (viewPos != -1)
			return mModel.getCurrentPosition() == viewPos;
		return false;
	}

	public void clearScenario() {
		mScenarioPanel.removeAll();
		mCurrentBigView = null;
		mCurrentSmallView = null;
		refresh();
	}

	public void refresh(JPanel comp) {
		comp.revalidate();
		comp.repaint();
	}

	public void refresh() {
		refresh(this);
	}

	// getters
	public JScrollBar getScenarioHScroll() {
		return mHScrollBar;
	}

	public TimeScrollBar getTimeBar() {
		return mTimeBar;
	}

	public JButton getPlayButton() {
		return mPlayBtn;
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

	public JButton getStopButton() {
		return mStopBtn;
	}

	public boolean isLooping() {
		return mLoop.isSelected();
	}

	public void setAfterRecordScenario() {
		if (mScenarioPanel.getComponents().length > 0
				&& mScenarioPanel.getComponent(0) instanceof JLabel) {
			mScenarioPanel.remove(0);
			refresh();
		}
	}
}
