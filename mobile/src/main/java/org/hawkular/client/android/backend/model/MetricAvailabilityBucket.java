
package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;

import java.io.Serializable;

public class MetricAvailabilityBucket implements Serializable,Parcelable {

    @SerializedName("start")
    protected long startTimestamp;

    @SerializedName("end")
    protected long endTimestamp;

    @SerializedName("empty")
    protected boolean empty;

    @SerializedName("uptimeRatio")
    protected String uptimeRatio;

    protected MetricAvailabilityBucket(Parcel in) {
        startTimestamp = in.readLong();
        endTimestamp = in.readLong();
        empty = in.readByte() != 0;
        uptimeRatio = in.readString();
    }

    public static final Creator<MetricAvailabilityBucket> CREATOR = new Creator<MetricAvailabilityBucket>() {
        @Override
        public MetricAvailabilityBucket createFromParcel(Parcel in) {
            return new MetricAvailabilityBucket(in);
        }

        @Override
        public MetricAvailabilityBucket[] newArray(int size) {
            return new MetricAvailabilityBucket[size];
        }
    };

    public long getStart() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEnd() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public boolean getEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public String getvalue() {
        return uptimeRatio;
    }

    public void setUptimeRatio(String uptimeRatio) {
        this.uptimeRatio = uptimeRatio;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startTimestamp);
        dest.writeLong(endTimestamp);
        dest.writeByte((byte) (empty ? 1 : 0));
        dest.writeString(uptimeRatio);
    }
}