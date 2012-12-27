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
		mEvents = new LinkedBlockingQueue<SensorEvent>();
	}

	/**
	 * Connect to app-under-test
	 */
	public void connect() {
		try {
			// mConn = new Socket();
			// SocketAddress devAddr = new InetSocketAddress("192.168.2.102",
			// 8111);
			// mConn.connect(devAddr, CONNECT_TIMEOUT);
			// mCmdIn = new DataInputStream(mConn.getInputStream());
			// mCmdout = new DataOutputStream(mConn.getOutputStream());

			mConn = new Socket("192.168.2.101", 8111);
			mCmdIn = new DataInputStream(mConn.getInputStream());
			mCmdout = new DataOutputStream(mConn.getOutputStream());

			// tell server wish to send continuous data stream
			mCmdout.writeInt(1);

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
	 * Connect to app-under-test
	 */
	public void connect(String ipAddress) {

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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Runnable mSending = new Runnable() {

		// determines how many events are put in one datagram
		private final int MAX_EVENTS = 20;

		@Override
		public void run() {
			DatagramSocket dSocket;
			long then = 0;

			try {
				// dSocket = new DatagramSocket();
				//
				// while (!Thread.interrupted()) {
				// try {
				// SensorEvent[] eventsToSend = new SensorEvent[MAX_EVENTS];
				//
				// // gather enough events for one packet
				// for (int i = 0; i < eventsToSend.length; i++) {
				// eventsToSend[i] = mEvents.take();
				// }
				//
				// ByteArrayOutputStream output = new ByteArrayOutputStream();
				// DataOutputStream dOut = new DataOutputStream(output);
				//
				// // write into stream
				// for (SensorEvent event : eventsToSend) {
				// dOut.writeInt(event.type);
				// dOut.writeInt(event.accuracy);
				// dOut.writeInt(event.values.length);
				// for (float value : event.values)
				// dOut.writeFloat(value);
				// }
				//
				// // send packet
				// byte[] buf = output.toByteArray();
				// DatagramPacket dPacket = new DatagramPacket(buf,
				// buf.length, mUdpAddress, mUdpPort);
				// dSocket.send(dPacket);
				// long now = System.currentTimeMillis();
				// System.out.println(now - then);
				// then = now;
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// mSendingThread.interrupt();
				// }
				// }
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
