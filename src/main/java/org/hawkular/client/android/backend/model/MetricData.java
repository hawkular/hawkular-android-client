package org.hawkular.client.android.backend.model;

import com.google.gson.annotations.SerializedName;

public final class MetricData {
    @SerializedName("value")
    private long value;

    @SerializedName("timestamp")
    private long timestamp;

    public MetricData(long timestamp, long value) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public long getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
