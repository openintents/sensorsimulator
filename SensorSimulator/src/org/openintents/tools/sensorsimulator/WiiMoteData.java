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
 * 09/Apr/08 Dale Thatcher <openintents at dalethatcher dot com>
 *           Added wii-mote data collection.
 */
package org.openintents.tools.sensorsimulator;

import java.io.File;
import java.util.Scanner;

/**
 * Reads sensor data from Wii mote through file.
 * 
 * @author Dale Thatcher
 *
 */
public class WiiMoteData {
	private String dataFilePath;
	private String status;
	private int roll;
	private int pitch;
	private double x, y, z;
	
	public void setDataFilePath(String newPath) {
		dataFilePath = newPath;
	}
	
	private boolean inCutOff(double value) {
		return -0.05 <= value && value <= 0.05;
	}
	
	public boolean updateData() {
		try {
			File f = new File(dataFilePath);
			
			if (! f.canRead()) {
				status = "Can't read file";
				return false;
			}
			
			Scanner scanner = new Scanner(f).useDelimiter(",|$");
			x = scanner.nextDouble();
			y = scanner.nextDouble();
			z = scanner.nextDouble();
			scanner.close();

			status = "x: " + x + ", y: " + y + ", z: " + z;
			
			int newRoll = roll;
			int newPitch = pitch;
			
			double yzResultant = Math.sqrt(y * y + z * z);
			if (! inCutOff(yzResultant)) {
				newRoll = (int)((180 * Math.atan(x / yzResultant)) / Math.PI);
			}
			
			double xzResultant = Math.sqrt(x * x + z * z);
			if (! inCutOff(xzResultant)) {
				newPitch = (int)((-180 * Math.atan(y / xzResultant)) / Math.PI);
			}
			

			if (z < 0) {
				// Wii-mote is upside down
				if (y < 0) {
					newPitch = 180 - newPitch;
				}
				else {
					newPitch = -180 - newPitch;
				}
				
				newRoll = -newRoll;
			}
			
			roll = newRoll;
			pitch = newPitch;
			
			return true;
		}
		catch (Exception ex) {
			status = "Exception: " + ex.getMessage();
			ex.printStackTrace();
		}

		return false;
	}
	
	public String getStatus() {
		return status;
	}
	
	public int getRoll() {
		return roll;
	}
	
	public int getPitch() {
		return pitch;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
}
