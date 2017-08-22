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

import org.hawkular.client.android.backend.model.AvailabilityCondition;
import org.hawkular.client.android.backend.model.Feed;
import org.hawkular.client.android.backend.model.FullTrigger;
import org.hawkular.client.android.backend.model.InventoryResponseBody;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ThresholdCondition;
import org.hawkular.client.android.backend.model.Trigger;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TriggerService {

    @GET("hawkular/alerts/triggers")
    Call<List<Trigger>> get(
    );

    @PUT("/hawkular/alerts/triggers/enabled")
    Call<List<String>> updateTrigger(
            @Query("triggerIds") String triggerId,
            @Query("enabled") Boolean enabled
    );

    @POST("/hawkular/alerts/triggers")
    Call<FullTrigger> createTrigger(
            @Body FullTrigger trigger
    );

    @POST("/hawkular/metrics/strings/raw/query/")
    Call<List<Resource>> getResourcesFromFeed(
            @Body InventoryResponseBody inventoryResponseBody
            );

    @GET("/hawkular/metrics/strings/tags/module:inventory,feed:*")
    Call<Feed> getFeeds();

    @DELETE("hawkular/alerts/triggers/{triggerId}")
    Call<Void> deleteTrigger(@Path("triggerId") String triggerId);

    @PUT("hawkular/alerts/triggers/{triggerId}/conditions/{triggerMode}")
    Call<String> setConditionsForTrigger(@Path("triggerId") String triggerId, @Path("triggerMode") String triggerMode, @Body List<ThresholdCondition> condition);

    @PUT("hawkular/alerts/triggers/{triggerId}/conditions/{triggerMode}")
    Call<String> setConditionsForAvailabilityTrigger(@Path("triggerId") String triggerId, @Path("triggerMode") String triggerMode, @Body List<AvailabilityCondition> condition);
}