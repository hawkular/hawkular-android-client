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
import android.app.Fragment;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricData;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Ports;
import org.hawkular.client.android.util.Uris;
import org.hawkular.client.android.util.Urls;
import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BackendClient {
    private final Activity activity;
    private final Fragment fragment;

    @NonNull
    public static BackendClient of(@NonNull Activity activity) {
        return new BackendClient(activity, null);
    }

    @NonNull
    public static BackendClient of(@NonNull Fragment fragment) {
        return new BackendClient(null, fragment);
    }

    private BackendClient(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    public void configureBackend(@NonNull String host) {
        URL backendUrl = Urls.getUrl(host);

        configureAuthorization(backendUrl);
        configurePipes(backendUrl);
    }

    public void configureBackend(@NonNull String host, @IntRange(from = Ports.MINIMUM, to = Ports.MAXIMUM) int port) {
        URL backendUrl = Urls.getUrl(host, port);

        configureAuthorization(backendUrl);
        configurePipes(backendUrl);
    }

    private void configureAuthorization(URL backendUrl) {
        AuthorizationManager.config(BackendAuthorization.NAME, OAuth2AuthorizationConfiguration.class)
            .setBaseURL(Urls.getUrl(backendUrl, BackendAuthorization.Paths.BASE))
            .setRedirectURL(Urls.getUrl(backendUrl, BackendAuthorization.Paths.REDIRECT).toString())
            .setAccessTokenEndpoint(BackendAuthorization.Endpoints.ACCESS)
            .setAuthzEndpoint(BackendAuthorization.Endpoints.AUTHZ)
            .setRefreshEndpoint(BackendAuthorization.Endpoints.REFRESH)
            .setAccountId(BackendAuthorization.Ids.ACCOUNT)
            .setClientId(BackendAuthorization.Ids.CLIENT)
            .asModule();
    }

    private void configurePipes(URL backendUrl) {
        configurePipe(BackendPipes.Names.ALERTS, backendUrl, BackendPipes.Roots.ALERTS, Alert.class);
        configurePipe(BackendPipes.Names.TENANTS, backendUrl, BackendPipes.Roots.INVENTORY, Tenant.class);
        configurePipe(BackendPipes.Names.ENVIRONMENTS, backendUrl, BackendPipes.Roots.INVENTORY, Environment.class);
        configurePipe(BackendPipes.Names.RESOURCES, backendUrl, BackendPipes.Roots.INVENTORY, Resource.class);
        configurePipe(BackendPipes.Names.METRICS, backendUrl, BackendPipes.Roots.INVENTORY, Metric.class);
        configurePipe(BackendPipes.Names.METRIC_DATA, backendUrl, BackendPipes.Roots.METRICS, MetricData.class);
    }

    private <T> void configurePipe(String pipeName, URL pipeUrl, String pipePath, Class<T> pipeClass) {
        PipeManager.config(pipeName, RestfulPipeConfiguration.class)
            .module(getAuthorizationModule())
            .withUrl(Urls.getUrl(pipeUrl, pipePath))
            .forClass(pipeClass);
    }

    private AuthzModule getAuthorizationModule() {
        return AuthorizationManager.getModule(BackendAuthorization.NAME);
    }

    public void authorize(@NonNull Activity activity, @NonNull Callback<String> callback) {
        getAuthorizationModule().requestAccess(activity, callback);
    }

    public void deauthorize() {
        if (getAuthorizationModule().hasCredentials()) {
            getAuthorizationModule().deleteAccount();
        }
    }

    public void getAlerts(@NonNull Callback<List<Alert>> callback) {
        readPipe(BackendPipes.Names.ALERTS, null, callback);
    }

    public void getTenants(@NonNull Callback<List<Tenant>> callback) {
        URI uri = Uris.getUri(BackendPipes.Paths.TENANTS);

        readPipe(BackendPipes.Names.TENANTS, uri, callback);
    }

    public void getEnvironments(@NonNull Callback<List<Environment>> callback) {
        URI uri = Uris.getUri(BackendPipes.Paths.ENVIRONMENTS);

        readPipe(BackendPipes.Names.ENVIRONMENTS, uri, callback);
    }

    public void getResources(@NonNull Environment environment,
                             @NonNull Callback<List<Resource>> callback) {
        URI uri = Uris.getUri(String.format(BackendPipes.Paths.RESOURCES, environment.getId()));

        readPipe(BackendPipes.Names.RESOURCES, uri, callback);
    }

    public void getMetrics(@NonNull Environment environment, @NonNull Resource resource,
                           @NonNull Callback<List<Metric>> callback) {
        URI uri = Uris.getUri(
            String.format(BackendPipes.Paths.METRICS, environment.getId(), resource.getId()));

        readPipe(BackendPipes.Names.METRICS, uri, callback);
    }

    public void getMetricData(@NonNull Metric metric,
                              @NonNull Date startTime, @NonNull Date finishTime,
                              @NonNull Callback<List<MetricData>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendPipes.Parameters.START, String.valueOf(startTime.getTime()));
        parameters.put(BackendPipes.Parameters.FINISH, String.valueOf(finishTime.getTime()));

        URI uri = Uris.getUri(
            String.format(BackendPipes.Paths.METRIC_DATA, metric.getId()), parameters);

        readPipe(BackendPipes.Names.METRIC_DATA, uri, callback);
    }

    @SuppressWarnings("unchecked")
    private <T> void readPipe(String pipeName, URI uri, Callback<List<T>> callback) {
        getPipe(pipeName).read(getFilter(uri), callback);
    }

    private LoaderPipe getPipe(String pipeName) {
        if (activity != null) {
            return PipeManager.getPipe(pipeName, activity);
        } else {
            return PipeManager.getPipe(pipeName, fragment, fragment.getActivity().getApplicationContext());
        }
    }

    private ReadFilter getFilter(URI uri) {
        ReadFilter filter = new ReadFilter();

        filter.setLinkUri(uri);

        return filter;
    }
}
