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
import java.util.Collection;
import java.util.UUID;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.FavMetricsAdapter;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
/**
 * Favourite Metrics fragment.
 * <p>
 * Displays available favourite metrics.
 */

public class FavMetricsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        FavMetricsAdapter.MetricListener, SearchView.OnQueryTextListener {

    @BindView(R.id.list) RecyclerView recyclerView;
    @BindView(R.id.content) SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<Metric> metrics;
    public SearchView searchView;
    public String searchText;
    public FavMetricsAdapter favMetricsAdapter;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        setUpBindings();
        setUpRefreshing();
        setUpMetrics();
        setUpState(state);
        setUpMenu();

    }

    private void setUpMetrics() {

        Context context = this.getActivity();
        SQLStore<Metric> store = openStore(context);
        store.openSync();

        Collection<Metric> array = store.readAll();
        metrics = new ArrayList<>(array);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        FavMetricsAdapter favMetricsAdapter = new FavMetricsAdapter(getActivity(), this, metrics);
        recyclerView.setAdapter(favMetricsAdapter);
        hideRefreshing();

        if(metrics.isEmpty()) {
            showMessage();
        } else {
            showList();
        }

        store.close();

    }

    private void setUpMenu() {
        setHasOptionsMenu(true);
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

    private SQLStore<Metric> openStore(Context context) {
        DataManager.config("FavouriteMetrics", SQLStoreConfiguration.class)
                .withContext(context)
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public String generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store(Metric.class);
        return (SQLStore<Metric>) DataManager.getStore("FavouriteMetrics");
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
        setUpMetrics();
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
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search1);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (metrics != null && metrics.size() != 0) {
            if (!TextUtils.isEmpty(query)) {
                ArrayList<Metric> filteredMetrics = new ArrayList<>();
                filteredMetrics.clear();
                for (int i=0;i<metrics.size();i++) {
                    String alertID = metrics.get(i).getName().toLowerCase();
                    if (alertID.contains(query.toLowerCase())) {
                        filteredMetrics.add(metrics.get(i));
                    }
                }
                favMetricsAdapter = new FavMetricsAdapter(getActivity(), this, filteredMetrics);
                recyclerView.setAdapter(favMetricsAdapter);
                searchText = query;
            } else {
                favMetricsAdapter = new FavMetricsAdapter(getActivity(), this, this.metrics);
                recyclerView.setAdapter(favMetricsAdapter);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    @Override
    public void onMetricMenuClick(View metricView, int metricPosition) {
        showMetricMenu(metricView, metricPosition);
    }

    @Override
    public void onMetricTextClick(View metricView, int metricPosition) {
        Intent intent = Intents.Builder.of(getActivity()).buildMetricIntent(metrics.get(metricPosition));
        startActivity(intent);
    }

    private void showMetricMenu(final View metricView, final int metricPosition) {
        PopupMenu metricMenu = new PopupMenu(getActivity(), metricView);

        metricMenu.getMenuInflater().inflate(R.menu.popup_delete, metricMenu.getMenu());

        metricMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Metric metric = getFavMetricsAdapter().getItem(metricPosition);

                switch (menuItem.getItemId()) {
                    case R.id.menu_delete:
                        Context context = getActivity();
                        SQLStore<Metric> store = openStore(context);
                        store.openSync();
                        store.remove(metric.getId());
                        onRefresh();
                        return true;

                    default:
                        return false;
                }
            }
        });

        metricMenu.show();
    }


    private FavMetricsAdapter getFavMetricsAdapter() {
        return (FavMetricsAdapter) recyclerView.getAdapter();
    }
}

