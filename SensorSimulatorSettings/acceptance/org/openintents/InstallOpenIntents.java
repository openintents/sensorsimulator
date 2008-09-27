package org.openintents;

import org.openintents.main.OpenIntentsView;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.TableLayout;
import android.widget.TableRow;

public class InstallOpenIntents extends positron.TestCase {
	

	protected String getString(int id) {
		return getTargetContext().getString(id);
	}

	public void test() {
		startActivity(new Intent(this.getContext(), OpenIntentsView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		pause();

		// 1.
		assertEquals(getString(R.string.app_name), activity().getTitle());

		// 2. main has two rows
		TableLayout results = (TableLayout) activity().findViewById(
				R.id.grid_main);
		assertEquals(2, results.getChildCount());

		// settings has one row
		results = (TableLayout) activity().findViewById(R.id.grid_settings);
		assertEquals(1, results.getChildCount());

		resume();
		
		// 3. start content browser
		press(DOWN);
		click();
		
		pause();		
		assertEquals(getString(R.string.content_browser), activity().getTitle());		
		resume();
		
		finish();
		
		
		press(RIGHT);		
		click();		
		
		pause();
		assertEquals(getString(R.string.list_of_locations), activity().getTitle());
		resume();
		finish();
		
		press(LEFT, DOWN);		
		click();		
		
		pause();
		//assertEquals(getString(R.string.shopping_list), activity().getTitle());
		resume();
		finish();
		
	}

	public void testContentProviders() throws NameNotFoundException {
		startActivity(new Intent(this.getContext(), OpenIntentsView.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		pause();
		
		ProviderInfo[] p = getTargetContext().getPackageManager().getPackageInfo(getTargetContext().getPackageName(), 8).providers;
		assertEquals(6, p.length);
		
		resume();
	}
	
	public void tearDown() {
		finishAll();
	}
}
