/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 * 
 * Copyright (C) 2008-2010 OpenIntents.org
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

package org.openintents.tools.sensorsimulator;

import java.util.ArrayList;

/**
 * FileRunnable creates new Thread for reading file and simulating values in the file.
 * Implementation of Runnable class is necessary to avoid blocking of GUI thread.
 * 
 * @author Josip Balic
 */
public class FileRunnable implements Runnable {

	public ArrayList<String> dateAndTime= new ArrayList<String>();
	public ArrayList<String> simulationType= new ArrayList<String>();
	public ArrayList<String> time = new ArrayList<String>();
	public ArrayList<String> values = new ArrayList<String>();
	public ArrayList<Integer> breakingPlaces = new ArrayList<Integer>();
	public int i = 0;
	public long[] time3;
	public FileThread thread;
	public TelnetServer myTelnetServer;
	
	/**
	 * Constructor for FileRunnable class. Here we initialize all the variables.
	 * 
	 * @param dateTime, ArrayList<String> that contains date and time of simulations.
	 * @param simulation, ArrayList<String> that contains type of simulations.
	 * @param time2, ArrayList<String> that contains time of events.
	 * @param values2, ArrayList<String> that contains values in certain times.
	 * @param time4, long[] that contains time of events converted to long milliseconds.
	 * @param telnetServer, TelnetServer through which we send battery values to emulator.
	 * @param places, ArrayList<Integer> that contains places when second time date is reached.
	 */
    public FileRunnable(ArrayList<String> dateTime, ArrayList<String> simulation, 
    		ArrayList<String> time2, ArrayList<String> values2, long[] time4, 
    		TelnetServer telnetServer, ArrayList<Integer> places){
    	dateAndTime = dateTime;
    	simulationType = simulation;
    	time = time2;
    	values = values2;
    	breakingPlaces = places;
    	time3 = time4;
    	i = time.size();
    	myTelnetServer = telnetServer;
    }
    
    /**
     * Run method for this Runnable object. In it we create new Thread and start it.
     */
	public void run() {
	    thread = new FileThread(dateAndTime, simulationType, time, 
	    		values, time3, myTelnetServer, breakingPlaces);
	    thread.start();
		
	}
	

}
