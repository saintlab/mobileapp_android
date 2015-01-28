package com.saintlab.android.linker.activity.restaurant;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.saintlab.android.linker.R;

import java.util.HashMap;

public class BeaconsListActivity extends ListActivity {

	public static void start(Context context, HashMap<String, BeaconsChartActivity.BeaconDataSerie> beaconsData) {
		data = beaconsData;
		context.startActivity(new Intent(context, BeaconsListActivity.class));
	}

	private static HashMap<String, BeaconsChartActivity.BeaconDataSerie> data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_list);
	    getListView().setAdapter(new BeaconAdapter(this, data.values()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.beacons_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
