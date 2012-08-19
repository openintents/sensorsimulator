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
package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollBar;
import javax.swing.filechooser.FileFilter;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.util.Interpolate;
import org.openintents.tools.simulator.util.XMLUtil;
import org.openintents.tools.simulator.view.SensorsScenarioView;
import org.openintents.tools.simulator.view.StateViewSmall;
import org.openintents.tools.simulator.view.gui.util.TimeScrollBar;

/**
 * Scenario controller manage the interaction between scenario model, 
 * scenario view and sensor simulator controller. Also it accepts
 * connections from real devices for recording sensors data and
 * loaded it in the scenario.
 *  
 * @author ilarele
 *
 */
public class SensorsScenarioController {

	private SensorsScenarioView mView;
	private SensorsScenarioModel mModel;
	/**
	 * Needed for communication with sensor simulator: to load
	 * scenario states into the simulator.
	 *  
	 */
	private SensorSimulatorController mSensorSimulatorController;

	private ServerSocket mServerSocket;
	private ObjectInputStream mInStream;

	private Hashtable<Integer, float[]> mRecordedSensors;
	private JButton mRecordBtn;

	private long mScenarioTime = 0;
	private long mScenarioInterpolatedTime = 0;
	private ArrayList<StateModel> mInterpolatedStates = new ArrayList<StateModel>();
	private int mCrtInterpolationPosition;

	private float mSavingTimeInterval = 0.5f;
	private float mInterpolationTimeInterval = 0.1f;
	private int mNumberOfIntermediarStates;

	protected boolean mJustAdded = false;
	private TimeScrollBar mTimeBar;
	private JButton mPlayPauseBtn;
	private JButton mStopBtn;

	public SensorsScenarioController(SensorsScenarioModel model,
			SensorsScenarioView view) {
		mModel = model;
		mView = view;
		mRecordedSensors = new Hashtable<Integer, float[]>();
	}

	public void setSensorSimulatorController(
			SensorSimulatorController sensorSimulatorController) {
		mSensorSimulatorController = sensorSimulatorController;
		initTopButtonsListeners();
		startListening();
	}

