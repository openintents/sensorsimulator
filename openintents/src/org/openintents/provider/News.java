package org.openintents.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import android.provider.BaseColumns;
import java.util.*;

public abstract class News {

	public static final String _TAG="News";
	
	public static final class RSSFeeds implements BaseColumns{
		
		public static final Uri CONTENT_URI= Uri
			.parse("content://org.openintents.news/rss");
		
		public static final String RSS_FORMAT="2.0";
		
		/* type long/int , unique channel id*/
		//user _ID field as it' aslready unique
		//public static final String CHANNEL_ID="channel_id";
		//public static final String CHANNEL_ID_TYPE="INTEGER";
		
		public static final String _ID="_id";
		public static final String _COUNT="_count";

		public static final String ITEM_GUID="item_guid";
		public static final String ITEM_GUID_TYPE="INTEGER";
		
		/*type uri, the address to fetch the data from*/
		public static final String CHANNEL_LINK="channel_link";
		public static final String CHANNEL_LINK_TYPE="VARCHAR";
		
		public static final String CHANNEL_NAME="channel_name";
		public static final String CHANNEL_NAME_TYPE="VARCHAR";
		
		/*how often the channel will update, in minutes.*/
		public static final String UPDATE_CYCLE="update_cycle";
		public static final String UPDATE_CYCLE_TYPE="INTEGER";
		
		/*how long items should be stored, 0=>forever*/
		public static final String HISTORY_LENGTH="history_len";
		public static final String HISTORY_LENGTH_TYPE="INTEGER";
		
		/*how long items should be stored, 0=>forever*/
		public static final String LAST_UPDATE="last_upd";
		public static final String LAST_UPDATE_TYPE="INTEGER";		
		
		/*the channel language */
		public static final String CHANNEL_LANG="channel_lang";
		public static final String CHANNEL_LANG_TYPE="VARCHAR";
		
		/*the channel description */
		public static final String CHANNEL_DESC="channel_desc";
		public static final String CHANNEL_DESC_TYPE="TEXT";
		
		public static final String CHANNEL_COPYRIGHT="channel_cpr";
		public static final String CHANNEL_COPYRIGHT_TYPE="VARCHAR";
		
		/*URI to an image, mostly a channel icon. make this a local uri whenever possible */
		public static final String CHANNEL_IMAGE_URI="channel_img";
		public static final String CHANNEL_IMAGE_URI_TYPE="VARCHAR";

		public static final String DEFAULT_SORT_ORDER = "";
		
		public static final String FEED_NATURE="feed_nature";
		
		
		
	}/*eoc RSSFeeds*/
	
	
	
	public static final class RSSFeedContents implements BaseColumns{
		/*Uri for accessing Channel Items in RSS2.0 Format*/
		public static final Uri CONTENT_URI= Uri
		.parse("content://org.openintents.news/rsscontents");
	
		
		public static final String RSS_FORMAT="2.0";

		public static final String CHANNEL_ID="channel_id";
		public static final String CHANNEL_ID_TYPE="INTEGER";
		
		public static final String ITEM_GUID="item_guid";
		public static final String ITEM_GUID_TYPE="INTEGER";

		public static final String ITEM_LINK="item_link";
		public static final String ITEM_LINK_TYPE="VARCHAR";
		
		/*type is CDATA/String, a short description of the article or the article itself*/
		public static final String ITEM_DESCRIPTION="item_desc";
		public static final String ITEM_DESCRIPTION_TYPE="TEXT";
		
		public static final String ITEM_TITLE="item_title";
		public static final String ITEM_TITLE_TYPE="VARCHAR";
		
		public static final String ITEM_AUTHOR="item_author";
		public static final String ITEM_AUTHOR_TYPE="VARCHAR";

		public static final String READ_STATUS="read_status";
		
	}/*eoc RSSFeedContents*/
	
	
	public static final class AtomFeeds implements BaseColumns{
	
		public static final Uri CONTENT_URI=
			Uri.parse("content://org.openintents.news/atom");
			
			public static final String DEFAULT_SORT_ORDER="";

			public static final String _ID="_id";
			public static final String _COUNT="_count";

			public static final String FEED_ID="feed_id";

			public static final String FEED_TITLE="feed_title";

			/*pls note: FEED_UPDATE is the updated elemnt of the xml stream
				whereas FEED_LAST_CHECKED should be used for internal time intervalls.
			*/
			public static final String FEED_UPDATED="feed_updated";
			public static final String FEED_LAST_CHECKED="feed_lastchecked";
			/*how often the channel will update, in minutes.*/
			public static final String UPDATE_CYCLE="update_cycle";
			/*how long items should be stored, 0=>forever*/
			public static final String HISTORY_LENGTH="history_len";
			
