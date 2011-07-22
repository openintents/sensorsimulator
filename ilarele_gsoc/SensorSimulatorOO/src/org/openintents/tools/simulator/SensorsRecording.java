package org.openintents.tools.simulator;

import org.openintents.tools.simulator.controller.SensorsRecordingController;
import org.openintents.tools.simulator.model.sensor.SensorsRecordingModel;
import org.openintents.tools.simulator.view.sensor.SensorsRecordingView;


public class SensorsRecording {
	public SensorsRecordingModel model = new SensorsRecordingModel(this);
	public SensorsRecordingView view = new SensorsRecordingView(model);
	public SensorsRecordingController controller = new SensorsRecordingController(
			model, view);
}
