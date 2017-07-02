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

import java.net.URL;

import org.hawkular.client.android.R;
import org.hawkular.client.android.auth.AuthData;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.BackendEndpoints;
import org.hawkular.client.android.backend.model.Persona;
import org.hawkular.client.android.util.Android;
import org.hawkular.client.android.util.Preferences;
import org.hawkular.client.android.util.Urls;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.jboss.aerogear.android.pipe.http.HttpException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Login activity.
 * <p/>
 * Provide with facility to login using username and password.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText mUsername;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.host)
    EditText mHost;

    @BindView(R.id.port)
    EditText mPort;

    private ProgressDialog authIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // -- Bind objects

        ButterKnife.bind(this);

        // -- Debug helper
        if (Android.isDebugging()) {
            mHost.setText(BackendEndpoints.Demo.HOST);
            mPort.setText(BackendEndpoints.Demo.PORT);
        }
    }

    @OnClick(R.id.button_login)
    public void login() {

        if(!validForm()) {
            return;
        }

        try {

            String host = mHost.getText().toString().trim();
            String port = mPort.getText().toString().trim();
            String user = mUsername.getText().toString().trim();
            String pass = mPassword.getText().toString().trim();

            URL backendUrl;
            if (port.isEmpty()) {
                backendUrl = Urls.getUrl(host);
            } else {
                backendUrl = Urls.getUrl(host, Integer.valueOf(port));
            }

            BackendClient.of(this).configureAuthorization(getApplicationContext());

            // Force logout before login
            BackendClient.of(this).deauthorize();

            // Authentication

            Intent intent = this.getIntent();
            intent.putExtra(AuthData.Credentials.USERNAME, user);
            intent.putExtra(AuthData.Credentials.PASSWORD, pass);
            intent.putExtra(AuthData.Credentials.URL, backendUrl.toString());
            intent.putExtra(AuthData.Credentials.CONTAIN, "true");

            BackendClient.of(this).authorize(this, new AuthorizeCallback());

            // Progress

            authIndicator = new ProgressDialog(this);
            authIndicator.setCancelable(false);
            authIndicator.setMessage(getString(R.string.logging));
            authIndicator.show();

        } catch (RuntimeException e) {
            Timber.d(e, "Authorization failed.");
            showError(R.string.error_authorization_host_port);
        }

    }

    private boolean validForm() {

        boolean check = true;

        TextView[] fields = new TextView[] {mHost, mUsername, mPassword};

        for (TextView field : fields) {
            if(field.getText().toString().trim().isEmpty()) {
                field.setError(getString(R.string.cannot_be_blank));
                check = false;
            }
        }

        return check;

    }

    private void setUpBackendCommunication(Persona persona) {
        String host = mHost.getText().toString().trim();
        String port = mPort.getText().toString().trim();

        if (port.isEmpty()) {
            BackendClient.of(this).configureCommunication(host.trim(), persona);
        } else {
            BackendClient.of(this).configureCommunication(host.trim(), Integer.valueOf(port), persona);
        }
    }

    private void succeed() {
        String host = mHost.getText().toString().trim();
        String port = mPort.getText().toString().trim();

        // Save backend preferences
        Preferences.of(this).host().set(host.trim());
        if (!port.isEmpty()) Preferences.of(this).port().set(Integer.valueOf(port));

        if (authIndicator.isShowing()) {
            authIndicator.dismiss();
        }

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void showError(@StringRes int errorMessage) {
        Snackbar
                .make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG)
                .show();
    }


    private static final class AuthorizeCallback extends AbstractActivityCallback<String> {

        @Override
        public void onSuccess(String authorization) {
            LoginActivity activity = (LoginActivity) getActivity();

            activity.setUpBackendCommunication(new Persona("hawkular"));
            ((LoginActivity) getActivity()).succeed();
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Authorization failed.");

            LoginActivity activity = (LoginActivity) getActivity();

            if (activity.authIndicator.isShowing()) {
                activity.authIndicator.dismiss();
            }

            if (e instanceof HttpException) {
                switch (((HttpException) e).getStatusCode()) {
                    case 404:
                        activity.showError(R.string.error_not_found);
                        break;
                    case 401:
                        activity.showError(R.string.error_unauth);
                        break;
                    default:
                        activity.showError(R.string.error_general);
                }
            } else if (e instanceof RuntimeException) {
                activity.showError(R.string.error_internet_connection);
            } else {
                activity.showError(R.string.error_general);
            }
        }

    }


}