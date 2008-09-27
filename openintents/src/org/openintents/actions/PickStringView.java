package org.openintents.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.R;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class PickStringView extends ListActivity {

	static final String EXTRA_LIST = "list";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		String list = getIntent().getExtras().getString(
				PickStringView.EXTRA_LIST);
		String[] actions = list.split(" ");
		ArrayAdapter adapter = new ArrayAdapter(this,
				R.layout.simple_list_item_1, actions);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView adapterview, View view,
							int i, long l) {
						String item = (String) getListAdapter().getItem(i);
						setResult(Activity.RESULT_OK, new android.content.Intent(item));
						finish();
					}

				});
	}
}
