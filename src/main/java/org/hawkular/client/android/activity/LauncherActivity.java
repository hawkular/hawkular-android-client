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
import android.widget.EditText;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.BackendEndpoints;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Intents;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public final class LauncherActivity extends AppCompatActivity implements Callback<String> {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.edit_server)
    EditText serverEdit;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_launcher);

        setUpBindings();

        setUpToolbar();

        setUpServerUrl();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setUpServerUrl() {
        serverEdit.setText(BackendEndpoints.COMMUNITY);
    }

    @OnClick(R.id.button_proceed)
    public void setUpContent() {
        setUpClient();

        setUpAuthorization();
    }

    private void setUpClient() {
        BackendClient.getInstance().setServerUrl(getServerUrl());
    }

    private String getServerUrl() {
        return serverEdit.getText().toString().trim();
    }

    private void setUpAuthorization() {
        if (!BackendClient.getInstance().isAuthorized()) {
            BackendClient.getInstance().authorize(this, this);
        } else {
            setUpTenants();
        }
    }

    @Override
    public void onSuccess(String authorizationResult) {
        Timber.d("Authorization :: Success!");

        setUpTenants();
    }

    @Override
    public void onFailure(Exception authenticationException) {
        Timber.d(authenticationException, "Authorization :: Failure...");
    }

    private void setUpTenants() {
        BackendClient.getInstance().getTenants(this, new TenantsCallback());
    }

    private void startResourceTypesActivity(Tenant tenant) {
        Intent intent = Intents.Builder.of(this).buildResourceTypesIntent(tenant);
        startActivity(intent);
    }

    private static final class TenantsCallback extends AbstractActivityCallback<List<Tenant>> {
        @Override
        public void onSuccess(List<Tenant> tenants) {
            Timber.d("Tenants :: Success!");

            LauncherActivity activity = (LauncherActivity) getActivity();

            activity.startResourceTypesActivity(tenants.get(0));
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Tenants :: Failure...");
        }
    }
}
