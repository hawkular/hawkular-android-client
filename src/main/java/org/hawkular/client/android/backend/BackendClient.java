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
package org.hawkular.client.android.backend;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.AlertStatus;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricData;
import org.hawkular.client.android.backend.model.Persona;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.util.ModifiedAuthzModule;
import org.hawkular.client.android.util.ModuleKeeper;
import org.hawkular.client.android.util.Ports;
import org.hawkular.client.android.util.Uris;
import org.hawkular.client.android.util.Urls;
import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.PipeConfiguration;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.module.PipeModule;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

/**
 * Backend client.
 *
 * A master controller for all backend-related operations.
 *
 * Configures proper {@link org.jboss.aerogear.android.pipe.module.PipeModule} instances for authorization
 * using {@link org.jboss.aerogear.android.authorization.AuthorizationManager}. Configures used in the application
 * {@link org.jboss.aerogear.android.pipe.Pipe} instances.
 *
 * Most of the configuration is stored using internal AeroGear long-lived objects. It is not necessary to handle
 * this class objects as singletons, it is intended to be used as a short-lived object.
 *

 * {@link org.jboss.aerogear.android.pipe.Pipe} instances are not exposed to class users, external API prefers
 * {@link org.jboss.aerogear.android.core.Callback} over them.
 */
public final class BackendClient {
    private final Activity activity;
    private final Fragment fragment;

    @NonNull
    @RequiresPermission(Manifest.permission.INTERNET)
    public static BackendClient of(@NonNull Activity activity) {
        return new BackendClient(activity, null);
    }

    @NonNull
    @RequiresPermission(Manifest.permission.INTERNET)
    public static BackendClient of(@NonNull Fragment fragment) {
        return new BackendClient(null, fragment);
    }

    private BackendClient(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;

    }



    public void configureAuthorization() {
        ModuleKeeper.modules.put("hawkular", new ModifiedAuthzModule(activity.getApplicationContext()));

    }


    public void configureCommunication(@NonNull String host, @NonNull Persona persona) {
        URL backendUrl = Urls.getUrl(host);

        configurePipes(backendUrl, persona);
    }

    public void configureCommunication(@NonNull String host,
                                       @IntRange(from = Ports.MINIMUM, to = Ports.MAXIMUM) int port,
                                       @NonNull Persona persona) {
        URL backendUrl = Urls.getUrl(host, port);

        configurePipes(backendUrl, persona);
    }

    private void configurePipes(URL backendUrl, Persona persona) {
        URL pipeUrl = Urls.getUrl(backendUrl, BackendPipes.Paths.ROOT);

        List<PipeModule> pipeModules = Arrays.asList(
            getAuthorizationModule(),
            getPersonnelModule(persona));

        configurePipe(BackendPipes.Names.ALERTS, pipeUrl, pipeModules, Alert.class);
        configurePipe(BackendPipes.Names.ALERT_ACKNOWLEDGE, pipeUrl, pipeModules, String.class);
        configurePipe(BackendPipes.Names.ALERT_RESOLVE, pipeUrl, pipeModules, String.class);
        configurePipe(BackendPipes.Names.ENVIRONMENTS, pipeUrl, pipeModules, Environment.class);
        configurePipe(BackendPipes.Names.METRICS, pipeUrl, pipeModules, Metric.class);
        configurePipe(BackendPipes.Names.METRIC_DATA_AVAILABILITY, pipeUrl, pipeModules, MetricData.class);
        configurePipe(BackendPipes.Names.METRIC_DATA_GAUGE, pipeUrl, pipeModules, MetricData.class);
        configurePipe(BackendPipes.Names.PERSONA, backendUrl, pipeModules, Persona.class);
        configurePipe(BackendPipes.Names.PERSONAS, backendUrl, pipeModules, Persona.class);
        configurePipe(BackendPipes.Names.RESOURCES, pipeUrl, pipeModules, Resource.class);
        configurePipe(BackendPipes.Names.TRIGGERS, pipeUrl, pipeModules, Trigger.class);
    }

    private AuthzModule getAuthorizationModule() {

            return ModuleKeeper.modules.get("hawkular");

    }

    private PipeModule getPersonnelModule(Persona persona) {
        return new BackendPersonnel(persona);
    }

    @SuppressWarnings("unchecked")
    private <T> void configurePipe(String pipeName, URL pipeUrl, List<PipeModule> pipeModules, Class<T> pipeClass) {
        PipeConfiguration pipeConfiguration = PipeManager.config(pipeName, RestfulPipeConfiguration.class)
            .withUrl(pipeUrl);

        for (PipeModule pipeModule : pipeModules) {
            pipeConfiguration.module(pipeModule);
        }

        pipeConfiguration.forClass(pipeClass);
    }

    public void authorize(@NonNull Activity activity, @NonNull Callback<String> callback) {
        getAuthorizationModule().requestAccess(activity, callback);
    }

