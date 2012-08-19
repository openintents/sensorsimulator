/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 *
 * Copyright (C) 2008-2011 OpenIntents.org
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

package org.openintents.tools.simulator.model.telnet;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * TelnetServer creates telnet connection with emulator's or Android's command
 * port. Once we connect with command port we only need to send correct
 * commands.
 * 
 * @author Josip Balic
 */
public class TelnetServer {

	// public TelnetSimulatorModel mSimulator;
	public PrintStream out;

	private Socket serverSocket;
	private FileRunnable runnable;

	private ArrayList<String> dateAndTime;
	private ArrayList<String> simulationType;
	private ArrayList<String> time;
	private ArrayList<String> values;

	private int counter;
	private ArrayList<Integer> breakingPlaces;

	/**
	 * Constructor.
	 * 
	 * @param telnetSimulator
	 *            , SensorSimulator instance of simulator that wants to
	 *            establish telnet connection with emulator
	 */

	/**
	 * Method used to connect with emulator's command port.
	 */
	public void connect(int port) {
		try {
			serverSocket = new Socket("localhost", port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out = new PrintStream(serverSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// mSimulator.addMessage("Telnet connection opened ");
	}

	/**
	 * Method used to close connection.
	 * 
	 * @return true or false
	 */
	public boolean disconnect() {
		if (serverSocket == null || serverSocket.isClosed())
			return false;
		try {
			serverSocket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Used to change power capacity.
	 * 
	 * @param value
	 *            , integer value of desired capacity
	 */
	public void changePower(int value) {
		if (out != null)
			out.println("power capacity " + Integer.toString(value));
		// else
		// mSimulator
		// .addMessage("Please connect to the simulator first");
	}

	/**
	 * Method used to change presence of battery
	 * 
	 * @param presence
	 *            , boolean true if present, false otherwise
	 */
	public void changePresence(boolean presence) {
		if (presence) {
			out.println("power present true");
		} else {
			out.println("power present false");
		}
	}

	/**
	 * Used to change AC of battery
	 * 
	 * @param ac
	 *            , boolean true if ac is on, false otherwise
	 */
	public void changeAC(boolean ac) {
		if (ac) {
			out.println("power ac on");
		} else {
			out.println("power ac off");
		}
	}

	/**
	 * Method used to change health of battery.
	 * 
	 * @param selectedItem
	 *            , Object
	 */
	public void changeHealth(Object selectedItem) {
		out.println("power health " + selectedItem.toString());
	}

	/**
	 * Used to change status of battery.
	 * 
	 * @param selectedItem
	 *            , Object
	 */
	public void changeStatus(Object selectedItem) {
		out.println("power status " + selectedItem.toString());
	}

	/**
	 * This method is used to send GPS command geo fix. IMPORTANT: geo fix
	 * command is still bugged, consult project documentation for more
	 * reference.
	 */
	public void sendGPS(float longitude, float latitude, float altitude) {
		if (out != null) {
			out.println("geo fix " + longitude + " " + latitude + " "
					+ altitude);
		}
	}

	/**
	 * Method used to read file and store attributes written inside a file to
	 * correct ArrayList.
	 * 
	 * @param file
	 *            , File to be read.
	 */
	public void openFile(File file) {
		int j = 0;
		counter = 0;
		breakingPlaces = new ArrayList<Integer>();
		dateAndTime = new ArrayList<String>();
		simulationType = new ArrayList<String>();
		time = new ArrayList<String>();
		values = new ArrayList<String>();
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.length() > 14) {
					dateAndTime.add(strLine);
					breakingPlaces.add(counter);
				} else if (strLine.equalsIgnoreCase("BATTERY")) {
					simulationType.add(strLine);
				} else if (strLine.length() > 4 && j > 2) {
					counter++;
					time.add(strLine);
				} else if (!strLine.equals("") && j > 2) {
					values.add(strLine);
				}
				j++;
			}
			// Close the input stream
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method used to start battery simulation from file. Here we convert time
	 * intervals to milliseconds and call FileRunnable that will create new
	 * Thread needed for real time simulation.
	 * 
	 * @param messagePanel
	 */
	public void slowEmulation() {
		try {
			long[] time2 = null;
			time2 = new long[time.size()];
			for (int i = 0; i < time.size(); i++) {
				time2[i] = getLongMsFromTimeString(time.get(i));
			}
			runnable = new FileRunnable(dateAndTime, simulationType, time,
					values, time2, this, breakingPlaces);
			runnable.run();
		} catch (Exception e) {
			// messagePanel.append("Open file for battery emulation first!\n");
		}
	}

	/**
	 * Method used to interrupt thread that is simulating battery from file.
	 * With this method we can jump to next time interval.
	 */
	public void nextTimeEvent() {
		if (runnable != null) {
			runnable.interrupt();
		}
	}

	/**
	 * This method is used to get milliseconds of our time intervals.
	 * 
	 * @param timeInString
	 *            , String that contains our time interval
	 * @return time, long milliseconds of our time interval.
	 */
	public static long getLongMsFromTimeString(String timeInString) {
		long time = 0;
		String[] array = timeInString.split(":");

		if (array.length == 4) {
			time = Integer.parseInt(array[0]) * 1000 * 60 * 60
					+ Integer.parseInt(array[1]) * 1000 * 60
					+ Integer.parseInt(array[2]) * 1000
					+ Integer.parseInt(array[3]);
		}
		return time;
	}

}
