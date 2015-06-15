package org.hawkular.client.android.backend.model;

import com.google.gson.annotations.SerializedName;

public final class AlertEvaluation {
    @SerializedName("condition")
    private AlertEvaluationCondition condition;

    @SerializedName("value")
    private double value;

    @SerializedName("dataTimestamp")
    private long dataTimestamp;

    @SerializedName("evalTimestamp")
    private long evaluationTimestamp;

    public AlertEvaluationCondition getCondition() {
        return condition;
    }

    public double getValue() {
        return value;
    }

    public long getDataTimestamp() {
        return dataTimestamp;
    }

    public long getEvaluationTimestamp() {
        return evaluationTimestamp;
    }
}