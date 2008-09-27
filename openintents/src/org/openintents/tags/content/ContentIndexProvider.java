package org.openintents.tags.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openintents.provider.ContentIndex;
import org.openintents.provider.ContentIndex.Dir;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ContentIndexProvider extends
		android.content.ContentProvider {

	private static final String TAG = "Tables";

	static final String DATABASE_NAME = "deepdroid.db";

	private static final int DATABASE_VERSION = 33;

	private SQLiteOpenHelper mOpenHelper;


	private static final UriMatcher URL_MATCHER;
	private static final int DIRECTORIES = 1;
	private static final int DIRECTORY = 2;
	private static final int INDEX_ENTRIES = 3;

	private static final Map<String, String> DIRECTORY_PROJECTION_MAP = null;

	static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Create tables");

			StringBuffer dirs = new StringBuffer();
			dirs.append("CREATE TABLE dirs (");
			dirs.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
			dirs.append("parent_id INTEGER,");
			dirs.append("uri TEXT NOT NULL,");
			dirs.append("package TEXT NOT NULL,");
			dirs.append("name TEXT,");
			dirs.append("text_columns TEXT,");
			dirs.append("id_column TEXT,");
			dirs.append("time_column TEXT,");
			dirs.append("intent_uri TEXT,");
			dirs.append("intent_action TEXT,");
			dirs.append("refreshed LONG,");
			dirs.append("updated LONG);");
			db.execSQL(dirs.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS dirs");
			onCreate(db);
		}

	}

	private final static String _TAG = "contentIndex";
