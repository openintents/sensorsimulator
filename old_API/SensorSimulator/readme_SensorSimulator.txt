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

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

   THIS IS A JAVA STANDALONE APPLICATION

* To launch it, open the sensorsimulator.jar file with your JRE
  (requires Java Runtime Environment 1.6.0 or higher).
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

---------------------------------------------------------
release: 1.0.0-beta1
date: 2009-07-01

- apply patch by jonacruz (Issue #165).
- apply patch by Danne to work around the
  sensor Emulator bug in the SDK 1.5.

---------------------------------------------------------
release: 0.9.0
date: 2008-09-05

upgrade to Android SDK 0.9.
- adapt new conventions for accelerometer and orientation
  sensors.

---------------------------------------------------------
release: 0.1.7
date: 2008-04-19

- Added Dale Thatcher's Wii mote patch.
  http://blog.dalethatcher.com/2008/04/howto-get-wii-mote-data-into-android.html

---------------------------------------------------------
release: 0.1.6
date: 2008-04-09

- no changes

---------------------------------------------------------
release: 0.1.5
date: 2008-03-28

- Performance patch included (by Ogurash).
- Special characters implemented as unicode.

---------------------------------------------------------
release: 0.1.4
date: 2008-03-12

new features:
  - support for Sensors class updateSensorRate() and related methods.
  - new physical model for accelerometer that allows for higher 
    time-resolution. Accelerometer can be specified by spring constant
    and damping terms.

---------------------------------------------------------
release: 0.1.3
date: 2008-02-24

new features:
  - update interval can be set and is monitored
  - more settings for accelerometer / acceleration:
    pixel per meter, limit for accelerometer.
  - random contribution to sensors
  
known issues:
  - the new Sensors methods related to sensor update
    rate are not yet implemented.

---------------------------------------------------------
release: 0.1.2
date: 2008-01-28

new features:
  - runs as standalone Java application
  - also runs as Java applet in web browser, but
    connection to Android emulator is not possible 
    due to browser security restrictions
  - sensors: accelerometer, compass, orientation, temperature
  - settings: gravity, magnetic field, temperature

known issues:
  - requires JRE 1.6.
  - official definitions or compass and orientation 
    sensors are not fully specified.
    Currently these definitions are used:
    - compass: magnetic field in micro-Tesla
    - orientation: yaw, pitch, and roll (in this order)
      in degree from -180 to +180.
    - temperature: temperature in degree Celsius
    