package org.openintents.tools.simulator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.openintents.tools.simulator.util.FileExtensionFilter;
import org.openintents.tools.simulator.view.SensorsScenarioView;

public class SensorsScenarioController {

	private SensorsScenarioView mView;
	private SensorsScenarioModel mModel;

	public SensorsScenarioController(SensorsScenarioModel model,
			SensorsScenarioView view) {
		this.mModel = model;
		this.mView = view;

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

		JButton loadBtn = mView.getLoadButton();
		loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mModel.emptyStates();

				// show open dialog
				File selectedFile = showOpenDialog();
				// File selectedFile = new
				// File("interpolationFiles/load.ss.xml");
				// load scenario from xml
				if (selectedFile != null)
					XMLUtil.loadScenarioFromXml(selectedFile, mModel);

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
					System.out.println("Saved");
				}
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
