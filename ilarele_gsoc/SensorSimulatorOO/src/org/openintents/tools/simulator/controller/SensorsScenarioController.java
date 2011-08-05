package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;

import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.util.FileExtensionFilter;
import org.openintents.tools.simulator.view.SensorsScenarioView;

public class SensorsScenarioController {

	private SensorsScenarioView mView;
	private SensorsScenarioModel mModel;
	private SensorSimulatorController mSensorSimulatorController;

	public SensorsScenarioController(SensorsScenarioModel model,
			SensorsScenarioView view) {
		this.mModel = model;
		this.mView = view;

	}

	public void setSensorSimulatorController(
			SensorSimulatorController sensorSimulatorController) {
		this.mSensorSimulatorController = sensorSimulatorController;
		initTopButtonsListeners();
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
				// File selectedFile = showOpenDialog();
				File selectedFile = new File("interpolationFiles/save.ss.xml");
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
				// File file = getSavingFile();
				File file = new File("interpolationFiles/save.ss.xml");
				if (file != null) {
					XMLUtil.saveScenarioToXml(file, mModel);
					System.out.println("Saved");
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

}
