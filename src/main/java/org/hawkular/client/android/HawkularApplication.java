package org.hawkular.client.android;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import org.hawkular.client.android.util.Android;

import dagger.ObjectGraph;
import timber.log.Timber;

public class HawkularApplication extends Application
{
	private ObjectGraph injector;

	public static HawkularApplication of(@NonNull Context context) {
		return (HawkularApplication) context.getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		setUpLogging();
		setUpDetections();

		setUpInjections();
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

	private void setUpInjections() {
		injector = ObjectGraph.create(new HawkularModule());
	}

	public void inject(Object injectionsConsumer) {
		injector.inject(injectionsConsumer);
	}
}
