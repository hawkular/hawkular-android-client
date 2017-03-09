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
package org.hawkular.client.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.ViewPagerAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Persona;
import org.hawkular.client.android.event.Events;
import org.hawkular.client.android.explorer.InventoryExplorerActivity;
import org.hawkular.client.android.fragment.FavMetricsFragment;
import org.hawkular.client.android.fragment.FavTriggersFragment;
import org.hawkular.client.android.fragment.TriggersFragment;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.Ports;
import org.hawkular.client.android.util.Preferences;
import org.jboss.aerogear.android.core.Callback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

/**
 * Main activity
 * <p>
 * The very first and main from the navigation standpoint screen.
 * Handles a {@link android.support.v4.widget.DrawerLayout}
 * and {@link android.support.design.widget.NavigationView}.
 * Manages personas and a current mode, i. e. Metrics and Alerts.}
 */
public final class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Callback<String> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer)
    DrawerLayout drawer;

    @BindView(R.id.navigation)
    NavigationView navigation;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    TextView host;

    TextView persona;

    ListView personas;

    ViewGroup personasLayout;

    ImageView personasActionIcon;

    ViewPagerAdapter adapter;

    @State
    @IdRes
    int currentNavigationId;

    // -- Android Life Cycle ----------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        // -- Bind objects

        ButterKnife.bind(this);

        View headerView = navigation.getHeaderView(0);
        host = ButterKnife.findById(headerView, R.id.text_host);
        persona = ButterKnife.findById(headerView, R.id.text_persona);
        personas = ButterKnife.findById(headerView, R.id.list_personas);
        personasLayout = ButterKnife.findById(headerView, R.id.layout_personas);
        personasActionIcon = ButterKnife.findById(headerView, R.id.image_personas_action);

        // -- Setup State
        Icepick.restoreInstanceState(this, state);

        // -- Toolbar

        setSupportActionBar(toolbar);

        // -- Navigation
        navigation.setNavigationItemSelectedListener(this);

        // -- ViewPager && TabLayout

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        setUpBackendClient();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Events.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Events.getBus().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        Icepick.saveInstanceState(this, state);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);

        if (request == Intents.Requests.AUTHORIZATION) {
            if (result == Activity.RESULT_OK) {
                setUpBackendClient();
            } else {
                finish();
            }
        }

        if (request == Intents.Requests.DEAUTHORIZATION) {
            if (result == Activity.RESULT_OK) {
                setUpBackendClient();
            }
        }
    }

    // -- NavigationView.OnNavigationItemSelectedListener -----------------------------------------

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menu_favourites:
                showFavourites();
                menuItem.setChecked(true);
                break;

            case R.id.menu_alerts:
                showAlerts();
                menuItem.setChecked(true);
                break;

            case R.id.menu_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(settingsIntent, Intents.Requests.DEAUTHORIZATION);
                break;

            case R.id.menu_feedback:
                String feedbackAddress = getString(R.string.feedback_address);
                String feedbackSubject = getString(R.string.feedback_subject);

                String feedbackUri = String.format("mailto:%s?subject=%s",
                        feedbackAddress, feedbackSubject);

                Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(feedbackUri));
                startActivity(feedbackIntent);
                break;

            case R.id.menu_explorer:
                startActivity(new Intent(getApplicationContext(), InventoryExplorerActivity.class));
                break;

            default:
                break;
        }

        currentNavigationId = menuItem.getItemId();

        drawer.closeDrawers();

        return true;

    }

    // -- Callback --------------------------------------------------------------------------------

    @Override
    public void onSuccess(String authorization) {
        if (currentNavigationId == 0) {
            showNavigation(R.id.menu_favourites);

            showFavourites();
        } else {
            showNavigation(currentNavigationId);
        }

        // -- Setup Navigation Header
        host.setText(Preferences.of(this).host().get());
        persona.setText(getPersona().getId());
    }

    @Override
    public void onFailure(Exception e) {
        Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
        startActivityForResult(intent, Intents.Requests.AUTHORIZATION);
    }

    // -- Gets ------------------------------------------------------------------------------------

    private Persona getPersona() {
        return new Persona(
                Preferences.of(this).personaId().get());
    }

    // -- Setup -----------------------------------------------------------------------------------

    private void setUpBackendClient() {
        setUpBackendClient(getPersona());
    }

    private void setUpBackendClient(Persona persona) {
        String backendHost = Preferences.of(this).host().get();
        int backendPort = Preferences.of(this).port().get();

        if (backendHost.isEmpty() && !Ports.isCorrect(backendPort)) {
            Intent intent = new Intent(getApplicationContext(), AuthorizationActivity.class);
            startActivityForResult(intent, Intents.Requests.AUTHORIZATION);
            return;
        }

        if (!Ports.isCorrect(backendPort)) {
            BackendClient.of(this).configureAuthorization(getApplicationContext());
            BackendClient.of(this).configureCommunication(backendHost, persona);
        } else {
            BackendClient.of(this).configureAuthorization(getApplicationContext());
            BackendClient.of(this).configureCommunication(backendHost, backendPort, persona);
        }

        BackendClient.of(this).authorize(this, this);
    }

    // -- Navigation ------------------------------------------------------------------------------

    @OnClick(R.id.drawer_menu_icon)
    void openDrawerMenu() {
        drawer.openDrawer(GravityCompat.START);
    }

    private void showNavigation(@IdRes int navigationId) {
        navigation.getMenu().findItem(navigationId).setChecked(true);
    }

    private void showFavourites() {
        getSupportActionBar().setTitle(R.string.title_favourites);
        adapter.reset();
        adapter.addFragment(new FavMetricsFragment(), "Metrics");
        adapter.addFragment(new FavTriggersFragment(), "Triggers");
        adapter.notifyDataSetChanged();

    }

    private void showAlerts() {
        getSupportActionBar().setTitle(R.string.title_alerts);
        adapter.reset();
        adapter.addFragment(Fragments.Builder.buildAlertsFragment(null), "Alerts");
        adapter.addFragment(new TriggersFragment(), "Triggers");
        adapter.notifyDataSetChanged();
    }

}
