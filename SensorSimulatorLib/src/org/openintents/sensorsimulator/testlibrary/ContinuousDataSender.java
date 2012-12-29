package org.openintents.sensorsimulator.testlibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ContinuousDataSender {

	private static final int CONNECT_TIMEOUT = 100;
	private SensorEventProducer mSensorEventProducer;
	private Socket mConn;
	private DataInputStream mCmdIn;
	private DataOutputStream mCmdout;
	private Thread mSendingThread;
	private InetAddress mUdpAddress;
	private int mUdpPort;
	private BlockingQueue<SensorEvent> mEvents;

	public ContinuousDataSender(SensorEventProducer sensorEventProducer) {
		mSensorEventProducer = sensorEventProducer;
		mEvents = new LinkedBlockingQueue<SensorEvent>(100);
	}

	/**
	 * Connect to app-under-test
	 */
	public void connect() {
		connect("192.168.2.101");
	}

	/**
	 * Connect to app-under-test
	 */
	public void connect(String ipAddress) {
		try {
			mConn = new Socket(ipAddress, 8111);
			mCmdIn = new DataInputStream(mConn.getInputStream());
			mCmdout = new DataOutputStream(mConn.getOutputStream());

			// tell server wish to send continuous data stream
			mCmdout.writeInt(1);
			mCmdout.flush();

			// read sensor event listener registrations
			Map<Sensor.Type, Integer> sensorRateMapping = new HashMap<Sensor.Type, Integer>();
			int n = mCmdIn.readInt();
			for (int i = 0; i < n; i++) {
				Sensor.Type sensor = Sensor.intToType(mCmdIn
						.readInt());
				int rate = mCmdIn.readInt();
				sensorRateMapping.put(sensor, rate);
			}

			// ... compute best sending mapping
			Map<Integer, Integer> samplingMap = mSensorEventProducer
					.registerSensors(sensorRateMapping);

			// send sensor - speed mapping
			for (Entry<Integer, Integer> entry : samplingMap.entrySet()) {
				mCmdout.writeInt(entry.getKey());
				mCmdout.writeInt(entry.getValue());
			}

			mUdpAddress = mConn.getInetAddress();
			mUdpPort = mCmdIn.readInt();

			Thread.sleep(500);

			mSendingThread = new Thread(mSending);
			mSendingThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			// app hasnt been started
			System.err
					.println("Connection was refused. Has the app been started?");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Stop internal thread and clean up.
	 * 
	 */
	public void disconnect() {

	}

	/**
	 * Enqueue SensorEvent.
	 * 
	 * @param sEvent
	 *            SensorEvent to enqueue.
	 */
	public void push(SensorEvent sEvent) {
		try {
			mEvents.put(sEvent);
			System.out.println(mEvents.size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Runnable mSending = new Runnable() {

		@Override
		public void run() {

			try {
				Socket dSocket2 = new Socket(mUdpAddress, mUdpPort);

				DataOutputStream dOut = new DataOutputStream(
						dSocket2.getOutputStream());
				
				while (!Thread.interrupted()) {
					try {
						SensorEvent event = mEvents.take();

						// write into stream
						dOut.writeInt(event.type);
						dOut.writeInt(event.accuracy);
						dOut.writeInt(event.values.length);
						for (float value : event.values)
							dOut.writeFloat(value);
						
						dOut.flush();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mSendingThread.interrupt();
					}
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.out.println("Sending ended");
			}
		}
	};
}
