package com.anstar.fieldwork;

import java.util.ArrayList;

import android.content.Context;

import com.anstar.common.Utils;
import com.kl.kitlocate.class_interface.KLLocationValue;
import com.kl.kitlocate.interfaces.KLMotionDetection;

public class KitLocateCallBackClass {

	public static void gotPeriodicLocation(Context context,
			KLLocationValue lvLocation) {
		// Toast.makeText(UrHotSpotApplication.getContext(),
		// "kit locate periodic method", Toast.LENGTH_LONG).show();

		if (lvLocation != null) {
			Utils.sendLocationPeriodic(
					String.valueOf(lvLocation.getLatitude()),
					String.valueOf(lvLocation.getLongitude()));
		}
	}

	public static void geofenceIn(Context context, ArrayList alGeofences) {

	}

	public static void geofenceOut(Context context, ArrayList alGeofences) {

	}

	public static void gotSingleLocation(Context context,
			KLLocationValue lvLocation) {

	}

	public static void onMotionStateChanged(Context context,
			KLMotionDetection.MotionFeature motionFeature,
			KLMotionDetection.MotionState motionState) {

	}

	public static void onCallbackException(Context context, String methodName,
			Throwable t) {

	}

}
