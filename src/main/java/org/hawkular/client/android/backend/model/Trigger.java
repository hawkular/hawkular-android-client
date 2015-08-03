package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Trigger implements Parcelable {
    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public static Creator<Trigger> CREATOR = new Creator<Trigger>() {
        @Override
        public Trigger createFromParcel(Parcel parcel) {
            return new Trigger(parcel);
        }

        @Override
        public Trigger[] newArray(int size) {
            return new Trigger[size];
        }
    };

    private Trigger(Parcel parcel) {
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
