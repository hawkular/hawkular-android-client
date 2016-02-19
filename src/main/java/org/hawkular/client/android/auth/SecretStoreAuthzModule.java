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
package org.hawkular.client.android.auth;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthzSession;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.http.HttpProvider;
import org.jboss.aerogear.android.pipe.http.HttpRestProvider;
import org.jboss.aerogear.android.pipe.module.AuthorizationFields;
import org.jboss.aerogear.android.pipe.module.ModuleFields;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import timber.log.Timber;

/**
 * ModifiedAuthzModule.
 * <p/>
 * Performs all related work of generating new key and secret pair, Storing them as account,
 * Retrieving stored account, Deleting stored account.
 */

public class SecretStoreAuthzModule implements AuthzModule {
    private final Context context;
    private final SQLStore<OAuth2AuthzSession> sessionStore;

    public SecretStoreAuthzModule(Context context) {
        this.context = context.getApplicationContext();
        this.sessionStore = openSessionStore();
        sessionStore.openSync();
    }

    private SQLStore<OAuth2AuthzSession> openSessionStore() {
        DataManager.config(AuthData.STORE, SQLStoreConfiguration.class)
                .forClass(OAuth2AuthzSession.class)
                .withContext(context)
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public String generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store();
        return (SQLStore<OAuth2AuthzSession>) DataManager.getStore(AuthData.STORE);
    }

    @Override
    public boolean isAuthorized() {
        return true;
    }

    @Override
    public boolean hasCredentials() {
        return sessionStore.read(AuthData.NAME) != null;
    }

    public void requestAccess(final Callback<String> callback,
                              final String username, final String password, final URL url) {
        new Thread(new Runnable() {
            @Override public void run() {
                HeaderAndBody result;
                try {
                    HttpProvider provider =
                            new HttpRestProvider(new URL(url.toString() + AuthData.Endpoints.ACCESS));
                    provider.setDefaultHeader("Accept", "application/json");
                    provider.setDefaultHeader("Authorization", "Basic "
                            + buildLoginData(username, password));
                    result = provider.post("");
                    callback.onSuccess(new String(result.getBody()));
                } catch (MalformedURLException e) {
                    Timber.d(SecretStoreAuthzModule.class.getSimpleName(), "Error with URL", e);
                    callback.onFailure(e);
                } catch (HttpException e) {
                    Timber.d(SecretStoreAuthzModule.class.getSimpleName(), "HTTP error", e);
                    callback.onFailure(e);
                } catch (RuntimeException e) {
                    Timber.d(SecretStoreAuthzModule.class.getSimpleName(), "Connection Exception", e);
                    callback.onFailure(e);
                }
            }
        }).start();

    }


    @Override
    public void requestAccess(final Activity activity, final Callback<String> callback) {
        callback.onSuccess(sessionStore.read(AuthData.NAME).getAccessToken());
    }

    private String buildLoginData(String username, String password) {
        String cred = username + ":" + password;
        return new String(Base64.encode(cred.getBytes(), Base64.NO_WRAP));
    }

    @Override
    public boolean refreshAccess() {
        return true;
    }

    @Override
    public void deleteAccount() {
        sessionStore.remove(AuthData.NAME);
    }

    @Override
    public AuthorizationFields getAuthorizationFields(URI requestUri, String method, byte[] requestBody) {
        AuthorizationFields fields = new AuthorizationFields();
        OAuth2AuthzSession storedAccount = sessionStore.read(AuthData.NAME);
        String cred = new String(Base64.encode((storedAccount.getAccessToken()).getBytes(), Base64.NO_WRAP));
        fields.addHeader("Authorization", "Basic " + cred);
        return fields;
    }

    @Override
    public ModuleFields loadModule(URI relativeURI, String httpMethod, byte[] requestBody) {
        AuthorizationFields authzFields = getAuthorizationFields(relativeURI, httpMethod, requestBody);
        ModuleFields moduleFields = new ModuleFields();
        moduleFields.setHeaders(authzFields.getHeaders());
        moduleFields.setQueryParameters(authzFields.getQueryParameters());
        return moduleFields;
    }

    @Override
    public boolean handleError(HttpException exception) {
        return false;
    }

    public void addAccount(OAuth2AuthzSession session) {
        sessionStore.save(session);
    }
}