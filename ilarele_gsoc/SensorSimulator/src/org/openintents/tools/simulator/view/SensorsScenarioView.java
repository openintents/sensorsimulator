package org.openintents.tools.simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
import org.openintents.tools.simulator.controller.StateControllerSmall;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.view.gui.util.TimeScrollBar;

public class SensorsScenarioView extends JPanel {
	private static final long serialVersionUID = -5566737606706780206L;
	private SensorsScenarioModel mModel;
	private JButton mCreateNewBtn;
	private JButton mLoadBtn;
	private JButton mRecordBtn;
	private JButton mSaveBtn;
	private JButton mPlayBtn;
	private JCheckBox mLoop;
	private ArrayList<StateViewSmall> mStatesViewArraySmall;
	private JPanel mScenarioPanel;
	private JPanel mRightPanel;

	private StateViewBig mCurrentBigView;
	private StateViewSmall mCurrentSmallView;

	private TimeScrollBar mTimeBar;
	private JScrollBar mHScrollBar;
	private JButton mStopBtn;

	public SensorsScenarioView(SensorsScenarioModel model) {
		mModel = model;
		mStatesViewArraySmall = new ArrayList<StateViewSmall>();
		fillLayout();
	}

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

		JPanel timeBarPanel = fillTimeBarPanel();
		timeBarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(timeBarPanel);

		JPanel flowControlPanel = fillFlowControlPanel();
		flowControlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(flowControlPanel);

		refreshStates();

	}

	private JPanel fillTimeBarPanel() {
		final TimeScrollBar timeBar = new TimeScrollBar();
		timeBar.addMouseListener(new MouseAdapter() {
			private boolean mIsDragStop = false;
			private boolean mIsDragStart = false;

			@Override
			public void mouseReleased(MouseEvent e) {
				int x = e.getX();
				if (!mIsDragStop) {
					if (!mIsDragStart) {
						if (x > TimeScrollBar.TIME_SCROLL_W_MARGIN) {
							int scenarioPosition = (int) (x / timeBar
									.getPxPerPos());
							setCrtState(scenarioPosition);
						}
					} else {
						mTimeBar.setStartStateAbsolute(x);
						refresh(SensorsScenarioView.this);
					}
				} else {
					mTimeBar.setStopStateAbsolute(x);
					refresh(SensorsScenarioView.this);
				}
				mIsDragStart = mIsDragStop = false;
				super.mouseReleased(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!mTimeBar.isDragStop(e)) {
					if (mTimeBar.isDragStart(e)) {
						mIsDragStart = true;
					}
				} else {
					mIsDragStop = true;
				}

				super.mousePressed(e);
			}
		});
		mTimeBar = timeBar;
		return timeBar;
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
		mHScrollBar = scrollScenario.getHorizontalScrollBar();
		mHScrollBar.setUnitIncrement(Global.W_DEVICE_SMALL / 2);

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

	public void refreshStates() {
		mStatesViewArraySmall.clear();
		mScenarioPanel.removeAll();
		ArrayList<StateModel> models = mModel.getStates();
		if (models != null) {
			for (StateModel stateModel : models) {
				addView(stateModel);
			}
			if (models.size() > 0) {
				mTimeBar.setEndPosition(models.size() - 1);
				refresh();
			}
		}

	}

	public void removeFromGui(StateViewSmall view) {
		view.setVisible(false);
		int removedPosition = mStatesViewArraySmall.indexOf(view);
		mStatesViewArraySmall.remove(removedPosition);
		mScenarioPanel.remove(removedPosition);
		if (mCurrentSmallView != null && mCurrentSmallView.equals(view)) {
			mCurrentBigView.setVisible(false);
			mCurrentBigView = null;
			mCurrentSmallView = null;
		}
		mTimeBar.removePosition(removedPosition);
		refresh();
	}

	public void addView(StateModel stateModel) {
		addView(stateModel, mStatesViewArraySmall.size());
	}

	public int addView(StateViewSmall afterView, StateModel stateModel) {
		int position = mStatesViewArraySmall.indexOf(afterView) + 1;
		addView(stateModel, position);
		return position;
	}

	private void addView(StateModel stateModel, int position) {
		StateViewSmall stateView = new StateViewSmall(stateModel);
		new StateControllerSmall(mModel, this, stateModel, stateView);
		mStatesViewArraySmall.add(position, stateView);
		mScenarioPanel.add(stateView, position);
		if (mStatesViewArraySmall.size() > 1) {
			mTimeBar.incrementNoStates();
		}
		refresh();
	}

	public void showBigView(StateModel model, StateViewSmall smallView) {
		mCurrentBigView = new StateViewBig(model, smallView);
		mCurrentSmallView = smallView;
		new StateControllerBig(mModel, this, model, mCurrentBigView);
		mRightPanel.removeAll();
		mRightPanel.add(mCurrentBigView, BorderLayout.CENTER);
		refresh(mRightPanel);
	}

	public JButton getPlayButton() {
		return mPlayBtn;
	}

	public boolean isLooping() {
		return mLoop.isSelected();
	}

	public void setStatusText(String text) {
		mScenarioPanel.add(new JLabel(text));
	}

	public void clearScenario() {
		mScenarioPanel.removeAll();
		mStatesViewArraySmall.clear();
		mCurrentBigView = null;
		mCurrentSmallView = null;
		mTimeBar.reset();
		refresh();
	}

	public int getStartState() {
		return mTimeBar.getStartState();
	}

	public int getStopState() {
		return mTimeBar.getStopState();
	}

	public void setStartState(int value) {
		mTimeBar.setStartState(value);
		refresh();
	}

	public void setStopState(int value) {
		mTimeBar.setStopState(value, true);
		refresh();
	}

	public void setCrtState(int position) {
		mTimeBar.setCurrentPosition(position);
		updateScrollPosition(position);
		refresh();
	}

	protected void updateScrollPosition(int scenarioPosition) {
		// set scenario scroll bar
		float scrollPxPerPosition = ((float) mHScrollBar.getMaximum() - mHScrollBar
				.getVisibleAmount()) / mStatesViewArraySmall.size();

		int newScrollValue = Math.round(scenarioPosition * scrollPxPerPosition);
		mHScrollBar.setValue(newScrollValue);
	}

	public void refresh(JPanel comp) {
		comp.revalidate();
		comp.repaint();
	}

	public JScrollBar getScenarioHScroll() {
		return mHScrollBar;
	}

	public int getScenarioSize() {
		return mStatesViewArraySmall.size();
	}

	public void setCrtPosition(int crtPosition) {
		mTimeBar.setCurrentPosition(crtPosition);
		refresh();
	}

	public void refresh() {
		refresh(this);
	}
}
