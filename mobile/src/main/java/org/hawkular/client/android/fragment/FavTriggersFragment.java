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


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.TriggersAdapter;
import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

/**
 * Favourite Triggers fragment.
 * <p>
 * Displays available favourite triggers.
 */

public class FavTriggersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        TriggersAdapter.TriggerListener {

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

        setUpState(state);

        setUpBindings();

        setUpRefreshing();

        setUpTriggers();
    }

    private void setUpTriggers() {

        Context context = this.getActivity();
        SQLStore<Trigger> store = openStore(context);
        store.openSync();

        Collection<Trigger> array = store.readAll();
        triggers = new ArrayList<>(array);
        list.setAdapter(new TriggersAdapter(getActivity(), this, triggers));
        hideRefreshing();

        if(triggers.isEmpty()) {
            showMessage();
        } else {
            showList();
        }
    }


    private void hideRefreshing() {
        contentLayout.setRefreshing(false);
    }

    private void showList() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.content);
    }

    private void showMessage() { ViewDirector.of(this).using(R.id.animator).show(R.id.message);}

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

    @Override public void onRefresh() {
        setUpTriggers();
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
    }


    @Override public void onTriggerMenuClick(View triggerView, int triggerPosition) {
        showTriggerMenu(triggerView, triggerPosition);
    }

    @Override public void onTriggerTextClick(View triggerView, int triggerPosition) {
        // TODO : Add detail Trigger fragment
    }

    private void showTriggerMenu(final View triggerView, final int triggerPosition) {
        PopupMenu triggerMenu = new PopupMenu(getActivity(), triggerView);

        triggerMenu.getMenuInflater().inflate(R.menu.popup_delete, triggerMenu.getMenu());

        triggerMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Trigger trigger = getTriggersAdapter().getItem(triggerPosition);

                switch (menuItem.getItemId()) {
                    case R.id.menu_delete:
                        Context context = getActivity();
                        SQLStore<Trigger> store = openStore(context);
                        store.openSync();
                        store.remove(trigger.getId());
                        onRefresh();
                        return true;

                    default:
                        return false;
                }
            }
        });

        triggerMenu.show();
    }


    private TriggersAdapter getTriggersAdapter() {
        return (TriggersAdapter) list.getAdapter();
    }
}
