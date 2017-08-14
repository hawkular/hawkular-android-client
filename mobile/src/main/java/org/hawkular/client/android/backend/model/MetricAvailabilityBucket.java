/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MetricAvailabilityBucket implements Serializable,Parcelable {

    @SerializedName("start")
    protected long start;

    @SerializedName("end")
    protected long end;

    @SerializedName("empty")
    protected boolean empty;

    @SerializedName("uptimeRatio")
    protected String uptimeRatio;

    protected MetricAvailabilityBucket(Parcel in) {
        start = in.readLong();
        end = in.readLong();
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
        return start;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.start = startTimestamp;
    }

    public long getEnd() {
        return end;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.end = endTimestamp;
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
        dest.writeLong(start);
        dest.writeLong(end);
        dest.writeByte((byte) (empty ? 1 : 0));
        dest.writeString(uptimeRatio);
    }
}