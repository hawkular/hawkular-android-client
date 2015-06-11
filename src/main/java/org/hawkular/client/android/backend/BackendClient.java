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
package org.hawkular.client.android.backend;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.hawkular.client.android.backend.model.ResourceType;
import org.hawkular.client.android.backend.model.Tenant;
import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.util.UrlUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@SuppressWarnings("unchecked")
public final class BackendClient {
    private static final class BackendClientHolder {
        public static final BackendClient BACKEND_CLIENT = new BackendClient();
    }

    private String serverUrl;

    public static BackendClient getInstance() {
        return BackendClientHolder.BACKEND_CLIENT;
    }

    private BackendClient() {
    }

    public void setServerUrl(@NonNull String serverUrl) {
        this.serverUrl = serverUrl;

        setUpAuthorization();

        setUpPipes();
    }

    private void setUpAuthorization() {
        AuthorizationManager.config(BackendAuthorization.NAME, OAuth2AuthorizationConfiguration.class)
            .setBaseURL(getServerUrl(BackendAuthorization.Paths.BASE))
            .setAccessTokenEndpoint(BackendAuthorization.Paths.ENDPOINT_ACCESS)
            .setAuthzEndpoint(BackendAuthorization.Paths.ENDPOINT_AUTHZ)
            .setRefreshEndpoint(BackendAuthorization.Paths.ENDPOINT_REFRESH)
            .setAccountId(BackendAuthorization.Ids.ACCOUNT).setClientId(BackendAuthorization.Ids.CLIENT)
            .setRedirectURL(getServerUrl(BackendAuthorization.Paths.REDIRECT).toString()).asModule();
    }

    private URL getServerUrl(String path) {
        return UrlUtils.appendToBaseURL(getServerUrl(), path);
    }

    private URL getServerUrl() {
        try {
            return new URL(serverUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpPipes() {
        setUpPipe(BackendPipes.Names.RESOURCE_TYPES, BackendPipes.Roots.INVENTORY, ResourceType.class);
        setUpPipe(BackendPipes.Names.TENANTS, BackendPipes.Roots.INVENTORY, Tenant.class);
    }

    private void setUpPipe(String pipeName, String pipePath, Class pipeClass) {
        PipeManager.config(pipeName, RestfulPipeConfiguration.class)
            .module(getAuthorizationModule())
            .withUrl(getServerUrl(pipePath)).forClass(pipeClass);
    }

    private AuthzModule getAuthorizationModule() {
        return AuthorizationManager.getModule(BackendAuthorization.NAME);
    }

    public boolean isAuthorized() {
        return getAuthorizationModule().isAuthorized();
    }

    public void authorize(@NonNull Activity activity, @NonNull Callback<String> authorizationCallback) {
        getAuthorizationModule().requestAccess(activity, authorizationCallback);
    }

    public void getTenants(@NonNull Activity activity, @NonNull Callback<List<Tenant>> callback) {
        PipeManager.getPipe(BackendPipes.Names.TENANTS, activity)
            .read(getFilter(BackendPipes.Paths.TENANTS), callback);
    }

    public void getResourceTypes(@NonNull Tenant tenant, @NonNull Activity activity,
                                 @NonNull Callback<List<ResourceType>> callback) {
        PipeManager.getPipe(BackendPipes.Names.RESOURCE_TYPES, activity)
            .read(getFilter(String.format(BackendPipes.Paths.RESOURCE_TYPES, tenant.getId())), callback);
    }

    private ReadFilter getFilter(String path) {
        ReadFilter filter = new ReadFilter();

        filter.setLinkUri(getPathUri(path));

        return filter;
    }

    private URI getPathUri(String path) {
        try {
            return new URI(null, null, path, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
