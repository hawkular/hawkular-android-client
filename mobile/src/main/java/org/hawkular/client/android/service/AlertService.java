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

    @FormUrlEncoded
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