/*
	public ContentIndexProvider() {
		super(DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	protected void upgradeDatabase(int oldVersion, int newVersion) {
		DatabaseHelper dbHelper = new DatabaseHelper();
		dbHelper.onUpgrade(db, oldVersion, newVersion);

	}
*/
	
	@Override
	public boolean onCreate(){
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = null;

		switch (URL_MATCHER.match(url)) {
		case DIRECTORIES:
			qb = new SQLiteQueryBuilder();
			qb.setTables("dirs");
			break;

		case DIRECTORY:
			qb = new SQLiteQueryBuilder();
			qb.setTables("dirs");
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;

		case INDEX_ENTRIES:
			return getBodyContent(url);
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = ContentIndex.Dir.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sort;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection,
				selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	@Override
	public Uri insert(Uri url, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
		switch (URL_MATCHER.match(url)) {
		case DIRECTORIES:
			if (initialValues.containsKey("_id")) {
				initialValues.remove("_id");
			}

			long id = db.insert("dirs", "dirs", initialValues);
			if (id >= 0) {
				return ContentUris.withAppendedId(Dir.CONTENT_URI, id);
			} else {
				return null;
			}
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

	private Cursor getBodyContent(Uri url) {
		List<String> bodyUris = null;
		try {
			bodyUris = url
					.getQueryParameters(ContentIndex.QUERY_CONTENT_BODY_URI);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bodyUris != null) {
			ArrayList bodies = new ArrayList();
			for (String bodyUri : bodyUris) {
				String[] bodyArray = getContentBody(Uri.parse(bodyUri));
				if (bodyArray != null && bodyArray.length > 1) {					
					ArrayList columns = new ArrayList();
					
					// add first column (not _id)
					String body = bodyArray[1];
					columns.add(body);

					// add second column if available					
					if (bodyArray.length > 2){
						body = bodyArray[2];
						columns.add(body);
					}
					bodies.add(columns);
				}
			}
			
			////// TODO ///////
			// Don't know how to do this!!
			//return new ArrayListCursor(ContentIndex.ContentBody.COLUMNS, bodies);
			
			// Have to use MatrixCursor - but how?
			
			return null;
		} else {
			return null;
		}
	}

	@Override
	public String getType(Uri url) {
		switch (URL_MATCHER.match(url)) {
		case DIRECTORIES:
			return "vnd.android.cursor.dir/vnd.openintents.contentdirectory";

		case DIRECTORY:
			return "vnd.android.cursor.item/vnd.openintents.contentdirectory";
		case INDEX_ENTRIES:
			return "vnd.android.cursor.item/vnd.openintents.contentindexentry";
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

/*
	@Override
	protected void bootstrapDatabase() {
		super.bootstrapDatabase();
		DatabaseHelper dbHelper = new DatabaseHelper();
		dbHelper.onCreate(db);
	}
*/

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
		switch (URL_MATCHER.match(url)) {
		case DIRECTORIES:
			count = db.delete("dirs", where, whereArgs);
			break;

		case DIRECTORY:
			String id = url.getLastPathSegment();
			count = db.delete(
					"dirs",
					"_id="
							+ id
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		return count;
	}

	@Override
	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
		switch (URL_MATCHER.match(url)) {
		case DIRECTORIES:
			count = db.update("dirs", values, where, whereArgs);
			break;

		case DIRECTORY:
			String id = url.getLastPathSegment();
			count = db.update(
					"dirs",
					values,
					"_id="
							+ id
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI("org.openintents.contentindices", "directories",
				DIRECTORIES);
		URL_MATCHER.addURI("org.openintents.contentindices", "directories/#",
				DIRECTORY);
		URL_MATCHER.addURI("org.openintents.contentindices", "entries",
				INDEX_ENTRIES);

	}

	private String[] getContentBody(Uri uri) {

		Directory dir = getDirectoryForUri(uri.toString());
		if (dir != null) {
			String[] projection = getProjection(dir);
			String orderby;

			if (StringUtils.isNotBlank(dir.time_column)) {
				orderby = dir.time_column;
				Log.d(TAG, "orderby=" + dir.time_column);
			} else {
				orderby = dir.id_column;
				Log.d(TAG, "orderby=" + dir.id_column);
			}

			Cursor cursor = getContext().getContentResolver().query(uri,
					projection, null, null, orderby);
			int idIndex = cursor.getColumnIndex("_id");

			int[] columnIndex = getColumnIndex(cursor, projection);
			String[] values = new String[columnIndex.length];

			if (cursor.moveToNext()) {
				String id = cursor.getString(idIndex);
				getValues(cursor, columnIndex, values);
				return values;
			} else {
				// e.g. deleted content will return a cursor with no entries.
				return new String[0];
			}

		} else {
			return null;
		}

	}

	public static ContentValues getContentValues(Directory dir) {
		ContentValues values = new ContentValues();
		values.put("_id", dir.id);
		values.put("parent_id", dir.parent_id);
		values.put("uri", dir.uri);
		values.put("package", dir.package_name);
		values.put("name", dir.name);
		values.put("text_columns", dir.text_columns);
		values.put("id_column", dir.id_column);
		values.put("time_column", dir.time_column);
		values.put("intent_uri", dir.intent_uri);
		values.put("intent_action", dir.intent_action);
		values.put("refreshed", 0);
		values.put("updated", 0);
		return values;
	}

	public static Directory getDirectory(Cursor cursor) {
		Directory dir = new Directory();
		dir.id = cursor.getLong(0);
		dir.parent_id = cursor.getLong(1);
		dir.uri = cursor.getString(2);
		dir.package_name = cursor.getString(3);
		dir.name = cursor.getString(4);
		dir.text_columns = cursor.getString(5);
		dir.id_column = cursor.getString(6);
		dir.time_column = cursor.getString(7);
		dir.intent_uri = cursor.getString(8);
		dir.intent_action = cursor.getString(9);
		dir.refreshed = cursor.getLong(10);
		dir.updated = cursor.getLong(11);
		return dir;
	}

	private Cursor getDirectories(String selection, String[] args,
			String orderBy) {
	
		SQLiteQueryBuilder qb =new SQLiteQueryBuilder();
		qb.setTables("dirs");
		String[] columns = new String[] { "_id", "parent_id", "uri", "package",
				"name", "text_columns", "id_column", "time_column",
				"intent_uri", "intent_action", "refreshed", "updated" };
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return qb.query(db, columns, selection, args, null, null, orderBy);
	}

	private ArrayList<Directory> getDirectoryList(String selection,
			String[] args, String orderBy) {
		ArrayList<Directory> list = new ArrayList<Directory>();

		Cursor c = getDirectories(selection, args, orderBy);
		while (c.moveToNext()) {
			Directory dir = getDirectory(c);
			list.add(dir);
		}
		c.close();

		return list;
	}

	private Directory getDirectoryForUri(String uri) {
		Directory result = null;
		int maxLength = -1;

		for (Directory dir : getDirectoryList(null, null, null)) {
			if (uri.startsWith(dir.uri) && uri.length() > maxLength) {
				result = dir;
				maxLength = uri.length();
			}
		}
		return result;
	}

	private String[] getProjection(Directory dir) {
		ArrayList<String> l = new ArrayList<String>();
		l.add("_id");

		String[] textColumns = dir.getTextColumns();

		for (int i = 0; textColumns != null && i < textColumns.length; i++) {
			if (StringUtils.isNotBlank(textColumns[i])) {
				l.add(textColumns[i].trim());
			}
		}

		return l.toArray(new String[l.size()]);
	}

	private void getValues(Cursor cursor, int[] columnIndex, String[] values) {
		for (int i = 0; i < columnIndex.length; i++) {			
			values[i] = cursor.getString(columnIndex[i]);
			Log.v(_TAG, i + ":" + values[i]);
		}
	}

	private int[] getColumnIndex(Cursor cursor, String[] projection) {
		int[] index = new int[projection.length];		
		for (int i = 0; i < projection.length; i++) {
			index[i] = cursor.getColumnIndex(projection[i]);
		}
		return index;
	}

}
