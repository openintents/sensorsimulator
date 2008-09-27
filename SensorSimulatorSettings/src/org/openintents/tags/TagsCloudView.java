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
import org.openintents.provider.Tag;
import org.openintents.provider.Tag.Contents;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TagsCloudView extends ListActivity {

	/** tag for logging */
	private static final String TAG = "tagCloud";


	private Tag mTag;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.tags_cloud);

		mTag = new Tag(this);
		fillCloud();
		
		if (getCallingActivity() != null) {
			setTitle(R.string.tags_cloud_pick);
		}
				
	}

	private void fillCloud() {
		Cursor c = mTag.findAllUsedTags();
		startManagingCursor(c);

		if (c == null) {
			Log.e(TAG, "missing tag provider");			
			return;
		}
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.tag_row_simple, c,
				new String[] { Contents.URI },
				new int[] { R.id.tag_tag});
		setListAdapter(adapter);

	}
	
  protected void onListItemClick(ListView l, View v, int position, long id) {
		
		if (getCallingActivity() != null) {
			l.setSelection(position);
			setResult(RESULT_OK, new Intent(((Cursor) l.getSelectedItem()).getString(1)));
			finish();
		}
	}

}