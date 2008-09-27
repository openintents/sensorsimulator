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
package org.openintents.tags;


import org.openintents.R;
import org.openintents.lib.DeliciousApiHelper;
import org.openintents.provider.Tag.Tags;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MagnoliaSettings extends Activity {


	public static final String PREFS_NAME = "MagnoliaSettings";

	public static final String MAGNOLIA_API="https://ma.gnolia.com/api/mirrord/v1";

	private String mScreenName=new String();
	private String mPassWd=new String();
    private String mAPIKey=new String();

	private EditText edScreenName;
	private EditText edPassWd;

	public static final String _TAG="MagnoliaSettings";

    @Override
    protected void onCreate(Bundle state){         
       super.onCreate(state);
        
       // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mScreenName= settings.getString("screenName", "");
        mPassWd= settings.getString("passWd", "");
	    mAPIKey= settings.getString("apiKey", "");


		setContentView(R.layout.magnoliasettings);
		edScreenName=(EditText)findViewById(R.id.magnoliasettings_screenname);
		edPassWd=(EditText)findViewById(R.id.magnoliasettings_passwd);
		
		edScreenName.setText(mScreenName);
		edPassWd.setText(mPassWd);

		Button button = (Button) findViewById(R.id.magnoliasettings_import);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				importMagnoliaTags();
			}
		});
		
		button = (Button) findViewById(R.id.magnoliasettings_createaccount);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Uri joinUri = Uri.parse("http://ma.gnolia.com/join");
				Intent intent = new Intent(Intent.ACTION_VIEW, joinUri);
				startActivity(intent);
			}
		});
			
       
    }
    
	@Override
	protected void onResume(){
		super.onResume();
		Log.d(_TAG,"onResume: entering");
		Log.d(_TAG,"onResume: mScreenName>>"+mScreenName+"<<");
		Log.d(_TAG,"onResume: mPassWd>>"+mPassWd+"<<");
		Log.d(_TAG,"onResume: leaving");

	}

	protected void onFreeze(Bundle icycle){
		super.onSaveInstanceState (icycle);
		Log.d(_TAG,"onFreeze: entering");
		mScreenName=edScreenName.getText().toString();
		mPassWd=edPassWd.getText().toString();
		Log.d(_TAG,"onFreeze: leaving");
	}

    @Override
    protected void onStop(){
        super.onStop();
		Log.d(_TAG,"onStop:entering");
		mScreenName=edScreenName.getText().toString();
		mPassWd=edPassWd.getText().toString();
		// Save user preferences. We need an Editor object to
		// make changes. All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		//editor.putBoolean("silentMode", mSilentMode);
		editor.putString("screenName",mScreenName);
		editor.putString("passWd",mPassWd);
		editor.putString("apiKey",mAPIKey);

		// Don't forget to commit your edits!!!
		editor.commit();
		Log.d(_TAG,"onStop:leaving");
    }


	private void importMagnoliaTags(){
		String[] tags;
		DeliciousApiHelper dah= new DeliciousApiHelper(
			DeliciousApiHelper.MAGNOLIA_API,
			mScreenName,
			mPassWd			
		);
	
		try
		{
			tags=dah.getTags();	
		}
		catch (java.io.IOException ioe)
		{
			Log.e(_TAG,"Couldn't retrive Tags, >>"+ioe.getMessage()+"<<");
			return;
		}
		
		if (tags!=null)
		{
			int tagsLen=tags.length;	
			ContentValues cv=new ContentValues(1);
			try 
			{
				for (int i=0;i<tagsLen ;i++ )
				{
					cv.clear();
					cv.put(Tags.URI_1,tags[i]);
					getContentResolver().insert(Tags.CONTENT_URI, cv);
				}
			} catch (Exception e) {
				Log.i(_TAG, "insert of tag  failed >>"+ e.getMessage()+"<<");
				return;
			}
		}

	}

}/*eoc*/
