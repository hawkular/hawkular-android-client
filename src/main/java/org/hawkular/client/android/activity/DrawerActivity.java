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

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.Preferences;
import org.jboss.aerogear.android.core.Callback;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    Callback<String> {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.drawer)
    DrawerLayout drawer;

    @InjectView(R.id.navigation)
    NavigationView navigation;

    @InjectView(R.id.text_title)
    TextView navigationTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        setUpBindings();

        setUpToolbar();
        setUpNavigation();

        setUpBackendClient();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setUpNavigation() {
        navigation.setNavigationItemSelectedListener(this);
    }

    private void setUpBackendClient() {
        String backendHost = Preferences.of(this).host().get();
        int backendPort = Preferences.of(this).port().get();

        if (backendHost.isEmpty() || backendPort == Preferences.Defaults.BACKEND_PORT) {
            startAuthorizationActivity();
            return;
        }

        BackendClient.of(this).configureBackend(backendHost, backendPort);

        BackendClient.of(this).authorize(this, this);
    }

    @Override
    public void onSuccess(String authorization) {
        setUpNavigationDefaults();
    }

    private void setUpNavigationDefaults() {
        showNavigation(R.id.menu_resources);

        setUpNavigationTitle();
    }

    private void setUpNavigationTitle() {
        navigationTitle.setText(Preferences.of(this).host().get());
    }

    private void showNavigation(@IdRes int navigationId) {
        onNavigationItemSelected(navigation.getMenu().findItem(navigationId));
    }

    @Override
    public void onFailure(Exception e) {
        startAuthorizationActivity();
    }

    private void startAuthorizationActivity() {
        Intent intent = Intents.Builder.of(this).buildAuthorizationIntent();
        startActivityForResult(intent, Intents.Requests.AUTHORIZATION);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);

        if (request != Intents.Requests.AUTHORIZATION) {
            return;
        }

        if (result == RESULT_OK) {
            setUpNavigationDefaults();
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_resources:
                showResourcesFragment();
                break;

            case R.id.menu_alerts:
                showAlertsFragment();
                break;

            case R.id.menu_feedback:
                startFeedbackActivity();
                break;

            default:
                break;
        }

        drawer.closeDrawers();

        menuItem.setChecked(true);

        return true;
    }

    private void showResourcesFragment() {
        Fragment fragment = Fragments.Builder.buildResourcesFragment(getTenant(), getEnvironment());

        Fragments.Operator.of(this).reset(R.id.layout_container, fragment);
    }

    private Tenant getTenant() {
        return new Tenant(Preferences.of(this).tenant().get());
    }

    private Environment getEnvironment() {
        return new Environment(Preferences.of(this).environment().get());
    }

    private void showAlertsFragment() {
        Fragment fragment = Fragments.Builder.buildAlertsFragment();

        Fragments.Operator.of(this).reset(R.id.layout_container, fragment);
    }

    private void startFeedbackActivity() {
        Intent intent = Intents.Builder.of(this).buildFeedbackIntent();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(Gravity.LEFT);
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