	private void initTopButtonsListeners() {
		JButton createBtn = mView.getCreateButton();
		createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearOldScenario();
				StateModel dummyStateModel = new StateModel(mModel
						.getSensorSimulatorModel());
				mModel.add(dummyStateModel);
				refreshStates();
			}
		});

		JButton loadBtn = mView.getLoadButton();
		loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearOldScenario();

				// show open dialog
				File selectedFile = showOpenDialog();
				// load scenario from xml
				if (selectedFile != null) {
					XMLUtil.loadScenarioFromXml(selectedFile, mModel);
				}

				int size = mModel.getStates().size();
				mModel.setStartState(0);
				mModel.setStopState(size);
				refreshStates();
			}
		});

		JButton saveBtn = mView.getSaveButton();
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// get file
				File file = getSavingFile();
				if (file != null) {
					XMLUtil.saveScenarioToXml(file, mModel);
				}
			}
		});

		mStopBtn = mView.getStopButton();
		mStopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mSensorSimulatorController
						.switchState(SensorSimulatorController.STOP);
			}
		});

		mPlayPauseBtn = mView.getPlayButton();
		mPlayPauseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch (mSensorSimulatorController.getState()) {
				case SensorSimulatorController.PAUSE:
					mSensorSimulatorController
							.switchState(SensorSimulatorController.PLAY);
					break;
				case SensorSimulatorController.PLAY:
					// pause simulation
					mSensorSimulatorController
							.switchState(SensorSimulatorController.PAUSE);
					break;
				default: {
					mSensorSimulatorController
							.switchState(SensorSimulatorController.PLAY);
				}
					break;
				}
			}
		});

		mRecordBtn = mView.getRecordButton();
		mRecordBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				switchRecord(mSensorSimulatorController.getState() == SensorSimulatorController.RECORD);
			}
		});

		final JScrollBar hScenarioScroll = mView.getScenarioHScroll();
		hScenarioScroll.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {

				int size = mModel.getStates().size();
				if (size != 0) {
					int scrollPxPerPosition = (int) (((float) hScenarioScroll
							.getMaximum() - hScenarioScroll.getVisibleAmount()) / size);
					if (scrollPxPerPosition != 0) {
						int crtPosition = (e.getValue() / scrollPxPerPosition);
						if (!mJustAdded
								|| mModel.getCurrentPosition() == crtPosition - 1) {
							crtPosition--;
							setCurrentPosition(crtPosition);
						} else {
							hScenarioScroll.setValue((mModel
									.getCurrentPosition() + 1)
									* scrollPxPerPosition);
							mJustAdded = false;
						}
						mView.refresh();
					}
				}
			}
		});

		mTimeBar = mView.getTimeBar();
		MouseAdapter mouseTimeBarListener = new MouseAdapter() {
			private boolean mIsDragStop = false;
			private boolean mIsDragStart = false;
			private boolean mIsDragCursor = false;

			@Override
			public void mouseReleased(MouseEvent e) {
				int x = e.getX();
				int scenarioPosition = (int) ((x - TimeScrollBar.TIME_SCROLL_W_MARGIN) / mTimeBar
						.getPxPerPos());
				if (mIsDragCursor) {
					setCurrentPosition(scenarioPosition);
				} else if (mIsDragStop) {
					mModel.setStopState(scenarioPosition);
				} else if (mIsDragStart) {
					mModel.setStartState(scenarioPosition);
				} else {
					if (x > TimeScrollBar.TIME_SCROLL_W_MARGIN) {
						setCurrentPosition(scenarioPosition);
					} else {
						setCurrentPosition(0);
					}
				}

				mTimeBar.unsetDrag();
				mIsDragCursor = mIsDragStart = mIsDragStop = false;
				mView.refresh();
				super.mouseReleased(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (mTimeBar.isDragStop(e)) {
					mIsDragStop = true;
				} else if (mTimeBar.isDragStart(e)) {
					mIsDragStart = true;
				} else if (mTimeBar.isDragCursor(e)) {
					mIsDragCursor = true;
				}
				super.mousePressed(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (mIsDragCursor) {
					mTimeBar.setDragCursor(e.getX());
					mView.refresh();
				} else if (mIsDragStart) {
					mTimeBar.setDragStartArrow(e.getX());
					mView.refresh();
				} else if (mIsDragStop) {
					mTimeBar.setDragStopArrow(e.getX());
					mView.refresh();
				}
				super.mouseDragged(e);
			}
		};

		mTimeBar.addMouseMotionListener(mouseTimeBarListener);
		mTimeBar.addMouseListener(mouseTimeBarListener);

		refreshStates();
	}

	public void refreshStates() {
		mView.clearScenario();
		Vector<StateModel> models = mModel.getStates();
		if (models != null) {
			for (StateModel stateModel : models) {
				StateViewSmall stateView = new StateViewSmall(stateModel, mView);
				new StateControllerSmall(this, stateModel, stateView);
				mView.addView(stateView);
			}
		}
		mTimeBar.scaleNumberOfPixelsPerPosition();
	}

	protected void setCurrentPosition(int position) {
		mModel.setCurrentPosition(position);
		mView.updateScrollPosition();
	}

	protected void switchRecord(boolean stopRecord) {
		if (stopRecord) {
			mRecordBtn.setText("Record");
			closeRecordingConnection();
			mSensorSimulatorController
					.switchState(SensorSimulatorController.NORMAL);
			mPlayPauseBtn.setEnabled(true);
			mStopBtn.setEnabled(true);
			mView.setAfterRecordScenario();
		} else {
			mRecordBtn.setText("Stop Recording");
			clearOldScenario();
			mSensorSimulatorController
					.switchState(SensorSimulatorController.RECORD);
			mView.addTextToScenario("You can start recording from the application.");
			mPlayPauseBtn.setEnabled(false);
			mStopBtn.setEnabled(false);
		}
	}

	private void closeRecordingConnection() {
		if (mInStream != null) {
			synchronized (mInStream) {
				try {
					mInStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void clearOldScenario() {
		mView.clearScenario();
		mModel.emptyStates();
		mRecordedSensors.clear();
	}

	protected File getSavingFile() {
		// read last dir from config file
		File lastDir = getConfigDir("save");
		JFileChooser fc;
		if (lastDir != null) {
			fc = new JFileChooser(lastDir);
		} else {
			fc = new JFileChooser();
		}
		int result = fc.showSaveDialog(mView);
		if (JFileChooser.APPROVE_OPTION == result) {
			File file = fc.getSelectedFile();
			if (file.getPath().endsWith(".xml")) {
				saveLastDir("save", file);
				return file;
			}
			file = new File(file.getAbsolutePath() + ".xml");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			saveLastDir("save", file);
			return file;
		}
		return null;
	}

	/**
	 * Saves last directory for an action in the configuration file.
	 * @param action can be save or load
	 * @param file
	 */
	private void saveLastDir(String action, File file) {
		StringBuffer toWriteBack = new StringBuffer();
		boolean found = false;
		// save file
		Scanner scn;
		try {
			scn = new Scanner(new File(Global.CONFIG_DIR));

			while (scn.hasNext()) {
				String line = scn.nextLine();
				String[] splitedLine = line.split(" ");
				if (splitedLine[0].equals(action)) {
					toWriteBack.append(action + " " + file.getParent() + "\n");
					found = true;
				} else {
					toWriteBack.append(line + "\n");
				}
			}
			scn.close();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		if (!found) {
			toWriteBack.append(action + " " + file.getAbsolutePath() + "\n");
		}

		// overwrite file
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(Global.CONFIG_DIR);
			out = new BufferedWriter(fstream);
			out.write(toWriteBack.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

	private File getConfigDir(String string) {
		Scanner scn = null;
		try {
			scn = new Scanner(new File(Global.CONFIG_DIR));

			while (scn.hasNext()) {
				String[] line = scn.nextLine().split(" ");
				if (line[0].equals(string))
					return new File(line[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scn != null) {
				scn.close();
			}
		}
		return null;
	}

	protected File showOpenDialog() {
		File lastDir = getConfigDir("load");
		JFileChooser fc;
		if (lastDir != null) {
			fc = new JFileChooser(lastDir);
		} else {
			fc = new JFileChooser();
		}
		FileExtensionFilter filter = new FileExtensionFilter(".xml");
		fc.addChoosableFileFilter(filter);
		int result = fc.showOpenDialog(mView);
		if (JFileChooser.APPROVE_OPTION == result) {
			File file = fc.getSelectedFile();
			saveLastDir("load", file);
			return file;
		}
		return null;
	}

	/**
	 * This method starts the server for for recording sensors data 
	 * from real device.
	 * A received message consists in sensor type and its values.
	 * The received data goes to mRecordedSensors HashTable from where is read periodically.
	 * 
	 * Note: Depending on the reading time (can be set in Settings->Saving Time),
	 * a sensor may send more values before the simulator saves one. In this case, the last
	 * sent value is saved.
	 */
	private void startListening() {
		try {
			mServerSocket = new ServerSocket(Global.RECORDING_PORT);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Socket connection;
					while (true) {
						try {
							connection = mServerSocket.accept();
							// switch to recording view
							switchRecord(false);
							mView.clearScenario();
							mInStream = new ObjectInputStream(connection
									.getInputStream());
							while (true) {
								Integer sensorType = (Integer) mInStream
										.readObject();
								float[] values = (float[]) mInStream
										.readObject();

								// System.out.println("received:" + sensorType
								// + ":" + values[0] + " " + values[1]
								// + " " + values[2]);
								mRecordedSensors.put(sensorType, values);
							}
						} catch (IOException e) {
							System.out.println("Connection closed!");
							switchRecord(true);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} finally {
							closeRecordingConnection();
						}
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			mServerSocket.close();
			mInStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Needs to be called when entering PLAY state from
	 * SensorSimulatorController.
	 * Sets the number of intermediary states in interpolation for
	 * the next playback. 
	 */
	public void prepareToPlay() {
		setCurrentPosition(mModel.getStartPosition());
		float interpolationTime = mSensorSimulatorController
				.getInterpolationTime();
		if (interpolationTime == 0) {
			interpolationTime = 0.1f;
		}
		mNumberOfIntermediarStates = (int) (mSensorSimulatorController
				.getSavingTime() / interpolationTime) - 1;
		if (mNumberOfIntermediarStates < 0) {
			mNumberOfIntermediarStates = 0;
		}
		mScenarioTime = System.currentTimeMillis();
		mScenarioInterpolatedTime = System.currentTimeMillis();
	}

	/**
	 * It is called from SensorSimulatorController on each update loop if
	 * the sensor simulator state need to be taken from the scenario (not 
	 * computed from sensor parameters from sensor simulator GUI).
	 * 
	 * @param state PLAY, RECORD, PAUSE, STOP
	 */
	public void doTime(int state) {
		switch (state) {
		case SensorSimulatorController.PLAY: {
			float limitMainStates = mSavingTimeInterval * Global.MS_IN_SECOND;
			float limitInterpolatedStates = mInterpolationTimeInterval
					* Global.MS_IN_SECOND;
			float passedScenarioTime = System.currentTimeMillis()
					- mScenarioTime;
			float passedInterpolatedTime = System.currentTimeMillis()
					- mScenarioInterpolatedTime;
			// check first if we should go to the next main state
			if (passedScenarioTime > limitMainStates) {
				// get next main state
				int crtPosition = mModel.getCurrentPosition();
				StateModel crtState = mModel.getState(crtPosition);
				if (crtState != null) {
					// load the next state in the model
					mSensorSimulatorController.loadStateIntoTheModel(crtState,
							crtPosition);
					mView.refresh();
				}

				// check if we should stop
				if (crtPosition == mModel.getStopPosition()) {
					if (!isLooping()) {
						mSensorSimulatorController
								.switchState(SensorSimulatorController.NORMAL);
						break;
					} else {
						crtPosition = mModel.getStartPosition();
						setCurrentPosition(crtPosition);
					}
				} else {
					// there are more states => generate interpolated states
					StateModel nextState = mModel.getState(crtPosition + 1);
					mInterpolatedStates = Interpolate.getIntermediateStates(
							crtState, nextState, mNumberOfIntermediarStates);
					mCrtInterpolationPosition = 0;
					setCurrentPosition(crtPosition + 1);
				}

				// update time
				mScenarioTime = System.currentTimeMillis();
			} else if (passedInterpolatedTime > limitInterpolatedStates
					&& mCrtInterpolationPosition < mInterpolatedStates.size()) {
				// if we should go to the next secondary state
				StateModel interpolatedState = mInterpolatedStates
						.get(mCrtInterpolationPosition);
				mSensorSimulatorController
						.loadStateIntoTheModel(interpolatedState);

				mScenarioInterpolatedTime = System.currentTimeMillis();
				mCrtInterpolationPosition++;
			}
		}
			break;
		case SensorSimulatorController.STOP: {
			setCurrentPosition(mModel.getStartPosition());
			mSensorSimulatorController
					.switchState(SensorSimulatorController.NORMAL);
			mView.refresh();
		}
			break;
		case SensorSimulatorController.PAUSE: {
			// mScenarioTime = System.currentTimeMillis();
		}
			break;
		case SensorSimulatorController.RECORD: {
			// if it is time for the next state from recording
			if (mSavingTimeInterval * Global.MS_IN_SECOND < (System
					.currentTimeMillis() - mScenarioTime)) {
				// enable recorded sensors in the simulator
				// (if they aren't yet)
				enableSensors(mRecordedSensors);
				// get next state
				StateModel nextState = StateModel
						.getStateFromRecordedSensors(mRecordedSensors);
				if (nextState != null) {
					// load the next state in the model
					mSensorSimulatorController.loadStateIntoTheModel(nextState);

					// add the next state in the scenario
					mModel.add(nextState);
					StateViewSmall stateView = new StateViewSmall(nextState,
							mView);
					new StateControllerSmall(this, nextState, stateView);
					mView.addView(stateView);
				}
				// update time
				mScenarioTime = System.currentTimeMillis();
			}
		}
			break;
		}
	}

	private void enableSensors(Hashtable<Integer, float[]> recordedSensors) {
		for (Entry<Integer, float[]> sensor : recordedSensors.entrySet()) {
			mSensorSimulatorController.enableSensor(sensor.getKey());
		}
	}

	private boolean isLooping() {
		return mView.isLooping();
	}

	public SensorsScenarioView getView() {
		return mView;
	}

	public SensorsScenarioModel getModel() {
		return mModel;
	}

	public void setJustAdded() {
		mJustAdded = true;
	}

	private class FileExtensionFilter extends FileFilter {
		private String mExtension;

		public FileExtensionFilter(String extension) {
			mExtension = extension;
		}

		@Override
		public boolean accept(File file) {
			if (file.isDirectory())
				return true;
			String path = file.getAbsolutePath();
			String extension = mExtension;
			if (path.endsWith(extension)
					&& (path.charAt(path.length() - extension.length()) == '.'))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return mExtension;
		}
	}
}
