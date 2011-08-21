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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SensorRecordFromDeviceActivity extends Activity {
	private static final String TAG = "SensorRecordFromDeviceActivity ";

	private static final String PREF_EMAIL_SENT = "send_device";
	private static final String PREF_IP_ADDRESS = "ip";
	private static final String PREF_SUPPORTED_SENSORS = "supported_sensors";

	protected static final CharSequence MSG_INTERNET_CONNECTION = "Please check your internet connection!";

	protected static final CharSequence MSG_CHECK_MIN_ONE = "Please choose at least one sensor!";

	private EditText mIpAddress;
	private SensorsAdapter mSensorsAdapter;
	private Button mSendEmail;
	private SharedPreferences mPreferences;
	private Editor mPrefsEditor;
	private String mFullDeviceName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefsEditor = mPreferences.edit();

		buildFullDeviceName();

		setIpText();
		setButtons();
		fillSensorsList();

		sendDeviceInfo();
	}

	private void buildFullDeviceName() {
		String firstChar = android.os.Build.MANUFACTURER.charAt(0) + "";
		mFullDeviceName = firstChar.toUpperCase()
				+ android.os.Build.MANUFACTURER.substring(1) + " - "
				+ android.os.Build.MODEL;
	}

	private void setIpText() {
		mIpAddress = (EditText) findViewById(R.id.ip_txt);
		// load from prefs
		String savedIpAddress = mPreferences.getString(PREF_IP_ADDRESS, "");
		if (!savedIpAddress.equals("")) {
			mIpAddress.setText(savedIpAddress);
		}
	}

	private void fillSensorsList() {
		ArrayList<SimpleSensor> sensorsObjects = new ArrayList<SimpleSensor>();
		// get saved sensors
		// if enableSensor[5] <=> sensor of type 5 (light) is enabled
		boolean[] enableSensor = new boolean[SimpleSensor.MAX_SENSORS];
		String[] savedSensorsString = mPreferences.getString(
				PREF_SUPPORTED_SENSORS, "").split(" ");
		for (String savedSensor : savedSensorsString) {
			if (!savedSensor.equals("")) {
				int type = Integer.parseInt(savedSensor);
				enableSensor[type] = true;
			}
		}
		for (int type = 1; type < SimpleSensor.MAX_SENSORS; type++) {
			sensorsObjects.add(new SimpleSensor(type, enableSensor[type]));
		}

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
				sendEMail();
				// mark it as send anyway (we won't bother the user again)
				mPrefsEditor.putBoolean(PREF_EMAIL_SENT, true);
				mPrefsEditor.commit();
				mSendEmail.setVisibility(View.GONE);
			}
		});
		final Button recordBtn = (Button) findViewById(R.id.record_btn);
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// get input and save it in preferences
				String currentIp = mIpAddress.getText().toString();

				ArrayList<Integer> enabledSensors = new ArrayList<Integer>();
				StringBuffer enabledString = new StringBuffer();
				for (int i = 0; i < mSensorsAdapter.getCount(); i++) {
					SimpleSensor item = mSensorsAdapter.getItem(i);
					if (item.isEnabled()) {
						int type = item.getType();
						enabledSensors.add(type);
						enabledString.append(type + " ");
					}
				}
				if (enabledString.length() > 0) {
					enabledString.deleteCharAt(enabledString.length() - 1);
				}
				saveToPrefs(currentIp, enabledString.toString());

				// check the Internet connection
				if (isInternetConnected()) {
					// connect to server (ip set in editText)
					if (currentIp == null || currentIp.equals("")) {
						Toast.makeText(v.getContext(), R.string.set_ip,
								Toast.LENGTH_SHORT).show();
						return;
					}

					Intent intent = new Intent(v.getContext(),
							SensorRecordService.class);
					intent.putExtra("ip", currentIp);
					if (enabledSensors.size() > 0) {
						int[] arrayEnabled = new int[enabledSensors.size()];
						for (int i = 0; i < enabledSensors.size(); i++) {
							arrayEnabled[i] = enabledSensors.get(i);
						}
						intent.putExtra("sensors", arrayEnabled);

						startService(intent);
					} else {
						Toast.makeText(SensorRecordFromDeviceActivity.this,
								MSG_CHECK_MIN_ONE, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(SensorRecordFromDeviceActivity.this,
							MSG_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
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

	protected void saveToPrefs(String ipAddress, String enabledString) {
		mPrefsEditor.putString(PREF_IP_ADDRESS, ipAddress);
		Log.d(TAG, "}" + enabledString + "{");

		mPrefsEditor.putString(PREF_SUPPORTED_SENSORS, enabledString);
		mPrefsEditor.commit();
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
		boolean sent = mPreferences.getBoolean(PREF_EMAIL_SENT, false);

		if (!sent) {
			// check if the device is known in the release
			String[] alreadySupported = getString(R.string.supported_phones)
					.split(",");
			for (String supported : alreadySupported) {

				if (supported.trim().toLowerCase()
						.equals(mFullDeviceName.toLowerCase()))
					return;
			}

			// if it is not found, show send email button
			mSendEmail.setVisibility(View.VISIBLE);
		}
	}

	private void sendEMail() {
		StringBuilder sendText = new StringBuilder();
		sendText.append("Dear OpenIntents support team,\n\n");

		sendText.append("Please find below device-specific information for the SensorSimulator.\n\n");

		sendText.append("Add device name in SensorRecordFromDevice/res/values/string/supported_phones.\n");
		sendText.append("Add configPhone.txt string in SensorSimulator/src/configPhone.txt.\n\n");

		sendText.append("Thanks.\n\n");

		sendText.append("-------------\n");
		sendText.append("Device name: " + mFullDeviceName + "\n");
		String configText = buildConfigText();
		sendText.append("configPhone.txt string: " + configText + "\n\n");

		sendText.append("More device info:\n");
		sendText.append("Manufacturer: " + Build.MANUFACTURER + "\n");
		sendText.append("Brand: " + Build.BRAND + "\n");
		sendText.append("Product: " + Build.PRODUCT + "\n");
		sendText.append("Device: " + Build.DEVICE + "\n");
		sendText.append("User: " + Build.USER + "\n");
		sendText.append("Version: " + Build.VERSION.RELEASE + "\n");
		sendText.append("-------------\n\n");

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { "support@openintents.org" });
		sendIntent.putExtra(Intent.EXTRA_TEXT, sendText.toString());
		sendIntent.putExtra(Intent.EXTRA_SUBJECT,
				"SensorSimulator: new device sensor configuration for "
						+ mFullDeviceName);
		sendIntent.setType("message/rfc822");
		startActivity(Intent.createChooser(sendIntent, "Send message:"));
	}

	private String buildConfigText() {
		SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// List of all available sensors
		List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ALL);

		StringBuffer sb = new StringBuffer();
		sb.append(mFullDeviceName + ";");
		for (Sensor sensor : list) {
			String name = getNameByType(sensor.getType());
			if (name != null) {
				sb.append(name + ",");
			}
		}
		if (list.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	private String getNameByType(int type) {
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
			return "accelerometer";
		case SimpleSensor.TYPE_GRAVITY:
			return "gravity";
		case Sensor.TYPE_GYROSCOPE:
			return "gyroscope";
		case Sensor.TYPE_LIGHT:
			return "light";
		case SimpleSensor.TYPE_LINEAR_ACCELERATION:
			return "linear acceleration";
		case Sensor.TYPE_MAGNETIC_FIELD:
			return "magnetic field";
		case Sensor.TYPE_ORIENTATION:
			return "orientation";
		case Sensor.TYPE_PRESSURE:
			return "pressure";
		case Sensor.TYPE_PROXIMITY:
			return "proximity";
		case SimpleSensor.TYPE_ROTATION_VECTOR:
			return "rotation vector";
		case Sensor.TYPE_TEMPERATURE:
			return "temperature";
		}
		return null;
	}
}