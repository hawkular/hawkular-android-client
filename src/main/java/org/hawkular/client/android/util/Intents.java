package org.hawkular.client.android.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.hawkular.client.android.activity.ResourceTypesActivity;
import org.hawkular.client.android.backend.model.Tenant;

public class Intents
{
	private Intents() {
	}

	public static final class Extras
	{
		private Extras() {
		}

		public static final String TENANT = "tenant";
	}

	public static final class Builder
	{
		private final Context context;

		public static Builder of(@NonNull Context context) {
			return new Builder(context);
		}

		private Builder(Context context) {
			this.context = context;
		}

		public Intent buildResourceTypesIntent(@NonNull Tenant tenant) {
			Intent intent = new Intent(context, ResourceTypesActivity.class);
			intent.putExtra(Extras.TENANT, tenant);

			return intent;
		}
	}
}
