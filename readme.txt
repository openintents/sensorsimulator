 ****************************************************************************
 * Port of OpenIntents simulator to Android 2.1, extension to multi         *
 * emulator support, and GPS and battery simulation is developed as a       *
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of    *
 * Electrical Engineering and Computing.                                    *
 *                                                                          *
 * Copyright (C) 2008-2011 OpenIntents.org                                  *
 *                                                                          *
 * Licensed under the Apache License, Version 2.0 (the "License");          *
 * you may not use this file except in compliance with the License.         *
 * You may obtain a copy of the License at                                  *
 *                                                                          *
 *      http://www.apache.org/licenses/LICENSE-2.0                          *
 *                                                                          *
 * Unless required by applicable law or agreed to in writing, software      *
 * distributed under the License is distributed on an "AS IS" BASIS,        *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and      *
 * limitations under the License.                                           *
 ****************************************************************************

SensorSimulator lets you simulate sensor events from 
accelerometer, compass, orientation, temperature, light, proximity,
pressure, linear acceleration, gravity, gyroscope and rotation vector
sensors.

It transmits the simulated sensor data to an Android emulator.
Also, it can record sensor data from an real Android device.

-------------------------------------------------------------

INSTRUCTIONS:

1) Launch bin/sensorsimulator.jar

2) Install bin/SensorSimulatorSettings.apk on your Android
   emulator

3) Connect from your Android emulator to the SensorSimulator
   by entering the IP address that is shown in the
   SensorSimulator (if there are several possibilities,
   try which works)

4) These IP settings are stored on your device.

5) Build one of the sample applications, for example
   samples/SensorDemo in Eclipse. Further instructions can be
   found in the source code.

6) To include the sensor simulator in your existing application
   include the lib/sensorsimulator-lib.jar and follow the
   instructions that can be found in the samples source codes.

7) To record from the real device you need internet connection on the
   Android device and on the pc/laptop on which the simulator is
   running. The ip address of the simulator must be reachable from
   your Android device (a local wireless network will do the trick).

Launch SensorSimulator Desktop Application
-------------------------------------------------------------
1) Build one of the sample applications, for example 
   samples/SensorDemo in Eclipse. Further instructions can be 
   found in the source code.

2) Launch the sample application.

3) Launch bin/sensorsimulator.jar with parameter "ip <phone ipadress>". Both
   this machine and the smartphone have to be on the same w-lan. 

Launch SensorSimulator Desktop Application console version
-------------------------------------------------------------
1) Same as above, but in step 3), launch sensorsimulator.jar with parameter
   "c".

2) Press "h" for an overview of commands.

Record a sensor sequence (Console version)
-------------------------------------------------------------
1) In Sensorsimulator, type "r <filename>" for recording.

2) On smartphone, launch SensorRecordFromDevice app.

3) Type in ip of machine, where SensorSimulator is running.

4) Press "Record", perform sensor sequence, e.g. shake handset, and press
   "Stop".

5) SensorSequence will be saved in same directory as SensorSimulator. 

Play a sensor sequence (Console version)
-------------------------------------------------------------
1) Launch app.

2) In SensorSimulator, type "c <ipaddress>" to connect (or "c" to use previous
   ip address).

3) Press "l <filename>". 

Directory structure:

- bin (*): contains the executables SensorSimulator.jar,
           SensorSimulatorSettings.apk and SensorRecordFromDevice.apk
- lib (*): contains the library sensorsimulator-lib.jar
- release: contains the build script to assemble the release
- samples: contains examples how to include the sensor simulator
           in your Android applications.
- SensorSimulator: contains the source code for the
            sensor simulator.
- SensorSimulatorSettings: contains the source code for
            sensor simulator settings.
- SensorRecordFromDevice: contains the source code for
            recording sensors from device.

(*) The directories "bin" and "lib" are only available in the
    downloadable release zip file.
