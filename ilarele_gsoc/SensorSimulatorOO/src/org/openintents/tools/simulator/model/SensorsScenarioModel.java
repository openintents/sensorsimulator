package org.openintents.tools.simulator.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.SensorsScenario;
import org.openintents.tools.simulator.model.sensor.SensorSimulatorModel;

public class SensorsScenarioModel {

	public static final double MAX_TIME = 100;
	public static final double MIN_TIME = 0.5;

	private ServerSocket mServerSocket;
	private ObjectInputStream mInStream;
	private ArrayList<StateModel> mStates;
	private SensorSimulatorModel mSensorSimulator;

	public SensorsScenarioModel(SensorsScenario sensorsRecording,
			SensorSimulatorModel sensorSimulatorModel) {
		this.mSensorSimulator = sensorSimulatorModel;
		mStates = new ArrayList<StateModel>();
		startListening();
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
							System.out.println("Wait for connection");
							connection = mServerSocket.accept();
							System.out.println("Connected");
							while (true) {
								mInStream = new ObjectInputStream(
										connection.getInputStream());
								Object rObj = mInStream.readObject();
								float[] obj = (float[]) rObj;
								System.out.println("obj:" + obj[0] + " "
										+ obj[1] + " " + obj[2]);
							}
						} catch (IOException e) {
							System.out.println("IOException:connection closed:"
									+ e.getMessage());
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

	public ArrayList<StateModel> getStates() {
		return mStates;
	}

	public void emptyStates() {
		mStates.clear();
	}

	public void remove(StateModel model) {
		mStates.remove(model);
	}

	public void add(StateModel model) {
		mStates.add(model);
	}

	public SensorSimulatorModel getSensorSimulatorModel() {
		return mSensorSimulator;
	}

}
