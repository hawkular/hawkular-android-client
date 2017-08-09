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
package org.hawkular.client.android.service;

import java.util.List;
import java.util.Map;

import org.hawkular.client.android.backend.model.InventoryResponseBody;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricAvailabilityBucket;
import org.hawkular.client.android.backend.model.MetricCounterBucket;
import org.hawkular.client.android.backend.model.Resource;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface MetricService {

    @GET("/hawkular/metrics")
    Call<String> getMetricsStatus();

    @GET("/hawkular/metrics/availability")
    Call<List<Metric>> getAvailabilityMetrics();

    @GET("/hawkular/metrics/counters")
    Call<List<Metric>> getCounterMetrics();

    @GET("/hawkular/metrics/gauges")
    Call<List<Metric>> getGaugeMetrics();

    @POST("/hawkular/metrics/strings/raw/query")
    Call<List<Resource>> getMetricType(  @Body InventoryResponseBody inventoryResponseBody);

    @POST("/hawkular/metrics/strings/raw/query")
    Call<List<Resource>> getMetricFromFeed(@Body InventoryResponseBody inventoryResponseBody);

    @GET("/hawkular/metrics/availability/{id}/data")
    Call<List<MetricAvailabilityBucket>> getMetricAvailabilityData(@Path("id") String id,
                                                                   @QueryMap Map<String, String> parameters);

    @GET("/hawkular/metrics/counters/{id}/data")
    Call<List<MetricCounterBucket>> getMetricCounterData(@Path("id") String id,
                                                         @QueryMap Map<String, String> parameters);
}

