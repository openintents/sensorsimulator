package org.openintents.sensorsimulator.record;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SensorRecordFromDeviceActivity extends Activity {
	protected static final String TAG = "SensorRecordFromDeviceActivity ";
	private static final int REQUEST_SENT_MAIL = 1;
	private EditText mIpAddress;
	private SensorsAdapter mSensorsAdapter;
	private Button mSendEmail;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);

		setIpText();
		setButtons();
		fillSensorsList();

		sendDeviceInfo();
	}

	private void setIpText() {
		mIpAddress = (EditText) findViewById(R.id.ip_txt);
	}

	private void fillSensorsList() {
		ArrayList<SimpleSensor> sensorsObjects = new ArrayList<SimpleSensor>();
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_ACCELEROMETER));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_MAGNETIC_FIELD));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_ORIENTATION));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_GYROSCOPE));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_LIGHT));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_PRESSURE));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_TEMPERATURE));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_PROXIMITY));
		sensorsObjects.add(new SimpleSensor(
				SimpleSensor.TYPE_LINEAR_ACCELERATION));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_GRAVITY));
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_ROTATION_VECTOR));

		final ListView sensorsList = (ListView) findViewById(R.id.sensors_list);
		mSensorsAdapter = new SensorsAdapter(this, sensorsObjects);
		sensorsList.setAdapter(mSensorsAdapter);
		sensorsList.setOnItemClickListener(mSensorsAdapter);
	}

	private void setButtons() {
		mSendEmail = (Button) findViewById(R.id.send_email);
		mSendEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(SensorRecordFromDeviceActivity.this);
				sendEMail();
				// mark it as send anyway (we won't bother the user again)
				Editor editor = prefs.edit();
				editor.putBoolean("send_device", true);
				editor.commit();
				mSendEmail.setVisibility(View.GONE);
			}
		});
		final Button recordBtn = (Button) findViewById(R.id.record_btn);
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// check the internet connection
				if (isInternetConnected()) {

					// connect to server (ip set in editText)
					String currentIp = mIpAddress.getText().toString();
					if (currentIp == null || currentIp.equals("")) {
						Toast.makeText(v.getContext(), R.string.set_ip,
								Toast.LENGTH_SHORT).show();
						return;
					}

					Intent intent = new Intent(v.getContext(),
							SensorRecordService.class);
					intent.putExtra("ip", currentIp);
					ArrayList<Integer> enabledSensors = new ArrayList<Integer>();
					for (int i = 0; i < mSensorsAdapter.getCount(); i++) {
						SimpleSensor item = mSensorsAdapter.getItem(i);
						if (item.isEnabled()) {
							enabledSensors.add(item.getType());
						}
					}
					int[] arrayEnabled = new int[enabledSensors.size()];
					for (int i = 0; i < enabledSensors.size(); i++) {
						arrayEnabled[i] = enabledSensors.get(i);
					}
					intent.putExtra("sensors", arrayEnabled);

					startService(intent);
				} else {
					Toast.makeText(SensorRecordFromDeviceActivity.this,
							"Check your internet connection",
							Toast.LENGTH_SHORT).show();
				}
			}

		});
		final Button stopBtn = (Button) findViewById(R.id.stop_btn);
		stopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						SensorRecordService.class);
				stopService(intent);
			}
		});
	}

	protected boolean isInternetConnected() {
		ConnectivityManager connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connection.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isAvailable()
				&& activeNetwork.isConnected())
			return true;
		return false;
	}

	private void sendDeviceInfo() {
		// if we don't have Internet connection, don't do useless operations
		if (!isInternetConnected())
			return;

		// check if I already send this report
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean sent = prefs.getBoolean("send_device", false);

		if (!sent) {
			// check if the device is known in the release
			String[] alreadySupported = getString(R.string.supported_phones)
					.split(",");
			for (String supported : alreadySupported) {
				if (supported.trim().equals(android.os.Build.MODEL.trim()))
					return;
			}

			// if it is not found, show send email button
			mSendEmail.setVisibility(View.VISIBLE);
		}
	}

	private void sendEMail() {
		SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// List of Sensors Available
		List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ALL);

		StringBuilder sendText = new StringBuilder();
		sendText.append("Dear OpenIntents support team,\n\n");
		sendText.append(android.os.Build.MODEL + ";");
		for (Sensor sensor : list) {
			sendText.append(sensor.getName() + ", ");
		}
		sendText.append("\n\nPlease fill this information in SensorSimulator/src/configPhone.txt file, "
				+ "in the following format example:\n");
		sendText.append("Nexus S;accelerometer,magnetic field,orientation,"
				+ "linear acceleration,gravity,rotation vector,gyroscope,light,proximity\n\n");

		sendText.append("Also add the device name in the string resource "
				+ "SensorRecordFromDevice/res/values/string/supported_phones\n");
		sendText.append("------\n\n");
		sendText.append("Thanks!\n");

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { "support@openintents.org" });
		sendIntent.putExtra(Intent.EXTRA_TEXT, sendText.toString());
		sendIntent.putExtra(Intent.EXTRA_SUBJECT,
				"SensorSimulator: new device sensor configuration.");
		sendIntent.setType("message/rfc822");
		startActivityForResult(
				Intent.createChooser(sendIntent, "Send message:"),
				REQUEST_SENT_MAIL);
	}
}