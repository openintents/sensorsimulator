package org.openintents.tools.simulator.model.sensor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.SensorsRecording;

public class SensorsRecordingModel {

	private ServerSocket mServerSocket;
	private ObjectInputStream mInStream;

	public SensorsRecordingModel(SensorsRecording sensorsRecording) {
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
							System.out.println("IOException:connection closed:" + e.getMessage());
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

}
