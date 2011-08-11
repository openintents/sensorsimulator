package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollBar;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.util.FileExtensionFilter;
import org.openintents.tools.simulator.util.XMLUtil;
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
				mView.refreshStates();
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
				mView.setStartState(0);
				mView.setStopState(size);
				mView.refreshStates();
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

		JButton stopBtn = mView.getStopButton();
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mSensorSimulatorController.switchState(
						SensorSimulatorController.STOP, -1, -1, false);
			}
		});

		JButton playPauseBtn = mView.getPlayButton();
		playPauseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int stop;
				int start;
				try {
					start = mView.getStartState();
					stop = mView.getStopState();
				} catch (Exception e) {
					e.printStackTrace();
					start = 0;
					stop = mModel.getStates().size() - 1;
				}
				switch (mSensorSimulatorController.getState()) {
				case SensorSimulatorController.PAUSE:
					mSensorSimulatorController.switchState(
							SensorSimulatorController.PLAY, start, stop, false);
					break;
				case SensorSimulatorController.PLAY:
					// pause simulation
					mSensorSimulatorController
							.switchState(SensorSimulatorController.PAUSE,
									start, stop, false);
					break;
				default: {
					boolean isLooping = mView.isLooping();
					// take it from settings
					float savingInterval = mSensorSimulatorController
							.getSavingTime();
					// can vary on each playback, take it from settings
					float interpolationInterval = mSensorSimulatorController
							.getInterpolationTime();

					mSensorSimulatorController.switchState(
							SensorSimulatorController.PLAY, start, stop,
							isLooping, savingInterval, interpolationInterval);
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
				int size = mView.getScenarioSize();
				if (size != 0) {
					float scrollPxPerPosition = ((float) hScenarioScroll
							.getMaximum() - hScenarioScroll.getVisibleAmount())
							/ size;

					int crtPosition = (int) (e.getValue() / scrollPxPerPosition);
					mView.setCrtPosition(crtPosition);

				}
			}
		});

	}

	protected void switchRecord(boolean record) {
		if (record) {
			mRecordBtn.setText("Record");
			closeRecordingConnection();
			mSensorSimulatorController.switchState(
					SensorSimulatorController.NORMAL, 0, 0, false);
		} else {
			mRecordBtn.setText("Stop Recording");
			clearOldScenario();
			mSensorSimulatorController.switchState(
					SensorSimulatorController.RECORD, 0, 0, false);
			mView.setStatusText("You can start recording from the application.");
		}
	}

	private void closeRecordingConnection() {
		synchronized (mInStream) {
			try {
				mInStream.close();
			} catch (IOException e) {
				e.printStackTrace();
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

	private void saveLastDir(String string, File file) {
		StringBuffer toWriteBack = new StringBuffer();
		boolean found = false;
		// save file
		Scanner scn;
		try {
			scn = new Scanner(new File(Global.CONFIG_DIR));

			while (scn.hasNext()) {
				String line = scn.nextLine();
				String[] splitedLine = line.split(" ");
				if (splitedLine[0].equals(string)) {
					toWriteBack.append(string + " " + file.getAbsolutePath()
							+ "\n");
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
			toWriteBack.append(string + " " + file.getAbsolutePath() + "\n");
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

	public Hashtable<Integer, float[]> getRecordedSensors() {
		return mRecordedSensors;
	}

}
