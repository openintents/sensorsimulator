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

package org.openintents.sensorsimulator.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Definition for content provider related to hardware.
 * Stores hardware abstraction and hardware simulator related data.
 *
 */
public abstract class SensorSimulator {
	
	/**
	 * TAG for logging.
	 */
	private static final String TAG = "SensorSimulator";
	
	/**
	 * Hardware preferences.
	 * Simple table to store name-value pairs.
	 */
	public static final class Settings implements BaseColumns {
		/**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI
                = Uri.parse(
                		"content://org.openintents.sensorsimulator/settings");

        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = "_id ASC";
        
       
        /**
         * The key of the setting.
         * <P>Type: TEXT</P>
         */
        public static final String KEY = "key";

        /**
         * An value of the setting.
         * <P>Type: TEXT</P>
         */
        public static final String VALUE = "value";

    }

	// Some default settings values
	/**
	 * The key for IP address.
	 * Value: 'IP address'.
	 */
	public static final String KEY_IPADDRESS = "IP address";
	
	/**
	 * The key for socket.
	 * Value: 'socket'.
	 */
	public static final String KEY_SOCKET = "Socket";
	
	/**
	 * The default value for socket.
	 * Value: 8010.
	 */
	public static final String DEFAULT_SOCKET = "8010";
	
	/**
	 * Default projection of table columns.
	 */
	public static final String[] SENSORSIMULATOR_PROJECTION = 
		new String[] { Settings._ID, Settings.KEY, Settings.VALUE };
}
