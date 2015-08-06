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

import java.util.List;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public final class Alert implements Parcelable {
    @SerializedName("alertId")
    private String id;

    @SerializedName("ctime")
    private long timestamp;

    @SerializedName("evalSets")
    private List<List<AlertEvaluation>> evaluations;

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<List<AlertEvaluation>> getEvaluations() {
        return evaluations;
    }

    public static Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel parcel) {
            return new Alert(parcel);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };

    private Alert(Parcel parcel) {
        this.id = parcel.readString();
        this.timestamp = parcel.readLong();

        parcel.readList(evaluations, List.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeLong(timestamp);

        parcel.writeList(evaluations);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}