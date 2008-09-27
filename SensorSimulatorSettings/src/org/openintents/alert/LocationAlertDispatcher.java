package org.openintents.alert;

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

import org.openintents.provider.Alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LocationAlertDispatcher extends BroadcastReceiver {

	private Cursor mCursor;
	private int cond1Row = 0;
	private int cond2Row = 0;
	private int intentRow = 0;
	private int intentCatRow = 0;
	private int intentUriRow = 0;
	private int typeRow = 0;

	public static final String _TAG = "LocationAlertDispatcher";

	public void onReceive(Context context, Intent intent) {
		Log.d(_TAG, "Received Intent>>" + intent.getAction() + "<<");
		Log.d(_TAG, "Received Intent>>" + intent + "<<");

		Intent i = null;
		Alert.init(context);
		String position = null;
		if (intent.getData() != null && "geo:".equals(intent.getData().getScheme())) {
			position = intent.getData().toString();
		} else {
			position = intent.getStringExtra(Alert.Location.POSITION);
		}
	
		mCursor = Alert.mContentResolver
				.query(
						Alert.Location.CONTENT_URI,
						Alert.Location.PROJECTION,
						Alert.Generic.ACTIVE + " = 1 AND " //
								+ Alert.Generic.CONDITION1 + " = ?", //
						new String[] {position},
						null);
		if (mCursor != null && mCursor.getCount() > 0) {

			cond1Row = mCursor.getColumnIndex(Alert.Generic.CONDITION1);
			cond2Row = mCursor.getColumnIndex(Alert.Generic.CONDITION2);
			intentRow = mCursor.getColumnIndex(Alert.Generic.INTENT);
			intentCatRow = mCursor
					.getColumnIndex(Alert.Generic.INTENT_CATEGORY);
			intentUriRow = mCursor.getColumnIndex(Alert.Generic.INTENT_URI);
			typeRow = mCursor.getColumnIndex(Alert.Generic.TYPE);

			mCursor.moveToFirst();
			while (!mCursor.isAfterLast()) {
				i = new Intent();
				try { // TODO: INtent MIME TYPE
					
					
					i.setAction(mCursor.getString(intentRow));
					
					String cat = mCursor.getString(intentCatRow);
					if (TextUtils.isEmpty(cat)){
						cat = Intent.CATEGORY_DEFAULT;
					}
					i.addCategory(cat);
					i.setData(Uri.parse(mCursor.getString(intentUriRow)));
					Log.d(_TAG, "going to broadcast Intent:\n " + i.toString()
							+ "\n--");
					// action>>"+i.getAction()+"\n
					// category>>"+i.getCategory()+"\n
					// Data>>"+i.getData()+"\n-------");
					context.sendBroadcast(i);
					int receiverCount = context.getPackageManager().queryBroadcastReceivers(i, 0).size(); 
					if (receiverCount == 0){
						int activityCount = context.getPackageManager().queryIntentActivities(i, 0).size();
						if (activityCount > 0){
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(i);
						} else {
							Log.w(_TAG, "no activity found");
						} 
					} else {
						Log.i(_TAG, "broadcast count:" + receiverCount);							
					}
					
					
				} catch (Exception e) {
					Log.e(_TAG, "coulndt launch intent, reason>>"
							+ e.getMessage());
				}
				mCursor.moveToNext();
			}

			mCursor.close();
		}
	}
}/**/