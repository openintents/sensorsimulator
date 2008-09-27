 ****************************************************************************
 * Copyright (C) 2007-2008 OpenIntents.org                                  *
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

OpenIntents defines and implements open interfaces for
improved interoperability of Android applications.

To obtain the current release, visit
  http://www.openintents.org
  
---------------------------------------------------------
release: 0.9.0
date: 2008-09-05

- upgrade to Android SDK 0.9.
- updated Sensors class to SensorManager and SensorListener

---------------------------------------------------------
release: 0.1.7
date: 2008-04-19

new features:
- added new activity to insert default locations,
  news feeds, and an mp3 at program start (issue #91, 97, 125)
- added copyright notice for maps and shopping list font
  (issue #106)
  
changes:

bug fixes:
- shopping list - add alert shows tag (issue #115)
- alert bug fixes (issues #92, 94, 113, 114, 120, 121)
- shopping list bug fixes (issues #107, 115, 119)
- Magnolia tagging bug fixes (issues #101, 102) 

---------------------------------------------------------
release: 0.1.6
date: 2008-04-09

- FavoriteLocations sample application removed, its 
  functionality moved into the core.
- Base issues fixed (issues #61, 62, 90, 98)
- Improved locations, extras for locations, distance to
  current location, copyright notice (issues #56, 60,
  72, 86, 88, 89, 95)
- Renamed ContentBrowser in TagBrowser, and 
  several issues fixed (issues #17, 25, 29, 30, 42, 
  71, 73, 74, 79, 81, 99, 100)
- Shopping list: Added GTalk support, themes, and 
  location alert (issues #20, 65, 66).

---------------------------------------------------------
release: 0.1.5
date: 2008-03-28

new features:
- support for bolt ( http://code.google.com/p/android-bolt/ ).
  (issue #36)
- new Media player and Media browser for audio and video,
  internal and from SD card. (issue #59)
- Content browser:
  - MultiWordAutoComplete widget for tagging.
  - ContextMenu (issue #22)
  - Tag Cloud (issue #31)
- new (alpha) Alert Framework.
  
changes:
- NewsreaderServiceSettings moved to core.
- 'List of tags' and 'add tag' merged (issue #57).

bug fixes:
- Magnolia updated.
- Locations updated (issues #5, 9, 10, 50, 52, 54, 55, 58)
  - Add location alert.
  - Extras for locations.
- SensorSimulator testing pane: issues resolved
  (issues #43, 46).
  

---------------------------------------------------------
release: 0.1.4
date: 2008-03-12
for: Android SDK m5-rc15

new features:
- unit testing with Android-Positron.
- JavaDoc.
- Shopping list PICK_ACTION added (issue #20).
- SensorSimulator Settings (tab Testing) now supports sensor update rates.

bug fixes:
- GPS access permissions set correctly (issues #32, 38).
- Content browser list sizes adapted, bugs fixed (issues #14, 15, 19, 21).
- SensorSimulator Settings saves IP and socket settings on leaving (issue #39).

---------------------------------------------------------
release: 0.1.3
date: 2008-02-24
for: Android SDK m5-rc14

new features:
- Update to Android SDK m5.
- Content browser implemented.
- New icons in the new Android style.
- Visual appearance of applications (shopping list)
  and menus adjusted.
- Main view of OpenIntents is an icon grid on 
  tabs. This replaces the plain activity list.

---------------------------------------------------------
release: 0.1.2
date: 2008-01-28
for: Android SDK m3-rc37a

new features:
- SensorSimulator
  - tools/SensorSimulator: standalone Java application
  - connect through SensorSimulatorView
    - set global connection settings
    - test connection to SensorSimulator
  - samples/OpenGLSensors
    - Displays pyramid that always points up,
      regardless how you hold the phone.
    - Works with accelerometer and orientation sensor.
    - Displays bar magnet for compass sensor.

---------------------------------------------------------
release: 0.1.1
date: 2008-01-07
for: Android SDK m3-rc37a

new features:
- ShoppingList
  - add items to list
  - mark items (displayed as strike-through)
  - clean up list (remove marked items)
  - create new list
  - delete list

known issues:
  - no automatic scrolling for very long shopping lists

---------------------------------------------------------
release: 0.1.0
date: 2007-12-19
for: Android SDK m3-rc37a

features:
- LocationsProvider
- TagsProvider
- TagView for OpenIntent.TAG action

use cases:
- view locations:
  1) select "show locations"
  2) add current location to database (using menu)
- view and add tags
  1) select "show tags"
  2) enter tag and content in text fields (or select tag from list of content)
  3) click "add"
- use OpenIntent.TAG
  1) create a new application, import OpenIntents-n-n-n.jar
  2) create an Intent with action = OpenIntent.TAG and uri = Tags.CONTENT_URI
  3) add content to Intent using putExtrag(Tags.QUERY_URI, myContentToTag)
  4) startSubActivity using the Intent
  5) return code is Activity.RESULT_OK if a tag has been added to the myContentToTag
  

know issues:
- LocationsProvider: remove location from database not possible.
- TagsProvider: remove tags and content from database not possible.
