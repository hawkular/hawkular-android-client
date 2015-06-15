package org.hawkular.client.android.backend.model;

import com.google.gson.annotations.SerializedName;

public enum AlertType {
    @SerializedName("THRESHOLD")
    THRESHOLD,

    @SerializedName("AVAILABILITY")
    AVAILABILITY
}