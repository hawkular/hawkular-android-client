package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Resource implements Parcelable {
    public static final class Properties implements Parcelable {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public static Creator<Properties> CREATOR = new Creator<Properties>() {
            @Override
            public Properties createFromParcel(Parcel parcel) {
                return new Properties(parcel);
            }

            @Override
            public Properties[] newArray(int size) {
                return new Properties[size];
            }
        };

        private Properties(Parcel parcel) {
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

    @SerializedName("id")
    private String id;

    @SerializedName("properties")
    private Properties properties;

    public String getId() {
        return id;
    }

    public Properties getProperties() {
        return properties;
    }

    public static Creator<Resource> CREATOR = new Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel parcel) {
            return null;
        }

        @Override
        public Resource[] newArray(int i) {
            return new Resource[0];
        }
    };

    private Resource(Parcel parcel) {
        this.id = parcel.readString();
        this.properties = parcel.readParcelable(Properties.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeParcelable(properties, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
