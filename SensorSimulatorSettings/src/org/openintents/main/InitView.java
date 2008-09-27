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

package org.openintents.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openintents.R;
import org.openintents.provider.Location;
import org.openintents.provider.News;
import org.openintents.provider.Tag;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Init dialog
 * 
 * @author openintent.org
 * 
 */
public class InitView extends Activity {

	private static final String LOG_TAG = "init_OI";
	private Location mLocation;
	private Tag mTag;
	private LinearLayout mMainView;
	private LinearLayout mRow;
	private CheckBox mCheckbox;

	@Override
	protected void onCreate(Bundle icicle) {

		super.onCreate(icicle);

		mMainView = new LinearLayout(this);
		mMainView.setGravity(Gravity.LEFT);
		mMainView.setOrientation(LinearLayout.VERTICAL);
		setContentView(mMainView);
		setTheme(android.R.style.Theme_Dialog);

		mLocation = new Location(this.getContentResolver());
		mTag = new Tag(this);
		News.mContentResolver = this.getContentResolver();

		//////////////////////////////////////////////////////////////////////
		//strange way to create a separator, i know ;) (zero)
		TextView t = createTextView();
		t.setHeight(20);
		t.setWidth(1);
		
		t = createTextView();
		t.setText("Welcome! To help you get started, we have "
				+ "prepared a couple of default values.");
		
		//////////////////////////////////////////////////////////////////////
		//strange way to create a separator, i know ;) (zero)
		t = createTextView();
		t.setHeight(20);
		t.setWidth(1);
		
		t = createTextView();
		t.setText("Here you can add default entries for locations.");
			//+ "and copy an MP3 song to the SD card.");
		
		Button button;		
		/*
		Button button = createButton();
		button.setText(R.string.init_add_all_values);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				addLocations();
				//addMusicFiles();
				Toast.makeText(InitView.this, R.string.init_done, Toast.LENGTH_SHORT).show();
			}

		});
		*/
		
		createRow();
		
		button = createButtonInRow();
		button.setText(R.string.init_add_locations);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				addLocations();
				Toast.makeText(InitView.this, R.string.init_done, Toast.LENGTH_SHORT).show();
			}

		});
		/*
		button = createButtonInRow();
		button.setText("Add default mp3");
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (addMusicFiles()) {
					Toast.makeText(InitView.this, R.string.init_done, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(InitView.this, "Problem adding file to SD card. Is SD card installed?", 
							Toast.LENGTH_LONG).show();
				}
			}

		});
		*/

		//////////////////////////////////////////////////////////////////////
		//strange way to create a separator, i know ;) (zero)
		t = createTextView();
		t.setHeight(20);
		t.setWidth(1);
		
		t = createTextView();
		t.setText("Use the following button to check "
				+ "whether the entries have been inserted:");
		
		createRow();
		
		button = createButtonInRow();
		button.setText(R.string.init_view_locations);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClassName("org.openintents","org.openintents.locations.LocationsView");
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				startActivity(intent);

			}

		});


		/*
		button = createButtonInRow();
		button.setText("View SD card content");
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						android.provider.MediaStore.Audio.Media
				        .EXTERNAL_CONTENT_URI);
				//intent.setClassName("org.openintents","org.openintents.applications.newsreader.Newsreader");
				//intent.setAction("org.openintents.action.SHOW_NEWSFEEDS");
				//intent.addCategory(Intent.DEFAULT_CATEGORY);
				startActivity(intent);
			}

		});
		*/
		
		mCheckbox = new CheckBox(this);
		android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mCheckbox.setLayoutParams(params);
		mMainView.addView(mCheckbox);
		mCheckbox.setText("Don't show again.");
		

		t = createTextView();
		t.setText("You can always access this activity from the "
				+"Settings tab of OpenIntents.");
		
	}
	
	@Override    
    protected void onResume() {
    	super.onResume();
    	
    	SharedPreferences prefs = getSharedPreferences(
    			org.openintents.OpenIntents.PREFERENCES_INIT_DEFAULT_VALUES, 0);
    	boolean b = prefs.getBoolean(
    			org.openintents.OpenIntents.PREFERENCES_DONT_SHOW_INIT_DEFAULT_VALUES, 
    			false);
    	mCheckbox.setChecked(b);
    	
	}
	
	@Override    
    protected void
    onPause() {
    	super.onPause();
    	SharedPreferences.Editor editor = getSharedPreferences(
    			org.openintents.OpenIntents.PREFERENCES_INIT_DEFAULT_VALUES, 0)
    			.edit();
    	editor.putBoolean(
    			org.openintents.OpenIntents.PREFERENCES_DONT_SHOW_INIT_DEFAULT_VALUES,
    			mCheckbox.isChecked());
    	editor.commit();
	}

	private Button createButton() {
		Button button = new Button(this);
		android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(params);
		mMainView.addView(button);
		return button;
	}
	
	private TextView createTextView() {

		//strange way to create a separator, i know ;) (zero)
		TextView t=new TextView(this);
		android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		t.setLayoutParams(params);
		mMainView.addView(t);
		return t;
	}
	
	private void createRow() {
		mRow = new LinearLayout(this);
		mRow.setGravity(Gravity.LEFT);
		mRow.setOrientation(LinearLayout.HORIZONTAL);
		android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mRow.setLayoutParams(params);
		mMainView.addView(mRow);
	}
	
	private Button createButtonInRow() {
		Button button = new Button(this);
		android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				0,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		button.setLayoutParams(params);
		mRow.addView(button);
		return button;
	}
	
	
	protected void addLocations() {

		Object[] locations = new Object[] {//				
		37.421902, -122.101198, "Rainbow grocery", 37.433378, -122.106258,
				"Alemany Farmer's Market", 37.449353, -122.119524,
				"Trader Joe's", 37.454663, -122.130391, "Cheese Plus",
				37.458778, -122.137215, "Gino's Grocery Co" };
		android.location.Location loc = new android.location.Location((String) null);
		for (int i = 0; i < locations.length / 3; i++) {
			loc.setLatitude((Double) locations[i * 3]);
			loc.setLongitude((Double) locations[i * 3 + 1]);
			Uri locUri = mLocation.addLocation(loc);
			Log.v(LOG_TAG, locUri.toString());
			mTag.insertTag((String) locations[i * 3 + 2], locUri.toString());
			mTag.insertTag("Shop", locUri.toString());
		}		
	}

	/** 
	 * 
	 * @return true if successful.
	 */
	public boolean addMusicFiles() {
/*
		int result=0;
       // Load the sample file and put them into our data directory:
       boolean success = false;
       
       
       try {
			   InputStream is=getResources().openRawResource(R.raw.ack_syn);
			   // Obtain path to SD card from environment:
			   String sdcardpath = android.os.Environment
			   		.getExternalStorageDirectory()
			   		.getAbsolutePath();
			   String filename = sdcardpath + "/" + "ack_syn.mp3";
			   
			   FileOutputStream fos=new FileOutputStream(new File(filename));
               int size = is.available();
               byte[] buffer = new byte[size];
               is.read(buffer);
               fos.write(buffer, 0, size);
               fos.close();
               is.close();
			   result++;
			   success = true;
       } catch (IOException e) {
		   Log.e("","not copy");
		   Log.e("","reasong >"+e.toString());
//		   e.printStackTrace();
		   
               // Should never happen
 //          throw new RuntimeException(e);
       }

       if (success) {
    	   // Start Media scanner:
    	   Intent intent = new Intent(Intent.ACTION_MAIN);
			ComponentName component = new ComponentName("com.google.android.development",
					"com.google.android.development.MediaScannerActivity");
			intent.setComponent(component);
			startActivity(intent);
       }
       return success;
       */
		return true;
	}

}
