package org.hawkular.client.android.service;

import java.util.List;
import java.util.Map;

import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricAvailabilityBucket;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface MetricService {

    @GET("/hawkular/metrics")
    Call<List<Metric>> getMetricsStatus();


    @GET("/hawkular/metrics/availability")
    Call<List<Metric>> getAvailabilityMetrics();

    @GET("/hawkular/metrics/counters")
    Call<List<Metric>> getCounterMetrics();

    @GET("/hawkular/metrics/gauges")
    Call<List<Metric>> getGaugeMetrics();

    @GET("/hawkular/metrics/availability/{id}/data")
    Call<List<MetricAvailabilityBucket>> getMetricAvailabilityData(@Path("id") String id,
                                                                   @QueryMap Map<String, String> parameters);

}

