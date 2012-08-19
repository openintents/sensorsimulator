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

package org.openintents.tools.simulator.model;

import java.util.Vector;

import org.openintents.tools.simulator.SensorsScenario;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;
import org.openintents.tools.simulator.util.SynchronizedInteger;

/**
 * 
 * SensorsScenarioModel keeps scenario data: the list of all states in the
 * scenario and information about playing them, like start/current/end position.
 * 
 * @author ilarele
 * 
 */
public class SensorsScenarioModel {

	// synchronized
	private Vector<StateModel> mStates;

	private SensorSimulatorModel mSensorSimulator;

	// used with synchronized() to protect integrity
	private SynchronizedInteger mStart = new SynchronizedInteger();
	private SynchronizedInteger mStop = new SynchronizedInteger();
	private SynchronizedInteger mPosition = new SynchronizedInteger();

	public SensorsScenarioModel(SensorsScenario sensorsRecording) {
		mStates = new Vector<StateModel>();
	}

	public void setSensorSimulatorModel(
			SensorSimulatorModel sensorSimulatorModel) {
		mSensorSimulator = sensorSimulatorModel;
	}

	// remove/add operations
	public void remove(StateModel model) {
		mStates.remove(model);
		setStopState(mStates.size() - 1);
	}

	public void add(int position, StateModel model) {
		mStates.add(position, model);
		setStopState(mStates.size() - 1);
	}

	public void add(StateModel stateModel) {
		int position = mStates.size();
		add(position, stateModel);
		setStopState(position - 1);
	}

	// set current/stop/start states
	public void setCurrentPosition(int value) {
		if (value != mPosition.getValue() && value < mStates.size()
				&& value > -1) {
			mPosition.setValue(value);

			if (mStart.getValue() > value) {
				mStart.setValue(value);
			}
			if (mStop.getValue() < value) {
				mStop.setValue(value);
			}
		}
	}

	public void setStartState(int value) {
		if (value < 0) {
			value = 0;
		}
		if (mStart.getValue() != value) {
			mStart.setValue(value);
			if (mPosition.getValue() < value) {
				mPosition.setValue(value);
			}

			if (mStop.getValue() < value) {
				mStop.setValue(value);
			}
		}
	}

	public void setStopState(int value) {
		int max = mStates.size() - 1;
		if (value < 0) {
			value = 0;
		}
		if (max < 0) {
			max = 0;
		}
		if (value > max) {
			value = max;
		}
		if (mStop.getValue() != value) {
			mStop.setValue(value);
			if (mPosition.getValue() == value - 1) {
				mPosition.setValue(value);
			} else if (mPosition.getValue() > value) {
				mPosition.setValue(value);
			}
			if (mStart.getValue() > value) {
				mStart.setValue(value);
			}
		}
	}

	// (re)setters & getters
	public void reset() {
		mStart.setValue(0);
		mPosition.setValue(0);
		mStop.setValue(0);
	}

	public void emptyStates() {
		mStates.clear();
	}

	public Vector<StateModel> getStates() {
		return mStates;
	}

	public StateModel getState(int index) {
		if (index < mStates.size())
			return mStates.get(index);
		return null;
	}

	public int getStartPosition() {
		return mStart.getValue();
	}

	public int getStopPosition() {
		return mStop.getValue();
	}

	public int getCurrentPosition() {
		return mPosition.getValue();
	}

	public int getMaxPosition() {
		int max = mStates.size() - 1;
		if (max < 0)
			return 0;
		return max;
	}

	public SensorSimulatorModel getSensorSimulatorModel() {
		return mSensorSimulator;
	}
}
