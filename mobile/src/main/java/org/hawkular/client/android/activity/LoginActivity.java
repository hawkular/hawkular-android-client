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
import org.hawkular.client.android.auth.AuthData;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Persona;
import org.hawkular.client.android.util.ErrorUtil;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.Preferences;
import org.hawkular.client.android.util.Urls;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.jboss.aerogear.android.pipe.http.HttpException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
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

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.username)
    EditText mUsername;

    @BindView(R.id.password)
    EditText mPassword;

    private String host;
    private String port;
    private ProgressDialog authIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle bundle = getIntent().getExtras();
        host = bundle.getString(Intents.Extras.HOST);
        port = bundle.getString(Intents.Extras.PORT);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    @OnClick(R.id.button_login)
    public void login() {

        try {

            URL backendUrl;
            if (port.isEmpty()) {
                backendUrl = Urls.getUrl(host.trim());
            } else {
                backendUrl = Urls.getUrl(host.trim(), Integer.valueOf(port));
            }

            String username = mUsername.getText().toString();
            String password = mPassword.getText().toString();

            BackendClient.of(this).configureAuthorization(getApplicationContext());
            BackendClient.of(this).deauthorize();

            Intent intent = this.getIntent();
            intent.putExtra(AuthData.Credentials.USERNAME, username);
            intent.putExtra(AuthData.Credentials.PASSWORD, password);
            intent.putExtra(AuthData.Credentials.URL, backendUrl.toString());
            intent.putExtra(AuthData.Credentials.CONTAIN, "true");

            BackendClient.of(this).authorize(this, new AuthorizeCallback());
            authIndicator=new ProgressDialog(this);
            authIndicator.setCancelable(false);
            authIndicator.setMessage("logging in...");
            authIndicator.show();

        } catch (RuntimeException e) {
            Timber.d(e, "Authorization failed.");
            ErrorUtil.showError(findViewById(android.R.id.content),R.string.error_authorization_host_port);
        }

    }

    private void setUpBackendCommunication(Persona persona) {
        if (port.isEmpty()) {
            BackendClient.of(this).configureCommunication(host.trim(), persona);
        } else {
            BackendClient.of(this).configureCommunication(host.trim(), Integer.valueOf(port), persona);
        }
    }

    private void succeed(Persona persona, Environment environment) {
        // Save Backend preferences
        Preferences.of(this).host().set(host.trim());
        if (!port.isEmpty()) Preferences.of(this).port().set(Integer.valueOf(port));
        Preferences.of(this).personaId().set(persona.getId());
        Preferences.of(this).environment().set(environment.getId());
        if (authIndicator.isShowing()){
            authIndicator.dismiss();
        }
        setResult(Activity.RESULT_OK);
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private static final class AuthorizeCallback extends AbstractActivityCallback<String> {
        @Override
        public void onSuccess(String authorization) {
            LoginActivity activity = (LoginActivity) getActivity();

            activity.setUpBackendCommunication(new Persona("hawkular"));
            BackendClient.of(getActivity()).getEnvironments(new EnvironmentsCallback(new Persona("hawkular")));
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Authorization failed.");

            LoginActivity activity = (LoginActivity) getActivity();
            if (activity.authIndicator.isShowing()){
                activity.authIndicator.setMessage("Error occurred");
                activity.authIndicator.dismiss();
            }
            if (e instanceof HttpException) {
                switch (((HttpException)e).getStatusCode()){
                    case 404:
                        ErrorUtil.showError(activity.findViewById(android.R.id.content),R.string.error_not_found);
                        break;
                    case 401:
                        ErrorUtil.showError(activity.findViewById(android.R.id.content),R.string.error_unauth);
                        break;
                    default:
                        ErrorUtil.showError(activity.findViewById(android.R.id.content),R.string.error_general);
                }
            } else if (e instanceof RuntimeException) {
                ErrorUtil.showError(activity.findViewById(android.R.id.content),R.string.error_internet_connection);
            } else {
                ErrorUtil.showError(activity.findViewById(android.R.id.content),R.string.error_general);
            }
        }

    }

    private static final class PersonasCallback extends AbstractActivityCallback<List<Persona>> {
        @Override
        public void onSuccess(List<Persona> personas) {
            if (personas.isEmpty()) {
                onFailure(new RuntimeException("Personas list is empty, this should not happen."));
                return;
            }

            // Unfortunately AeroGear does not support single item fetching.
            Persona persona = personas.get(0);
            LoginActivity activity = (LoginActivity) getActivity();

            activity.setUpBackendCommunication(persona);
            BackendClient.of(getActivity()).getEnvironments(new EnvironmentsCallback(persona));
        }

        @Override
        public void onFailure(Exception e) {
            LoginActivity activity = (LoginActivity) getActivity();
            if (activity.authIndicator.isShowing()){
                activity.authIndicator.setMessage("Error occurred");
                activity.authIndicator.dismiss();
            }
            Timber.d(e, "Personas fetching failed.");
        }
    }

    private static final class EnvironmentsCallback extends AbstractActivityCallback<List<Environment>> {
        private final Persona persona;

        public EnvironmentsCallback(@NonNull Persona persona) {
            this.persona = persona;
        }

        @Override
        public void onSuccess(List<Environment> environments) {
            if (environments.isEmpty()) {
                onFailure(new RuntimeException("Environments list is empty, this should not happen."));
                return;
            }

            // The first environment is picked and used everywhere, this should change in the future.
            Environment environment = environments.get(0);
            LoginActivity activity = (LoginActivity) getActivity();
            activity.succeed(persona, environment);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Environments fetching failed.");

        }
    }

}