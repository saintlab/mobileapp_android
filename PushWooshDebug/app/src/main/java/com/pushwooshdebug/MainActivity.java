package com.pushwooshdebug;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.android.push.BasePushMessageReceiver;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.RegisterBroadcastReceiver;
import com.pushwoosh.debug.R;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

	public static final String PREFS = "prefs";
	public static final String PREF_DEVICE_TOKEN = "device_token";

	BroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver()
	{
		@Override
		public void onRegisterActionReceive(Context context, Intent intent)
		{
			checkMessage(intent);
			String pushToken = PushManager.getPushToken(context);
			String pushwooshHWID = PushManager.getPushwooshHWID(context);
			getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(PREF_DEVICE_TOKEN, pushToken).commit();
			txtToken.setText(pushToken);
			Log.d("TAG", pushToken + " " + pushwooshHWID);
		}
	};

	private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
	{
		@Override
		protected void onMessageReceive(Intent intent)
		{
			showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
		}
	};
	private TextView txtData;
	private TextView txtToken;

	public void registerReceivers()
	{
		IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

		registerReceiver(mReceiver, intentFilter);

		registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
	}

	public void unregisterReceivers()
	{
		try
		{
			unregisterReceiver(mReceiver);
		}
		catch (Exception e)
		{
			// pass.
		}

		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch (Exception e)
		{
			//pass through
		}
	}

	private void checkMessage(Intent intent)
	{
		if (null != intent)
		{
			if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
			{
				showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
			}
			else if (intent.hasExtra(PushManager.REGISTER_EVENT))
			{
				showMessage("register");
			}
			else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
			{
				showMessage("unregister");
			}
			else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
			{
				showMessage("register error");
			}
			else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
			{
				showMessage("unregister error");
			}

			resetIntentValues();
		}
	}

	private void resetIntentValues()
	{
		Intent mainAppIntent = getIntent();

		if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
		}
		else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
		{
			mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
		}

		setIntent(mainAppIntent);
	}

	private void showMessage(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		txtData.setText(message);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    txtData = (TextView) findViewById(R.id.push_data);
	    txtToken = (TextView) findViewById(R.id.push_token);
	    txtToken.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			    CharSequence text = txtToken.getText();
			    cm.setPrimaryClip(ClipData.newPlainText(text, text));
			    Toast.makeText(MainActivity.this, "Token copied to clipboard", Toast.LENGTH_SHORT).show();
		    }
	    });

	    registerReceivers();

	    PushManager pushManager = PushManager.getInstance(this);
	    try {
		    pushManager.onStartup(this);
	    }
	    catch(Exception e)
	    {
		    Log.e(TAG, "PushManager.onStartup()", e);
	    }

	    pushManager.registerForPushNotifications();
	    checkMessage(getIntent());
    }

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);

		checkMessage(intent);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		txtToken.setText(getSharedPreferences(PREFS, MODE_PRIVATE).getString(PREF_DEVICE_TOKEN, "unknown_token"));
		registerReceivers();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unregisterReceivers();
	}
}
