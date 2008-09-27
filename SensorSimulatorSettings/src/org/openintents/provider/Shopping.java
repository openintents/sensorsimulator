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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Definition for content provider related to shopping.
 * 
 */
public abstract class Shopping {

	/**
	 * TAG for logging.
	 */
	private static final String TAG = "Shopping";
	public static final String ITEM_TYPE = "vnd.android.cursor.item/vnd.openintents.shopping.item";

	/**
	 * Items that can be put into shopping lists.
	 */
	public static final class Items implements BaseColumns {
		/**
		 * The content:// style URL for this table.
		 */
		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.shopping/items");

		/**
		 * The default sort order for this table.
		 */
		public static final String DEFAULT_SORT_ORDER = "modified ASC";

		/**
		 * The name of the item.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * An image of the item (uri).
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String IMAGE = "image";

		/**
		 * The timestamp for when the item was created.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the item was last modified.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		/**
		 * The timestamp for when the item was last accessed.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String ACCESSED_DATE = "accessed";
		
		/**
		 * Generic projection map.
		 */
		public static final String[] PROJECTION = {
			_ID,
			NAME,
			IMAGE,
			CREATED_DATE,
			MODIFIED_DATE,
			ACCESSED_DATE
		};
		
		/**
		 * Offset in PROJECTION array.
		 */
		public static final int PROJECTION_ID = 0;
		public static final int PROJECTION_NAME = 1;
		public static final int PROJECTION_IMAGE = 2;
		public static final int PROJECTION_CREATED_DATE = 3;
		public static final int PROJECTION_MODIFIED_DATE = 4;
		public static final int PROJECTION_ACCESSED_DATE = 5;
	}

	/**
	 * Shopping lists that can contain items.
	 */
	public static final class Lists implements BaseColumns {
		/**
		 * The content:// style URL for this table.
		 */
		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.shopping/lists");

		/**
		 * The default sort order for this table.
		 */
		public static final String DEFAULT_SORT_ORDER
		// = "modified DESC";
		= "modified ASC";

		/**
		 * The name of the list.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * An image of the list (uri).
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String IMAGE = "image";

		/**
		 * The timestamp for when the item was created.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the item was last modified.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		/**
		 * The timestamp for when the item was last accessed.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String ACCESSED_DATE = "accessed";

		/**
		 * The name of the shared shopping list that should be worldwide unique.
		 * 
		 * It is formed of the current user's email address and a unique suffix.
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SHARE_NAME = "share_name";

		/**
		 * The comma separated list of contacts with whom this list is shared.
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SHARE_CONTACTS = "share_contacts";

		/**
		 * Name of background image.
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SKIN_BACKGROUND = "skin_background";

		/**
		 * Name of font in list.
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SKIN_FONT = "skin_font";

		/**
		 * Color of text in list.
		 * 
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SKIN_COLOR = "skin_color";

		/**
		 * Color of strikethrough text in list.
		 * 
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SKIN_COLOR_STRIKETHROUGH = "skin_color_strikethrough";
	}

	/**
	 * Information which list contains which items/lists/(recipes)
	 */
	public static final class Contains implements BaseColumns {
		/**
		 * The content:// style URL for this table.
		 */
		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.shopping/contains");

		/**
		 * The default sort order for this table.
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The id of the item.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String ITEM_ID = "item_id";

		/**
		 * The id of the list that contains item_id.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String LIST_ID = "list_id";

		/**
		 * Quantity specifier.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String QUANTITY = "quantity";

		/**
		 * Status: WANT_TO_BUY or BOUGHT.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String STATUS = "status";

		/**
		 * The timestamp for when the item was created.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the item was last modified.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		/**
		 * The timestamp for when the item was last accessed.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String ACCESSED_DATE = "accessed";

		/**
		 * Name of person who inserted the item.
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SHARE_CREATED_BY = "share_created_by";

		/**
		 * Name of person who changed status of the item, for example mark it as
		 * bought.
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SHARE_MODIFIED_BY = "share_modified_by";
	}

	/**
	 * Combined table of contents, items, and lists.
	 */
	public static final class ContainsFull implements BaseColumns {

		/**
		 * The content:// style URL for this table.
		 */
		public static final Uri CONTENT_URI = Uri
				.parse("content://org.openintents.shopping/containsfull");

		/**
		 * The default sort order for this table.
		 */
		public static final String DEFAULT_SORT_ORDER
		// = "contains.modified DESC";
		= "contains.modified ASC";

		// Elements from Contains

		/**
		 * The id of the item.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String ITEM_ID = "item_id";

		/**
		 * The id of the list that contains item_id.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String LIST_ID = "list_id";

		/**
		 * Quantity specifier.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String QUANTITY = "quantity";

		/**
		 * Status: WANT_TO_BUY or BOUGHT.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String STATUS = "status";

		/**
		 * The timestamp for when the item was created.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the item was last modified.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		/**
		 * The timestamp for when the item was last accessed.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String ACCESSED_DATE = "accessed";

		/**
		 * Name of person who inserted the item.
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SHARE_CREATED_BY = "share_created_by";

		/**
		 * Name of person who crossed out the item.
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * Available since release 0.1.6.
		 */
		public static final String SHARE_MODIFIED_BY = "share_modified_by";

