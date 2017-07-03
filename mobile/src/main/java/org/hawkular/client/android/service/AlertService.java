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

import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.Note;
import org.hawkular.client.android.backend.model.Trigger;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface AlertService {

    @GET("hawkular/alerts")
    Call<List<Alert>> get(@QueryMap Map<String, String> parameters);

    @FormUrlEncoded
    @POST("/hawkular/alerts/ack/{alertId}")
    Call<List<String>> ackAlert(@Path("alertId") String alertId);


    @FormUrlEncoded
    @POST("/hawkular/alerts/resolve/{alertId}")
    Call<List<String>> resolveAlert(@Path("alertId") String alertId);

    @FormUrlEncoded
    @POST("/hawkular/alerts/note/{alertId}")
    Call<List<String>> noteOnAlert(@QueryMap Map<String, String> parameters, @Path("alertId") String alertId);

}
