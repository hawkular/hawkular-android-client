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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.AlertsAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractFragmentCallback;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public final class AlertsFragment extends Fragment implements AlertsAdapter.AlertMenuListener {
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

        setUpAlerts();
    }

    private void setUpBindings() {
        ButterKnife.inject(this, getView());
    }

    private void setUpList() {
        list.setSelector(android.R.color.transparent);
    }

    private void setUpAlerts() {
        showProgress();

        BackendClient.of(this).getAlerts(new AlertsCallback());
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private void setUpAlerts(List<Alert> alerts) {
        sortAlerts(alerts);

        list.setAdapter(new AlertsAdapter(getActivity(), this, alerts));

        hideProgress();
    }

    private void sortAlerts(List<Alert> alerts) {
        Collections.sort(alerts, new AlertsComparator());
    }

    @Override
    public void onAlertMenuClick(View alertView, int alertPosition) {
        showAlertMenu(alertView, alertPosition);
    }

    private void showAlertMenu(final View alertView, final  int alertPosition) {
        PopupMenu alertMenu = new PopupMenu(getActivity(), alertView);

        alertMenu.getMenuInflater().inflate(R.menu.menu_alerts, alertMenu.getMenu());

        alertMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_resolve:
                        Timber.d("Alert %d was not resolved intentionally.", alertPosition);
                        return true;

                    case R.id.menu_acknowledge:
                        Timber.d("Alert %d was not acknowledged intentionally.", alertPosition);
                        return true;

                    default:
                        return false;
                }
            }
        });

        alertMenu.show();
    }

    private void hideProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.list);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        tearDownBindings();
    }

    private void tearDownBindings() {
        ButterKnife.reset(this);
    }

    private static final class AlertsCallback extends AbstractFragmentCallback<List<Alert>> {
        @Override
        public void onSuccess(List<Alert> alerts) {
            AlertsFragment fragment = (AlertsFragment) getFragment();

            fragment.setUpAlerts(alerts);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Alerts fetching failed.");
        }
    }

    private static final class AlertsComparator implements Comparator<Alert> {
        @Override
        public int compare(Alert leftAlert, Alert rightAlert) {
            Date leftAlertTimestamp = new Date(leftAlert.getTimestamp());
            Date righAlerttTimestamp = new Date(rightAlert.getTimestamp());

            return leftAlertTimestamp.compareTo(righAlerttTimestamp);
        }
    }
}
