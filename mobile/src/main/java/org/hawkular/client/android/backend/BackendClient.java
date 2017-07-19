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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.hawkular.client.android.HawkularApplication;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.Feed;
import org.hawkular.client.android.backend.model.FullTrigger;
import org.hawkular.client.android.backend.model.InventoryResponseBody;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricBucket;
import org.hawkular.client.android.backend.model.MetricType;
import org.hawkular.client.android.backend.model.Note;
import org.hawkular.client.android.backend.model.Operation;
import org.hawkular.client.android.backend.model.OperationProperties;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.service.AlertService;
import org.hawkular.client.android.service.MetricService;
import org.hawkular.client.android.service.TriggerService;
import org.hawkular.client.android.util.Preferences;
import org.hawkular.client.android.util.Uris;
import org.jboss.aerogear.android.pipe.callback.AbstractCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static org.hawkular.client.android.HawkularApplication.retrofit;

/**
 * Backend client.
 * <p/>
 * A master controller for all backend-related operations.
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

    public void getAlerts(@NonNull Date startTime, @NonNull Date finishTime, @NonNull List<Trigger> triggers,
                               @NonNull Callback<List<Alert>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendPipes.Parameters.START_TIME, String.valueOf(startTime.getTime()));
        parameters.put(BackendPipes.Parameters.FINISH_TIME, String.valueOf(finishTime.getTime()));

        AlertService service = retrofit.create(AlertService.class);
        Call call = service.get();
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
                                 @NonNull retrofit2.Callback<List<String>> callback) {
        AlertService service = retrofit.create(AlertService.class);
        Call call = service.ackAlert(alert.getId());
        call.enqueue(callback);

    }

    public void resolveAlert(@NonNull Alert alert, @NonNull Callback<List<String>> callback) {
        AlertService service = retrofit.create(AlertService.class);
        Call call = service.resolveAlert(alert.getId());
        call.enqueue(callback);
    }


    public void noteOnAlert(@NonNull Note note,
                            @NonNull Callback<List<String>> callback) {
        // TODO : after moving to retrofit complete
    }

    public void updateTrigger(@NonNull Trigger trigger, @NonNull retrofit2.Callback<List<String>> callback){
        TriggerService service = retrofit.create(TriggerService.class);
        Call call = service.updateTrigger(trigger);
        call.enqueue(callback);
    }

    public void createTrigger(@NonNull FullTrigger trigger, @NonNull retrofit2.Callback<List<String>> callback){
        TriggerService service = retrofit.create(TriggerService.class);
        Call call = service.createTrigger(trigger);
        call.enqueue(callback);
    }


    public void getFeeds(@NonNull retrofit2.Callback<Feed> callback) {
        TriggerService service = retrofit.create(TriggerService.class);
        Call call = service.getFeeds();
        call.enqueue(callback);
    }

    public void getMetricType(@NonNull retrofit2.Callback callback, @NonNull InventoryResponseBody body){
        MetricService service = retrofit.create(MetricService.class);
        Call call = service.getMetricType(body);
        call.enqueue(callback);
    }

    public void getOpreations(@NonNull AbstractCallback<List<Operation>> callback, Resource resource) {
        // TODO : after moving to retrofit complete
    }

    public void getOperationProperties(@NonNull AbstractCallback<List<OperationProperties>> callback, Operation operation, Resource resource) {
        // TODO : after moving to retrofit complete
    }

    public void getResourcesFromFeed(@NonNull retrofit2.Callback<List<Resource>> callback, @NonNull InventoryResponseBody body){

        TriggerService service = retrofit.create(TriggerService.class);
        Call call = service.getResourcesFromFeed(body);
        call.enqueue(callback);
    }


    public void getRecResourcesFromFeed(@NonNull retrofit2.Callback callback, Resource resource) {
        // TODO : after moving to retrofit complete
    }


    public void getMetricsFromFeed(@NonNull retrofit2.Callback<List<Resource>> callback, @NonNull InventoryResponseBody body) {
        MetricService service = retrofit.create(MetricService.class);
        Call call = service.getMetricFromFeed(body);
        call.enqueue(callback);
    }


    public void getMetrics(@NonNull Resource resource, @NonNull Callback<List<Metric>> callback){
        // TODO : after moving to retrofit complete
    }


    public void getMetricData(@NonNull Metric metric, long bucket,
                                          @NonNull Date startTime, @NonNull Date finishTime,
                                          @NonNull Callback<List<MetricBucket>> callback) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(BackendPipes.Parameters.START, String.valueOf(startTime.getTime()));
        parameters.put(BackendPipes.Parameters.FINISH, String.valueOf(finishTime.getTime()));
        parameters.put(BackendPipes.Parameters.BUCKETS, String.valueOf(bucket));


        MetricService service = retrofit.create(MetricService.class);
        String id = metric.getId();
        String encodedurl = "";
        try{
            encodedurl = URLEncoder.encode(id,"utf-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("BackendClient",encodedurl);
        Call call = null;
        if (metric.getConfiguration().getType().equalsIgnoreCase("AVAILABILITY")) {
            call = service.getMetricAvailabilityData(encodedurl, parameters);
        } else if (metric.getConfiguration().getType().equalsIgnoreCase("COUNTER")){
            call = service.getMetricCounterData(encodedurl, parameters);
        } else if (metric.getConfiguration().getType().equalsIgnoreCase("GAUGE")) {
            call = service.getMetricGaugesData(encodedurl, parameters);
        }

        call.enqueue(callback);
    }

    public void getTriggers(@NonNull Callback<List<Trigger>> callback) {
        TriggerService service = retrofit.create(TriggerService.class);
        Call call = service.get();
        call.enqueue(callback);


    }

    public void configureAuthorization(String url, String username, String password) {
        HawkularApplication.setUpRetrofit(url, username, password);
    }


    public void authorize(Callback<List<Metric>> callback) {
        MetricService service = retrofit.create(MetricService.class);
        Call call = service.getAvailabilityMetrics();
        call.enqueue(callback);
    }

    public void deauthorize(Context context) {
        Preferences.of(context).authenticated().set(false);
    }

    public void configureAuthorization(Context context) {
        String url = Preferences.of(context).url().get();
        String username = Preferences.of(context).username().get();
        String password = Preferences.of(context).password().get();
        HawkularApplication.setUpRetrofit(url, username, password);
    }
}