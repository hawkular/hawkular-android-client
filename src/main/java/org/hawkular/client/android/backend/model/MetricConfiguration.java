package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class MetricConfiguration implements Parcelable {
    @SerializedName("type")
    private MetricType type;

    public MetricType getType() {
        return type;
    }

    public static Creator<MetricConfiguration> CREATOR = new Creator<MetricConfiguration>() {
        @Override
        public MetricConfiguration createFromParcel(Parcel parcel) {
            return new MetricConfiguration(parcel);
        }

        @Override
        public MetricConfiguration[] newArray(int size) {
            return new MetricConfiguration[size];
        }
    };

    private MetricConfiguration(Parcel parcel) {
        this.type = parcel.readParcelable(MetricType.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(type, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
