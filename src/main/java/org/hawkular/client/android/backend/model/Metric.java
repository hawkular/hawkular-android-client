/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
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

public final class Metric implements Parcelable {
    @SerializedName("id")
    private String id;

    @SerializedName("properties")
    private MetricProperties properties;

    public String getId() {
        return id;
    }

    public MetricProperties getProperties() {
        return properties;
    }

    public static Creator<Metric> CREATOR = new Creator<Metric>() {
        @Override
        public Metric createFromParcel(Parcel parcel) {
            return new Metric(parcel);
        }

        @Override
        public Metric[] newArray(int size) {
            return new Metric[size];
        }
    };

    private Metric(Parcel parcel) {
        this.id = parcel.readString();
        this.properties = parcel.readParcelable(MetricProperties.class.getClassLoader());
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
