package org.hawkular.client.android.service;

import java.util.List;

import org.hawkular.client.android.backend.model.Trigger;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TriggerService {

    @GET("hawkular/alerts/triggers")
    Call<List<Trigger>> get();
}
