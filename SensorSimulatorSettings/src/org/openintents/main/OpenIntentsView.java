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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openintents.OpenIntents;
import org.openintents.R;
import org.openintents.locations.MockLocationService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.AlignmentSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;


/**
 * Main activity to start simple activities for ContentProviders.
 * 
 * Currently supported:
 * LocationsProvider
 * TagsProvider
 * 
 *
 */
public class OpenIntentsView extends Activity {

	private TabHost mTabHost;
	
	TableLayout mGridMain;
	TableLayout mGridSettings;
	
	private static final int MENU_ABOUT = Menu.FIRST;

	private static final String BUNDLE_TABHOST = "tabHost";
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		Context context = this;
        // Get the Resources object from our context
        Resources res = context.getResources();
    
		mTabHost = (TabHost)findViewById(R.id.tabhost);
		mTabHost.setup();
		
		TabSpec tabspec = mTabHost.newTabSpec("openintents");
		tabspec.setIndicator(res.getString(R.string.openintents), res.getDrawable(R.drawable.openintents002a_32));
		tabspec.setContent(R.id.content1);
		mTabHost.addTab(tabspec);
		
		tabspec = mTabHost.newTabSpec("settings");
		tabspec.setIndicator(res.getString(R.string.settings), res.getDrawable(R.drawable.settings001a_32));
		tabspec.setContent(R.id.content2);
		mTabHost.addTab(tabspec);
		
		// issue #62
		int currentTab = 0;
		if (icicle != null && icicle.containsKey(BUNDLE_TABHOST)){
			currentTab = icicle.getInt(BUNDLE_TABHOST);
		}		
		mTabHost.setCurrentTab(currentTab);
		
		// loadApps(); // do this in onresume?
		
		mGridMain = (TableLayout) findViewById(R.id.grid_main);
		mGridSettings = (TableLayout) findViewById(R.id.grid_settings);
		
		
		// fill the list manually:
		fillGrid(mGridMain, OpenIntents.MAIN_CATEGORY);
		fillGrid(mGridSettings, OpenIntents.SETTINGS_CATEGORY);
		
		// Optionally call preferences:
		OpenIntents.suggestInitDefaultValues(this);
		
		Intent mockProvider = new Intent(this, MockLocationService.class);
		startService(mockProvider );
	}

	void fillGrid(TableLayout table, String category) {
		
		// Get all actions in 'category'
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(category);

        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(mainIntent, 0);
        
        // Sort the list alphabetically
        
        final Comparator<ResolveInfo> RESOLVEINFO_ORDER =
            new Comparator<ResolveInfo>() {
				public int compare(ResolveInfo o1, ResolveInfo o2) {
					String s1 = o1.activityInfo.loadLabel(getPackageManager()).toString();
					String s2 = o2.activityInfo.loadLabel(getPackageManager()).toString();
					return s1.compareTo(s2);
			}
		};

        Collections.sort(apps, RESOLVEINFO_ORDER);
        
        // Put them into the table layout
        int max = apps.size();
        int row = 0;
        int col = 0;
        int pos = 0;
        int colmax = 3;
        TableRow rowview = new TableRow(this);

        while (pos < max) {
        	LinearLayout ll = getCustomButton(apps, pos);
        	if (col == 0) {
        		rowview.addView(ll, new TableRow.LayoutParams(1));
        	} else {
        		rowview.addView(ll, new TableRow.LayoutParams());
        	}
        	col++;
        	if (col >= colmax) {
        		col = 0;
        		row++;
        		table.addView(rowview, new TableLayout.LayoutParams());
        		rowview = new TableRow(this);
        	};
        	pos++;
        }
        if (col > 0) {
        	table.addView(rowview, new TableLayout.LayoutParams());
        }
        
	}
	
	private LinearLayout getCustomButton(List<ResolveInfo> apps, int pos) {
		ResolveInfo info = apps.get(pos);

    	// Add image:
    	ImageView iv = new ImageView(this);
        iv.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
        iv.setScaleType(ImageView.ScaleType.FIT_END);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        		48,
        		48);
        lp.topMargin = 10;
        iv.setLayoutParams(lp);
        
        
        // Add label:
        TextView tv = new TextView(this);
        lp = new LinearLayout.LayoutParams(
        		85,
        		35);
        lp.bottomMargin = 6;
        tv.setLayoutParams(lp);
        
        tv.setText(info.activityInfo.loadLabel(getPackageManager()));
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(15);
        
        // First convert text to 'spannable'
		tv.setText(tv.getText(), TextView.BufferType.SPANNABLE);
		Spannable str = (Spannable) tv.getText();
		
		// Align center
     	str.setSpan(
				new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 
				0, tv.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
        
        // Add both to a custom button:
        LinearLayout ll = new LinearLayout(this);
        lp = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.WRAP_CONTENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(iv);
        ll.addView(tv);
        ll.setBackgroundResource(android.R.drawable.btn_default);
        ll.setFocusable(true);
        ll.setClickable(true);
        ll.setGravity(Gravity.CENTER);
        
        // Make the button clickable:
        ll.setOnClickListener(new myOnClickListener(info));
        
		return ll;
	}
	
	class myOnClickListener implements OnClickListener {
		ResolveInfo mInfo;
		
		myOnClickListener(ResolveInfo info) {
			mInfo = info;
		}
		
		
		public void onClick(View arg0) {
			Intent intent = new Intent(
					Intent.ACTION_MAIN, 
					null);
			intent.setClassName(
					mInfo.activityInfo.packageName, 
					mInfo.activityInfo.name);
			startActivity(intent);
		}
	}
	
	/////////////////////////////////////////////////////////
	// Menu

	/**
	 * Creates the menu structure.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Standard menu
		menu.add(0, MENU_ABOUT, 0, R.string.about)
			.setIcon(R.drawable.about001a)
			.setShortcut('0', 'a');
	
		// Generate any additional actions that can be performed.
        // This allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(
            Menu.CATEGORY_ALTERNATIVE, 0, 0, new ComponentName(this, OpenIntentsView.class),
            null, intent, 0, null);
        
        return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		// Nothing to be done here.
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ABOUT:
			showAboutDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	
	private void showAboutDialog() {
		Intent intent = new Intent(OpenIntentsView.this, About.class);
		startActivity(intent);
	};
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {	
		super.onSaveInstanceState (outState);
		outState.putInt(BUNDLE_TABHOST, mTabHost.getCurrentTab());
	}

}
