/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.client.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.ResourcesAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ResourceType;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class ResourcesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.list)
    ListView list;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_list);

        setUpBindings();

        setUpToolbar();
        setUpList();

        setUpResources();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpList() {
        list.setOnItemClickListener(this);
    }

    private void setUpResources() {
        showProgress();

        BackendClient.getInstance().getResources(getTenant(), getResourceType(), this, new ResourcesCallback());
    }

    private void showProgress() {
        ViewDirector.of(this, R.id.animator).show(R.id.progress);
    }

    private Tenant getTenant() {
        return getIntent().getParcelableExtra(Intents.Extras.TENANT);
    }

    private ResourceType getResourceType() {
        return getIntent().getParcelableExtra(Intents.Extras.RESOURCE_TYPE);
    }

    private void setUpResources(List<Resource> resources) {
        list.setAdapter(new ResourcesAdapter(this, resources));

        hideProgress();
    }

    private void hideProgress() {
        ViewDirector.of(this, R.id.animator).show(R.id.list);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Resource resource = getResourcesAdapter().getItem(position);

        startMetricTypesActivity(resource);
    }

    private ResourcesAdapter getResourcesAdapter() {
        return (ResourcesAdapter) list.getAdapter();
    }

    private void startMetricTypesActivity(Resource resource) {
        Intent intent = Intents.Builder.of(this).buildMetricsIntent(getTenant(), resource);
        startActivity(intent);
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

    private static final class ResourcesCallback extends AbstractActivityCallback<List<Resource>> {
        @Override
        public void onSuccess(List<Resource> resources) {
            Timber.d("Resources :: Success!");

            ResourcesActivity activity = (ResourcesActivity) getActivity();

            activity.setUpResources(resources);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Resources :: Failure...");
        }
    }
}
