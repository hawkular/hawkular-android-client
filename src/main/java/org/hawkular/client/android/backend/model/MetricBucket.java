/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
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

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class MetricBucket implements Parcelable {

    @SerializedName("avg")
    private String value;

    @SerializedName("start")
    private long startTimestamp;

    @SerializedName("end")
    private long endTimestamp;

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public String getValue() {
        return value;
    }

    protected MetricBucket(Parcel in) {
        value = in.readString();
        startTimestamp = in.readLong();
        endTimestamp = in.readLong();
    }

    public static final Creator<MetricBucket> CREATOR = new Creator<MetricBucket>() {
        @Override
        public MetricBucket createFromParcel(Parcel in) {
            return new MetricBucket(in);
        }

        @Override
        public MetricBucket[] newArray(int size) {
            return new MetricBucket[size];
        }
    };



    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeLong(startTimestamp);
        dest.writeLong(endTimestamp);
    }
}
