package org.hawkular.client.android.backend.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Alert {
    @SerializedName("ctime")
    private long timestamp;

    @SerializedName("evalSets")
    private List<List<AlertEvaluation>> evaluations;

    public long getTimestamp() {
        return timestamp;
    }

    public List<List<AlertEvaluation>> getEvaluations() {
        return evaluations;
    }
}