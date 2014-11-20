package com.omnom.android.debug;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Ch3D on 20.11.2014.
 */
public class ActivityRecognitionIntentService extends IntentService {

	private static final String TAG = ActivityRecognitionIntentService.class.getSimpleName();

	public ActivityRecognitionIntentService() {
		super(TAG);
	}

	private String getNameFromType(int activityType) {
		switch(activityType) {
			case DetectedActivity.IN_VEHICLE:
				return "in_vehicle";
			case DetectedActivity.ON_BICYCLE:
				return "on_bicycle";
			case DetectedActivity.ON_FOOT:
				return "on_foot";
			case DetectedActivity.STILL:
				return "still";
			case DetectedActivity.UNKNOWN:
				return "unknown";
			case DetectedActivity.TILTING:
				return "tilting";
		}
		return "unknown";
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		// If the incoming intent contains an update
		if(ActivityRecognitionResult.hasResult(intent)) {
			ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
			DetectedActivity mostProbableActivity = result.getMostProbableActivity();
			/*
			 * Get the probability that this activity is the
             * the user's actual activity
             */
			int confidence = mostProbableActivity.getConfidence();
            /*
             * Get an integer describing the type of activity
             */
			int activityType = mostProbableActivity.getType();
			String activityName = getNameFromType(activityType);
			Log.e(TAG, "conf " + confidence + " activity = " + activityName);
	        /*
             * At this point, you have retrieved all the information
             * for the current update. You can display this
             * information to the user in a notification, or
             * send it to an Activity or Service in a broadcast
             * Intent.
             */
		} else {
            /*
             * This implementation ignores intents that don't contain
             * an activity update. If you wish, you can report them as
             * errors.
             */
		}
	}
}
