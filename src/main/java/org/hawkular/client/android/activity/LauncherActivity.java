package org.hawkular.client.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import org.hawkular.client.android.HawkularApplication;
import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Tenant;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class LauncherActivity extends Activity
{
	@Inject
	BackendClient backendClient;

	@InjectView(R.id.edit_server)
	EditText serverEdit;

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);

		setContentView(R.layout.activity_launcher);

		setUpInjections();
	}

	private void setUpInjections() {
		HawkularApplication.of(this).inject(this);

		ButterKnife.inject(this);
	}

	@OnClick(R.id.button_fetch_tenants)
	public void setUpContent() {
		setUpClient();

		setUpTenants();
	}

	private void setUpClient() {
		backendClient.setServerUrl(getServerUrl());
	}

	private String getServerUrl() {
		return serverEdit.getText().toString().trim();
	}

	private void setUpTenants() {
		LoaderPipe<Tenant> tenantsPipe = backendClient.getPipe(BackendClient.Pipes.TENANTS, this);

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
