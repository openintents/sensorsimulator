package org.openintents.actions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.openintents.OpenIntents;
import org.openintents.R;
import org.openintents.provider.Intents;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class TypesListView extends ListActivity {

	private static final String LOG_TAG = "intentsListView";

	protected static final int REQUEST_PICK = 1;
	private static final int REQUEST_VIEW_URI = 2;

	/** extra bundle name for type information of picked intent */
	public static final String EXTRA_TYPE = "type";
	/** extra bundle name for action name of picked intent */
	public static final String EXTRA_ACTION = "action";
	/**
	 * extra bundle name for uri of picked intent, used alternatively to
	 * EXTRA_TYPE
	 */
	public static final String EXTRA_URI = "uri";

	private static final int MENU_VIEW_ALL = 0;

	LayoutParams params = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);

	final int flags = PackageManager.GET_RESOLVED_FILTER;

	protected Dialog mDialog ;

	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();
	// Create runnable for posting
	final Runnable mUpdateList = new Runnable() {
		public void run() {
			updateList();
		}

	};

	private MatrixCursor mTypesCursor = null;

	private HashSet<String> mActionSet;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.intents_types_view);
		mDialog = new Dialog(this);
		
		Thread t = new Thread() {
			@Override
			public void run() {
				createTypesList();
				mHandler.post(mUpdateList);
			}

		};
		t.start();
	}

	protected void updateList() {
		if (getCallingActivity() != null){
			if (mActionSet.size() == 1){
				setTitle(getResources().getString(R.string.intents_list_pick_type, mActionSet.iterator().next()));
			} else {
				setTitle(getResources().getString(R.string.intents_list_pick_type_action, mActionSet.size()));				
			}
		}

		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				TypesListView.this, R.layout.intents_types, mTypesCursor,
				new String[] { "type", "actions" }, new int[] {
						R.id.intents_types_name, R.id.intents_types_icon });
		adapter.setViewBinder(new ViewBinder() {

			public boolean setViewValue(View view, Cursor cursor, int i) {
				if (view instanceof TextView) {
					String type = cursor.getString(i);
					String[] typeParts = type.split("/");
					CharSequence translatedType = type;
					if (typeParts.length == 2){
						if (Intents.TYPE_PREFIX_DIR.equals(typeParts[0])){
							translatedType = getResources().getString(R.string.intents_list_of, typeParts[1]);
						} else if (Intents.TYPE_PREFIX_ITEM.equals(typeParts[0])){
							translatedType = getResources().getString(R.string.intents_item_of, typeParts[1]);
						}
					}					
					((TextView) view).setText(translatedType);
					
				} else if (view instanceof ImageView) {
					Drawable icon = getIcon(cursor.getString(0), cursor
							.getString(1));
					((ImageView) view).setImageDrawable(icon);
				}
				return true;
			}

		});
		setListAdapter(adapter);

		getListView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView adapterview, View view,
							int i, long l) {
						getListView().setSelection(i);
						MatrixCursor cursor = (MatrixCursor) getListAdapter()
								.getItem(i);

						String actionList = cursor.getString(1);
						String[] actions = actionList.split(" ");
						// requires pick action request if type has more than
						// one action
						// and this activity was initialized with a different
						// action than
						// this type allows.
						boolean requiresPickRequest = actions.length > 1;
						String action = null;
						if (!requiresPickRequest) {
							action = actions[0];
						} else {
							requiresPickRequest = (mActionSet.size() > 1 || !Arrays
									.asList(actions).contains(
											mActionSet.iterator().next()));
							if (!requiresPickRequest) {
								action = mActionSet.iterator().next();
							}
						}

						if (requiresPickRequest) {
							Intent intent = new Intent(TypesListView.this,
									PickStringView.class);
							intent.putExtra(PickStringView.EXTRA_LIST,
									actionList);
							startActivityForResult(intent, REQUEST_PICK);
						} else {
							completeActionPick(action, cursor.getString(0));
						}

					}

				});

	}

	private void createTypesList() {
		if (mTypesCursor == null) {
			HashMap<String, ArrayList<IntentFilter>> map = createIntentsMap();
		
			ArrayList<ArrayList> list = new ArrayList<ArrayList>();

			for (Entry<String, ArrayList<IntentFilter>> e : map.entrySet()) {
				StringBuffer sb = new StringBuffer();
				HashSet<String> actions = new HashSet<String>();
				for (IntentFilter i : e.getValue()) {
					if (i.actionsIterator() != null) {
						for (Iterator<String> i2 = i.actionsIterator(); i2
								.hasNext();) {
							String action = i2.next();
							if (!actions.contains(action)) {
								sb.append(action + " ");
								actions.add(action);
							}
						}
					} else {
						Log.w(LOG_TAG, " no actions for " + i);
					}
				}

				ArrayList<Object> row = new ArrayList<Object>();
				row.add(e.getKey());
				row.add(sb);
				Log.v(LOG_TAG, e.getKey() + " " + sb + " added.");
				list.add(row);
			}

			// comparator for a list of rows containing only string as first
			// element.
			final Comparator<ArrayList> comparator = new Comparator<ArrayList>() {
				public int compare(ArrayList object1, ArrayList object2) {
					return ((String) object1.get(0))
							.compareToIgnoreCase((String) object2.get(0));
				}
			};

			Collections.sort(list, comparator);

			mTypesCursor = new MatrixCursor(
					new String[] { "type", "actions" });
			for (ArrayList<Object> row: list)  {
				mTypesCursor.addRow(row);
			}
		}

	}

	private Drawable getIcon(String type, String actionList) {
		Intent intent = new Intent();
		intent.setDataAndType(null, type);
		if (actionList.contains(Intent.ACTION_VIEW)) {
			intent.setAction(Intent.ACTION_VIEW);
		} else {
			intent.setAction(actionList.split(" ")[0]);
		}
		try {
			return getPackageManager().getActivityIcon(intent);
		} catch (NameNotFoundException e) {
			return getPackageManager().getDefaultActivityIcon();
		}
	}

	private HashMap<String, ArrayList<IntentFilter>> createIntentsMap() {

		// inspect actions defined at Intent.class
		HashMap<String, ArrayList<IntentFilter>> map = new HashMap<String, ArrayList<IntentFilter>>();

		mActionSet = new HashSet<String>();
		if (getIntent() != null) {
			if (getIntent().getBooleanExtra(Intents.EXTRA_ANDROID_ACTIONS,
					false)) {

				for (Field f : Intent.class.getFields()) {

					if (f.getName().endsWith("ACTION")) {
						String action = null;
						try {
							action = (String) f.get(Intent.class);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}

						if (action != null) {
							mActionSet.add(action);
							addIntentsToMap(map, action);
						}
					}
				}
			}

			String actionList = getIntent().getStringExtra(
					Intents.EXTRA_ACTION_LIST);
			if (actionList != null) {
				String[] actions = actionList.split(",");
				for (String action : actions) {
					mActionSet.add(action);
					addIntentsToMap(map, action.trim());
				}
			}
		}

		// return result
		return map;
	}

	private void addIntentsToMap(HashMap<String, ArrayList<IntentFilter>> map,
			String action) {

		// prepare intent
		Intent intent = new Intent();
		intent.setDataAndType(null, "*/*");
		intent.setAction(action);

		List<ResolveInfo> activities = this.getPackageManager()
				.queryIntentActivities(intent, flags);
		// Log.i(LOG_TAG, intent + ":" + activities.size());

		resolveListToMap(intent, activities, map);
	}

	private void resolveListToMap(Intent intent, List<ResolveInfo> activities,
			HashMap<String, ArrayList<IntentFilter>> map) {

		for (ResolveInfo ri : activities) {
			StringBuffer fi = new StringBuffer();
			if (ri.filter != null) {
				Iterator<String> i = ri.filter.typesIterator();
				if (i != null) {
					while (i.hasNext()) {
						String type = i.next();
						fi.append(type);
						ArrayList<IntentFilter> set = map.get(type);
						if (set == null) {
							set = new ArrayList<IntentFilter>();
							map.put(type, set);
						}
						set.add(ri.filter);
						// Log.i("test", type + ": " + intent);

					}
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		String type = ((MatrixCursor) getListAdapter().getItem(
				getSelectedItemPosition())).getString(0);

		switch (requestCode) {
		case REQUEST_PICK:
			completeActionPick(resultIntent.getAction(), type);
			break;
		case REQUEST_VIEW_URI:
			Intent finishIntent = new Intent();
			finishIntent.putExtra(Intents.EXTRA_ACTION, Intent.ACTION_VIEW);
			finishIntent.putExtra(Intents.EXTRA_URI, resultIntent.getAction());			
			finishOrAction(finishIntent);
		}
	}

	private void completeActionPick(String action, String type) {
		if (Intent.ACTION_VIEW.equals(action)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setDataAndType(null, type);
			if (getPackageManager().resolveActivity(intent, 0) == null) {
				intent.setAction(Intent.ACTION_GET_CONTENT);
			}
			if (getPackageManager().resolveActivity(intent, 0) != null) {
				startActivityForResult(intent, REQUEST_VIEW_URI);
			} else {
				Intent resultIntent = new Intent();
				resultIntent.putExtra(Intents.EXTRA_ACTION, action);
				resultIntent.putExtra(Intents.EXTRA_TYPE, type);
				finishOrAction(resultIntent);
			}
		} else {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(Intents.EXTRA_ACTION, action);
			resultIntent.putExtra(Intents.EXTRA_TYPE, type);
			finishOrAction(resultIntent);
		}
	}

	private void finishOrAction(Intent resultIntent) {
		if (getCallingActivity() != null) {
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		} else {
			Intent intent = new Intent();
			intent.setAction(resultIntent.getStringExtra(Intents.EXTRA_ACTION));
			Uri uri = null;
			String uriString = resultIntent.getStringExtra(Intents.EXTRA_URI);
			if (uriString != null) {
				uri = Uri.parse(uriString);
			}
			intent.setDataAndType(uri, resultIntent.getStringExtra(Intents.EXTRA_TYPE));
			startActivity(intent);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_VIEW_ALL:
			Intent intent = new Intent(this, IntentsListView.class);
			intent.putExtra(Intents.EXTRA_ANDROID_ACTIONS, true);
			intent.putExtra(Intents.EXTRA_ACTION_LIST, OpenIntents.TAG_ACTION);
			startActivity(intent);
			break;
		}
		return true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_VIEW_ALL, 0, R.string.intents_list);

		return true;
	}

}
