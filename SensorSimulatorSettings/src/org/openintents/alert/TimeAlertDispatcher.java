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


public class TimeAlertDispatcher extends BroadcastReceiver{


	private Cursor mCursor;
	private long now=0;


	public void onReceive(Context context,Intent intent){

//			now=System.getCurrentMillis();
			//TODO: compute dat/time from millis +- offset for finding intentn.
			Alert.mContentResolver=context.getContentResolver();
			mCursor=Alert.mContentResolver.query(Alert.DateTime.CONTENT_URI,Alert.DateTime.PROJECTION,null,null,null);

			if (mCursor.getCount()>0)
			{
				mCursor.moveToFirst();



			}
				


	}



}/*eoc*/