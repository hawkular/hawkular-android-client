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
package org.hawkular.client.android.backend;

import static org.hawkular.client.android.HawkularApplication.retrofit;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hawkular.client.android.auth.ModuleKeeper;
import org.hawkular.client.android.auth.SecretStoreAuthzModule;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Feed;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricAvailabilityBucket;
import org.hawkular.client.android.backend.model.MetricBucket;
import org.hawkular.client.android.backend.model.MetricCounterBucket;
import org.hawkular.client.android.backend.model.MetricGaugeBucket;
import org.hawkular.client.android.backend.model.MetricType;
import org.hawkular.client.android.backend.model.Note;
import org.hawkular.client.android.backend.model.Operation;
import org.hawkular.client.android.backend.model.OperationProperties;
import org.hawkular.client.android.backend.model.Persona;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.service.AlertService;
import org.hawkular.client.android.service.MetricService;
import org.hawkular.client.android.service.TriggerService;
import org.hawkular.client.android.util.CanonicalPath;
import org.hawkular.client.android.util.Ports;
import org.hawkular.client.android.util.Uris;
import org.hawkular.client.android.util.Urls;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.PipeConfiguration;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.module.PipeModule;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.support.v4.app.ServiceCompat;
import android.util.Base64;

import retrofit2.Call;

