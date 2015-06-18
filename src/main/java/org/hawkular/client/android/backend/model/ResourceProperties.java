package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class ResourceProperties implements Parcelable {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public static Creator<ResourceProperties> CREATOR = new Creator<ResourceProperties>() {
        @Override
        public ResourceProperties createFromParcel(Parcel parcel) {
            return new ResourceProperties(parcel);
        }

        @Override
        public ResourceProperties[] newArray(int size) {
            return new ResourceProperties[size];
        }
    };

    private ResourceProperties(Parcel parcel) {
        this.url = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
