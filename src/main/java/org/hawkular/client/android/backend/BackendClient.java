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

import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricData;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ResourceType;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Uris;
import org.hawkular.client.android.util.Urls;
import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;

import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class BackendClient {
    private static final class BackendClientHolder {
        public static final BackendClient BACKEND_CLIENT = new BackendClient();
    }

    private URL serverUrl;

    public static BackendClient getInstance() {
        return BackendClientHolder.BACKEND_CLIENT;
    }

    private BackendClient() {
    }

    public void setUpBackend(@NonNull String serverHost, int port) {
        this.serverUrl = Urls.getUrl(serverHost, port);

        setUpAuthorization();

        setUpPipes();
    }

    private void setUpAuthorization() {
        AuthorizationManager.config(BackendAuthorization.NAME, OAuth2AuthorizationConfiguration.class)
            .setBaseURL(Urls.getUrl(serverUrl, BackendAuthorization.Paths.BASE))
            .setRedirectURL(Urls.getUrl(serverUrl, BackendAuthorization.Paths.REDIRECT).toString())
            .setAccessTokenEndpoint(BackendAuthorization.Endpoints.ACCESS)
            .setAuthzEndpoint(BackendAuthorization.Endpoints.AUTHZ)
            .setRefreshEndpoint(BackendAuthorization.Endpoints.REFRESH)
            .setAccountId(BackendAuthorization.Ids.ACCOUNT)
            .setClientId(BackendAuthorization.Ids.CLIENT)
            .asModule();
    }

    private void setUpPipes() {
        setUpPipe(BackendPipes.Names.ALERTS, BackendPipes.Roots.ALERTS, Alert.class);
        setUpPipe(BackendPipes.Names.ENVIRONMENTS, BackendPipes.Roots.INVENTORY, Environment.class);
        setUpPipe(BackendPipes.Names.METRICS, BackendPipes.Roots.INVENTORY, Metric.class);
        setUpPipe(BackendPipes.Names.METRIC_DATA, BackendPipes.Roots.METRICS, MetricData.class);
        setUpPipe(BackendPipes.Names.RESOURCE_TYPES, BackendPipes.Roots.INVENTORY, ResourceType.class);
        setUpPipe(BackendPipes.Names.RESOURCES, BackendPipes.Roots.INVENTORY, Resource.class);
        setUpPipe(BackendPipes.Names.TENANTS, BackendPipes.Roots.INVENTORY, Tenant.class);
    }

    private void setUpPipe(String pipeName, String pipePath, Class pipeClass) {
        PipeManager.config(pipeName, RestfulPipeConfiguration.class)
            .module(getAuthorizationModule())
            .withUrl(Urls.getUrl(serverUrl, pipePath))
            .forClass(pipeClass);
    }

    private AuthzModule getAuthorizationModule() {
        return AuthorizationManager.getModule(BackendAuthorization.NAME);
    }

    public void authorize(@NonNull Activity activity, @NonNull Callback<String> callback) {
        getAuthorizationModule().requestAccess(activity, callback);
    }

    public void getAlerts(@NonNull Activity activity, @NonNull Callback<List<Alert>> callback) {
        PipeManager.getPipe(BackendPipes.Names.ALERTS, activity)
            .read(callback);
    }

    public void getTenants(@NonNull Activity activity, @NonNull Callback<List<Tenant>> callback) {
        PipeManager.getPipe(BackendPipes.Names.TENANTS, activity)
            .read(getFilter(
                Uris.getUri(BackendPipes.Paths.TENANTS)), callback);
    }

    public void getEnvironments(@NonNull Tenant tenant,
                                @NonNull Activity activity, @NonNull Callback<List<Environment>> callback) {
        PipeManager.getPipe(BackendPipes.Names.ENVIRONMENTS, activity)
            .read(getFilter(
                Uris.getUri(String.format(BackendPipes.Paths.ENVIRONMENTS, tenant.getId()))), callback);
    }

    public void getResourceTypes(@NonNull Tenant tenant,
                                 @NonNull Activity activity, @NonNull Callback<List<ResourceType>> callback) {
        PipeManager.getPipe(BackendPipes.Names.RESOURCE_TYPES, activity)
            .read(getFilter(
                Uris.getUri(String.format(BackendPipes.Paths.RESOURCE_TYPES, tenant.getId()))), callback);
    }

    public void getResources(@NonNull Tenant tenant, @NonNull ResourceType resourceType,
                             @NonNull Activity activity, @NonNull Callback<List<Resource>> callback) {
        PipeManager.getPipe(BackendPipes.Names.RESOURCES, activity)
            .read(getFilter(
                Uris.getUri(String.format(BackendPipes.Paths.RESOURCES,tenant.getId(), resourceType.getId()))), callback);
    }

    public void getMetrics(@NonNull Tenant tenant, @NonNull Resource resource,
                           @NonNull Activity activity, @NonNull Callback<List<Metric>> callback) {
        PipeManager.getPipe(BackendPipes.Names.METRICS, activity)
            .read(getFilter(
                Uris.getUri(String.format(BackendPipes.Paths.METRICS, tenant.getId(), resource.getId()))), callback);
    }

    public void getMetricData(@NonNull Tenant tenant, @NonNull Metric metric,
                              @NonNull Activity activity, @NonNull Callback<List<MetricData>> callback) {
        Calendar startTime = GregorianCalendar.getInstance();
        startTime.add(Calendar.MINUTE, -10);

        Calendar finishTime = GregorianCalendar.getInstance();

        Map<String, String> pipeParameters = new HashMap<>();
        pipeParameters.put(BackendPipes.Parameters.START, String.valueOf(startTime.getTimeInMillis()));
        pipeParameters.put(BackendPipes.Parameters.FINISH, String.valueOf(finishTime.getTimeInMillis()));

        PipeManager.getPipe(BackendPipes.Names.METRIC_DATA, activity)
            .read(getFilter(
                Uris.getUri(String.format(BackendPipes.Paths.METRIC_DATA, tenant.getId(), metric.getId()),
                pipeParameters)), callback);
    }

    private ReadFilter getFilter(URI uri) {
        ReadFilter filter = new ReadFilter();

        filter.setLinkUri(uri);

        return filter;
    }
}
