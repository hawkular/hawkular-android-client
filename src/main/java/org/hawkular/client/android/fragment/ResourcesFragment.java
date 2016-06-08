/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.ResourcesAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.event.Events;
import org.hawkular.client.android.event.ResourceSelectedEvent;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractFragmentCallback;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

/**
 * Resources fragment.
 *
 * Displays resources as a list.
 */
public final class ResourcesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.list)
    ListView list;

    @BindView(R.id.content)
    SwipeRefreshLayout contentLayout;

    @State
    @Nullable
    ArrayList<Resource> resources;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        setUpState(state);

        setUpBindings();

        setUpRefreshing();

        setUpResources();
    }

    private void setUpState(Bundle state) {
        Icepick.restoreInstanceState(this, state);
    }

    private void setUpBindings() {
        ButterKnife.bind(this, getView());
    }

    private void setUpRefreshing() {
        contentLayout.setOnRefreshListener(this);
        contentLayout.setColorSchemeResources(ColorSchemer.getScheme());
    }

    @Override
    public void onRefresh() {
        setUpResourcesForced();
    }

    private void setUpResourcesForced() {
        BackendClient.of(this).getResources(getEnvironment(), new ResourcesCallback());
    }

    @OnClick(R.id.button_retry)
    public void setUpResources() {
        if (resources == null) {
            showProgress();

            setUpResourcesForced();
        } else {
            setUpResources(resources);
        }
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Environment getEnvironment() {
        return getArguments().getParcelable(Fragments.Arguments.ENVIRONMENT);
    }

    private void setUpResources(List<Resource> resources) {
        this.resources = new ArrayList<>(filterResources(resources));

        sortResources(this.resources);

        list.setAdapter(new ResourcesAdapter(getActivity(), this.resources));

        hideRefreshing();

        showList();
    }

    private List<Resource> filterResources(List<Resource> resources) {
        // TODO: think about better backend API.
        // Filter resources without properties set.
        // This is mostly a hack at this point because of not standardized properties.

        List<Resource> filteredResources = new ArrayList<>();

        for (Resource resource : resources) {
            if ((resource.getProperties() != null) && (resource.getProperties().getUrl() != null)) {
                filteredResources.add(resource);
            }
        }

        return filteredResources;
    }

    private void sortResources(List<Resource> resources) {
        Collections.sort(resources, new ResourcesComparator());
    }

    private void hideRefreshing() {
        contentLayout.setRefreshing(false);
    }

    private void showList() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.content);
    }

    private void showMessage() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.message);
    }

    private void showError() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.error);
    }

    @OnItemClick(R.id.list)
    public void setUpResource(int position) {
        Resource resource = getResourcesAdapter().getItem(position);

        Events.getBus().post(new ResourceSelectedEvent(resource));
    }

    private ResourcesAdapter getResourcesAdapter() {
        return (ResourcesAdapter) list.getAdapter();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        tearDownState(state);
    }

    private void tearDownState(Bundle state) {
        Icepick.saveInstanceState(this, state);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        tearDownBindings();
    }

    private void tearDownBindings() {
        //TODO: Modify it
        //ButterKnife.unbind(this);
    }

    private static final class ResourcesCallback extends AbstractFragmentCallback<List<Resource>> {
        @Override
        public void onSuccess(List<Resource> resources) {
            if (!resources.isEmpty()) {
                getResourcesFragment().setUpResources(resources);
            } else {
                getResourcesFragment().showMessage();
            }
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Resources fetching failed.");

            getResourcesFragment().showError();
        }

        private ResourcesFragment getResourcesFragment() {
            return (ResourcesFragment) getFragment();
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
