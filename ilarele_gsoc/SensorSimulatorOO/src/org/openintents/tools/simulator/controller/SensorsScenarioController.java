package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.util.FileExtensionFilter;
import org.openintents.tools.simulator.view.SensorsScenarioView;

public class SensorsScenarioController {

	private SensorsScenarioView mView;
	private SensorsScenarioModel mModel;
	private SensorSimulatorController mSensorSimulatorController;

	private ServerSocket mServerSocket;
	private ObjectInputStream mInStream;

	private Hashtable<Integer, float[]> mRecordedSensors;
	private JButton mRecordBtn;

	public SensorsScenarioController(SensorsScenarioModel model,
			SensorsScenarioView view) {
		this.mModel = model;
		this.mView = view;
		mRecordedSensors = new Hashtable<Integer, float[]>();
	}

	public void setSensorSimulatorController(
			SensorSimulatorController sensorSimulatorController) {
		this.mSensorSimulatorController = sensorSimulatorController;
		initTopButtonsListeners();
		startListening();
	}

	private void initTopButtonsListeners() {
		JButton createBtn = mView.getCreateButton();
		createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mModel.emptyStates();
				StateModel dummyStateModel = new StateModel(mModel
						.getSensorSimulatorModel());
				mModel.add(dummyStateModel);
				mView.refreshStates();
			}
		});
		final JFormattedTextField startState = mView.getStartStateTxt();
		final JFormattedTextField stopState = mView.getStopStateTxt();

		JButton loadBtn = mView.getLoadButton();
		loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mModel.emptyStates();

				// show open dialog
				File selectedFile = showOpenDialog();
				// File selectedFile = new
				// File("interpolationFiles/save.ss.xml");
				// load scenario from xml
				if (selectedFile != null)
					XMLUtil.loadScenarioFromXml(selectedFile, mModel);

				int size = mModel.getStates().size();
				if (size > 0) {
					startState.setValue(1);
				} else
					startState.setValue(0);
				stopState.setValue(size);
				mView.refreshStates();
			}
		});

		JButton saveBtn = mView.getSaveButton();
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// get file
				File file = getSavingFile();
				// File file = new File("interpolationFiles/save.ss.xml");
				if (file != null) {
					XMLUtil.saveScenarioToXml(file, mModel);
				}
			}
		});

		JButton playBtn = mView.getPlayButton();
		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int stop;
				int start;
				try {
					start = Integer.parseInt(startState.getValue().toString()) - 1;
					stop = Integer.parseInt(stopState.getValue().toString()) - 1;
				} catch (Exception e) {
					e.printStackTrace();
					start = 0;
					stop = mModel.getStates().size() - 1;
				}
				boolean isLooping = mView.isLooping();
				mSensorSimulatorController.switchState(
						SensorSimulatorController.PLAY, start, stop, isLooping);
			}
		});
		JButton pauseBtn = mView.getPauseButton();
		pauseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mSensorSimulatorController.switchState(
						SensorSimulatorController.PAUSE, -1, -1, false);
			}
		});
		JButton stopBtn = mView.getStopButton();
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mSensorSimulatorController.switchState(
						SensorSimulatorController.STOP, -1, -1, false);
			}
		});

		mRecordBtn = mView.getRecordButton();
		mRecordBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				switchRecord(mSensorSimulatorController.getState() == SensorSimulatorController.RECORD);
			}
		});

	}

	protected void switchRecord(boolean record) {
		if (record) {
			mRecordBtn.setText("Record");
			mSensorSimulatorController.switchState(
					SensorSimulatorController.NORMAL, -1, -1, false);
		} else {
			mRecordBtn.setText("Stop Recording");
			mModel.emptyStates();
			mSensorSimulatorController.switchState(
					SensorSimulatorController.RECORD, 0, 0, false);
			mView.clearScenario();
			mView.setStatusText("You can start recording from the application.");

		}
	}

	protected File getSavingFile() {
		JFileChooser fc = new JFileChooser();
		int result = fc.showSaveDialog(mView);
		if (JFileChooser.APPROVE_OPTION == result) {
			File file = fc.getSelectedFile();
			if (file.getPath().endsWith(".xml"))
				return file;
			file = new File(file.getAbsolutePath() + ".xml");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			return file;
		}
		return null;
	}

	protected File showOpenDialog() {
		JFileChooser fc = new JFileChooser();
		FileExtensionFilter filter = new FileExtensionFilter(".xml");
		fc.addChoosableFileFilter(filter);
		int result = fc.showOpenDialog(mView);
		if (JFileChooser.APPROVE_OPTION == result)
			return fc.getSelectedFile();
		return null;
	}

	private void startListening() {
		try {
			mServerSocket = new ServerSocket(Global.RECORDING_PORT);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Socket connection;
					while (true) {
						try {
							mRecordedSensors.clear();
							System.out
									.println("Waiting for connection; blocked in accept");
							connection = mServerSocket.accept();
							System.out.println("Connected");
							mView.clearScenario();
							mInStream = new ObjectInputStream(connection
									.getInputStream());
							while (true) {
								Integer sensorType = (Integer) mInStream
										.readObject();
								float[] values = (float[]) mInStream
										.readObject();

								// System.out.println("received:" + sensorType +
								// ":" + values[0]
								// + " " + values[1] + " " + values[2]);
								mRecordedSensors.put(sensorType, values);
							}
						} catch (IOException e) {
							System.out.println("Connection closed!");
							switchRecord(true);

						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} finally {
							try {
								mInStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
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

	public Hashtable<Integer, float[]> getRecordedSensors() {
		return mRecordedSensors;
	}

}
