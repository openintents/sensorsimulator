package com.openintents.sensorsimulator.testlibrary;

import java.util.Queue;

public interface SequenceHandler {

	public void handle(Queue<SensorEvent> mSensorEvents);

}
