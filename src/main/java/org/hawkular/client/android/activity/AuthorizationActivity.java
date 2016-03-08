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

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendEndpoints;
import org.hawkular.client.android.util.Android;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.Ports;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Authorization activity.
 *
 * Performs all related to authorization user operations, including accepting server host and port information
 * and triggering the OAuth flow.
 */
public final class AuthorizationActivity extends AppCompatActivity {
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

        setUpLogin();
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

    private String getPort() {
        return portEdit.getText().toString().trim();
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

    private void showError(EditText errorEdit, @StringRes int errorMessage) {
        errorEdit.setError(getString(errorMessage));
    }

    private void setUpLogin() {
        try {
            Intent intent = Intents.Builder.of(getApplicationContext())
                    .buildLoginIntent(getHost(), getPort());
            startActivity(intent);
            finish();
        } catch (RuntimeException e) {
            Timber.d(e, "Authorization failed.");

        }
    }


}
