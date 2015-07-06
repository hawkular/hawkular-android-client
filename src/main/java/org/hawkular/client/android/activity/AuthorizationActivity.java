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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.BackendEndpoints;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Android;
import org.hawkular.client.android.util.Ports;
import org.hawkular.client.android.util.Preferences;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public final class AuthorizationActivity extends AppCompatActivity implements Callback<String> {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.edit_host)
    EditText hostEdit;

    @Bind(R.id.edit_port)
    EditText portEdit;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_authorization);

        setUpBindings();

        setUpToolbar();

        setUpDefaults();
    }

    private void setUpBindings() {
        ButterKnife.bind(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setUpDefaults() {
        if (Android.isDebugging()) {
            hostEdit.setText(BackendEndpoints.Demo.HOST);
            portEdit.setText(BackendEndpoints.Demo.PORT);
        }
    }

    @OnClick(R.id.button_authorize)
    public void setUpAuthorization() {
        if (!isHostAvailable()) {
            showError(hostEdit, R.string.error_empty);
            return;
        }

        if (isPortAvailable() && !isPortCorrect()) {
            showError(portEdit, R.string.error_authorization_port);
            return;
        }

        setUpBackendAuthorization();
    }

    private boolean isHostAvailable() {
        return !getHost().isEmpty();
    }

    private String getHost() {
        return hostEdit.getText().toString().trim();
    }

    private boolean isPortAvailable() {
        return !getPort().isEmpty();
    }

    private boolean isPortCorrect() {
        try {
            return Ports.isCorrect(getPortNumber());
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int getPortNumber() {
        return Integer.valueOf(getPort());
    }

    private String getPort() {
        return portEdit.getText().toString().trim();
    }

    private void showError(EditText errorEdit, @StringRes int errorMessage) {
        errorEdit.setError(getString(errorMessage));
    }

    private void setUpBackendAuthorization() {
        try {
            if (!isPortAvailable()) {
                BackendClient.of(this).configureBackend(getHost());
            } else {
                BackendClient.of(this).configureBackend(getHost(), Integer.valueOf(getPort()));
            }

            BackendClient.of(this).deauthorize();
            BackendClient.of(this).authorize(this, this);
        } catch (RuntimeException e) {
            Timber.d(e, "Authorization failed.");

            showError(R.string.error_authorization_host_port);
        }
    }

    private void showError(@StringRes int errorMessage) {
        Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(String authorization) {
        setUpTenant();
    }

    @Override
    public void onFailure(Exception e) {
        Timber.d(e, "Authorization failed.");

        showError(R.string.error_general);
    }

    private void setUpTenant() {
        BackendClient.of(this).getTenants(new TenantsCallback());
    }

    private void setUpEnvironment(Tenant tenant) {
        BackendClient.of(this).getEnvironments(new EnvironmentsCallback(tenant));
    }

    private void succeed(Tenant tenant, Environment environment) {
        saveBackendPreferences(tenant, environment);

        setResult(Activity.RESULT_OK);
        finish();
    }

    private void saveBackendPreferences(Tenant tenant, Environment environment) {
        Preferences.of(this).host().set(getHost());
        if (isPortAvailable()) {
            Preferences.of(this).port().set(getPortNumber());
        }

        Preferences.of(this).tenant().set(tenant.getId());
        Preferences.of(this).environment().set(environment.getId());
    }

    private static final class TenantsCallback extends AbstractActivityCallback<List<Tenant>> {
        @Override
        public void onSuccess(List<Tenant> tenants) {
            if (tenants.isEmpty()) {
                Timber.d("Tenants list is empty, this should not happen.");
                return;
            }

            // This is a potentially dangerous action.
            // AeroGear does not support single item fetching.
            Tenant tenant = tenants.get(0);

            AuthorizationActivity activity = (AuthorizationActivity) getActivity();

            activity.setUpEnvironment(tenant);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Tenants fetching failed.");
        }
    }

    private static final class EnvironmentsCallback extends AbstractActivityCallback<List<Environment>> {
        private final Tenant tenant;

        public EnvironmentsCallback(@NonNull Tenant tenant) {
            this.tenant = tenant;
        }

        @Override
        public void onSuccess(List<Environment> environments) {
            if (environments.isEmpty()) {
                Timber.d("Environments list is empty, this should not happen.");
                return;
            }

            // This is a potentially dangerous action.
            // The first environment is picked and used as main, this should change in the future.
            Environment environment = environments.get(0);

            AuthorizationActivity activity = (AuthorizationActivity) getActivity();

            activity.succeed(tenant, environment);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Environments fetching failed.");
        }
    }
}
