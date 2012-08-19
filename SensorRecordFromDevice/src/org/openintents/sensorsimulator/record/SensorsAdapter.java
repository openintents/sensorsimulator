package org.openintents.sensorsimulator.record;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SensorsAdapter extends ArrayAdapter<SimpleSensor> implements
		OnItemClickListener {

	private LayoutInflater mInflater;

	public SensorsAdapter(Context context, ArrayList<SimpleSensor> objects) {
		super(context, R.layout.list_item, objects);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final SimpleSensor sensor = getItem(position);
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.enable = (CheckBox) convertView
					.findViewById(R.id.item_enabled);

			holder.name = (TextView) convertView.findViewById(R.id.item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.

		holder.name.setText(sensor.getName());
		holder.enable.setChecked(sensor.isEnabled());
		return convertView;
	}

	class ViewHolder {
		public TextView name;
		public CheckBox enable;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SimpleSensor sensor = getItem(position);
		sensor.changeEnable();
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.enable.setChecked(sensor.isEnabled());
	}
}
