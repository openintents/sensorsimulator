package org.openintents.tags.content;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;

public class ContentListAdapter extends CursorAdapter implements Filterable {

	
	public ContentListAdapter(Cursor c, Context context) {
		super(context, c );		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ContentListRow row = (ContentListRow) view;
		row.bindCursor(cursor);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ContentListRow row = new ContentListRow(context);
		bindView(row, context, cursor);
		return row;
	}

}