			public static final String FEED_LINK="feed_link";
			public static final String FEED_LINK_SELF="feed_link_self"; //<- use this for retrieving data.
			public static final String FEED_LINK_ALTERNATE="feed_link_alternate";
	
			public static final String FEED_ICON="feed_icon";
			public static final String FEED_RIGHTS="feed_rights";
			
			public static final String FEED_NATURE="feed_nature";

	}/*eoc AtomFeeds*/

	public static final class AtomFeedContents implements BaseColumns{
		
		public static final Uri CONTENT_URI=
			Uri.parse("content://org.openintents.news/atomcontents");

		public static final String DEFAULT_SORT_ORDER="";

		public static final String _ID="_id";
		public static final String _COUNT="_count";

		public static final String FEED_ID="feed_id";

		public static final String ENTRY_ID="entry_id";

		public static final String ENTRY_TITLE="entry_title";

		public static final String ENTRY_UPDATED="entry_updated";

		public static final String ENTRY_CONTENT="entry_content";
		public static final String ENTRY_CONTENT_TYPE="entry_content_type";

		public static final String ENTRY_LINK="entry_link";
		public static final String ENTRY_LINK_ALTERNATE="entry_link_alternate";

		public static final String ENTRY_SUMMARY="entry_summary";
		public static final String ENTRY_SUMMARY_TYPE="entry_summary_type"; //Defaults to text

		public static final String READ_STATUS="read_status";

	}/*eoc AtomFeedContents*/


	public static ContentResolver mContentResolver;


	//some conveniece constants.
	public static final String FEED_TYPE="FEEDTYPE";
	public static final String FEED_TYPE_RSS="RSS";
	public static final String FEED_TYPE_ATOM="ATOM";

	public static final String	FEED_NATURE="feed_nature";
	public static final int		FEED_NATURE_USER=0;
	public static final int		FEED_NATURE_SYSTEM=1;


	public static final String MESSAGE_COUNT="MESSAGE_COUNT";
	public static final String _ID="_id";


	public static final int STATUS_UNREAD=0;
	public static final int STATUS_READ=1;



	/**
	*@deprecated use insert(uri,contentvalues) instead. Will be removed in release 0.1.5
	*@see insert(Uri uri,ContentValues cv)
	*/
	public static Uri ins(ContentValues cv){

		return mContentResolver.insert(RSSFeeds.CONTENT_URI,cv);//works
	}

	/**
	 *@param uri the content uri to insert to
	 *@param cv the ContentValues that will be inserted to
	*/
	public static Uri insert(Uri uri, ContentValues cv){

		return mContentResolver.insert(uri,cv);
		
	}

	/**
	 *@param uri the content uri to insert to
	 *@param selection the selection to check against
	 *@param selectionArgs the arguments applied to selection string (optional)
	 *@param cs the ContentValues that will be inserted if selection returns 0 rows.
	 */
	public static Uri insertIfNotExists(Uri uri,String selection,String[] selectionArgs,ContentValues cs){
		Uri u=null;

		String[] projection={BaseColumns._ID};
		//query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
		Log.d(_TAG,"insertIfNotExists:entering");
		Log.d(_TAG,"checking for\n uri:"+uri+"\n selection:"+selection+"\n selArgs[]:"+selectionArgs+"\n cv:"+cs);
		android.database.Cursor c=mContentResolver.query(uri,projection,selection,selectionArgs,null);
		Log.d(_TAG,"returned count of>>"+c.getCount());
		if (c.getCount()<=0)
		{
			u=insert(uri,cs);
		}else{
			c.moveToFirst();
			Log.d(_TAG,"uri part>>"+uri.getSchemeSpecificPart()+"<<");
			String tid=c.getString(c.getColumnIndex(BaseColumns._ID));
			//Uri temp=Uri.fromParts(
			u=Uri.fromParts(
					uri.getScheme(),
					uri.getSchemeSpecificPart(),
					tid
				);
			 //u=android.content.ContentUris.withAppendedId(temp,c.getLong(c.getColumnIndex(BaseColumns._ID)));
			 

		}
		//Log.d(_TAG,"valueset:"+valueSet.toString());
		//return null;			
		
		//return insert(uri,cs);
		c.close();
		Log.d(_TAG,"insertIfNotExists:leaving");
		return u;
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



}
