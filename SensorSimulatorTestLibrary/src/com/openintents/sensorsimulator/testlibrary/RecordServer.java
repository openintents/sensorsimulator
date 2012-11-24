package com.openintents.sensorsimulator.testlibrary;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Manages a server, that can be written to by the "SensorRecordFromDevice" app.
 * Reads all sensor events sent by client until client shuts down the
 * connection, then passes them to something that can process the sequence (e.g.
 * display them in a GUI or store them in a file).
 * 
 * @author Qui Don Ho
 * 
 */
public class RecordServer {

	private BlockingQueue<SensorEvent> mSensorEvents;
	private Thread mReceivingThread;
	private SequenceHandler mSequenceHandler;

	public RecordServer(SequenceHandler sequenceHandler) {
		System.out.println("RecorderServer started.");

		mSequenceHandler = sequenceHandler;
		mSensorEvents = new LinkedBlockingQueue<SensorEvent>();
		mReceivingThread = new Thread(mReceiving);
	}

	public void start() {
		mReceivingThread.start();
	}

	private Runnable mReceiving = new Runnable() {

		@Override
		public void run() {
			System.out.println("Receiving thread started.");

			try {
				ServerSocket serverSocket = new ServerSocket(9100);
				Socket client = serverSocket.accept();
				System.out.println("Client connected.");

				DataInputStream clientIn = new DataInputStream(
						client.getInputStream());

				while (true) {
					// read sensor event
					int type = clientIn.readInt();
					int accuracy = clientIn.readInt();
					long timestamp = clientIn.readLong();
					int valLength = clientIn.readInt();
					float[] values = new float[valLength];
					for (int i = 0; i < valLength; i++) {
						values[i] = clientIn.readFloat();
					}

					mSensorEvents.put(new SensorEvent(type, accuracy,
							timestamp, values));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EOFException e) {
				// client has finished sending sensor events
				System.out.println("Client has finished.");

				// handle events
				mSequenceHandler.handle(mSensorEvents);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Exiting receiving thread...");
		}
	};
}