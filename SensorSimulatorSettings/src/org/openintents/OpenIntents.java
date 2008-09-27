/* 
 * Copyright (C) 2007-2008 OpenIntents.org
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

package org.openintents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Provides OpenIntents action and category specifiers. <p/> These specifiers
 * extend the standard Android specifiers.
 */
public abstract class OpenIntents {
	// !! CAREFUL !!
	// If you change any of the string definitions, you have
	// to change it in all Manifests that use them as well!

	// -----------------------------------------------
	// Tags
	// -----------------------------------------------
	/**
	 * Identifier for tag action.
	 */
	public static final String TAG_ACTION = "org.openintents.action.TAG";

	// -----------------------------------------------
	// Shopping
	// -----------------------------------------------
	/**
	 * Change share settings for an item.
	 * 
	 * Currently implemented for shopping list.
	 */
	public static final String SET_SHARE_SETTINGS_ACTION = "org.openintents.action.SET_SHARE_SETTINGS";

	/**
	 * Change theme settings or appearance for an item.
	 * 
	 * Currently implemented for shopping list.
	 */
	public static final String SET_THEME_SETTINGS_ACTION = "org.openintents.action.SET_THEME_SETTINGS";

	/**
	 * Broadcasts updated information about an item or a list.
	 * 
	 * If the list does not exist on one of the recipients, it is created. If
	 * the item does not exist, it is created. This action is intended to be
	 * received through GTalk or XMPP.
	 */
	public static final String SHARE_UPDATE_ACTION = "org.openintents.action.SHARE_UPDATE";

	/**
	 * Inserts an item into a shared shopping list.
	 * 
	 * This action is intended to be received through GTalk or XMPP.
	 */
	public static final String SHARE_INSERT_ACTION = "org.openintents.action.SHARE_INSERT";

	/**
	 * Notifies a list that the content changed.
	 */
	public static final String REFRESH_ACTION = "org.openintents.action.REFRESH";

	/**
	 * Adds a location alert to a specific item.
	 * 
	 * Currently implemented for shopping list.
	 */
	public static final String ADD_LOCATION_ALERT_ACTION = "org.openintents.action.ADD_LOCATION_ALERT";

	public static final String LOCATION_ALERT_DISPATCH="org.openintents.action.LOCATION_ALERT_DISPATCH";
	public static final String DATE_TIME_ALERT_DISPATCH="org.openintents.action.DATE_TIME_ALERT_DISPATCH";
	public static final String SERVICE_MANAGER="org.openintents.action.SERVICE_MANAGER";

	// -----------------------------------------------
	// Categories
	// -----------------------------------------------
	/**
	 * Main category specifier. <p/> Applications placed into this category in
	 * the AndroidManifest.xml file are displayed in the main view of
	 * OpenIntents.
	 */
	public static final String MAIN_CATEGORY = "org.openintents.category.MAIN";

	/**
	 * Settings category specifier. <p/> Applications placed into this category
	 * in the AndroidManifest.xml file are displayed in the settings tab of
	 * OpenIntents.
	 */
	public static final String SETTINGS_CATEGORY = "org.openintents.category.SETTINGS";

	/** identifier for adding generic alerts action. 
	*@deprecated will be removed by 0.2.1 latest
	*/
	public static final String ADD_GENERIC_ALERT = "org.openintents.action.ADD_GENERIC_ALERT";
	/** identifier for adding generic alerts action. 
	*@deprecated will be removed by 0.2.1 latest
	*/
	public static final String EDIT_GENERIC_ALERT = "org.openintents.action.EDIT_GENERIC_ALERT";



	/**
	 * shows an English message if open intents is not installed, finishes the activity after user clicked "ok". 
	 * @param activity
	 */
	public static final void requiresOpenIntents(final Activity activity) {
		try {
			activity.getPackageManager().getPackageInfo("org.openintents", 0);
		} catch (NameNotFoundException e) {
			new AlertDialog.Builder(activity).setTitle("Warning").setMessage("Requires OpenIntents! Please install the open intents application from www.openintents.org first.")
			.setPositiveButton("ok", new OnClickListener() {

				public void onClick(DialogInterface arg0,
						int arg1) {
					activity.finish();
				}

			})
			.show();

		}
	}
	
	/**
	 * calls the InitDefaultValues activity (unless unchecked).
	 * 
	 */
	public static final void suggestInitDefaultValues(final Activity activity) {
		SharedPreferences prefs = activity.getSharedPreferences(PREFERENCES_INIT_DEFAULT_VALUES, 0);
    	boolean b = prefs.getBoolean(PREFERENCES_DONT_SHOW_INIT_DEFAULT_VALUES, false);
    	if (b == false) {
    		// User does not want to see intro screen again.
    		Intent intent = new Intent();
    		intent.setClassName("org.openintents","org.openintents.main.InitView");
    		activity.startActivity(intent);
			
    	}
    	
	}
	public static final String PREFERENCES_INIT_DEFAULT_VALUES = "InitView";
	public static final String PREFERENCES_DONT_SHOW_INIT_DEFAULT_VALUES = "dontShowInitDefaultValues";
}
