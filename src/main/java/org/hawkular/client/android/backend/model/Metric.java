package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Metric implements Parcelable {
    public static final class Properties implements Parcelable {
        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
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

    @SerializedName("id")
    private String id;

    @SerializedName("properties")
    private Properties properties;

    @SerializedName("type")
    private MetricType type;

    public String getId() {
        return id;
    }

    public Properties getProperties() {
        return properties;
    }

    public MetricType getType() {
        return type;
    }

    public static Creator<Metric> CREATOR = new Creator<Metric>() {
        @Override
        public Metric createFromParcel(Parcel parcel) {
            return null;
        }

        @Override
        public Metric[] newArray(int i) {
            return new Metric[0];
        }
    };

    private Metric(Parcel parcel) {
        this.id = parcel.readString();
        this.properties = parcel.readParcelable(Properties.class.getClassLoader());
        this.type = parcel.readParcelable(MetricType.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeParcelable(properties, flags);
        parcel.writeParcelable(type, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
