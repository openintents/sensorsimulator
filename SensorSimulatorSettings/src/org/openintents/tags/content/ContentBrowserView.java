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

package org.openintents.tags.content;

import java.util.ArrayList;

import org.openintents.R;
import org.openintents.provider.Tag;
import org.openintents.provider.ContentIndex.Dir;
import org.openintents.provider.Tag.Tags;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * View to show tags in a hierarchical manner.
 * 
 * 
 * 
 */
public class ContentBrowserView extends ListActivity implements Runnable {

	// tag for logging
	private static final String TAG = "tagHierarchyView";

	// resource
	private static final int MENU_ADD_TAG = 1;
	private static final int MENU_VIEW_CONTENT = 2;
	private static final int MENU_REMOVE_TAG = 3;
	private static final int MENU_PACKAGES = 4;
	private static final int MENU_ADD_ANY_TAG = 5;

	protected static final int REQUEST_DIR_PICK = 1;
	protected static final int REQUEST_CONTENT_PICK = 2;
	protected static final int REQUEST_TAG_PICK = 3;

	private AutoCompleteTextView mTagFilter;

	private Tag mTags;

	private Cursor mContentCursor;

	private TextView mEmptyResultView;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param icicle
	 *            bundle
	 */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.tags_content_browser);

		getListView().setOnCreateContextMenuListener(
				new View.OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu contextmenu,
							View view, ContextMenu.ContextMenuInfo obj) {
						contextmenu.add(0, MENU_REMOVE_TAG,0,
								R.string.tags_remove_tag).setIcon(
								R.drawable.tag_delete001a);
						contextmenu.add(0, MENU_VIEW_CONTENT,0,
								R.string.tags_view_content).setIcon(
								R.drawable.view_001a);
					}

				});

		mTagFilter = (AutoCompleteTextView) findViewById(R.id.tag_filter);

		mTagFilter.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence cs, int a, int b, int c) {
				fillDataTaggedContent();
			}

			public void beforeTextChanged(CharSequence cs, int a, int b, int c) {
				// do nothing
			}
			public void afterTextChanged(Editable editable){
				// do nothing
			}
		});

		mEmptyResultView = (TextView) findViewById(android.R.id.empty);

		mTags = new Tag(this);

		ImageButton searchButton = (ImageButton) findViewById(R.id.tags_search_button);
		searchButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				Intent intent = new Intent(Intent.ACTION_PICK, Tags.CONTENT_URI);
				startActivityForResult(intent, REQUEST_TAG_PICK);
			}

		});

		fillDataTagFilter();

		// load directories from xml
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		if (mTagFilter != null && mEmptyResultView != null) {
			if (TextUtils.isEmpty(mTagFilter.getText())) {
				mEmptyResultView.setText(R.string.empty_tag);
			} else {
				mEmptyResultView.setText(R.string.no_tags);
			}
		}
	}

	/**
	 * fill data for filter (auto complete).
	 */
	private void fillDataTagFilter() {
		// Get a cursor with all tags (used and unused)
		Cursor c = mTags.findAllUsedTags();
		startManagingCursor(c);

		if (c == null) {
			Log.e(TAG, "missing tag provider");
			mTagFilter.setAdapter(new ArrayAdapter(this,
					android.R.layout.simple_list_item_1,
					new String[] { "no tag provider" }));
			return;
		}

		ArrayList<String> list = new ArrayList<String>();
		// TODO add functionallity for all
		//list.add(getString(R.string.tag_all));
		while (c.moveToNext()) {
			list.add(c.getString(1));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		mTagFilter.setAdapter(adapter);

	}

	/**
	 * fill main list.
	 */
	private void fillDataTaggedContent() {

		// Get a cursor with all content of the selected tag
		mContentCursor = getContentResolver().query(Tags.CONTENT_URI,
				new String[] { Tags._ID, Tags.URI_2 }, "content1.uri like ?",
				new String[] { mTagFilter.getText().toString() },
				"content1.uri");
		startManagingCursor(mContentCursor);

		ListAdapter result;
		if (mContentCursor == null) {
			Log.e(TAG, "missing tag provider");
			result = new ArrayAdapter(this,
					android.R.layout.simple_list_item_1,
					new String[] { "no tag provider" });

		} else {
			result = new ContentListAdapter(mContentCursor, this);
		}
		setListAdapter(result);
		
		if (mTagFilter != null && mEmptyResultView != null) {
			if (TextUtils.isEmpty(mTagFilter.getText())) {
				mEmptyResultView.setText(R.string.empty_tag);
			} else {
				mEmptyResultView.setText(R.string.no_tags);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu
				.add(0, MENU_ADD_TAG, 0, R.string.tags_add_tag).setIcon(
						R.drawable.tag_add001a);
		menu.add(0, MENU_ADD_ANY_TAG, 0, R.string.tags_add_any_tag).setIcon(
				R.drawable.tag_add001a);

		menu.add(0, MENU_PACKAGES, 0, R.string.menu_package_list).setIcon(
				R.drawable.tagging_packages001a);

		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case MENU_VIEW_CONTENT:
			viewContent(menuInfo.position);
			break;
		case MENU_REMOVE_TAG:
			removeTag(menuInfo.position);
			break;
		}

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);

		Intent intent;
		switch (item.getItemId()) {
		case MENU_ADD_TAG:
			// pick a directory, expect a content uri of the given directory as
			// return value
			intent = new Intent(Intent.ACTION_PICK, Dir.CONTENT_URI);
			startActivityForResult(intent, REQUEST_DIR_PICK);
			break;
		case MENU_ADD_ANY_TAG:
			// query for a string as uri and
			// start tag action
			// data is the picked content
			String tag = mTagFilter.getText().toString();
			mTags.startAddTagActivity(tag, null);
			break;
		case MENU_PACKAGES:
			startActivity(new Intent(this, PackageList.class));
			break;
		}

		return true;
	}

	private void removeTag(int position) {
		String tag = mTagFilter.getText().toString();
		String uri = ((Cursor) getListAdapter().getItem(position)).getString(1);
		mTags.removeTag(tag, uri);
	}

	private void viewContent(int position) {
		Intent intent;
		String uri = ((Cursor) getListAdapter().getItem(position)).getString(1);
		try {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			intent = null;
		}
		if (intent != null) {
			if (getPackageManager().resolveActivity(intent, 0) != null) {
				startActivity(intent);
			} else {
				new AlertDialog.Builder(this).setTitle("info").setMessage("content can not be shown").show();
			}

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent resultIntent) {
		super.onActivityResult(requestCode, resultCode, resultIntent);

		
		String tag = mTagFilter.getText().toString();
		String data;
		if (resultIntent != null){
			data = resultIntent.getAction();
		} else {
			data = null;
		}
		switch (requestCode) {
		case REQUEST_DIR_PICK:
			if (data != null) {
				// data is the picked content directory
				Uri uri = Uri.parse(data);
				Intent intent = new Intent(Intent.ACTION_PICK, uri);
				if (getPackageManager().resolveActivity(intent, 0) != null) {
					startActivityForResult(intent, REQUEST_CONTENT_PICK);
				} else {
					new AlertDialog.Builder(this).setTitle("info").setMessage("no pick activity for "
							+ data).show();					
				}
			}
			break;
		case REQUEST_CONTENT_PICK:
			if (data != null) {
				// data is the picked content
				mTags.startAddTagActivity(tag, data);
			}
			break;
		case REQUEST_TAG_PICK:
			if (data != null) {
				mTagFilter.setText(data);
			}
		}
	}

	@Override
	protected void onListItemClick(ListView listview, View view, int i, long l1) {
		setSelection(i);
	}

	public void run() {
		DirectoryRegister r = new DirectoryRegister(this);
		Resources res = getResources();
		try {
			r.fromXML(res.openRawResource(R.raw.browser));
			r.fromXML(res.openRawResource(R.raw.contacts));
			r.fromXML(res.openRawResource(R.raw.notepad));
			r.fromXML(res.openRawResource(R.raw.media));
			r.fromXML(res.openRawResource(R.raw.shopping));
			r.fromXML(res.openRawResource(R.raw.location));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(String uri) {
		Log.i("contentbrowser", "delete tags " + uri);
		mTags.removeAllTags(uri);

	}
}