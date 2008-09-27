package org.openintents.tags.content;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class Directory {

	private static final String TAG = "Directory";

	private static final Pattern INTENT_URI_PTN = Pattern.compile("\\{([a-zA-Z_]+)\\}");
	
	public long id;

	public long parent_id;
	
	public String uri;
	
	public String name;
	
	public String package_name;
	
	public String text_columns;
	
	public String id_column;
	
	public String time_column;
	
	public String intent_action;
	
	public String intent_uri;
	
	public Drawable icon;
	
	public long refreshed;
	
	public long updated;
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Directory) {
			Directory other = (Directory) o;
			return (this.id == other.id);
		}
		return false;
	}

	public String[] getTextColumns() {
		return text_columns == null ? null : text_columns.split("\\s*,\\s*");
	}

	public Drawable getIcon(Context ctx) {
		if (icon != null) {
			return icon;
		}

		PackageManager pm = ctx.getPackageManager();
		try {
			icon = pm.getApplicationIcon(package_name);
		} catch (Exception e) {
			Log.e(TAG, "getIcon", e);
			icon = pm.getDefaultActivityIcon();
		}
		return icon;
	}

	public Intent createIntent(String id, String body) throws URISyntaxException {
		Intent intent = new Intent(intent_action, createURI(id, body));
		return intent;
	}

	public Uri createURI(String _id, String body) throws URISyntaxException {
		if (text_columns == null || body == null) {
			return  Uri.parse(uri);
		}

		String[] names = getTextColumns();
		String[] values = body.split("\t");

		return createURI(_id, names, values);
	}

	public Uri createURI(String _id, String[] names, String[] values) throws URISyntaxException {
		String uri = intent_uri.replace("{_id}", _id);

		if (names == null || names.length == 0 || values == null || values.length == 0) {
			return Uri.parse(uri);
		}

		Matcher m = INTENT_URI_PTN.matcher(uri);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String name = m.group(1);
			for (int i = 0; i < names.length && i < values.length; i++) {
				if (names[i] != null && names[i].equals(name)) {
					m.appendReplacement(buf, values[i]);
				}
			}
		}
		m.appendTail(buf);

		Log.d(TAG, "uri=" + buf.toString());

		return Uri.parse(buf.toString());
	}

}
