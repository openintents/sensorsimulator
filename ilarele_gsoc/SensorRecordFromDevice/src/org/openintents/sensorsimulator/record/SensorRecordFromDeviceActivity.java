package org.openintents.sensorsimulator.record;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SensorRecordFromDeviceActivity extends Activity {
	protected static final String TAG = "SensorRecordFromDeviceActivity ";
	private EditText mIpAddress;
	private SensorsAdapter mSensorsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);

		setIpText();
		setButtons();
		fillSensorsList();
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
		sensorsObjects.add(new SimpleSensor(SimpleSensor.TYPE_BARCODE_READER));
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
		final Button recordBtn = (Button) findViewById(R.id.record_btn);
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// connect to server (ip set in edittext)
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
					if (item.isEnabled())
						enabledSensors.add(item.getType());
				}
				int[] arrayEnabled = new int[enabledSensors.size()];
				for (int i = 0; i < enabledSensors.size(); i++) {
					arrayEnabled[i] = enabledSensors.get(i);
				}
				intent.putExtra("sensors", arrayEnabled);

				startService(intent);
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
}