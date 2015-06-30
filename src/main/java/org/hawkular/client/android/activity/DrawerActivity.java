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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.Preferences;
import org.hawkular.client.android.util.ViewTransformer;
import org.jboss.aerogear.android.core.Callback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.Icicle;

public final class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    Callback<String> {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer)
    DrawerLayout drawer;

    @Bind(R.id.navigation)
    NavigationView navigation;

    @Bind(R.id.text_title)
    TextView navigationTitle;

    @Bind(R.id.image_title)
    ImageView navigationImage;

    @Bind(R.id.layout_accounts)
    View navigationAccounts;

    @Icicle
    @IdRes
    int currentNavigationId;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_drawer);

        setUpState(state);

        setUpBindings();

        setUpToolbar();
        setUpNavigation();

        setUpBackendClient();
    }

    private void setUpState(Bundle state) {
        Icepick.restoreInstanceState(this, state);
    }

    private void setUpBindings() {
        ButterKnife.bind(this);
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
        if (currentNavigationId == 0) {
            showNavigation(R.id.menu_resources);

            showResourcesFragment();
        } else {
            showNavigation(currentNavigationId);
        }

        setUpNavigationTitle();
    }

    private void showNavigation(@IdRes int navigationId) {
        navigation.getMenu().findItem(navigationId).setChecked(true);
    }

    private void setUpNavigationTitle() {
        navigationTitle.setText(Preferences.of(this).host().get());
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

        currentNavigationId = menuItem.getItemId();

        menuItem.setChecked(true);

        closeDrawer();

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

    private void closeDrawer() {
        drawer.closeDrawers();
    }

    @OnClick(R.id.layout_header)
    public void showNavigationAccounts() {
        if (navigationAccounts.getVisibility() == View.GONE) {
            ViewTransformer.of(navigationAccounts).expand();
        } else {
            ViewTransformer.of(navigationAccounts).collapse();
        }

        ViewTransformer.of(navigationImage).rotate();
    }

    @OnClick(R.id.button_deauthorize)
    public void tearDownAuthorization() {
        closeDrawer();

        showNavigationAccounts();

        Preferences.of(this).host().delete();
        Preferences.of(this).port().delete();
        Preferences.of(this).tenant().delete();
        Preferences.of(this).environment().delete();

        BackendClient.of(this).deauthorize();

        setUpBackendClient();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        tearDownState(state);
    }

    private void tearDownState(Bundle state) {
        Icepick.saveInstanceState(this, state);
    }
}
