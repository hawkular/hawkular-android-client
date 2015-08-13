package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public enum MetricType implements Parcelable {
    @SerializedName("AVAILABILITY")
    AVAILABILITY,

    @SerializedName("GAUGE")
    GAUGE,

    @SerializedName("COUNTER")
    COUNTER,

    @SerializedName("COUNTER_RATE")
    COUNTER_RATE;

    public static Creator<MetricType> CREATOR = new Creator<MetricType>() {
        @Override
        public MetricType createFromParcel(Parcel parcel) {
            return MetricType.valueOf(parcel.readString());
        }

        @Override
        public MetricType[] newArray(int size) {
            return new MetricType[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
