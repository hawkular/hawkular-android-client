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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.Preferences;
import org.jboss.aerogear.android.core.Callback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public final class LauncherActivity extends AppCompatActivity implements Callback<String> {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_launcher);

        setUpBindings();

        setUpToolbar();

        setUpBackendClient();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setUpBackendClient() {
        String backendHost = Preferences.ofBackend(this).host().get();
        String backendPort = Preferences.ofBackend(this).port().get();

        if (backendHost.isEmpty() || backendPort.isEmpty()) {
            startAuthorizationActivity();
            return;
        }

        BackendClient.getInstance().setUpBackend(backendHost, backendPort);

        BackendClient.getInstance().authorize(this, this);
    }

    @Override
    public void onSuccess(String authorization) {
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

        if ((request == Intents.Requests.AUTHORIZATION) && (result != RESULT_OK)) {
            finish();
        }
    }

    @OnClick(R.id.button_tenants)
    public void setUpTenants() {
        Intent intent = Intents.Builder.of(this).buildResourceTypesIntent(getTenant());
        startActivity(intent);
    }

    private Tenant getTenant() {
        return new Tenant(Preferences.ofBackend(this).tenant().get());
    }

    @OnClick(R.id.button_alerts)
    public void setUpAlerts() {
        Intent intent = Intents.Builder.of(this).buildAlertsIntent();
        startActivity(intent);
    }
}
