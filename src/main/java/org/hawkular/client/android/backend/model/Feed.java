package org.hawkular.client.android.backend.model;

import org.jboss.aerogear.android.core.RecordId;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anuj on 7/6/16.
 */
public class Feed implements Parcelable {

    @RecordId
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public Feed(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel parcel) {
            return new Feed(parcel);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    private Feed(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}