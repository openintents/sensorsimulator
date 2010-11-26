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

package org.openintents.tools.sensorsimulator;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * Provides the SensorSimulator in form of a Java Applet to be run in a web browser.
 * 
 * The applet can be used in a HTML document in the following way:
 * <pre>
 * &lt;applet code="org.openintents.tools.sensorsimulator.SensorSimulatorWeb.class" 
 *         archive="sensorsimulator.jar" width="640" height="440" alt="OpenIntents"&gt;
 * &lt;/applet&gt;
 * </pre>
 * 
 * The applet currently does not take any parameters.
 * 
 * Note that the applet is meant as a demo only. Due to web browser restrictions,
 * an Android application running in the emulator is not allowed to connect
 * to a Java applet.
 * 
 * @author Peli
 *
 */
public class SensorSimulatorWeb extends JApplet {
	
	public SensorSimulator mSensorSimulator;
	
	public void init() {
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't successfully complete");
        }
    }
	
    private void createGUI() {        
        //Create the text field and make it uneditable.
        mSensorSimulator = new SensorSimulator();

        //Set the layout manager so that the text field will be
        //as wide as possible.
        setLayout(new java.awt.GridLayout(1,0));

        //Add the text field to the applet.
        add(mSensorSimulator);
    }

    public void start() {
        
    }

    public void stop() {
        
    }

    public void destroy() {
        
    }
    
    private void cleanUp() {
        //Execute a job on the event-dispatching thread:
        //taking the text field out of this applet.
    	
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    remove(mSensorSimulator);
                }
            });
        } catch (Exception e) {
            System.err.println("cleanUp didn't successfully complete");
        }
        mSensorSimulator = null;
        
    }

}
