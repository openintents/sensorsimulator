/*
 * Copyright (C) 2008 OpenIntents.org
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

/*
 * 25/Mar/2011 Prithvi Raj < prithvi dot raj at ufl dot edu>
 *           Added file playback and recording.
 */
package org.openintents.tools.sensorsimulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Records sensor data (yaw, roll, pitch) to file and allows playback from it
 * @author prithviraj
 *
 */
public class FileData{

	private FileWriter fstream;
	private BufferedWriter out;
	private double yaw, roll, pitch;
	private Boolean isRecording,isPlaying;
	private FileReader fr;
	private BufferedReader br;
	
	public FileData(){
		isRecording=false;
		isPlaying=false;
		
	}
	
	public boolean createFile() {
	    isRecording=!isRecording;//toggle state
	    if(isRecording){
		    try {
				fstream= new FileWriter("SensorData.txt");
				out=new BufferedWriter(fstream);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
	    } else {
	    	try {
	    		out.close();
	    		fstream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	return false;
	    }
	}
	
	public void recordData(double yaw,double roll,double pitch) {
		if(isRecording) {
			try {
				String s = yaw + "," + roll + "," + pitch + "\n";
				out.write(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean openFile(){
	    if (!isRecording) {
			isPlaying = !isPlaying;
		}else{
			return false;
		}
	    
		try {
			fr = new FileReader("SensorData.txt");
			br= new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean playData() {
		if(isRecording){
			return false;
		}else if (isPlaying) {
			try {
				String nextToken = br.readLine();
				if (nextToken == null){
					isPlaying=false;
					return false;
				}else {
					Scanner scanner = new Scanner(nextToken).useDelimiter(",");
					yaw = Double.parseDouble(scanner.next());
					roll = Double.parseDouble(scanner.next());
					pitch = Double.parseDouble(scanner.next()); 
					scanner.close();
					return true;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	public boolean isPlaying(){
		return isPlaying;
	}
	
	public boolean isRecording(){
		return isRecording;
	}
	
	public int getYaw(){
		return (int) yaw;
	}
	
	public int getRoll(){
		return (int) roll;
	}
	
	public int getPitch(){
		return (int) pitch;
	}
}
