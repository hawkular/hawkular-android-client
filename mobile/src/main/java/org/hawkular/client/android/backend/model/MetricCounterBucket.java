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

public class MetricCounterBucket extends MetricBucket implements Parcelable {

    public static final Creator<MetricCounterBucket> CREATOR = new Creator<MetricCounterBucket>() {
        @Override
        public MetricCounterBucket createFromParcel(Parcel in) {
            return new MetricCounterBucket(in);
        }

        @Override
        public MetricCounterBucket[] newArray(int size) {
            return new MetricCounterBucket[size];
        }
    };
    @SerializedName("avg")
    private String value;

    protected MetricCounterBucket(Parcel in) {
        value = in.readString();
        empty = in.readString().equals("true");
        startTimestamp = in.readLong();
        endTimestamp = in.readLong();
    }

    public String getValue() {
        return value;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeString(empty ? "true" : "false");
        dest.writeLong(startTimestamp);
        dest.writeLong(endTimestamp);
    }
}
