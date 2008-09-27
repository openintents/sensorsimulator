package org.openintents.lib;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

public class MergedCursor implements Cursor {

	private Cursor[] mCursors;
	private HashMap<String, Integer> mColNames;
	private HashMap<Integer, Integer> mRanges;
	private int totalRange = 0;

	private boolean hasUpdates = false;
	private boolean isAfterLast = false;
	private boolean isLast = false;
	private boolean isBeforeFirst = true;

	private int internalPosition = 0;

	public MergedCursor(Cursor[] mCursors) {

		this.mCursors = mCursors;
		init();

	}

	private void init() {
		totalRange = 0;
		mRanges = new HashMap<Integer, Integer>();
		mColNames = new HashMap<String, Integer>();
		for (int i = 0; i < mCursors.length; i++) {
			mRanges.put(mCursors[i].getCount(), i);
			totalRange = totalRange + mCursors[i].getCount();
			String[] cNames = mCursors[i].getColumnNames();
			for (int n = 0; n < cNames.length; n++) {
				mColNames.put(cNames[n], i);
			}
		}
	}

	public boolean getWantsAllOnMoveCalls() {
		return false;
	}

	public int getCount() {
		return totalRange;
	}

	public String[] getColumnNames() {
		return null;
		// TODO: reteive keyset of mColNames, convert to String[]
	}

	public int getColumnIndexOrThrow(String s) {
		return getColumnIndex(s);
	}

	public int getColumnIndex(String colName) {
		if (!colName.equals("_id")) {

			int cursorIndex = mColNames.get(colName);
			return mCursors[cursorIndex].getColumnIndex(colName);

		}
		return 0;
	}

	public boolean isClosed() {
		boolean res = true;
		for (int i = 0; i < mCursors.length; i++) {
			res = res & mCursors[i].isClosed();

		}
		return res;
	}

	public boolean moveToFirst() {
		internalPosition = 0;
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].moveToFirst();

		}
		return true;

	}

	public boolean moveToNext() {
		internalPosition++;
		return true;
	}

	public boolean requery() {
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].requery();
		}
		init();
		return true;
	}

	public boolean isNull(int colIndex) {
		return false;
	}

	public Bundle respond(Bundle b) {
		return null;
	}


	public void close() {
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].close();
		}
		// init();
	}

	public boolean commitUpdates() {
		boolean result = true;
		for (int i = 0; i < mCursors.length; i++) {
			result = result && mCursors[i].requery();
		}
		init();
		return result;
	}

	public void deactivate() {
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].deactivate();
		}

	}

	public boolean deleteRow() {
		return false;
	}

	public int getColumnCount() {
		return mColNames.size();
	}

	public Bundle getExtras() {
		return null;
	}

	public boolean hasUpdates() {
		return this.hasUpdates;
	}

	public boolean isAfterLast() {
		return internalPosition > totalRange;
	}

	public boolean isBeforeFirst() {
		return this.isBeforeFirst;
	}

	public boolean isLast() {
		return (internalPosition == totalRange);
	}

	public boolean moveToLast() {
		internalPosition = totalRange;
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].moveToLast();

		}
		return true;
	}

	public boolean move(int offset) {
		this.internalPosition = this.internalPosition + offset;
		return true;
	}

	public boolean moveToPosition(int pos) {
		this.internalPosition = pos;
		return true;
	}

	public int getPosition() {
		return this.internalPosition;
	}

	public boolean moveToPrevious() {
		this.internalPosition--;
		return true;
	}

	public void registerContentObserver(ContentObserver co) {
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].registerContentObserver(co);
		}
	}

	public void registerDataSetObserver(DataSetObserver da) {
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].registerDataSetObserver(da);
		}
	}

	public void unregisterContentObserver(ContentObserver co) {
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].unregisterContentObserver(co);
		}
	}

	public void unregisterDataSetObserver(DataSetObserver da) {
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].unregisterDataSetObserver(da);
		}
	}

	public boolean supportsUpdates() {
		// oh yea, we do! :)
		return true;
	}

	public void setNotificationUri(ContentResolver cr, Uri uri) {
		for (int i = 0; i < mCursors.length; i++) {
			mCursors[i].setNotificationUri(cr, uri);
		}
	}

	public boolean commitUpdates(
			Map<? extends Long, ? extends Map<String, Object>> values) {
		return false;
	}

	public boolean updateBlob(int columnIndex, byte[] value) {
		return false;
	}

	public boolean updateDouble(int columnIndex, double value) {
		return false;
	}

	public boolean updateFloat(int columnIndex, float value) {
		return false;
	}

	public boolean updateInt(int columnIndex, int value) {
		return false;
	}

	public boolean updateLong(int columnIndex, long value) {
		return false;
	}

	public boolean updateShort(int columnIndex, short value) {
		return false;
	}

	public boolean updateString(int columnIndex, String value) {
		return false;
	}

	public boolean updateToNull(int columnIndex) {
		return false;
	}

	public byte[] getBlob(int columnIndex) {
		return null;
	}

	public double getDouble(int columnIndex) {
		return 0;
	}

	public float getFloat(int columnIndex) {
		return 0f;
	}

	public int getInt(int columnIndex) {
		return 0;
	}

	public long getLong(int columnIndex) {
		return 0l;
	}

	public short getShort(int columnIndex) {
		return 0;
	}

	public String getString(int columnIndex) {
		return null;
	}

	public String getColumnName(int colIndex) {
		return "";
	}

	public boolean isFirst() {
		if (internalPosition == 0) {
			return true;
		}
		return false;
	}

	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		// copied from android.database.AbstractCursor.
		String result = getString(columnIndex);
		if (result != null) {
			char data[] = buffer.data;
			if (data == null || data.length < result.length())
				buffer.data = result.toCharArray();
			else
				result.getChars(0, result.length(), data, 0);
			buffer.sizeCopied = result.length();
		}
	}

}/* eoc */