/**
 * Backend client.
 * <p/>
 * A master controller for all backend-related operations.
 * <p/>
 * Configures proper {@link org.jboss.aerogear.android.pipe.module.PipeModule} instances for authorization
 * using {@link org.jboss.aerogear.android.authorization.AuthorizationManager}. Configures used in the application
 * {@link org.jboss.aerogear.android.pipe.Pipe} instances.
 * <p/>
 * Most of the configuration is stored using internal AeroGear long-lived objects. It is not necessary to handle
 * this class objects as singletons, it is intended to be used as a short-lived object.
 * <p/>
 * The next step is to configure {@link org.jboss.aerogear.android.pipe.Pipe} instances.
 * The best time to to do so of course is after the authorization process.
 * <p/>
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

    public void configureAuthorization(Context context) {
        ModuleKeeper.modules.put("hawkular", new SecretStoreAuthzModule(context));
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
        URL alertResolveUrl = Urls.getUrl(pipeUrl, BackendPipes.Paths.ALERT_RESOLVE);
        URL alertAckUrl = Urls.getUrl(pipeUrl, BackendPipes.Paths.ALERT_ACKNOWLEDGE);
        URL alertNoteUrl = Urls.getUrl(pipeUrl, BackendPipes.Paths.ALERT_NOTE);
        URL triggerUpdateUrl = Urls.getUrl(pipeUrl,BackendPipes.Paths.UPDATE_TRIGGER);
        List<PipeModule> pipeModules = Arrays.asList(
                getAuthorizationModule(),
                getPersonnelModule(persona));

        configurePipe(BackendPipes.Names.ALERTS, pipeUrl, pipeModules, Alert.class);
        configurePipe(BackendPipes.Names.ALERT_ACKNOWLEDGE, alertAckUrl, pipeModules, String.class);
        configurePipe(BackendPipes.Names.ALERT_RESOLVE, alertResolveUrl, pipeModules, String.class);
        configurePipe(BackendPipes.Names.FEEDS, pipeUrl, pipeModules, Feed.class);
        configurePipe(BackendPipes.Names.FEED_METRICS, pipeUrl, pipeModules, Metric.class);
        configurePipe(BackendPipes.Names.FEED_CHILD_RESOURCES, pipeUrl, pipeModules, Resource.class);
        configurePipe(BackendPipes.Names.FEED_RESOURCES, pipeUrl, pipeModules, Resource.class);
        configurePipe(BackendPipes.Names.METRICS, pipeUrl, pipeModules, Metric.class);
        configurePipe(BackendPipes.Names.METRIC_DATA_AVAILABILITY, pipeUrl, pipeModules,
                MetricAvailabilityBucket.class);
        configurePipe(BackendPipes.Names.METRIC_DATA_COUNTER, pipeUrl, pipeModules, MetricCounterBucket.class);
        configurePipe(BackendPipes.Names.METRIC_DATA_GAUGE, pipeUrl, pipeModules, MetricGaugeBucket.class);
        configurePipe(BackendPipes.Names.NOTE, alertNoteUrl, pipeModules, Note.class);
        configurePipe(BackendPipes.Names.OPERATIONS, pipeUrl, pipeModules, Operation.class);
        configurePipe(BackendPipes.Names.OPERATION_PROPERTIES, pipeUrl, pipeModules, OperationProperties.class);
        configurePipe(BackendPipes.Names.PERSONA, backendUrl, pipeModules, Persona.class);
        configurePipe(BackendPipes.Names.TRIGGERS, pipeUrl, pipeModules, Trigger.class);
        configurePipe(BackendPipes.Names.UPDATE_TRIGGER, triggerUpdateUrl ,pipeModules,String.class);
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


    public void getRetroAlerts(@NonNull Date startTime, @NonNull Date finishTime, @NonNull List<Trigger> triggers,
                          @NonNull retrofit2.Callback<List<Alert>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendPipes.Parameters.START_TIME, String.valueOf(startTime.getTime()));
        parameters.put(BackendPipes.Parameters.FINISH_TIME, String.valueOf(finishTime.getTime()));

        if (triggers != null) {
            parameters.put(BackendPipes.Parameters.TRIGGERS, Uris.getParameter(getTriggerIds(triggers)));
        }
        URI uri = Uris.getUri(BackendPipes.Paths.ALERTS, parameters);

        AlertService service = retrofit.create(AlertService.class);
        Call call = service.get(parameters);
        call.enqueue(callback);

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
        savePipe(BackendPipes.Names.ALERT_ACKNOWLEDGE, alert, callback);
    }

    public void acknowledgeRetroAlert(@NonNull Alert alert,
                                 @NonNull retrofit2.Callback<List<String>> callback) {

        AlertService service = retrofit.create(AlertService.class);
        Call call = service.postAckAlert();
        call.enqueue(callback);

    }

    public void resolveRetroAlert(@NonNull Alert alert,
                                  @NonNull retrofit2.Callback<List<String>> callback) {

        AlertService service = retrofit.create(AlertService.class);
        Call call = service.postResolveAlert();
        call.enqueue(callback);
    }

    public void resolveAlert(@NonNull Alert alert,
                             @NonNull Callback<List<String>> callback) {
        savePipe(BackendPipes.Names.ALERT_RESOLVE, alert, callback);
    }


    public void noteOnAlert(@NonNull Note note,
                            @NonNull Callback<List<String>> callback) {
        savePipe(BackendPipes.Names.NOTE, note, callback);
    }

    public void updateTrigger(@NonNull Trigger trigger, @NonNull Callback<List<String>> callback){
        savePipe(BackendPipes.Names.UPDATE_TRIGGER,trigger,callback);
    }


    public void getFeeds(@NonNull Callback<List<Feed>> callback) {
        URI uri = Uris.getUri(BackendPipes.Paths.FEEDS);

        readPipe(BackendPipes.Names.FEEDS, uri, callback);
    }

    public void getOpreations(@NonNull Callback<List<Operation>> callback, Resource resource) {
        URI uri = Uris.getUri(CanonicalPath.getByString(resource.getType().getPath())
                .fix(BackendPipes.Paths.OPERATIONS));

        readPipe(BackendPipes.Names.OPERATIONS, uri, callback);
    }

    public void getOperationProperties(@NonNull Callback<List<OperationProperties>> callback, Operation operation, Resource resource) {
        URI uri = Uris.getUri(CanonicalPath.getByString(resource.getType().getPath())
                .fix(BackendPipes.Paths.OPERATIONS)+";name="+operation.getName()+"/d;parameterTypes");

        readPipe(BackendPipes.Names.OPERATION_PROPERTIES, uri, callback);
    }

    public void getResourcesFromFeed(@NonNull Callback<List<Resource>> callback, Feed feed) {
        URI uri = Uris.getUri(CanonicalPath.getByString(feed.getPath()).fix(BackendPipes.Paths.FEED_RESOURCES));

        readPipe(BackendPipes.Names.FEED_RESOURCES, uri, callback);
    }


    public void getRecResourcesFromFeed(@NonNull Callback<List<Resource>> callback, Resource resource) {

        URI uri = Uris.getUri(CanonicalPath.getByString(resource.getPath()).
                fix(BackendPipes.Paths.FEED_CHILD_RESOURCES));

        readPipe(BackendPipes.Names.FEED_CHILD_RESOURCES, uri, callback);
    }


    public void getRetroMetricsFromFeed(@NonNull retrofit2.Callback<List<Metric>> callback, Resource resource) {
        URI uri = Uris.getUri(CanonicalPath.getByString(resource.getPath()).
                fix(BackendPipes.Paths.FEED_METRICS));

        MetricService service = retrofit.create(MetricService.class);
        Call call = service.getMetricFromFeed();
        call.enqueue(callback);
    }


    public void getRetroMetrics(@NonNull Environment environment, @NonNull Resource resource,
                                @NonNull retrofit2.Callback<List<Metric>> callback){

        URI uri = Uris.getUri(CanonicalPath.getByString(resource.getPath()).
                fix(BackendPipes.Paths.METRICS));

        MetricService service = retrofit.create(MetricService.class);

        Call call = service.get();
        call.enqueue(callback);
    }


    public void getRetroMetricData(@NonNull Metric metric, long bucket,
                                   @NonNull Date startTime, @NonNull Date finishTime,
                                   @NonNull retrofit2.Callback<List<MetricBucket>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendPipes.Parameters.START, String.valueOf(startTime.getTime()));
        parameters.put(BackendPipes.Parameters.FINISH, String.valueOf(finishTime.getTime()));
        parameters.put(BackendPipes.Parameters.BUCKETS, String.valueOf(bucket));

        String path;
        String name;

        if (metric.getConfiguration().getType()== MetricType.AVAILABILITY) {
            path = BackendPipes.Paths.METRIC_DATA_AVAILABILITY;
            name = BackendPipes.Names.METRIC_DATA_AVAILABILITY;
        } else if (metric.getConfiguration().getType()== MetricType.COUNTER) {
            path = BackendPipes.Paths.METRIC_DATA_COUNTER;
            name = BackendPipes.Names.METRIC_DATA_COUNTER;
        } else {
            path = BackendPipes.Paths.METRIC_DATA_GAUGE;
            name = BackendPipes.Names.METRIC_DATA_GAUGE;
        }

        URI uri = Uris.getUri(String.format(path, Uris.getEncodedParameter(metric.getId())), parameters);

        //readPipe(name, uri, callback);
        MetricService service = retrofit.create(MetricService.class);
        Call call = service.getMetricData(uri.toString());
        call.enqueue(callback);
    }

    public void getRetroTriggers(@NonNull retrofit2.Callback<List<Trigger>> callback) {
        URI uri = Uris.getUri(BackendPipes.Paths.TRIGGERS);
        TriggerService service = retrofit.create(TriggerService.class);


        Map<String, String> map = new HashMap<>();
        String cred = new String(Base64.encode(("jdoe:password").getBytes(), Base64.NO_WRAP));
        map.put("Authorization", "Basic "+cred);
        map.put("Hawkular-Tenant", "hawkular");

        Call call = service.get(map);
        call.enqueue(callback);

    }

    @SuppressWarnings("unchecked")
    private <T> void readPipe(String pipeName, URI uri, Callback<List<T>> callback) {
        getPipe(pipeName).read(getFilter(uri), callback);
    }

    @SuppressWarnings("unchecked")
    private <T> void savePipe(String pipeName, Object object, Callback<List<T>> callback) {
        getPipe(pipeName).save(object, callback);
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