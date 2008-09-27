package org.openintents.locations;

import org.openintents.R;
import org.openintents.provider.Location;
import org.openintents.provider.Tag;
import org.openintents.provider.Tag.Tags;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class TagsViewBinder implements ViewBinder {

	Tag mTag;
	private android.location.Location mCurrLocation;

	public TagsViewBinder(Context context, android.location.Location location) {
		mTag = new Tag(context);
		mCurrLocation = location;
	}

	public boolean setViewValue(View view, Cursor cursor, int i) {
		if (view instanceof TextView && "_id".equals(cursor.getColumnName(i))) {
			if (view.getId() == R.id.tags) {
				String tags = mTag.findTags(ContentUris.withAppendedId(
						Location.Locations.CONTENT_URI, cursor.getLong(i))
						.toString(), ", ");
				((TextView) view).setText(tags);
				return true;
			} else {
				final double lat = cursor.getDouble(1);
				final double lon = cursor.getDouble(2);
				final TextView textView = (TextView) view;
				float dist = -1;
				String unit = null;
				if (mCurrLocation != null) {
					android.location.Location loc = new android.location.Location((String) null);
					loc.setLatitude(lat);
					loc.setLongitude(lon);
					dist = mCurrLocation.distanceTo(loc);
					if (dist > 1000) {
						dist = dist / 1000.0f;
						unit = "km";
					} else {
						unit = "m";
					}
				}
				if (dist >= 10) {
					// for numbers >= 10, digits after the decimal point are distracting
					textView.setText(" (" + String.format("%.0f", dist) + " " + unit
							+ " away)");
				} else if (dist >= 0) {
					textView.setText(" (" + String.format("%.1f", dist) + " " + unit
							+ " away)");
				} else {
					textView.setText("");
				}

				return true;
			}
		} else {
			return false;
		}
	}

}
