package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class MetricType implements Parcelable {
    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public static Creator<MetricType> CREATOR = new Creator<MetricType>() {
        @Override
        public MetricType createFromParcel(Parcel parcel) {
            return new MetricType(parcel);
        }

        @Override
        public MetricType[] newArray(int size) {
            return new MetricType[size];
        }
    };

    private MetricType(Parcel parcel) {
        this.id = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
