package org.hawkular.client.android.service;

import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricBucket;
import org.hawkular.client.android.backend.model.Trigger;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by pallavi on 25/06/17.
 */

public interface MetricService {

    @GET("hawkular/inventory/traversal/e;{e}/{r}/rl;incorporates/type=m")
    Call<List<Metric>> get();

    @GET
    Call<List<MetricBucket>> getMetricData(
            @Url String url
    );

    @GET("metrics/strings/tags/module:inventory,feed:*")
    Call<List<Metric>> getMetricFromFeed();

}

