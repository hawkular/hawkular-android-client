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
import org.hawkular.client.android.util.Preferences;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public final class AuthorizationActivity extends AppCompatActivity implements Callback<String> {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.edit_host)
    EditText hostEdit;

    @InjectView(R.id.edit_port)
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
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setUpDefaults() {
        if (Android.isDebugging()) {
            hostEdit.setText(BackendEndpoints.Community.HOST);
            portEdit.setText(BackendEndpoints.Community.PORT);
        }
    }

    @OnClick(R.id.button_authorize)
    public void setUpAuthorization() {
        if (getHost().isEmpty()) {
            showError(hostEdit, R.string.error_empty);
            return;
        }

        if (getPort().isEmpty()) {
            showError(portEdit, R.string.error_empty);
            return;
        }

        setUpBackendAuthorization();
    }

    private String getHost() {
        return hostEdit.getText().toString().trim();
    }

    private String getPort() {
        return portEdit.getText().toString().trim();
    }

    private void showError(EditText errorEdit, @StringRes int errorMessage) {
        errorEdit.setError(getString(errorMessage));
    }

    private void setUpBackendAuthorization() {
        try {
            BackendClient.getInstance().setUpBackend(getHost(), getPort());

            BackendClient.getInstance().authorize(this, this);
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
        BackendClient.getInstance().getTenants(this, new TenantsCallback());
    }

    private void setUpEnvironment(Tenant tenant) {
        BackendClient.getInstance().getEnvironments(tenant, this, new EnvironmentsCallback(tenant));
    }

    private void succeed(Tenant tenant, Environment environment) {
        saveBackendPreferences(tenant, environment);

        setResult(Activity.RESULT_OK);
        finish();
    }

    private void saveBackendPreferences(Tenant tenant, Environment environment) {
        Preferences.ofBackend(this).host().set(getHost());
        Preferences.ofBackend(this).port().set(getPort());
        Preferences.ofBackend(this).tenant().set(tenant.getId());
        Preferences.ofBackend(this).environment().set(environment.getId());
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
