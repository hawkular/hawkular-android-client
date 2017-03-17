/**
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

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public final class AlertEvaluation implements Parcelable {
    @SerializedName("condition")
    private AlertEvaluationCondition condition;

    @SerializedName("value")
    private String value;

    @SerializedName("dataTimestamp")
    private long dataTimestamp;

    @VisibleForTesting
    public AlertEvaluation(@NonNull AlertEvaluationCondition condition, @NonNull String value, long dataTimestamp) {
        this.condition = condition;
        this.value = value;
        this.dataTimestamp = dataTimestamp;
    }

    public AlertEvaluationCondition getCondition() {
        return condition;
    }

    public String getValue() {
        return value;
    }

    public long getDataTimestamp() {
        return dataTimestamp;
    }

    public static Creator<AlertEvaluation> CREATOR = new Creator<AlertEvaluation>() {
        @Override
        public AlertEvaluation createFromParcel(Parcel parcel) {
            return new AlertEvaluation(parcel);
        }

        @Override
        public AlertEvaluation[] newArray(int size) {
            return new AlertEvaluation[size];
        }
    };

    private AlertEvaluation(Parcel parcel) {
        this.condition = parcel.readParcelable(AlertEvaluationCondition.class.getClassLoader());
        this.value = parcel.readString();
        this.dataTimestamp = parcel.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(condition, flags);
        parcel.writeString(value);
        parcel.writeLong(dataTimestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}