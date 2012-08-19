 ****************************************************************************
 * Copyright (C) 2008-2009 OpenIntents.org                                  *
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
accelerometer, compass, orientation, and temperature sensors.
It transmits the simulated sensor data to an Android emulator.


-------------------------------------------------------------

INSTRUCTIONS:

1) Launch bin/SensorSimulator.jar

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

-------------------------------------------------------------

Directory structure:

- bin (*): contains the executables SensorSimulator.jar
           and SensorSimulatorSettings.apk
- lib (*): contains the library sensorsimulator-lib.jar
- release: contains the build script to assemble the release
- samples: contains examples how to include the sensor simulator
           in your Android applications.
- SensorSimulator: contains the source code for the
            sensor simulator.
- SensorSimulatorSettings: contains the source code for
            sensor simulator settings.

(*) The directories "bin" and "lib" are only available in the
    downloadable release zip file.
