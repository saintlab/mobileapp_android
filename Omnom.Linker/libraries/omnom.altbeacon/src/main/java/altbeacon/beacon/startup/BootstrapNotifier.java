package altbeacon.beacon.startup;

import android.content.Context;

import altbeacon.beacon.MonitorNotifier;

public interface BootstrapNotifier extends MonitorNotifier {
	public Context getApplicationContext();
}
