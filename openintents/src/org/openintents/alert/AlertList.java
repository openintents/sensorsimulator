
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

import org.openintents.R;
import org.openintents.provider.Alert;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * Displays a list of alerts.
 * 
 */
public class AlertList extends ListActivity{

	private static final String _TAG="AlertList";

	private Cursor mCursor;



	private static final int MENU_GENERIC_CREATE=101;
	private static final int MENU_GENERIC_EDIT=102;
	private static final int MENU_DELETE=103;
	private static final int MENU_DEBUG=104;

	private static final int MENU_DEBUG_START=201;
	private static final int MENU_DEBUG_STOP=202;

	/**
     *  Definition of the requestCode for the subactivity. 
     */
    static final private int SUBACTIVITY_GENERIC_CREATE = 1;
    static final private int SUBACTIVITY_GENERIC_EDIT = 2;
    
	@Override
	protected void onSaveInstanceState (Bundle icicle){
		stopManagingCursor(mCursor);
		Log.d(_TAG, "onFreeze: entering");
		
	}  

	@Override
	protected void onResume(){
		super.onResume();
		Log.d(_TAG,"onResume: entering");
		//init();
	}

    @Override
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        
		setContentView(R.layout.alert_list);
   
		init();
    }


	public void init(){
	
		Alert.mContentResolver=getContentResolver();

//		Log.d(_TAG,"contetnresovler is>>"+Alert.mContentResolver.toString());
//		Log.d(_TAG,"uri is >>"+Alert.Generic.CONTENT_URI+"<<");
//		Log.d(_TAG,"projection is >>"+Alert.Generic.PROJECTION.toString()+"<<");
		//Log.d(_TAG,"strange data path >>"+Alert.mContentResolver.getDataFilePath(Alert.Generic.CONTENT_URI)+"<<");

//		AlertProvider.test(Alert.Generic.CONTENT_URI);
//		AlertProvider.test(Uri.parse("content://org.openintents.alert"));
//		AlertProvider.test(Uri.parse("content://org.openintents.alert/"));
//		AlertProvider.test(Alert.Location.CONTENT_URI);
		
		mCursor=managedQuery(Alert.Generic.CONTENT_URI,Alert.Generic.PROJECTION,null,null, null);
		//new AlertProvider().query(Alert.Generic.CONTENT_URI,Alert.Generic.PROJECTION,null,null,null);
		//mCursor=Alert.mContentResolver.query(Alert.Generic.CONTENT_URI,Alert.Generic.PROJECTION,null,null,null);
		Log.d(_TAG,"cursor is now>>"+mCursor+"<<");

		if (mCursor.getCount()==0)
		{
			Log.e(_TAG,"Cursor was empty");

		}
		SimpleCursorAdapter sca=new SimpleCursorAdapter(
			this,
			R.layout.alert_list_row,
			mCursor,
			Alert.Generic.PROJECTION,
			new int[]{
				R.id.alert_id,
				R.id.alert_count,
				R.id.alert_cond1,
				R.id.alert_cond2,
				R.id.alert_type,
				R.id.alert_rule,
				R.id.alert_nature,
				R.id.alert_active,
				R.id.alert_onboot,
				R.id.alert_intent,
				R.id.alert_intentcat,
				R.id.alert_intenturi
				}
		);

		this.setListAdapter(sca);


		// Add context menu

		getListView().setOnCreateContextMenuListener(
			new View.OnCreateContextMenuListener() {

				public void onCreateContextMenu(ContextMenu contextmenu,
						View view, ContextMenu.ContextMenuInfo obj) {
					contextmenu.add(0, MENU_GENERIC_EDIT,0,
							"Edit Generic").setIcon(R.drawable.alert001a);
					contextmenu.add(0, MENU_DELETE, 0,
							"Delete Generic").setIcon(R.drawable.alert_delete001a);
				}

			});
		
		getListView().setOnItemClickListener(
			new AdapterView.OnItemClickListener() {


				public void onItemClick(AdapterView parent, View v, int position,
						long id) {
					// Clicking an item starts editing it
					menuEdit(position);
				}
				
			});


	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Log.v(_TAG,"onOptionsItemSelected: item.id>>"+item.getItemId()+"<<");
		int iID=item.getItemId();
		if (iID==MENU_GENERIC_CREATE)
		{
			menuCreate();
		}/*else if (iID==MENU_GENERIC_EDIT)
		{
			menuEdit();
		}else if (iID==MENU_DELETE)
		{
			menuDelete();
		}*/else if (iID==MENU_DEBUG_START)
		{
			menuDebugStart();
		}else if (iID==MENU_DEBUG_STOP)
		{
			menuDebugStop();
		}


		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case MENU_GENERIC_EDIT:
			menuEdit(menuInfo.position);
			break;
		case MENU_DELETE:
			menuDelete(menuInfo.position);
			break;
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		boolean result= super.onCreateOptionsMenu(menu);
		
		menu.add(0,MENU_GENERIC_CREATE,0,"Add Generic").setIcon(R.drawable.alert_add001a);
		/*
		// Moved to context menu:
		menu.add(0,MENU_GENERIC_EDIT,"Edit Generic",R.drawable.alert001a);
		menu.add(0,MENU_DELETE,"Delete Generic",R.drawable.alert_delete001a);
		*/
//		menu.add(0,MENU_DEBUG,"Debug Locations",R.drawable.settings001a);
		//menu.add(0,MENU_SERVICESETTINGS, "ServiceSettings",R.drawable.settings001a);
				
		
		android.view.SubMenu submenu;
		submenu=menu.addSubMenu(0,MENU_DEBUG,0, "Debug Locations");
		submenu.add(0,MENU_DEBUG_START,0,"Start Service").setIcon(R.drawable.settings001a);
		submenu.add(0,MENU_DEBUG_STOP,0,"Stop Service").setIcon(R.drawable.settings001a);



		return result;
		
	}


	private void menuCreate(){

		Intent intent = new Intent();
		intent.setAction(org.openintents.OpenIntents.ADD_GENERIC_ALERT);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		//intent.putExtras(b);
		startActivityForResult(intent, SUBACTIVITY_GENERIC_CREATE);		
	}
	private void menuEdit(int position){
		long i=0;
		Intent intent = new Intent();
		intent.setAction(org.openintents.OpenIntents.EDIT_GENERIC_ALERT);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		//intent.putExtras(b);
		//i=getSelectedItemId();
		i = ((Cursor) getListAdapter().getItem(position)).getLong(0); //0..id
		Uri u=ContentUris.withAppendedId(Alert.Generic.CONTENT_URI,i);
		intent.putExtra(Alert.EXTRA_URI, u.toString());
		startActivityForResult(intent, SUBACTIVITY_GENERIC_EDIT);		
	
	}
	private void menuDelete(int position){
		long i=0;
		int res=0;
		
		//i=getSelectedItemId();

		i = ((Cursor) getListAdapter().getItem(position)).getLong(0); //0..id
		
		Uri u=ContentUris.withAppendedId(Alert.Generic.CONTENT_URI,i);
		Log.i(_TAG, "Delete item: pos = " + position + ", id = " + i);
		res=Alert.delete(u,null,null);
	}

	private void menuDebugStart(){
			startService(new Intent(
					this,
					DebugGPSService.class));
	}
	private void menuDebugStop(){
			stopService(new Intent(
					this,
					DebugGPSService.class)
			);
	}

	/**
	 * @see android.app.Activity#onActivityResult(int, int, java.lang.String, android.os.Bundle)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent resultIntent) {
		
		switch (requestCode) {
		case SUBACTIVITY_GENERIC_CREATE:
			// Update list
			mCursor.requery();
			break;
		case SUBACTIVITY_GENERIC_EDIT:
			mCursor.requery();
			break;
		default:
			Log.i(_TAG, "AlertList: Unknown activity result: " + requestCode);
		}

		super.onActivityResult(requestCode, resultCode,resultIntent);
	}
	
	

}/*eoc*/