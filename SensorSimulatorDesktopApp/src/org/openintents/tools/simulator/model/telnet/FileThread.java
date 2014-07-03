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

import java.util.ArrayList;

/**
 * FileThread is Thread used to simulate battery events from text file. It's
 * called by FileRunnable, works in background and fires itself when certain
 * time event is reached.
 * 
 * @author Josip Balic
 * 
 */
public class FileThread extends Thread {

	public ArrayList<String> dateAndTime = new ArrayList<String>();
	public ArrayList<String> simulationType = new ArrayList<String>();
	public ArrayList<String> time = new ArrayList<String>();
	public ArrayList<String> values = new ArrayList<String>();
	public ArrayList<Integer> breakingPlaces = new ArrayList<Integer>();
	public int i = 0;
	public long[] time3;
	public TelnetServer myTelnetServer;

	/**
	 * Constructor for FileThread. Used to initialize variables.
	 * 
	 * @param dateTime
	 *            , ArrayList<String> that contains date and time of
	 *            simulations.
	 * @param simulation
	 *            , ArrayList<String> that contains type of simulations.
	 * @param time2
	 *            , ArrayList<String> that contains time of events.
	 * @param values2
	 *            , ArrayList<String> that contains values in certain times.
	 * @param time4
	 *            , long[] that contains time of events converted to long
	 *            milliseconds.
	 * @param telnetServer
	 *            , TelnetServer through which we send battery values to
	 *            emulator.
	 * @param places
	 *            , ArrayList<Integer> that contains places when second time
	 *            date is reached.
	 */
	public FileThread(ArrayList<String> dateTime, ArrayList<String> simulation,
			ArrayList<String> time2, ArrayList<String> values2, long[] time4,
			TelnetServer telnetServer, ArrayList<Integer> places) {
		dateAndTime = dateTime;
		simulationType = simulation;
		time = time2;
		values = values2;
		time3 = time4;
		breakingPlaces = places;
		i = time.size();
		myTelnetServer = telnetServer;
	}

	/**
	 * This method is called when thread runs itself. In this method we use
	 * variables previously initialized through constructor. Time events are
	 * generated to threads sleep method. Thread is at sleep between time
	 * intervals. Once new time interval is reached, thread is called up and
	 * battery value is send to emulator.
	 */
	public synchronized void run() {
		// four counters used to correctly emulate values
		int k = dateAndTime.size();
		int j = 0;
		int c = 0;
		int z = 0;

		// flag for document with only one Date and Time
		boolean oneDateAndTime = true;

		while (j < i) {
			// if we have multiple date and times in document
			if (k != 1) {
				if (c == breakingPlaces.get(z)) {
					// print first date and time
					// increase counter for array that holds breaking places
					if (z < breakingPlaces.size() - 1) {
						z++;
					}
					// send value to emulator
					myTelnetServer.out.println("power capacity "
							+ values.get(j));
					c++;
					j++;
					// if we are still in the same date and time go through time
					// intervals
				} else {
					myTelnetServer.out.println("power capacity "
							+ values.get(j));
					c++;
					j++;
				}
				// here we simulate time intervals for document that has only
				// one date and time
			} else {
				if (oneDateAndTime) {
					oneDateAndTime = false;
				}
				myTelnetServer.out.println("power capacity " + values.get(j));
				j++;
			}
			if (j < time.size()) {
				try {
					this.wait(time3[j] - time3[j - 1]);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}

	}

}
