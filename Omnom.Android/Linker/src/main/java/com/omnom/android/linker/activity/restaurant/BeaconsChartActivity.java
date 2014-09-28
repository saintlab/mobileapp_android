package com.omnom.android.linker.activity.restaurant;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.omnom.android.linker.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import hugo.weaving.DebugLog;

import static com.omnom.util.utils.AndroidUtils.showToast;

public class BeaconsChartActivity extends Activity {

	public class BeaconDataSerie extends XYSeries {
		private final String mName;
		private final int mTxPower;
		ArrayList<Integer> rssiList = new ArrayList<Integer>();
		private int mColor;
		private int index = 0;

		BeaconDataSerie(String name, int txPower) {
			super(name);
			mName = name;
			mTxPower = txPower;
		}

		public String getName() {
			return mName;
		}

		public int getTxPower() {
			return mTxPower;
		}

		public void addRecord(BeaconDataRecord record) {
			add(index, record.getRssi());
			index++;
			rssiList.add((int) record.getRssi());
		}



		public int size() {
			return rssiList.size();
		}

		public int getColor() {
			return mColor;
		}

		public void setColor(int color) {
			mColor = color;
		}
	}

	private class BeaconDataRecord {

		private double mRssi;

		public BeaconDataRecord(double rssi) {
			mRssi = rssi;
		}

		public double getRssi() {
			return mRssi;
		}
	}

	private XYMultipleSeriesRenderer mRenderer;
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private BluetoothAdapter.LeScanCallback mLeScanCallback;
	private BeaconParser parser;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mAdapter;
	private boolean mScanning;
	private HashMap<String, BeaconDataSerie> beaconsData = new HashMap<String, BeaconDataSerie>();
	private LinearLayout mRootView;

	private int[] mColors = new int[]{Color.RED, Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GREEN, Color.LTGRAY, Color.MAGENTA,
			Color.WHITE, Color.YELLOW};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacons_chart);

		mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		mAdapter = mBluetoothManager.getAdapter();
		parser = new BeaconParser();
		parser.setBeaconLayout(getResources().getString(R.string.redbear_beacon_layout));

		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			@DebugLog
			public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);
						saveData(beacon);
						Log.d("BEACON", beacon.toDebugString());
					}
				});
			}
		};

		mRootView = (LinearLayout) findViewById(R.id.root);
	}

	private void saveData(Beacon beacon) {
		String minor = beacon.getIdValue(2);
		BeaconDataSerie beaconDataSerie = beaconsData.get(minor);
		if(beaconDataSerie == null) {
			beaconDataSerie = new BeaconDataSerie(minor, beacon.getTxPower());
		}
		beaconDataSerie.addRecord(new BeaconDataRecord(beacon.getRssi()));
		beaconsData.put(minor, beaconDataSerie);
		mRootView.invalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.beacons_chart, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch(id) {
			case R.id.action_details:
				BeaconsListActivity.start(this, beaconsData);
				return true;

			case R.id.action_start:
				if(!mScanning) {
					showToast(this, R.string.started);
					mAdapter.startLeScan(mLeScanCallback);
					beaconsData.clear();
					mDataset.clear();
					mScanning = true;
				}
				return true;

			case R.id.action_stop:
				if(mScanning) {
					showToast(this, R.string.stopped);
					mAdapter.stopLeScan(mLeScanCallback);
					onScanStopped();
					mScanning = false;
				}
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onScanStopped() {
		mRootView.removeAllViews();
		int i = 0;
		mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setLabelsTextSize(24);
		mRenderer.setLegendTextSize(24);
		mRenderer.setXLabels(0);
		mRenderer.setPanEnabled(true);
		mRenderer.setShowGrid(true);
		mRenderer.setShowCustomTextGrid(true);
		mRenderer.setGridColor(Color.LTGRAY);
		mRenderer.setYAxisMax(-40);
		mRenderer.setYAxisMin(-70);
		for (int y = -40; y > -80; y-=2) {
			mRenderer.addYTextLabel(y, y + "");
		}
		mDataset.clear();

		for(Map.Entry<String, BeaconDataSerie> entry : beaconsData.entrySet()) {
			BeaconDataSerie value = entry.getValue();
			final int color = mColors[i % mColors.length];
			value.setColor(color);
			mDataset.addSeries(value);
			XYSeriesRenderer renderer = new XYSeriesRenderer();
			renderer.setAnnotationsTextSize(40);
			renderer.setChartValuesTextSize(40);
			renderer.setFillPoints(true);
			renderer.setLineWidth(6);
			renderer.setColor(color);
			i++;
			mRenderer.addSeriesRenderer(renderer);
		}

		GraphicalView test = ChartFactory.getTimeChartView(this, mDataset, mRenderer, "TEST");
		mRootView.addView(test);
	}
}
