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
package org.hawkular.client.android.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.ResourcesAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractFragmentCallback;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public final class ResourcesFragment extends Fragment implements AdapterView.OnItemClickListener {
    @InjectView(R.id.list)
    ListView list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        setUpBindings();

        setUpList();

        setUpResources();
    }

    private void setUpBindings() {
        ButterKnife.inject(this, getView());
    }

    private void setUpList() {
        list.setOnItemClickListener(this);
    }

    private void setUpResources() {
        showProgress();

        BackendClient.of(this).getResources(getTenant(), getEnvironment(), new ResourcesCallback());
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Tenant getTenant() {
        return getArguments().getParcelable(Fragments.Arguments.TENANT);
    }

    private Environment getEnvironment() {
        return getArguments().getParcelable(Fragments.Arguments.ENVIRONMENT);
    }

    private void setUpResources(List<Resource> resources) {
        sortResources(resources);

        list.setAdapter(new ResourcesAdapter(getActivity(), resources));

        hideProgress();
    }

    private void sortResources(List<Resource> resources) {
        Collections.sort(resources, new ResourcesComparator());
    }

    private void hideProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.list);
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
        Intent intent = Intents.Builder.of(getActivity()).buildMetricsIntent(getTenant(), getEnvironment(), resource);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        tearDownBindings();
    }

    private void tearDownBindings() {
         ButterKnife.reset(this);
    }

    private static final class ResourcesCallback extends AbstractFragmentCallback<List<Resource>> {
        @Override
        public void onSuccess(List<Resource> resources) {
            ResourcesFragment fragment = (ResourcesFragment) getFragment();

            fragment.setUpResources(resources);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Resources fetching failed.");
        }
    }

    private static final class ResourcesComparator implements Comparator<Resource> {
        @Override
        public int compare(Resource leftResource, Resource rightResource) {
            String leftResourceTypeId = leftResource.getType().getId();
            String rightResourceTypeId = rightResource.getType().getId();

            String leftResourceUrl = leftResource.getProperties().getUrl();
            String rightResourceUrl = rightResource.getProperties().getUrl();

            if (leftResourceTypeId.equals(rightResourceTypeId)) {
                return leftResourceUrl.compareTo(rightResourceUrl);
            } else {
                return leftResourceTypeId.compareTo(rightResourceTypeId);
            }
        }
    }
}
