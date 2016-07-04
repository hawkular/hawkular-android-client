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
package org.hawkular.client.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.PersonasAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Persona;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.event.Events;
import org.hawkular.client.android.event.ResourceSelectedEvent;
import org.hawkular.client.android.explorer.InventoryExplorerActivity;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.Ports;
import org.hawkular.client.android.util.Preferences;
import org.hawkular.client.android.util.ViewTransformer;
import org.hawkular.client.android.util.Views;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import com.squareup.otto.Subscribe;

import android.app.Activity;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

/**
 * Drawer activity
 *
 * The very first and main from the navigation standpoint screen.
 * Handles a {@link android.support.v4.widget.DrawerLayout}
 * and {@link android.support.design.widget.NavigationView}.
 * Manages personas and a current mode, i. e. Metrics and Alerts.
 * Contains {@link org.hawkular.client.android.fragment.ResourcesFragment}
 */
public final class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Callback<String> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer)
    DrawerLayout drawer;

    @BindView(R.id.navigation)
    NavigationView navigation;

    TextView host;

    TextView persona;

    ListView personas;

    ViewGroup personasLayout;

    ImageView personasActionIcon;

    @State
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
        View headerView = navigation.getHeaderView(0);
        host = ButterKnife.findById(headerView, R.id.text_host);
        persona = ButterKnife.findById(headerView, R.id.text_persona);
        personas = ButterKnife.findById(headerView, R.id.list_personas);
        personasLayout = ButterKnife.findById(headerView, R.id.layout_personas);
        personasActionIcon = ButterKnife.findById(headerView, R.id.image_personas_action);
        ButterKnife.bind(this);

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setUpNavigation() {
        navigation.setNavigationItemSelectedListener(this);
    }

    private void setUpBackendClient() {
        setUpBackendClient(getPersona());
    }

    private void setUpBackendClient(Persona persona) {
        String backendHost = getBackendHost();
        int backendPort = getBackendPort();

        if (backendHost.isEmpty() && !Ports.isCorrect(backendPort)) {
            startAuthorizationActivity();
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

    private String getBackendHost() {
        return Preferences.of(this).host().get();
    }

    private int getBackendPort() {
        return Preferences.of(this).port().get();
    }

    private Persona getPersona() {
        return new Persona(
            Preferences.of(this).personaId().get(),
            Preferences.of(this).personaName().get());
    }

    @Override
    public void onSuccess(String authorization) {
        setUpNavigationDefaults();
    }

    private void setUpNavigationDefaults() {
        if (currentNavigationId == 0) {
            showNavigation(R.id.menu_metrics);

            showResourcesFragment();
        } else {
            showNavigation(currentNavigationId);
        }

        setUpNavigationHeader();
    }

    private void showNavigation(@IdRes int navigationId) {
        navigation.getMenu().findItem(navigationId).setChecked(true);
    }

    private void showResourcesFragment() {
        Fragments.Operator.of(this).reset(R.id.layout_container, getResourcesFragment());
    }

    private Fragment getResourcesFragment() {
        return Fragments.Builder.buildResourcesFragment(getEnvironment());
    }

    private Environment getEnvironment() {
        return new Environment(Preferences.of(this).environment().get());
    }

    private void setUpNavigationHeader() {
        host.setText(getBackendHost());
        persona.setText(getPersona().getName());

        setUpPersonas();
    }

    private void setUpPersonas() {
        BackendClient.of(this).getPersonas(new PersonasCallback());
    }

    private void setUpPersonas(List<Persona> personasList) {
        personas.setAdapter(new PersonasAdapter(this, personasList));

        setUpPersonasList();
        setUpPersonasAction();
    }

    private void setUpPersonasList() {
        ViewGroup.LayoutParams personasParams = personas.getLayoutParams();
        personasParams.height = Views.measureHeight(personas);

        personas.setLayoutParams(personasParams);
        personas.requestLayout();
    }

    private void setUpPersonasAction() {
        if (arePersonasAvailable()) {
            showPersonasAction();
        } else {
            hidePersonasAction();
        }
    }

    private boolean arePersonasAvailable() {
        return (getPersonasAdapter() != null) && (getPersonasAdapter().getCount() > 1);
    }

    private PersonasAdapter getPersonasAdapter() {
        return (PersonasAdapter) personas.getAdapter();
    }

    private void showPersonasAction() {
        ViewTransformer.of(personasActionIcon).show();
    }

    private void hidePersonasAction() {
        ViewTransformer.of(personasActionIcon).hide();
    }

    // TODO : Failing caused by upgrade in butterknife and not needed at present.
    /*
    @Nullable
    @OnItemClick(R.id.list_personas)
    public void setUpPersona(int personaPosition) {
        Persona persona = getPersonasAdapter().getItem(personaPosition);

        setUpBackendClient(persona);

        hidePersonas();
    }
    */

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

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_settings:
                startSettingsActivity();
                break;

            case R.id.menu_feedback:
                startFeedbackActivity();
                break;

            case R.id.menu_tokens:
                startActivity(new Intent(getApplicationContext(), TokensActivity.class));
                break;

            case R.id.menu_explorer:
                startActivity(new Intent(getApplicationContext(), InventoryExplorerActivity.class));
                break;

            default:
                break;
        }

        currentNavigationId = menuItem.getItemId();

        menuItem.setChecked(true);

        closeDrawers();

        return true;
    }

    private void startSettingsActivity() {
        Intent intent = Intents.Builder.of(this).buildSettingsIntent();
        startActivityForResult(intent, Intents.Requests.DEAUTHORIZATION);
    }

    private void startFeedbackActivity() {
        Intent intent = Intents.Builder.of(this).buildFeedbackIntent();
        startActivity(intent);
    }

    private void closeDrawers() {
        drawer.closeDrawers();
    }

    // TODO : Failing caused by upgrade in butterknife and not needed at present.
    /*
    @OnClick(R.id.layout_header)
    public void triggerPersonas() {
        if (!arePersonasAvailable()) {
            return;
        }

        if (Views.isVisible(personasLayout)) {
            hidePersonas();
        } else {
            showPersonas();
        }
    }
    */

    private void showPersonas() {
        ViewTransformer.of(personasLayout).expand();

        ViewTransformer.of(personasActionIcon).rotate();
    }

    private void hidePersonas() {
        ViewTransformer.of(personasLayout).collapse();

        ViewTransformer.of(personasActionIcon).rotate();
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

    @Subscribe
    public void onResourceSelected(ResourceSelectedEvent event) {
        Resource resource = event.getResource();

        if (areMetricsCurrentNavigation()) {
            startMetricsActivity(resource);
        } else {
            startAlertsActivity(resource);
        }
    }

    private boolean areMetricsCurrentNavigation() {
        return navigation.getMenu().findItem(R.id.menu_metrics).isChecked();
    }

    private void startMetricsActivity(Resource resource) {
        Intent intent = Intents.Builder.of(this).buildMetricsIntent(getEnvironment(), resource);
        startActivity(intent);
    }

    private void startAlertsActivity(Resource resource) {
        Intent intent = Intents.Builder.of(this).buildAlertsIntent(resource);
        startActivity(intent);
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

        tearDownState(state);
    }

    private void tearDownState(Bundle state) {
        Icepick.saveInstanceState(this, state);
    }

    private static final class PersonasCallback extends AbstractActivityCallback<List<Persona>> {
        @Override
        public void onSuccess(List<Persona> personas) {
            getDrawerActivity().setUpPersonas(personas);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Personas fetching failed.");

            getDrawerActivity().setUpPersonas(new ArrayList<Persona>());
        }

        private DrawerActivity getDrawerActivity() {
            return (DrawerActivity) getActivity();
        }
    }
}
