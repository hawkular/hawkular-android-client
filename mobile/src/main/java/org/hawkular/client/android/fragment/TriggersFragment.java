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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.hawkular.client.android.R;
import org.hawkular.client.android.activity.TriggerDetailActivity;
import org.hawkular.client.android.adapter.TriggersAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractSupportFragmentCallback;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

/**
 * Triggers fragment.
 *
 * Displays triggers as a list.
 */
public final class TriggersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        TriggersAdapter.TriggerListener {
    private boolean isTriggersFragmentAvailable;

    @BindView(R.id.list)
    ListView list;

    @BindView(R.id.content)
    SwipeRefreshLayout contentLayout;

    @State
    @Nullable
    ArrayList<Trigger> triggers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {

        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        isTriggersFragmentAvailable = true;

        setUpBindings();

        setUpRefreshing();

        setUpTriggers();
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
        setUpTriggersForced();
    }

    private void setUpTriggersForced() {

        if( getArguments().getString("state").equalsIgnoreCase("From Favourite")) {
            setUpFavTriggers();
        }
        else {
            BackendClient.of(this).getTriggers(new TriggersCallback());
        }
    }

    @OnClick(R.id.button_retry)
    public void setUpTriggers() {
        if (triggers == null) {
            showProgress();
            setUpTriggersForced();
        } else {
            setUpTriggers(triggers);
        }
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

    private void setUpTriggers(List<Trigger> triggers) {
        this.triggers = new ArrayList<>(triggers);

        sortTriggers(this.triggers);

        list.setAdapter(new TriggersAdapter(getActivity(), this, triggers));

        hideRefreshing();

        showList();
    }

    private void sortTriggers(List<Trigger> triggers) {
        Collections.sort(triggers, new TriggersComparator());
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
    public void setUpTrigger(int position) {
        Trigger trigger = getTriggersAdapter().getItem(position);
    }

    private void setUpFavTriggers() {

        Context context = this.getActivity();
        SQLStore<Trigger> store = openStore(context);
        store.openSync();

        Collection<Trigger> array = store.readAll();
        triggers = new ArrayList<>(array);
        sortTriggers(this.triggers);
        list.setAdapter(new TriggersAdapter(getActivity(), this, triggers));
        hideRefreshing();
        if(triggers.isEmpty()){
            showMessage();
        }
        else{
            showList();
        }

        store.close();
    }


    private SQLStore<Trigger> openStore(Context context) {
        DataManager.config("FavouriteTriggers", SQLStoreConfiguration.class)
                .withContext(context)
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public String generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store(Trigger.class);
        return (SQLStore<Trigger>) DataManager.getStore("FavouriteTriggers");
    }

    private TriggersAdapter getTriggersAdapter() {
        return (TriggersAdapter) list.getAdapter();
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

        isTriggersFragmentAvailable = false;
    }

    @Override public void onTriggerToggleChanged(View TriggerView, int triggerPosition, boolean state) {
        Trigger updatedTrigger = this.triggers.get(triggerPosition);
        updatedTrigger.setEnabledStatus(state);
        if (state){
            Snackbar snackbar = Snackbar.make(getView(),R.string.trigger_on, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        else {
            Snackbar snackbar = Snackbar.make(getView(),R.string.trigger_off, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        BackendClient.of(TriggersFragment.this).updateTrigger(updatedTrigger,new TriggerUpdateCallback());
    }

    @Override public void onTriggerTextClick(View triggerView, int triggerPosition) {
        Intent intent = new Intent(getActivity(), TriggerDetailActivity.class);
        Trigger trigger = getTriggersAdapter().getItem(triggerPosition);
        intent.putExtra(Intents.Extras.TRIGGER,trigger);
        startActivity(intent);
    }

    /*private void showTriggerMenu(final View triggerView, final int triggerPosition) {
        PopupMenu triggerMenu = new PopupMenu(getActivity(), triggerView);

        triggerMenu.getMenuInflater().inflate(R.menu.popup_add, triggerMenu.getMenu());

        triggerMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Trigger trigger = getTriggersAdapter().getItem(triggerPosition);

                switch (menuItem.getItemId()) {
                    case R.id.menu_add:
                        Context context = getActivity();
                        SQLStore<Trigger> store = openStore(context);
                        store.openSync();
                        store.save(trigger);
                        onRefresh();
                        return true;

                    default:
                        return false;
                }
            }
        });

        triggerMenu.show();
    }

    private SQLStore<Trigger> openStore(Context context) {
        DataManager.config("FavouriteTriggers", SQLStoreConfiguration.class)
                .withContext(context)
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public String generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store(Trigger.class);
        return (SQLStore<Trigger>) DataManager.getStore("FavouriteTriggers");
    }*/

    private static final class TriggersCallback extends AbstractSupportFragmentCallback<List<Trigger>> {
        @Override
        public void onSuccess(List<Trigger> triggers) {
            if (getTriggersFragment().isTriggersFragmentAvailable) {
                if (!triggers.isEmpty()) {
                    getTriggersFragment().setUpTriggers(triggers);
                } else {
                    getTriggersFragment().showMessage();
                }
            }
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Triggers fetching failed.");

            if (getTriggersFragment().isTriggersFragmentAvailable) {
                getTriggersFragment().showError();
            }
        }

        private TriggersFragment getTriggersFragment() {
            return (TriggersFragment) getSupportFragment();
        }
    }

    private class TriggerUpdateCallback extends AbstractSupportFragmentCallback{
        @Override public void onSuccess(Object data) {
        }

        @Override public void onFailure(Exception e) {
        }
    }

    private static final class TriggersComparator implements Comparator<Trigger> {
        @Override
        public int compare(Trigger leftTrigger, Trigger rightTrigger) {
            String leftTriggerDescription = leftTrigger.getId();
            String rightTriggerDescription = rightTrigger.getId();

            return leftTriggerDescription.compareTo(rightTriggerDescription);
        }
    }
}
