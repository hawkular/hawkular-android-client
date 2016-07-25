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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendEndpoints;
import org.hawkular.client.android.util.Android;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.Ports;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Authorization activity.
 * <p/>
 * Performs all related to authorization user operations, including accepting server host and port information
 * and triggering the OAuth flow.
 */
public final class AuthorizationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_host)
    EditText hostEdit;

    @BindView(R.id.edit_port)
    EditText portEdit;

    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);
        setContentView(R.layout.activity_authorization);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (Android.isDebugging()) {
            hostEdit.setText(BackendEndpoints.Demo.HOST);
            portEdit.setText(BackendEndpoints.Demo.PORT);
        }

    }

    @OnClick(R.id.button_authorize)
    public void setUpAuthorization() {
        String host = hostEdit.getText().toString().trim();
        String port = portEdit.getText().toString().trim();

        if (host.isEmpty()) {
            showError(hostEdit, R.string.error_empty);
            return;
        }

        if ((!port.isEmpty()) && (!Ports.isCorrect(Integer.valueOf(port)))) {
            showError(portEdit, R.string.error_authorization_port);
            return;
        }

        try {
            Intent intent = Intents.Builder.of(getApplicationContext())
                    .buildLoginIntent(host, port);
            startActivity(intent);
            finish();
        } catch (RuntimeException e) {
            Timber.d(e, "Authorization failed.");
        }

    }

    // TODO Should be moved to a util class and reused
    private void showError(EditText errorEdit, @StringRes int errorMessage) {
        errorEdit.setError(getString(errorMessage));
    }

}