    public void deauthorize() {
        AuthzModule authorizationModule = getAuthorizationModule();

        if (authorizationModule.hasCredentials()) {
            authorizationModule.deleteAccount();
        }
    }

    public void getAlerts(@NonNull Date startTime, @NonNull Date finishTime, @NonNull List<Trigger> triggers,
                          @NonNull Callback<List<Alert>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendPipes.Parameters.START_TIME, String.valueOf(startTime.getTime()));
        parameters.put(BackendPipes.Parameters.FINISH_TIME, String.valueOf(finishTime.getTime()));
        parameters.put(BackendPipes.Parameters.TRIGGERS, Uris.getParameter(getTriggerIds(triggers)));
        parameters.put(BackendPipes.Parameters.STATUSES, String.valueOf(AlertStatus.OPEN));

        URI uri = Uris.getUri(BackendPipes.Paths.ALERTS, parameters);

        readPipe(BackendPipes.Names.ALERTS, uri, callback);
    }

    private List<String> getTriggerIds(List<Trigger> triggers) {
        List<String> triggerIds = new ArrayList<>(triggers.size());

        for (Trigger trigger : triggers) {
            triggerIds.add(trigger.getId());
        }

        return triggerIds;
    }

    public void acknowledgeAlert(@NonNull Alert alert,
                                 @NonNull Callback<List<String>> callback) {
        URI uri = Uris.getUri(String.format(BackendPipes.Paths.ALERT_ACKNOWLEDGE, alert.getId()));

        readPipe(BackendPipes.Names.ALERT_ACKNOWLEDGE, uri, callback);
    }

    public void resolveAlert(@NonNull Alert alert,
                             @NonNull Callback<List<String>> callback) {
        URI uri = Uris.getUri(String.format(BackendPipes.Paths.ALERT_RESOLVE, alert.getId()));

        readPipe(BackendPipes.Names.ALERT_RESOLVE, uri, callback);
    }

    public void getEnvironments(@NonNull Callback<List<Environment>> callback) {
        URI uri = Uris.getUri(BackendPipes.Paths.ENVIRONMENTS);

        readPipe(BackendPipes.Names.ENVIRONMENTS, uri, callback);
    }

    public void getMetrics(@NonNull Environment environment, @NonNull Resource resource,
                           @NonNull Callback<List<Metric>> callback) {
        URI uri = Uris.getUri(String.format(BackendPipes.Paths.METRICS, environment.getId(), resource.getId()));

        readPipe(BackendPipes.Names.METRICS, uri, callback);
    }

    public void getMetricDataAvailability(@NonNull Resource resource,
                                          @NonNull Date startTime, @NonNull Date finishTime,
                                          @NonNull Callback<List<MetricData>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendPipes.Parameters.START, String.valueOf(startTime.getTime()));
        parameters.put(BackendPipes.Parameters.FINISH, String.valueOf(finishTime.getTime()));

        URI uri = Uris.getUri(String.format(BackendPipes.Paths.METRIC_DATA_AVAILABILITY, resource.getId()), parameters);

        readPipe(BackendPipes.Names.METRIC_DATA_AVAILABILITY, uri, callback);
    }

    public void getMetricDataGauge(@NonNull Metric metric,
                                   @NonNull Date startTime, @NonNull Date finishTime,
                                   @NonNull Callback<List<MetricData>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendPipes.Parameters.START, String.valueOf(startTime.getTime()));
        parameters.put(BackendPipes.Parameters.FINISH, String.valueOf(finishTime.getTime()));

        URI uri = Uris.getUri(String.format(BackendPipes.Paths.METRIC_DATA_GAUGE, metric.getId()), parameters);

        readPipe(BackendPipes.Names.METRIC_DATA_GAUGE, uri, callback);
    }

    public void getPersona(@NonNull Callback<List<Persona>> callback) {
        URI uri = Uris.getUri(BackendPipes.Paths.PERSONA);

        readPipe(BackendPipes.Names.PERSONA, uri, callback);
    }

    public void getPersonas(@NonNull Callback<List<Persona>> callback) {
        URI uri = Uris.getUri(BackendPipes.Paths.PERSONAS);

        readPipe(BackendPipes.Names.PERSONAS, uri, callback);
    }

    public void getResources(@NonNull Environment environment,
                             @NonNull Callback<List<Resource>> callback) {
        URI uri = Uris.getUri(String.format(BackendPipes.Paths.RESOURCES, environment.getId()));

        readPipe(BackendPipes.Names.RESOURCES, uri, callback);
    }

    public void getTriggers(@NonNull Callback<List<Trigger>> callback) {
        URI uri = Uris.getUri(BackendPipes.Paths.TRIGGERS);

        readPipe(BackendPipes.Names.TRIGGERS, uri, callback);
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
