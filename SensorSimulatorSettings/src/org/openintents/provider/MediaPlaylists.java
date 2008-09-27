package org.openintents.provider;

/*


 ****************************************************************************
 * Copyright (C) 2007-2008 OpenIntents.org                                  *
 *                                                                          *
 * Licensed under the Apache License, Version 2.0 (the "License");          *
 * you may not use this file except in compliance with the License.         *
 * You may obtain a copy of the License at                                  *
 *                                                                          *
 *      http://www.apache.org/licenses/LICENSE-2.0                          *
 *                                                                          *
 * Unless required by applicable law or agreed to in writing, software      *
 * distributed under the License is distributed on an "AS IS" BASIS,        *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and      *
 * limitations under the License.                                           *
 ****************************************************************************

OpenIntents defines and implements open interfaces for
improved interoperability of Android applications.

To obtain the current release, visit
  http://code.google.com/p/openintents/


*/


import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import android.provider.BaseColumns;
import java.util.*;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;

public class MediaPlaylists{

	private static final String _TAG="MEDIAPLAYLISTS";


	public static final class Playlist implements BaseColumns{
		public static final Uri CONTENT_URI=
			Uri.parse("org.openintents.media.playlists/playlists");

		public static final String DEFAULT_SORT_ORDER="";
		public static final String TYPE="type";
		public static final String NAME="name";
		public static final String NATURE="list_nature";
		
		public static final String[] PROJECTION={
			_ID,
			_COUNT,
			NAME,
			NATURE
		};

	};

	public static final class ListItem implements BaseColumns{
		public static final Uri CONTENT_URI=
			Uri.parse("org.openintents.media.playlists/listitems");

		public static final String DEFAULT_SORT_ORDER="listposition ASC";
		public static final String TYPE="type";
		public static final String NAME="name";
		public static final String FILEPATH="filepath";
		public static final String LISTPOSITION="listposition";
		public static final String LIST_ID="list_id";
	
		public static final String[] PROJECTION={
			_ID,
			_COUNT,
			LIST_ID,
			NAME,
			FILEPATH,
			LISTPOSITION
		};
		


	};

	public static ContentResolver mContentResolver;



	public static final String	LIST_NATURE="list_nature";
	public static final int		LIST_NATURE_USER=0;
	public static final int		LIST_NATURE_SYSTEM=1;



	/**
	 *@param uri the content uri to insert to
	 *@param cv the ContentValues that will be inserted to
	*/
	public static Uri insert(Uri uri, ContentValues cv){

		return mContentResolver.insert(uri,cv);
		
	}


	/**
	 *@param uri the content uri to delete
	 *@param selection the selection to check against
	 *@param selectionArgs the arguments applied to selection string (optional)	 
	 *@return number of deleted rows
	 */
	public static int delete(Uri uri,String selection,String[] selectionArgs){

		return mContentResolver.delete(uri,selection,selectionArgs);
	}

	/**
	 *@param uri the content uri to update
	 *@param cv the ContentValues that will be update in selected rows.
	 *@param selection the selection to check against
	 *@param selectionArgs the arguments applied to selection string (optional)	 
	 *@return number of updated rows
	 */
	public static int update(Uri uri,ContentValues values, String selection, String[] selectionArgs){
		return mContentResolver.update(uri,values,selection,selectionArgs);
	}



}/*eoc*/