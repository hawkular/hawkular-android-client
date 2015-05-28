package org.hawkular.client.android;

import android.app.Application;
import android.os.StrictMode;

import org.hawkular.client.android.util.Android;

import timber.log.Timber;

public class HawkularApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		setUpLogging();
		setUpDetections();
	}

	private void setUpLogging() {
		if (Android.isDebugging()) {
			Timber.plant(new Timber.DebugTree());
		}
	}

	private void setUpDetections() {
		if (Android.isDebugging()) {
			StrictMode.enableDefaults();
		}
	}
}
