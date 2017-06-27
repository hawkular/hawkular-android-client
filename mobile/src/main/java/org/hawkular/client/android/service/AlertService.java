package org.hawkular.client.android.service;

import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.Trigger;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by pallavi on 26/06/17.
 */

public interface AlertService {

    @GET("hawkular/alerts")
    Call<List<Alert>> get(
            @QueryMap Map<String, String> parameters
    );


    @FormUrlEncoded
    @POST("/hawkular/alerts/ack")
    Call<List<String>> postAckAlert();


    @FormUrlEncoded
    @POST("/hawkular/alerts/resolve")
    Call<List<String>> postResolveAlert();

}
