package org.openintents.media.playlists;


import org.openintents.provider.MediaPlaylists;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MediaPlaylistsProvider extends ContentProvider {

	private SQLiteOpenHelper mOpenHelper;
	
	private static final String DATABASE_NAME="mediaplaylists.db";
	private static final int DATABASE_VERSION=1;

	private static final String TABLE_LISTS="lists";
	private static final String TABLE_ITEMS="listitems";
	


	private static final String TAG="MediaPlaylistProvider";
	

	private static final int PLISTS=101;
	private static final int PLISTS_ID=102;
	private static final int ITEMS=103;
	private static final int ITEMS_ID=104;

	private static final UriMatcher URL_MATCHER;
	

	
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG,"Creating table "+TABLE_LISTS);
			
			db.execSQL("CREATE TABLE "+TABLE_LISTS+" ("+
					MediaPlaylists.Playlist._ID +" INTEGER PRIMARY KEY,"+
					MediaPlaylists.Playlist._COUNT+" INTEGER,"+
					MediaPlaylists.Playlist.NATURE+" INTEGER,"+
					MediaPlaylists.Playlist.NAME+" STRING"+
					";"
			);
			Log.d(TAG,"Creating table "+TABLE_ITEMS);
			
			db.execSQL("CREATE TABLE "+TABLE_ITEMS+" ("+
					MediaPlaylists.ListItem._ID +" INTEGER PRIMARY KEY,"+
					MediaPlaylists.ListItem._COUNT+" INTEGER,"+
					MediaPlaylists.ListItem.LIST_ID+" INTEGER,"+
					MediaPlaylists.ListItem.NAME+" STRING,"+
					MediaPlaylists.ListItem.FILEPATH+" STRING,"+
					MediaPlaylists.ListItem.LISTPOSITION+" INTEGER"+
					";"
			);
			Log.d(TAG,"INSERTING DEFAULT PLAYLIST");
			db.execSQL("INSERT INTO "+TABLE_LISTS+" "+
					MediaPlaylists.Playlist._ID +","+
					MediaPlaylists.Playlist._COUNT+","+
					MediaPlaylists.Playlist.NATURE+","+
					MediaPlaylists.Playlist.NAME+" "+
					"VALUES (1,1,0,'default')"+
					";"
			);


			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.w(TAG,"upgrade not supported");
			//Log.v(TAG, "");
			
			
		}
		
		
		
	}//class helper

	@Override
	public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match=URL_MATCHER.match(uri);
		Log.d(this.TAG,"DELETE,URI MATCHER RETURNED >>"+match+"<<");
		long rowID=0;
		switch (match){
			case PLISTS:
				break;
			case PLISTS_ID:
				break;
			case ITEMS:
				break;
			case ITEMS_ID:
				break;
		}


		//TODO
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @author Zero
	 * @version 1.0
	 * @argument uri ContentURI NOT NULL
	 * @argument values ContentValues NOT NULL
	 * @return uri of the new item.
	 * 
	 */
	public Uri insert(Uri uri, ContentValues values) {
		int match=URL_MATCHER.match(uri);
		Log.d(this.TAG,"INSERT,URI MATCHER RETURNED >>"+match+"<<");
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowID=0;
		switch (match){
			case PLISTS:
				rowID=db.insert(TABLE_LISTS, "", values);			
				if (rowID > 0) {
					Uri nUri = ContentUris.withAppendedId(MediaPlaylists.Playlist.CONTENT_URI,rowID);
					getContext().getContentResolver().notifyChange(nUri, null);
					return nUri;
				}
				throw new SQLException("Failed to insert row into " + uri);	
				//break;

			case ITEMS:

					rowID=db.insert(TABLE_ITEMS, "", values);			
					if (rowID > 0) {
						Uri nUri = ContentUris.withAppendedId(MediaPlaylists.ListItem.CONTENT_URI,rowID);
						getContext().getContentResolver().notifyChange(nUri, null);
						return nUri;
					}
					throw new SQLException("Failed to insert row into " + uri);	
				//break;
		}
		return null;
	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int match=URL_MATCHER.match(uri);
		Log.d(this.TAG,"INSERT,URI MATCHER RETURNED >>"+match+"<<");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy=null;

		long rowID=0;
		switch (match){
			case PLISTS:
				qb.setTables(TABLE_LISTS);
				if (projection==null)
				{
					projection=MediaPlaylists.Playlist.PROJECTION;
				}
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = MediaPlaylists.Playlist.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case PLISTS_ID:
				qb.setTables(TABLE_LISTS);
				//qb.setProjectionMap(FEED_PROJECTION_MAP);
				qb.appendWhere("_id=" + uri.getLastPathSegment());
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = MediaPlaylists.Playlist.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case ITEMS:
				qb.setTables(TABLE_ITEMS);
				if (projection==null)
				{
					projection=MediaPlaylists.ListItem.PROJECTION;
				}
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = MediaPlaylists.ListItem.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
			case ITEMS_ID:
				qb.setTables(TABLE_ITEMS);
				//qb.setProjectionMap(FEED_PROJECTION_MAP);
				qb.appendWhere("_id=" + uri.getLastPathSegment());
				
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = MediaPlaylists.ListItem.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}
				break;
		}
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count=0;

		int match=URL_MATCHER.match(uri);
		Log.d(this.TAG,"INSERT,URI MATCHER RETURNED >>"+match+"<<");
		String rowID="";


		switch (match){
			case PLISTS:
				count= db.update(TABLE_LISTS, values, selection,selectionArgs);
				//getContext().getContentResolver().notifyChange(nUri, null);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case PLISTS_ID:
				rowID=uri.getPathSegments().get(1);
				count= db
					.update(TABLE_LISTS,
							values,
							"_id="+rowID
							+(!TextUtils.isEmpty(selection) ? " AND (" + selection
                            + ')' : ""),
							selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case ITEMS:
				count= db.update(TABLE_ITEMS, values, selection,selectionArgs);
				//getContext().getContentResolver().notifyChange(nUri, null);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			case ITEMS_ID:
				rowID=uri.getPathSegments().get(1);
				count= db
					.update(TABLE_ITEMS,
							values,
							"_id="+rowID
							+(!TextUtils.isEmpty(selection) ? " AND (" + selection
                            + ')' : ""),
							selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
		}
		return count;
	}



	static{
	
		URL_MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
		
		URL_MATCHER.addURI("org.openintents.media.playlists","playlists",PLISTS);
		URL_MATCHER.addURI("org.openintents.media.playlists","playlists/#",PLISTS_ID);
		URL_MATCHER.addURI("org.openintents.media.playlists","listitems",ITEMS);
		URL_MATCHER.addURI("org.openintents.media.playlists","listitems/#",ITEMS_ID);

		
	}


}/*eoc*/