package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class MetricProperties implements Parcelable {
    @SerializedName("description")
    private String description;

    public String getDescription() {
        return description;
    }

    public static Creator<MetricProperties> CREATOR = new Creator<MetricProperties>() {
        @Override
        public MetricProperties createFromParcel(Parcel parcel) {
            return new MetricProperties(parcel);
        }

        @Override
        public MetricProperties[] newArray(int size) {
            return new MetricProperties[size];
        }
    };

    private MetricProperties(Parcel parcel) {
        this.description = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
