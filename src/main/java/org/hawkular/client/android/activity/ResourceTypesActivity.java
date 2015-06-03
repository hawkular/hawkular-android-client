package org.hawkular.client.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.ResourceTypesAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.ResourceType;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public final class ResourceTypesActivity extends AppCompatActivity
{
	@InjectView(R.id.toolbar)
	Toolbar toolbar;

	@InjectView(R.id.list)
	ListView list;

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_resource_types);

		setUpBindings();

		setUpToolbar();

		setUpResourceTypes();
	}

	private void setUpBindings() {
		ButterKnife.inject(this);
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setUpResourceTypes() {
		showProgress();

		BackendClient.getInstance().getResourceTypes(getTenant(), this, new ResourceTypesCallback());
	}

	private void showProgress() {
		ViewDirector.of(this, R.id.animator).show(R.id.progress);
	}

	private Tenant getTenant() {
		return getIntent().getParcelableExtra(Intents.Extras.TENANT);
	}

	private void setUpResourceTypes(List<ResourceType> resourceTypes) {
		list.setAdapter(new ResourceTypesAdapter(this, resourceTypes));

		hideProgress();
	}

	private void hideProgress() {
		ViewDirector.of(this, R.id.animator).show(R.id.list);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private static final class ResourceTypesCallback extends AbstractActivityCallback<List<ResourceType>>
	{
		@Override
		public void onSuccess(List<ResourceType> resourceTypes) {
			Timber.d("Resource type :: Success!");

			ResourceTypesActivity activity = (ResourceTypesActivity) getActivity();

			activity.setUpResourceTypes(resourceTypes);
		}

		@Override
		public void onFailure(Exception e) {
			Timber.d("Resource type :: Failure...");
		}
	}
}
