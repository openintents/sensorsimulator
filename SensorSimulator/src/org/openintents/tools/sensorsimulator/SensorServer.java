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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens for incoming connections from an Android phone or emulator.
 * 
 * Connections are then passed to the {@link SensorServerThread}.
 * 
 * @author Peli
 *
 */
public class SensorServer implements Runnable {

	public SensorSimulator mSensorSimulator;
	
	/**
	 * linked list of all successors, so that we can destroy them 
	 * when needed.
	 */
	public Thread mThread;
	public SensorServerThread firstThread;
	public SensorServerThread lastThread;
	
	private ServerSocket serverSocket;
	
	
	public int port;
	public boolean listening; 
	
	
	/**
	 * Constructor to start as server that listens for connections.
	 * @param newSensorSimulator
	 */
	public SensorServer(SensorSimulator newSensorSimulator) {
		mSensorSimulator = newSensorSimulator;
		firstThread = null;
		lastThread = null;
		listening = true;
		
		// start ourselves:
		mThread = new Thread(this);
		mThread.start();
	}
	
	
	// Called when starting a thread for network connection
	public void run() {
		listenServer();
	}
	
    public void listenServer() {
    	// obtain port number:
    	port = mSensorSimulator.getPort();
    	if (port == 0) return;
    	
    	serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            //System.err.println("Could not listen on port: 4444.");
            //System.exit(1);
        	mSensorSimulator.addMessage("Could not listen on port: " + port);
        	return;
        }
        
        
        Socket clientSocket = null;
        try {
        	mSensorSimulator.addMessage("Listening on port " + port + "...");
        	while (listening) {
        		clientSocket = serverSocket.accept();
        		
        		// First we notify this:
        		mSensorSimulator.newClient();
        		
        		 // Start again new thread:
                SensorServerThread newThread 
                	= new SensorServerThread(mSensorSimulator, clientSocket);
                
                // set the linking:
                if (firstThread == null) {
                	// this is the first thread:
                	firstThread = newThread;
                	lastThread = newThread;
                } else {
                	// link into chain:
                	lastThread.nextThread = newThread;
                	newThread.previousThread = lastThread;
                	lastThread = newThread;
                }
        	}
            
        } catch (IOException e) {
        	if (listening) {
        		System.err.println("Accept failed.");
        		//System.exit(1);
        	} else {
        		// everything ok, socket closed by user.
        	}
        }
        
        try {
        	serverSocket.close();
        } catch (IOException e) {
            System.err.println("Close failed.");
            System.exit(1);
        }
    }
    
    /**
     * Stop all active threads and then oneself.
     */
    public void stop() {
    	
    	// go through the list and kill in turn
    	SensorServerThread sst;
    	SensorServerThread ssthelp;
    	for (sst = firstThread; sst != null; sst = ssthelp) {
    		// first remember next pointer before it is gone:
    		ssthelp = sst.nextThread;
    		//sst.mThread.interrupt();
    		sst.stop();
    	}
    	
    	// finally kill ourselves:
    	listening = false;
    	//mThread.interrupt();
    	
    	// rather close the socket
    	// (see http://java.sun.com/j2se/1.4.2/docs/guide/misc/threadPrimitiveDeprecation.html )
    	try {
    		if (serverSocket != null) {
    			mSensorSimulator.addMessage("Closing listening server...");
    			serverSocket.close();
    		}
        } catch (IOException e) {
            System.err.println("Close failed.");
            System.exit(1);
        }
    	
    }
    
    
}
