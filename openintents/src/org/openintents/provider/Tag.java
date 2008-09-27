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

package org.openintents.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Definition for content provider related to tag.
 * 
 */
public class Tag {

	public static final class Tags implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.tags/tags");

		/**
		 * The default sort order for this table.
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The id of the tag.
		 * <P>
		 * Type: STRING
		 * </P>
		 */
		public static final String TAG_ID = "tag_id";

		/**
		 * The id of the content.
		 * <P>
		 * Type: STRING
		 * </P>
		 */
		public static final String CONTENT_ID = "content_id";

		/**
		 * The timestamp for when the tag was created.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the tag was last modified.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		/**
		 * The timestamp for when the tag was last modified.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String ACCESS_DATE = "accessed";

		/**
		 * First URI of the relationship (usually the tag).
		 * 
		 */
		public static final String URI_1 = "uri_1";

		/**
		 * Second URI of the relationship (usually the content).
		 * 
		 */
		public static final String URI_2 = "uri_2";

		/**
		 * The Uri to be tagged that the query is about.
		 * 
		 */
		public static final String QUERY_URI = "uri";

		/**
		 * The tag that the query is about.
		 */
		public static final String QUERY_TAG = "tag";

		public static final String DISTINCT = "distinct";
		public static final String QUERY_UNIQUE_TAG = "unique";
	}

	public static final class Contents implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.tags/contents");

		/**
		 * The default sort order for this table.
		 */
		public static final String DEFAULT_SORT_ORDER = "type DESC, uri";

		/**
		 * The uri of the content, or the tag text if the uri starts with "TAG".
		 * This can be tested in SQL using "WHERE type like 'TAG%'".
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String URI = "uri";

		/**
		 * The type of the content, e.g TAG null means CONTENT.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String TYPE = "type";

		/**
		 * The timestamp for when the note was created.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		public static final String QUERY_BY_TYPE = "byType";

	}

	private static final String TAG = "Tag.java";

	private static final String DELETE_URI = "tag.content_id = (select content2._id FROM content content2 WHERE content2.uri = ?)";

	private static final String DELETE_TAG_URI = "tag.tag_id = (select content1._id FROM content content1 WHERE content1.uri = ?) "
			+ "AND tag.content_id = (select content2._id FROM content content2 WHERE content2.uri = ?)";

	private Context mContext;

	public Tag(Context context) {
		mContext = context;
	}

	public void removeTag(String tag, String uri) {

		mContext.getContentResolver().delete(Tags.CONTENT_URI,
				Tag.DELETE_TAG_URI, new String[] { tag, uri });

	}

	public void removeAllTags(String uri) {

		mContext.getContentResolver().delete(Tags.CONTENT_URI, Tag.DELETE_URI,
				new String[] { uri });

	}

	public void insertTag(String tag, String content) {
		ContentValues values = new ContentValues(2);
		values.put(Tags.URI_1, tag);
		values.put(Tags.URI_2, content);

		try {
			mContext.getContentResolver().insert(Tags.CONTENT_URI, values);
		} catch (Exception e) {
			Log.i(TAG, "insert failed", e);
		}
	}

	public void insertUniqueTag(String tag, String content) {
		ContentValues values = new ContentValues(2);
		values.put(Tags.URI_1, tag);
		values.put(Tags.URI_2, content);

		try {
			Uri uri = Tags.CONTENT_URI.buildUpon().appendQueryParameter(
					Tags.QUERY_UNIQUE_TAG, "true").build();
			mContext.getContentResolver().insert(uri, values);
		} catch (Exception e) {
			Log.i(TAG, "insert failed", e);
		}
	}

	/**
	 * cursor over contentUriStrings is returned where the content is tagged
	 * with the given tag.
	 * 
	 * @param tag
	 * @param contentUri
	 * @return
	 */
	public Cursor findTaggedContent(String tag, String contentUri) {
		Cursor c = mContext.getContentResolver().query(Tags.CONTENT_URI,
				new String[] { Tags._ID, Tags.URI_2 },
				"content1.uri like ? and content2.uri like ?",
				new String[] { tag, contentUri + "%" }, "content2.uri");
		return c;
	}

	/**
	 * cursor over tags with all tags for the given content is returned.
	 * 
	 * @param tag
	 * @param contentUri
	 * @return
	 */
	public Cursor findTags(String contentUri) {
		Cursor c = mContext.getContentResolver().query(Tags.CONTENT_URI,
				new String[] { Tags._ID, Tags.URI_1 }, "content2.uri = ?",
				new String[] { contentUri }, "content1.uri");
		return c;
	}

	public String findTags(String uri, String separator) {
		Cursor tags = findTags(uri);
		StringBuffer sb = new StringBuffer();
		int colIndex = tags.getColumnIndex(Tags.URI_1);
		while (tags.moveToNext()) {
			sb.append(tags.getString(colIndex));
			sb.append(separator);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - separator.length());
		}
		return sb.toString();
	}

	/**
	 * cursor over tags with all tags for the given content is returned.
	 * 
	 * @param contentUriPrefix
	 * @return
	 */
	public Cursor findTagsForContentType(String contentUriPrefix) {
		Uri uri = Contents.CONTENT_URI.buildUpon().appendQueryParameter(
				Tags.DISTINCT, "true").build();
		Cursor c = mContext
				.getContentResolver()
				.query(
						uri,
						new String[] { Contents._ID, Contents.URI },
						"exists(select * from content content2, tag tag where content2.uri like ? and content2._id = tag.content_id and content._id = tag.tag_id)",
						new String[] { contentUriPrefix + "%" }, "content.uri");
		return c;
	}

	/**
	 * Get a cursor with all tags
	 * 
	 * @return
	 */
	public Cursor findAllTags() {
		Cursor c = mContext.getContentResolver().query(Contents.CONTENT_URI,
				new String[] { Contents._ID, Contents.URI, Contents.TYPE },
				"type like 'TAG%'", null, Contents.DEFAULT_SORT_ORDER);
		return c;
	}

	/**
	 * Get a cursor with all used tags, i.e. at least one content has been
	 * tagged with this tag.
	 * 
	 * @return
	 */
	public Cursor findAllUsedTags() {
		Cursor c = mContext
				.getContentResolver()
				.query(
						Contents.CONTENT_URI,
						new String[] { Contents._ID, Contents.URI,
								Contents.TYPE },
						"type like 'TAG%' and (select count(*) from tag where tag.tag_id = content._id) > 0",
						null, Contents.DEFAULT_SORT_ORDER);
		return c;
	}

	/**
	 * start add tag activity. Only useful, if tag or uri is null. Consider
	 * using insertTag if you want to add the tag without user interaction.
	 * 
	 * @param tag
	 * @param uri
	 */
	public void startAddTagActivity(String tag, String uri) {
		Intent intent = new Intent(org.openintents.OpenIntents.TAG_ACTION,
				Tags.CONTENT_URI).putExtra(Tags.QUERY_TAG, tag).putExtra(
				Tags.QUERY_URI, uri);
		mContext.startActivity(intent);

	}

}
