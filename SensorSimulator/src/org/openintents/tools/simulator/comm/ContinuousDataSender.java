package org.openintents.tools.simulator.comm;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
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

import org.openintents.tools.simulator.model.sensors.SensorType;

public class ContinuousDataSender {

	private static final int CONNECT_TIMEOUT = 100;
	private SensorEventProducer mSensorEventProducer;
	private Socket mConn;
	private DataInputStream mCmdIn;
	private DataOutputStream mCmdout;
	private Thread mSendingThread;
	private InetAddress mUdpAddress;
	private int mUdpPort;

	public ContinuousDataSender(SensorEventProducer sensorEventProducer) {
		mSensorEventProducer = sensorEventProducer;
		mEvents = new LinkedBlockingQueue<SensorEventContainer>();
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

			mConn = new Socket("192.168.2.102", 8111);
			mCmdIn = new DataInputStream(mConn.getInputStream());
			mCmdout = new DataOutputStream(mConn.getOutputStream());

			// tell server wish to send continuous data stream
			mCmdout.writeInt(1);

			// read sensor event listener registrations
			Map<SensorType, Integer> sensorRateMapping = new HashMap<SensorType, Integer>();
			int n = mCmdIn.readInt();
			for (int i = 0; i < n; i++) {
				SensorType sensor = SensorEventProducer.intToSentype(mCmdIn
						.readInt());
				int rate = mCmdIn.readInt();
				sensorRateMapping.put(sensor, rate);
			}

			// ... compute best sending mapping
			Map<Integer, Integer> samplingMap = mSensorEventProducer
					.setRegisteredSensorRates(sensorRateMapping);

			// send sensor - speed mapping
			for (Entry<Integer, Integer> entry : samplingMap.entrySet()) {
				mCmdout.writeInt(entry.getKey());
				mCmdout.writeInt(entry.getValue());
			}

			mUdpAddress = mConn.getInetAddress();
			mUdpPort = mCmdIn.readInt();

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
	public void push(SensorEventContainer sEvent) {
		try {
			mEvents.put(sEvent);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Runnable mSending = new Runnable() {

		@Override
		public void run() {
			// establish udp socket
			DatagramSocket dSocket;

			try {
				dSocket = new DatagramSocket();

				while (!Thread.interrupted()) {
					try {
						SensorEventContainer event = mEvents.take();

						int type = event.type;
						int accuracy = event.accuracy;
						int valLength = event.values.length;
						float[] values = event.values;

						ByteArrayOutputStream output = new ByteArrayOutputStream();
						DataOutputStream dOut = new DataOutputStream(output);
						dOut.writeInt(type);
						dOut.writeInt(accuracy);
						dOut.writeInt(valLength);
						for (float value : values)
							dOut.writeFloat(value);

						byte[] buf = output.toByteArray();
						DatagramPacket dPacket = new DatagramPacket(buf,
								buf.length, mUdpAddress, mUdpPort);

						dSocket.send(dPacket);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mSendingThread.interrupt();
					}
				}

				// // TODO remove test data
				// for (int i = 0; i < 200; i++) {
				//
				// int type = i % 2 == 0 ? 1 : 5;
				// int accuracy = i;
				// int valLength = 3;
				// float[] values = { (float) i, 2.0f, 3.0f };
				//
				// ByteArrayOutputStream output = new ByteArrayOutputStream();
				// DataOutputStream dOut = new DataOutputStream(output);
				// dOut.writeInt(type);
				// dOut.writeInt(accuracy);
				// dOut.writeInt(valLength);
				// for (float value : values)
				// dOut.writeFloat(value);
				//
				// byte[] buf = output.toByteArray();
				// DatagramPacket dPacket = new DatagramPacket(buf,
				// buf.length, mUdpAddress, mUdpPort);
				//
				// System.out.println(i + "th time");
				// dSocket.send(dPacket);
				// }
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private BlockingQueue<SensorEventContainer> mEvents;
}
