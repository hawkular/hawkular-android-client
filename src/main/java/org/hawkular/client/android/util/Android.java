package org.hawkular.client.android.util;

import org.hawkular.client.android.BuildConfig;

public final class Android
{
	private Android() {
	}

	public static boolean isDebugging() {
		return BuildConfig.DEBUG;
	}
}
