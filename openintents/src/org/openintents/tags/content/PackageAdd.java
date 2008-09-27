package org.openintents.tags.content;

import java.io.InputStream;

import org.openintents.R;
import org.openintents.provider.ContentIndex;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PackageAdd extends Activity {

	private static final String TAG = "PackageAdd";

	private EditText mURL;

	private ProgressDialog mProgress;

	private volatile Handler mHandler;
		
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.package_add);

		mHandler = new Handler();

		mURL = (EditText) findViewById(R.id.url);

		Button add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doLoad();
			}
		});
	}

	private void doLoad() {
		String title = getString(R.string.dialog_title_package_add);
		String msg = getString(R.string.dialog_message_package_add);
		mProgress = ProgressDialog.show(PackageAdd.this, title, msg, true, false);

		Thread t = new Thread(new LoadingWorker(mURL.getText().toString()));
		t.start();
	}

	private class LoadingWorker implements Runnable {
		private String url;

		public LoadingWorker(String url) {
			this.url = url;
		}

		public void run() {
			String message = null;
			
			InputStream in = null;
			try {
				in = HTTPUtils.open(url);
				
				DirectoryRegister reg = new DirectoryRegister(PackageAdd.this);
				reg.fromXML(in);
			} catch (Exception e) {
				Log.e(TAG, "LoadingWorker.run", e);
				message = e.getMessage();
			}
			finally {
				HTTPUtils.close(in);
				mHandler.post(new ClosingWorker(message));
			}
		}
	}

	private class ClosingWorker implements Runnable {

		private final String message;

		public ClosingWorker(String message) {
			this.message = message;
		}

		public void run() {
			mProgress.dismiss();
			
			if (message != null) {
				String title = getString(R.string.dialog_title_package_add);				
				new AlertDialog.Builder(PackageAdd.this).setTitle(title). 
				setMessage(message).show();					
			}
			else {
				finish();
			}
		}
	}
}
