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
package org.openintents.alert;

import org.openintents.provider.Alert;
import org.openintents.provider.Intents;
import org.openintents.provider.Alert.Generic;
import org.openintents.provider.Alert.Location;
import org.openintents.provider.Location.Locations;
import org.openintents.OpenIntents;
import org.openintents.R;
import org.openintents.R.*;

import android.app.ListActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.content.ContentValues;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class AddGenericAlertActivity extends Activity {

	public static final int STATE_CREATE = 1001;
	public static final int STATE_EDIT = 1002;
	public static final int STATE_SUB = 1003;
	public static final int STATE_SUB_CREATE = 1004;
	public static final int STATE_SUB_EDIT = 1005;

	public static final int STATE_SAVE = 201;
	public static final int STATE_CANCEL = 202;

	private Uri cUri = null;

	private int mState;

	private Cursor mCursor;

	public static final String _TAG = "AddGenericAlertActivity";
	private static final int MENU_PICK_LOC = 1;
	private static final int MENU_PICK_ACTION = 2;
	private static final int MENU_PICK_DATE_TIME = 3;
	private static final int REQUEST_PICK_LOC = 1;
	private static final int REQUEST_PICK_ACTION = 2;
	private static final int REQUEST_PICK_DATE_TIME = 3;

	private TextView mCond1;
	private TextView mCond2;
	private TextView mIntent;
	private TextView mIntentCat;
	private TextView mIntentUri;
	private Spinner mType;

	private int cond1Row = 0;
	private int cond2Row = 0;
	private int intentRow = 0;
	private int intentCatRow = 0;
	private int intentUriRow = 0;
	private int typeRow = 0;
	private CheckBox mActive;
	private CheckBox mOnBoot;
	private int activeRow;
	private int onBootRow;

	public void onCreate(Bundle b) {

		super.onCreate(b);

		setContentView(R.layout.alert_add_generic);

		mCond1 = (TextView) findViewById(R.id.alert_addgeneric_condition1);
		mCond2 = (TextView) findViewById(R.id.alert_addgeneric_condition2);
		mIntent = (TextView) findViewById(R.id.alert_addgeneric_intent);
		mIntentCat = (TextView) findViewById(R.id.alert_addgeneric_intentcategory);
		mIntentUri = (TextView) findViewById(R.id.alert_addgeneric_intenturi);

		mType = (Spinner) findViewById(R.id.alert_addgeneric_type);
		mActive = (CheckBox) findViewById(R.id.alert_addgeneric_active);
		mOnBoot = (CheckBox) findViewById(R.id.alert_addgeneric_onboot);
			
		
		Alert.init(this);

		ArrayAdapter ad = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, new String[] {
						Alert.TYPE_GENERIC, Alert.TYPE_LOCATION,
						Alert.TYPE_DATE_TIME });
		ad
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mType.setAdapter(ad);
		mType.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView parent, View v,
					int position, long id) {
				Log.i("addGenericAlert", "select");
				updateLabels();
				
			}

			public void onNothingSelected(AdapterView arg0) {
				Log.i("addGenericAlert", "nothing");
				updateLabels();
				
			}

		});

		updateLabels();

		if (getIntent().getAction().equals(
				org.openintents.OpenIntents.ADD_GENERIC_ALERT)) {
			mState = STATE_CREATE;
			mType.setSelection(1);
			// mChannelLink.setText("EHLO CREATOR!");
			setTitle(R.string.alert_add);
			
		} else if (getIntent().getAction().equals(OpenIntents.EDIT_GENERIC_ALERT)) {
			mState = STATE_EDIT;
			setTitle(R.string.alert_edit);
			cUri = Uri.parse(getIntent().getStringExtra(Alert.EXTRA_URI));
			Log.v(_TAG, "edit " + cUri);
			
			mCursor = managedQuery(cUri, Alert.Generic.PROJECTION, null, null, null);
			cond1Row = mCursor.getColumnIndex(Alert.Generic.CONDITION1);
			cond2Row = mCursor.getColumnIndex(Alert.Generic.CONDITION2);
			intentRow = mCursor.getColumnIndex(Alert.Generic.INTENT);
			intentCatRow = mCursor
					.getColumnIndex(Alert.Generic.INTENT_CATEGORY);
			intentUriRow = mCursor.getColumnIndex(Alert.Generic.INTENT_URI);
			typeRow = mCursor.getColumnIndex(Alert.Generic.TYPE);
			activeRow = mCursor.getColumnIndex(Alert.Generic.ACTIVE);
			onBootRow = mCursor.getColumnIndex(Alert.Generic.ACTIVATE_ON_BOOT);
			
			if (mCursor.getCount() > 0) {
				mCursor.moveToFirst();

				mCond1.setText(mCursor.getString(cond1Row));
				mCond2.setText(mCursor.getString(cond2Row));
				mIntent.setText(mCursor.getString(intentRow));
				mIntentCat.setText(mCursor.getString(intentCatRow));
				mIntentUri.setText(mCursor.getString(intentUriRow));
				mType.setSelection(((ArrayAdapter) mType.getAdapter()).getPosition(mCursor.getString(typeRow)));
				mActive.setChecked(!mCursor.isNull(activeRow) && mCursor.getInt(activeRow) == 1);
				mOnBoot.setChecked(!mCursor.isNull(onBootRow) && mCursor.getInt(onBootRow) == 1);
			}

		}
		Log.d(_TAG, "state>>" + mState);

	}

	private void createDataSet() {
		ContentValues cv = new ContentValues();
		Uri typedUri = Alert.Generic.CONTENT_URI;
		String sType = (String) mType.getSelectedItem();
		if (sType.equals(Alert.TYPE_LOCATION)) {
			typedUri = Alert.Location.CONTENT_URI;
		} else if (sType.equals(Alert.TYPE_DATE_TIME)) {
			typedUri = Alert.DateTime.CONTENT_URI;
		}
		Log.d(_TAG, "creating dataset now");
		cv.put(Alert.Generic.CONDITION1, mCond1.getText().toString());
		cv.put(Alert.Generic.CONDITION2, mCond2.getText().toString());
		cv.put(Alert.Generic.INTENT, mIntent.getText().toString());
		cv.put(Alert.Generic.INTENT_CATEGORY, mIntentCat.getText().toString());
		cv.put(Alert.Generic.INTENT_URI, mIntentUri.getText().toString());
		cv.put(Alert.Generic.TYPE, sType);
		cv.put(Alert.Generic.ACTIVE, mActive.isChecked());
		cv.put(Alert.Generic.ACTIVATE_ON_BOOT, mOnBoot.isChecked());
		
		cUri = Alert.insert(typedUri, cv);
		mCursor = managedQuery(cUri, Alert.Generic.PROJECTION, null, null, null);
		//Issue 113: pick action/location adds alert
		// only allow to create an alert once, thereafter the state changes to edit.
		mState = STATE_EDIT;
	}

	private void saveDataSet() {
		Log.d(_TAG, "save dataset now");	
		
	//	Log.v(_TAG, "next: " + mCursor.next());
		Log.v(_TAG, "first: " + mCursor.moveToFirst());

		ContentValues values = new ContentValues();

		CharSequence c=null;
		String s=new String();
		//ugly hack following
		c=mCond1.getText();
		if (TextUtils.isEmpty(c))
		{
			s="";
		}else{
			s=c.toString();
		}
		values.put(Generic.CONDITION1, s);

		c=mCond2.getText();
		if (TextUtils.isEmpty(c))
		{
			s="";
		}else{
			s=c.toString();
		}

		Log.d(_TAG,"cond2row set 2 >>"+s+"<<");
		values.put(Generic.CONDITION2, s);
		

		c=mIntent.getText();
		if (TextUtils. isEmpty(c))
		{
			s="";
		}else{
			s=c.toString();
		}

		values.put(Generic.INTENT, s);		

		c=mIntentCat.getText();
		if (TextUtils. isEmpty(c))
		{
			s="";
		}else{
			s=c.toString();
		}

		values.put(Generic.INTENT_CATEGORY, s);
		
		c=mIntentUri.getText();
		if (TextUtils. isEmpty(c))
		{
			s="";
		}else{
			s=c.toString();
		}
		values.put(Generic.INTENT_URI, s);
		
		values.put(Generic.TYPE, ((String) mType.getSelectedItem()));
		
		values.put(Generic.ACTIVE, (mActive.isChecked() ? 1 : 0));

		values.put(Generic.ACTIVATE_ON_BOOT, (mOnBoot.isChecked() ? 1 : 0));
		
		Uri typedUri;
		if (Alert.TYPE_LOCATION.equals(mType.getSelectedItem())) {
			typedUri = Alert.Location.CONTENT_URI;
		} else if (Alert.TYPE_DATE_TIME.equals(mType.getSelectedItem())) {
			typedUri = Alert.DateTime.CONTENT_URI;
		} else {
			typedUri = Alert.Generic.CONTENT_URI;
		}
		Alert.update(typedUri, values, null, null);
		
		if (Alert.TYPE_LOCATION.equals(mType.getSelectedItem())){		
			ContentValues cv = new ContentValues();
			cv.put(Location.POSITION, mCond1.getText().toString());
			cv.put(Location.DISTANCE, mCond2.getText().toString());
			Alert.registerLocationAlert(cv );
		}

	}

	public void onPause() {
		super.onPause();
		Log.d(_TAG,"onPause: state is >>"+mState+"<<");

		if (mState == STATE_CREATE) {
			createDataSet();
		} else if (mState == STATE_EDIT) {
			saveDataSet();
		}
		if (mCursor != null) {
			mCursor.close();
		}

	}

	public void onResume(){
		super.onResume();
		Log.d(_TAG,"onResume: state is >>"+mState+"<<");
		
		if (mState==STATE_EDIT)
		{
			Log.d(_TAG,"onResume: Edit State, reqeuiyn cursor>>"+mCursor+"<<");
			//mCursor.requery();
			//mCursor.first();
			Log.d(_TAG,"onResume: Edit State, cursor has >>"+mCursor.getCount()+"<<  rows");

		}
		if (mState==STATE_SUB_CREATE)
		{
			mState=STATE_CREATE;
		}else if (mState==STATE_SUB_EDIT)
		{
			mState=STATE_EDIT;
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_PICK_LOC, 0, R.string.alert_pick_location);
		menu.add(0, MENU_PICK_ACTION, 0, R.string.alert_pick_action);
		//descoped
		//menu.add(0, MENU_PICK_DATE_TIME, R.string.alert_pick_date_time);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		boolean result = true;

		Intent intent;
		switch (item.getItemId()) {
		case MENU_PICK_LOC:
			intent = new Intent(Intent.ACTION_PICK, Locations.CONTENT_URI);

			if (mState==STATE_CREATE)
			{
				mState=STATE_SUB_CREATE;
			}else if (mState==STATE_EDIT)
			{
				mState=STATE_SUB_EDIT;
			}
			
			startActivityForResult(intent, REQUEST_PICK_LOC);
			break;
		case MENU_PICK_ACTION:
			intent = new Intent(Intent.ACTION_PICK, Intents.CONTENT_URI);
			intent.putExtra(Intents.EXTRA_ACTION_LIST, Intent.ACTION_VIEW);
			if (mState==STATE_CREATE)
			{
				mState=STATE_SUB_CREATE;
			}else if (mState==STATE_EDIT)
			{
				mState=STATE_SUB_EDIT;
			}

			startActivityForResult(intent, REQUEST_PICK_ACTION);
			break;
		case MENU_PICK_DATE_TIME:

			break;
		default:
			result = false;
		}

		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_PICK_ACTION:
				mIntent.setText(resultIntent.getStringExtra(Intents.EXTRA_ACTION));
				mIntentUri.setText(resultIntent.getStringExtra(Intents.EXTRA_URI));
				break;
			case REQUEST_PICK_LOC:
				mCond1.setText(resultIntent.getStringExtra(Locations.EXTRA_GEO));				
				break;
			}
		}
		//Log.v(_TAG, "onActivityResult:requery: " +mCursor.requery());

	}

	private void updateLabels() {
		String sType = (String) mType.getSelectedItem();
		int cond1;
		int cond2;
		if (sType.equals(Alert.TYPE_LOCATION)) {
			cond1 = R.string.alert_label_location;
			cond2 = R.string.alert_label_range;
		} else if (sType.equals(Alert.TYPE_DATE_TIME)) {
			cond1 = R.string.alert_label_date_time;
			cond2 = R.string.alert_label_unused;
		} else {
			cond1 = R.string.alert_label_condition1;
			cond2 = R.string.alert_label_condition2;
		}

		((TextView) findViewById(R.id.alert_addgeneric_label_condition1))
				.setText(cond1);
		((TextView) findViewById(R.id.alert_addgeneric_label_condition2))
				.setText(cond2);
	}

}/* eoc */