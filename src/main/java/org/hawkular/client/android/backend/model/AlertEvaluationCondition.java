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

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public final class AlertEvaluationCondition implements Parcelable {
    @SerializedName("threshold")
    private double threshold;

    @SerializedName("type")
    private AlertType type;

    @VisibleForTesting
    public AlertEvaluationCondition(double threshold, @NonNull AlertType type) {
        this.threshold = threshold;
        this.type = type;
    }

    public double getThreshold() {
        return threshold;
    }

    public AlertType getType() {
        return type;
    }

    public static Creator<AlertEvaluationCondition> CREATOR = new Creator<AlertEvaluationCondition>() {
        @Override
        public AlertEvaluationCondition createFromParcel(Parcel parcel) {
            return new AlertEvaluationCondition(parcel);
        }

        @Override
        public AlertEvaluationCondition[] newArray(int size) {
            return new AlertEvaluationCondition[size];
        }
    };

    private AlertEvaluationCondition(Parcel parcel) {
        this.threshold = parcel.readDouble();
        this.type = parcel.readParcelable(AlertType.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(threshold);
        parcel.writeParcelable(type, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}