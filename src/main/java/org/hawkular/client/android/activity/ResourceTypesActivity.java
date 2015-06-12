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

public final class ResourceTypesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
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

        setUpResourceTypes();
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

    private void setUpResourceTypes() {
        showProgress();

        BackendClient.getInstance().getResourceTypes(getTenant(), this, new ResourceTypesCallback());
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Tenant getTenant() {
        return getIntent().getParcelableExtra(Intents.Extras.TENANT);
    }

    private void setUpResourceTypes(List<ResourceType> resourceTypes) {
        list.setAdapter(new ResourceTypesAdapter(this, resourceTypes));

        hideProgress();
    }

    private void hideProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.list);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ResourceType resourceType = getResourceTypesAdapter().getItem(position);

        startResourcesActivity(resourceType);
    }

    private ResourceTypesAdapter getResourceTypesAdapter() {
        return (ResourceTypesAdapter) list.getAdapter();
    }

    private void startResourcesActivity(ResourceType resourceType) {
        Intent intent = Intents.Builder.of(this).buildResourcesIntent(getTenant(), resourceType);
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

    private static final class ResourceTypesCallback extends AbstractActivityCallback<List<ResourceType>> {
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
