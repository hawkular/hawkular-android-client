package org.hawkular.client.android.backend.model;

import com.google.gson.annotations.SerializedName;

public final class AlertEvaluationCondition {
    @SerializedName("threshold")
    private double threshold;

    @SerializedName("type")
    private AlertType type;

    public double getThreshold() {
        return threshold;
    }

    public AlertType getType() {
        return type;
    }
}