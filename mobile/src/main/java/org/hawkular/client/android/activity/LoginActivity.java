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
import java.util.List;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.BackendEndpoints;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.util.Android;
import org.hawkular.client.android.util.Preferences;
import org.hawkular.client.android.util.Urls;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    private URL backendUrl;

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

            if (port.isEmpty()) {
                backendUrl = Urls.getUrl(host);
            } else {
                backendUrl = Urls.getUrl(host, Integer.valueOf(port));
            }

            BackendClient.of(this).configureAuthorization(backendUrl.toString(), user, pass);

            BackendClient.of(this).authorize(new AuthorizeCallback(this));

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

    private void succeed() {

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        Preferences.of(getApplicationContext()).authenticated().set(true);
        Preferences.of(getApplicationContext()).url().set(backendUrl.toString());
        Preferences.of(getApplicationContext()).username().set(username);
        Preferences.of(getApplicationContext()).password().set(password);

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


    private static final class AuthorizeCallback implements Callback<List<Metric>> {

        LoginActivity activity;

        public AuthorizeCallback(LoginActivity activity) {
            this.activity = activity;
        }

        @Override public void onResponse(Call<List<Metric>> call, Response<List<Metric>> response) {
            if (response.isSuccessful()){
                LoginActivity activity = getActivity();
                getActivity().succeed();
            }
        }

        @Override public void onFailure(Call<List<Metric>> call, Throwable t) {
            Timber.d(t, "Authorization failed.");

            LoginActivity activity = getActivity();

            if (activity.authIndicator.isShowing()) {
                activity.authIndicator.dismiss();
            }

        }

        public LoginActivity getActivity() {
            return activity;
        }
    }


}