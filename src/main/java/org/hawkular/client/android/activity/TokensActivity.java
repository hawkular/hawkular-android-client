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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.hawkular.client.android.R;
import org.hawkular.client.android.auth.AuthData;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Token;
import org.hawkular.client.android.fragment.TokensFragment;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Ports;
import org.hawkular.client.android.util.Preferences;
import org.hawkular.client.android.util.Urls;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.http.HttpProvider;
import org.jboss.aerogear.android.pipe.http.HttpRestProvider;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TokensActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.action_button)
    FloatingActionButton actionButton;

    Fragment tokenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);

        setUpBindings();

        setUpToolbar();

        setUpTokens();

        actionButton.setVisibility(View.VISIBLE);
        actionButton.setImageResource(R.drawable.ic_plus_white);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(TokensActivity.this);
                integrator.initiateScan();
            }
        });
    }

    private void setUpBindings() {
        ButterKnife.bind(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpTokens() {
        tokenFragment = getTokensFragment();
        Fragments.Operator.of(this).set(R.id.layout_container, tokenFragment);
    }

    public Fragment getTokensFragment() {
        return Fragments.Builder.buildTokensFragment();
    }

    private String getHost() {
        String host = Preferences.of(getApplicationContext()).host().get();
        return host.trim();
    }

    private int getPortNumber() {
        return Preferences.of(getApplicationContext()).port().get();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent response) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, response);
        if (scanResult != null && scanResult.getContents() != null) {
            BackendClient.of(this).configureAuthorization(this.getApplicationContext());
            String authorization = scanResult.getContents();
            String[] authArray = authorization.split(",");
            URL backendUrl;
            String key;
            String secret;
            if (authArray.length >= 2) {
                try {
                    if (!Ports.isCorrect(getPortNumber())) {
                        backendUrl = Urls.getUrl(getHost());
                    } else {
                        backendUrl = Urls.getUrl(getHost(), getPortNumber());
                    }
                    key = authArray[0];
                    secret = authArray[1];
                    doCheck(this, key, secret, backendUrl);
                } catch (RuntimeException e) {
                    Timber.d(e, "Authorization failed.");
                }
            }
        }
    }

    private void doCheck(final Activity activity, final String key, final String secret, final URL url) {

        AsyncTask<Void, Void, HeaderAndBody> task = new AsyncTask<Void, Void, HeaderAndBody>() {
            private Exception exception;
            private String errorMessage;

            @Override
            protected HeaderAndBody doInBackground(Void... params) {
                HeaderAndBody result = null;
                try {
                    HttpProvider provider =
                            new HttpRestProvider(new URL(url.toString() + AuthData.Endpoints.PERSONA));
                    provider.setDefaultHeader("Accept", "application/json");
                    provider.setDefaultHeader("Authorization", "Basic "
                            + buildLoginData(key, secret));
                    result = provider.get();
                } catch (MalformedURLException e) {
                    Timber.d(TokensActivity.class.getSimpleName(), "Error with URL", e);
                    exception = e;
                    errorMessage = "Please check your host and port";
                } catch (HttpException e) {
                    Timber.d(TokensActivity.class.getSimpleName(), "HTTP error", e);
                    exception = e;
                    errorMessage = new String(e.getData());
                } catch (RuntimeException e) {
                    Timber.d(TokensActivity.class.getSimpleName(), "Connection Exception", e);
                    exception = e;
                    errorMessage = "Please check your Internet connection";
                }
                return result;
            }

            @Override
            protected void onPostExecute(HeaderAndBody headerAndBody) {
                JSONObject persona = null;
                if (exception == null) {
                    String body = new String(headerAndBody.getBody());
                    try {
                        persona = new JSONObject(body);
                        Context context = activity;
                        SQLStore<Token> store = openStore(context);
                        store.openSync();
                        Token token = new Token(persona.getString("name"), persona.getString("id"), key, secret);
                        store.save(token);
                        ((TokensFragment) tokenFragment).onRefresh();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private SQLStore<Token> openStore(Context context) {
        DataManager.config("Store", SQLStoreConfiguration.class)
                .forClass(Token.class)
                .withContext(context)
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public String generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store();
        return (SQLStore<Token>) DataManager.getStore("Store");
    }

    private String buildLoginData(String key, String secret) {
        String cred = key + ":" + secret;
        return new String(Base64.encode(cred.getBytes(), Base64.NO_WRAP));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}