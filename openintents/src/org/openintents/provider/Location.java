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

import com.google.android.maps.GeoPoint;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.BaseColumns;

/**
 * Definition for content provider related to location.
 * 
 */
public class Location {

	public static final class Locations implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.locations/locations");

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The latitude of the location
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String LATITUDE = "latitude";

		/**
		 * The longitude of the location
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String LONGITUDE = "longitude";

		/**
		 * The timestamp for when the note was created
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the note was last modified
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		/**
		 * bundle/extra key for pick action, containing location uri with scheme geo: 
		 */
		public static final String EXTRA_GEO = "geo";
	}

	public static final class Extras implements BaseColumns {

		public static final String LOCATION_ID = "locationId";
		public static final String KEY = "key";
		public static final String VALUE = "value";

		public static final String URI_PATH_EXTRAS = "extras";
		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.locations/extras");

	}

	private ContentResolver mResolver;

	public Location(ContentResolver resolver) {
		mResolver = resolver;
	}

	public Uri addLocation(android.location.Location location) {
		ContentValues values = new ContentValues(2);
		values.put(Locations.LATITUDE, location.getLatitude());
		values.put(Locations.LONGITUDE, location.getLongitude());
		return mResolver.insert(Locations.CONTENT_URI, values);
	}

	public int deleteLocation(long id) {

		return mResolver.delete(ContentUris.withAppendedId(
				Locations.CONTENT_URI, id), null, null);

	}

	public GeoPoint getPoint(long id) {
		Cursor cursor = mResolver.query(ContentUris.withAppendedId(
				Locations.CONTENT_URI, id), new String[] { Locations._ID,
				Locations.LATITUDE, Locations.LONGITUDE }, null, null,
				Locations.DEFAULT_SORT_ORDER);
		if (cursor.moveToNext()) {
			int lat = Double.valueOf(cursor.getDouble(1) * 1E6).intValue();
			int lon = Double.valueOf(cursor.getDouble(2) * 1E6).intValue();
			return new GeoPoint(lat, lon);
		} else {
			return null;
		}
	}

	public Cursor queryExtras(long locationId) {
		Builder uri = Locations.CONTENT_URI.buildUpon().appendPath(
				String.valueOf(locationId)).appendPath(Extras.URI_PATH_EXTRAS);
		return mResolver.query(uri.build(), new String[] { Extras._ID,
				Extras.KEY, Extras.VALUE}, null, null, Extras.KEY + "," + Extras.VALUE);
	}
	
	public void updateExtras(long locationId, long extrasId, ContentValues values){
		
	}

	public int deleteExtra(long extraId) {
		return mResolver.delete(ContentUris.withAppendedId(Extras.CONTENT_URI,
				extraId), null, null);

	}

	public Uri addExtra(long locationId) {
		Builder uri = Locations.CONTENT_URI.buildUpon().appendPath(
				String.valueOf(locationId)).appendPath(Extras.URI_PATH_EXTRAS);
		return mResolver.insert(uri.build(), null);

	}
}
