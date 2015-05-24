package org.hawkular.client.android.activity;

import android.app.Activity;
import android.os.Bundle;

import org.hawkular.client.android.backend.HawkularClient;
import org.hawkular.client.android.backend.model.Tenant;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import timber.log.Timber;

public class LauncherActivity extends Activity
{
	private HawkularClient hawkularClient;

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);

		setUpClient();

		setUpTenants();
	}

	private void setUpClient() {
		hawkularClient = new HawkularClient();
		hawkularClient.setServerUrl("http://209.132.178.218:18090/");
	}

	private void setUpTenants() {
		LoaderPipe<Tenant> tenantsPipe = hawkularClient.getPipe(HawkularClient.Pipes.TENANTS, this);

		tenantsPipe.read(new TenantsCallback());
	}

	private static final class TenantsCallback extends AbstractActivityCallback<List<Tenant>>
	{
		@Override
		public void onSuccess(List<Tenant> tenants) {
			Timber.d("Success!");
		}

		@Override
		public void onFailure(Exception e) {
			Timber.d("Failure...");
		}
	}
}
