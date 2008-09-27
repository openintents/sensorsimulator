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

import java.util.HashMap;

import org.openintents.provider.Tag.Contents;
import org.openintents.provider.Tag.Tags;

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

/**
 * Provides access to a database of tags and contents. Each tag has a tag_id and
 * a content_id, a creation date and a modified data. Both ids refer to an entry
 * to the contents table. A content row has a uri and a type.
 * 
 */
public class TagsProvider extends ContentProvider {

	private SQLiteOpenHelper mOpenHelper;

	private static final String TAG = "TagsProvider";
	private static final String DATABASE_NAME = "tags.db";
	private static final int DATABASE_VERSION = 1; // Release 0.1.0

	private static HashMap<String, String> TAG_PROJECTION_MAP;
	private static HashMap<String, String> CONTENT_PROJECTION_MAP;
	private static HashMap<String, String> CONTENT_PROJECTION_MAP_2;

	private static final int TAGS = 1;
	private static final int TAG_ID = 2;
	private static final int CONTENTS = 3;
	private static final int CONTENT_ID = 4;

	private static final UriMatcher URL_MATCHER;

	private static final String DEFAULT_TAG = "DEFAULT";

	private static final String TAG_TYPE_TAG = "TAG";

	private static final String TAG_TYPE_UNIQUE_TAG = "TAG_UNIQUE";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE content (_id INTEGER PRIMARY KEY,"
					+ "uri VARCHAR," + "type VARCHAR," + "created INTEGER"
					+ ");");
			db.execSQL("CREATE TABLE tag (_id INTEGER PRIMARY KEY,"
					+ "tag_id LONG," + "content_id LONG," + "created INTEGER,"
					+ "modified INTEGER," + "accessed INTEGER" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS content");
			db.execSQL("DROP TABLE IF EXISTS tag");
			onCreate(db);
		}
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String defaultOrderBy = null;
		switch (URL_MATCHER.match(url)) {
		case TAGS:
			// queries for tags also return the uris, not only the ids.
			qb.setTables("tag tag, content content1, content content2");
			qb.appendWhere("tag.tag_id = content1._id AND "
					+ "tag.content_id = content2._id");
			qb.setProjectionMap(TAG_PROJECTION_MAP);
			qb.setDistinct(url.getQueryParameter(Tags.DISTINCT) != null);

			defaultOrderBy = Tags.DEFAULT_SORT_ORDER;
			break;

		case TAG_ID:
			// queries for a tag just returns the ids.
			qb.setTables("tag");
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;

		case CONTENTS:

			qb.setProjectionMap(CONTENT_PROJECTION_MAP);
			qb.setTables("content");
			qb.setDistinct(url.getQueryParameter(Tags.DISTINCT) != null);
			defaultOrderBy = Contents.DEFAULT_SORT_ORDER;
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		// If no sort order is specified use the default

		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = defaultOrderBy;
		} else {
			orderBy = sort;
		}
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	@Override
	public Uri insert(Uri url, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowID = 0l;
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);

		} else {
			values = new ContentValues();
		}

		// insert is only supported for tags
		if (URL_MATCHER.match(url) != TAGS) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		Long now = Long.valueOf(System.currentTimeMillis());

		// Make sure that the fields are all set
		if (!values.containsKey(Tags.CREATED_DATE)) {
			values.put(Tags.CREATED_DATE, now);
		}

		if (!values.containsKey(Tags.MODIFIED_DATE)) {
			values.put(Tags.MODIFIED_DATE, now);
		}

		if (!values.containsKey(Tags.ACCESS_DATE)) {
			values.put(Tags.ACCESS_DATE, now);
		}

		boolean uniqueTag = !TextUtils.isEmpty(url
				.getQueryParameter(Tags.QUERY_UNIQUE_TAG));
		String tagType;
		if (uniqueTag) {
			tagType = TAG_TYPE_UNIQUE_TAG;
		} else {
			tagType = TAG_TYPE_TAG;
		}
		// lookup id for uri or create new
		replaceUriById(values, Tags.TAG_ID, Tags.URI_1, tagType, DEFAULT_TAG);

		if (!values.containsKey(Tags.CONTENT_ID)
				&& !values.containsKey(Tags.URI_2)) {
			new SQLException("missing uri or content_id for insert " + url);
		}
		// lookup id for uri or create new
		replaceUriById(values, Tags.CONTENT_ID, Tags.URI_2, null, DEFAULT_TAG);

		Long tagId = values.getAsLong(Tags.TAG_ID);
		Long contentId = values.getAsLong(Tags.CONTENT_ID);
		if (tagId == null || tagId == -1 || contentId == null
				|| contentId == -1) {
			return null;
		}

		// check whether tag already exists
		Cursor existingTag = db.query("tag", new String[] { Tags._ID },
				"tag_id = ? AND content_id = ?", new String[] {
						String.valueOf(tagId), String.valueOf(contentId) },
				null, null, null);

		if (!existingTag.moveToNext()) {

			boolean inserted = false;

			existingTag = db.query("tag tag, content content", new String[] {
					"tag._id", "tag.content_id" },
					"tag_id = ? and tag_id = content._id and content.type = ?",
					new String[] { String.valueOf(values.get(Tags.TAG_ID)),
							TAG_TYPE_UNIQUE_TAG }, null, null, null);
			if (existingTag.moveToNext()) {
				rowID = existingTag.getLong(0);
				ContentValues cv = new ContentValues();
				cv.put(Tags.CONTENT_ID, values.getAsString(Tags.CONTENT_ID));
				db.update("tag", cv, "tag_id = ?", new String[] { existingTag
						.getString(0) });
				inserted = true;
			}

			if (!inserted) {
				// finally insert the tag.
				rowID = db.insert("tag", "tag", values);
			}
			if (rowID > 0) {
				Uri uri = ContentUris.withAppendedId(Tags.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(uri, null);
				return uri;
			}
			throw new SQLException("Failed to insert row into " + url);
		} else {
			return null;
		}

	}

	/**
	 * lookup content id for given uri or create new entry in content table if
	 * not present
	 * 
	 * @param values
	 * @param idColumnName
	 * @param uriColumnName
	 * @param tagType
	 * @param defaultValue
	 */
	private void replaceUriById(ContentValues values, String idColumnName,
			String uriColumnName, String tagType, String defaultValue) {
		if (!values.containsKey(idColumnName)) {
			String tagUri;
			if (values.containsKey(uriColumnName)) {
				tagUri = values.getAsString(uriColumnName);
				values.remove(uriColumnName);
			} else {
				tagUri = defaultValue;
			}
			SQLiteDatabase db = mOpenHelper.getReadableDatabase();
			Cursor existingTag = db.query("content",
					new String[] { Contents._ID }, "uri = ?",
					new String[] { tagUri }, null, null, null);

			String contentId;
			if (!existingTag.moveToNext()) {
				contentId = String.valueOf(insertContent(tagUri, tagType));
			} else {
				contentId = existingTag.getString(0);
			}
			values.put(idColumnName, contentId);
		}
	}

	/**
	 * create new entry in content table
	 * 
	 * @param uri
	 * @param type
	 * @return
	 */
	private long insertContent(String uri, String type) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Contents.URI, uri);
		if (type != null) {
			values.put(Contents.TYPE, type);
		}
		if (!TextUtils.isEmpty(uri)) {
			long rowId = db.insert("content", "content", values);
			return rowId;
		} else {
			return -1;
		}
	}

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		long rowId = 0;
		switch (URL_MATCHER.match(url)) {
		case TAGS:
			count = db.delete("tag", where, whereArgs);

			deleteUnrefContent();
			break;

		case TAG_ID:
			String segment = url.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			String whereString;
			if (!TextUtils.isEmpty(where)) {
				whereString = " AND (" + where + ')';
			} else {
				whereString = "";
			}

			count = db.delete("tag", "_id=" + segment + whereString, whereArgs);

			deleteUnrefContent();
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	private void deleteUnrefContent() {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db
				.delete(
						"content",
						"type not like 'TAG%' and not exists(select content_id from tag where tag.content_id = content._id)",
						null);

	}

	@Override
	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		// TODO which values can be updated.
		return 0;
	}

	@Override
	public String getType(Uri url) {
		switch (URL_MATCHER.match(url)) {
		case TAGS:
			return "vnd.android.cursor.dir/vnd.openintents.tag";

		case TAG_ID:
			return "vnd.android.cursor.item/vnd.openintents.tag";

		case CONTENTS:
			return "vnd.android.cursor.dir/vnd.openintents.content";

		case CONTENT_ID:
			return "vnd.android.cursor.item/vnd.openintents.content";

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI("org.openintents.tags", "tags", TAGS);
		URL_MATCHER.addURI("org.openintents.tags", "tags/#", TAG_ID);
		URL_MATCHER.addURI("org.openintents.tags", "contents", CONTENTS);
		URL_MATCHER.addURI("org.openintents.tags", "contents/#", CONTENT_ID);

		TAG_PROJECTION_MAP = new HashMap<String, String>();
		TAG_PROJECTION_MAP.put(Tags._ID, "tag._id");
		TAG_PROJECTION_MAP.put(Tags.TAG_ID, "tag.tag_id");
		TAG_PROJECTION_MAP.put(Tags.CONTENT_ID, "tag.content_id");
		TAG_PROJECTION_MAP.put(Tags.CREATED_DATE, "tag.created");
		TAG_PROJECTION_MAP.put(Tags.MODIFIED_DATE, "tag.modified");
		TAG_PROJECTION_MAP.put(Tags.ACCESS_DATE, "tag.accessed");
		TAG_PROJECTION_MAP.put(Tags.URI_1, "content1.uri as uri_1");
		TAG_PROJECTION_MAP.put(Tags.URI_2, "content2.uri as uri_2");

		CONTENT_PROJECTION_MAP = new HashMap<String, String>();
		CONTENT_PROJECTION_MAP.put(Contents._ID, "_id");
		CONTENT_PROJECTION_MAP.put(Contents.URI, "uri");
		CONTENT_PROJECTION_MAP.put(Contents.TYPE, "type");

		CONTENT_PROJECTION_MAP_2 = new HashMap<String, String>();
		CONTENT_PROJECTION_MAP_2.put(Contents._ID, "content1._id");
		CONTENT_PROJECTION_MAP_2.put(Contents.URI, "content1.uri");
		CONTENT_PROJECTION_MAP_2.put(Contents.TYPE, "content1.type");
	}
}
