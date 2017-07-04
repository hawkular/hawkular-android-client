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

import org.hawkular.client.android.backend.model.Feed;
import org.hawkular.client.android.backend.model.FullTrigger;
import org.hawkular.client.android.backend.model.InventoryResponseBody;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Trigger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface TriggerService {

    @GET("hawkular/alerts/triggers")
    Call<List<Trigger>> get(
    );

    @POST("/hawkular/alerts/triggers")
    Call<List<String>> updateTrigger(
            @Body Trigger trigger
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

}
