/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
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
import org.hawkular.client.android.adapter.MetricsAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.ErrorUtil;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractSupportFragmentCallback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import timber.log.Timber;

/**
 * Metrics fragment.
 *
 * Displays metrics as a list.
 */

public class MetricsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.list) RecyclerView recyclerView;
    @BindView(R.id.content) SwipeRefreshLayout swipeRefreshLayout;

    public ArrayList<Metric> metrics;
    public MetricsAdapter metricsAdapter;

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
        setUpMetrics();
    }

    private void setUpState(Bundle state) {
        Icepick.restoreInstanceState(this, state);
    }

    private void setUpBindings() {
        ButterKnife.bind(this, getView());
    }

    private void setUpRefreshing() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(ColorSchemer.getScheme());
    }

    @Override
    public void onRefresh() {
        setUpMetricsForced();
    }

    private void setUpMetricsForced() {
        BackendClient.of(this).getMetrics(getEnvironment(), getResource(), new MetricsCallback());
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Environment getEnvironment() {
        return getArguments().getParcelable(Fragments.Arguments.ENVIRONMENT);
    }

    private Resource getResource() {
        return getArguments().getParcelable(Fragments.Arguments.RESOURCE);
    }

    private void setUpMetrics(List<Metric> metrics) {
        this.metrics = new ArrayList<>(metrics);

        sortMetrics(this.metrics);

        recyclerView.setAdapter(metricsAdapter);

        hideRefreshing();

        showList();
    }

    private void sortMetrics(List<Metric> metrics) {
        Collections.sort(metrics, new MetricsComparator());
    }

    private void hideRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showList() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.content);
    }

    private void showMessage() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.message);
    }

    private MetricsAdapter getMetricsAdapter() {
        return metricsAdapter;
    }

    private void startMetricActivity(Metric metric) {
        Intent intent = Intents.Builder.of(getActivity()).buildMetricIntent(metric);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        tearDownState(state);
    }

    @OnClick(R.id.button_retry)
    public void setUpMetrics() {
        if (metrics == null) {
            showProgress();
            setUpMetricsForced();
        } else {
            setUpMetrics(metrics);
        }
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

    private static final class MetricsCallback extends AbstractSupportFragmentCallback<List<Metric>> {

        @Override
        public void onSuccess(List<Metric> metrics) {
            if (!metrics.isEmpty()) {
                getMetricsFragment().setUpMetrics(metrics);
            } else {
                getMetricsFragment().showMessage();
            }
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Metrics fetching failed.");

            ErrorUtil.showError(getMetricsFragment(),R.id.animator,R.id.error);
        }

        private MetricsFragment getMetricsFragment() {
            return (MetricsFragment) getSupportFragment();
        }
    }

    private static final class MetricsComparator implements Comparator<Metric> {

        @Override
        public int compare(Metric leftMetric, Metric rightMetric) {
            String leftMetricDescription = leftMetric.getProperties().getDescription();
            String rightMetricDescription = rightMetric.getProperties().getDescription();

            return leftMetricDescription.compareTo(rightMetricDescription);
        }
    }
}
