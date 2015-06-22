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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.AlertsAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public final class AlertsActivity extends AppCompatActivity implements AlertsAdapter.AlertMenuListener {
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

        setUpAlerts();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        list.setAdapter(new AlertsAdapter(this, this, alerts));

        hideProgress();
    }

    @Override
    public void onAlertMenuClick(View alertView, int alertPosition) {
        showAlertMenu(alertView, alertPosition);
    }

    private void showAlertMenu(final View alertView, final  int alertPosition) {
        PopupMenu alertMenu = new PopupMenu(this, alertView);

        alertMenu.getMenuInflater().inflate(R.menu.menu_alerts, alertMenu.getMenu());

        alertMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_resolve:
                        Timber.d("Alert %d was not deleted intentionally.", alertPosition);
                        return true;

                    case R.id.menu_acknowledge:
                        Timber.d("Alert %d was not removed intentionally.", alertPosition);
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private static final class AlertsCallback extends AbstractActivityCallback<List<Alert>> {
        @Override
        public void onSuccess(List<Alert> alerts) {
            AlertsActivity activity = (AlertsActivity) getActivity();

            activity.setUpAlerts(alerts);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Alerts fetching failed.");
        }
    }
}
