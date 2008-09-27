package org.openintents.devtools;

import java.lang.reflect.Field;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ViewInspector {

	private static final String TAG = "ViewInspector";

	public static void analyzeView(View v) {
		Log.i(TAG, v.getClass().toString() + " android.R.id."
				+ printName(v.getId()) + " (" + v.getId() + ")");
		try {
			analyzeViewGroup((ViewGroup) v, "");
		} catch (ClassCastException e) {
			// Log.i(TAG, prefix + " ---- no Viewgroup");
		}
	}

	public static void analyzeViewGroup(ViewGroup a, String prefix) {
		String newprefix = prefix + "- ";
		for (int j = 0; j < a.getChildCount(); j++) {
			View c = a.getChildAt(j);
			// Log.i(TAG, prefix + " Child " + j + " android.R.id." +
			// printName(c.getId()) + " + (" + c.getId() + ")");
			// Log.i(TAG, prefix + " Class: " + c.getClass().toString());
			Log.i(TAG, prefix + c.getClass().toString() + " android.R.id."
					+ printName(c.getId()) + " + (" + c.getId() + ")");
			try {
				analyzeViewGroup((ViewGroup) c, newprefix);
			} catch (ClassCastException e) {
				// Log.i(TAG, prefix + " ---- no Viewgroup");
			}

		}
	}

	public static String printName(int x) {
		android.R.id myid = new android.R.id();
		Field[] f = myid.getClass().getFields();
		for (int i = 0; i < f.length; i++) {
			try {
				// Log.i(TAG, "field " + f[i].getName() + " : " +
				// f[i].getInt(null));
				if (x == f[i].getInt(null)) {
					return f[i].getName();
				}
			} catch (IllegalAccessException e) {
				// Log.i(TAG, " - Illegalaccess");
			}
		}
		return "";
	}

}