		// Elements from Items

		/**
		 * The name of the item.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ITEM_NAME = "item_name";

		/**
		 * An image of the item (uri).
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ITEM_IMAGE = "item_image";

		// Elements from Lists

		/**
		 * The name of the list.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String LIST_NAME = "list_name";

		/**
		 * An image of the list (uri).
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String LIST_IMAGE = "list_image";
	}

	/**
	 * Status of "contains" element.
	 */
	public static final class Status {

		/**
		 * Want to buy this item.
		 */
		public static final long WANT_TO_BUY = 1;

		/**
		 * Have bought this item.
		 */
		public static final long BOUGHT = 2;

		/**
		 * Checks whether a status is a valid possibility.
		 * 
		 * @param s
		 *            status to be checked.
		 * @return true if status is a valid possibility.
		 */
		public static boolean isValid(final long s) {
			return s == WANT_TO_BUY || s == BOUGHT;
		}

	}

	// Some convenience functions follow

	// The content resolver has to be set before accessing
	// any of these functions.
	public static ContentResolver mContentResolver;

	/**
	 * Gets or creates a new item and returns its id. If the item exists
	 * already, the existing id is returned. Otherwise a new item is created.
	 * 
	 * @param name
	 *            New name of the item.
	 * @return id of the new or existing item.
	 */
	public static long getItem(final String name) {
		// TODO check whether item exists

		// Add item to list:
		ContentValues values = new ContentValues(1);
		values.put(Items.NAME, name);
		try {
			Uri uri = mContentResolver.insert(Items.CONTENT_URI, values);
			Log.i(TAG, "Insert new item: " + uri);
			return Long.parseLong(uri.getPathSegments().get(1));
		} catch (Exception e) {
			Log.i(TAG, "Insert item failed", e);
			return -1;
		}
	}

	/**
	 * Gets or creates a new shopping list and returns its id. If the list
	 * exists already, the existing id is returned. Otherwise a new list is
	 * created.
	 * 
	 * @param name
	 *            New name of the list.
	 * @return id of the new or existing list.
	 */
	public static long getList(final String name) {
		// TODO check whether list exists

		// Add item to list:
		ContentValues values = new ContentValues(1);
		values.put(Lists.NAME, name);
		try {
			Uri uri = mContentResolver.insert(Lists.CONTENT_URI, values);
			Log.i(TAG, "Insert new list: " + uri);
			return Long.parseLong(uri.getPathSegments().get(1));
		} catch (Exception e) {
			Log.i(TAG, "insert list failed", e);
			return -1;
		}
	}

	/**
	 * Adds a new item to a specific list and returns its id. If the item exists
	 * already, the existing id is returned.
	 * 
	 * @param itemId
	 *            The id of the new item.
	 * @param listId
	 *            The id of the shopping list the item is added.
	 * @param itemType
	 *            The type of the new item
	 * @return id of the "contains" table entry, or -1 if insert failed.
	 */
	public static long addItemToList(final long itemId, final long listId) {
		// TODO check whether "contains" entry exists

		// Add item to list:
		ContentValues values = new ContentValues(2);
		values.put(Contains.ITEM_ID, itemId);
		values.put(Contains.LIST_ID, listId);
		try {
			Uri uri = mContentResolver.insert(Contains.CONTENT_URI, values);
			Log.i(TAG, "Insert new entry in 'contains': " + uri);
			return Long.parseLong(uri.getPathSegments().get(1));
		} catch (Exception e) {
			Log.i(TAG, "insert into table 'contains' failed", e);
			return -1;
		}
	}

	/**
	 * Returns the id of the default shopping list. Currently this is always 1.
	 * 
	 * @return The id of the default shopping list.
	 */
	public static long getDefaultList() {
		// TODO: Once CentralTagging is available,
		// the shopping list that is tagged as "default"
		// should be returned here.
		return 1;
	}

	// TODO: Can we write a convenience function like this?
	// How can we store information about Activity?
	public static void showList(long listId) {
		// Intent intent = new Intent(Intent.MAIN_ACTION,
		// Shopping.Lists.CONTENT_URI);
		// startActivity(intent);
	}

	public static Uri getListForItem(String itemId) {
		Cursor cursor = mContentResolver.query(Contains.CONTENT_URI,
				new String[] { Contains.LIST_ID }, Contains.ITEM_ID + " = ?",
				new String[] { itemId }, Contains.DEFAULT_SORT_ORDER);
		if (cursor != null && cursor.moveToNext()) {
			return Uri.withAppendedPath(Shopping.Lists.CONTENT_URI, ""
					+ cursor.getString(0));
		} else {
			return null;
		}
	}
}
