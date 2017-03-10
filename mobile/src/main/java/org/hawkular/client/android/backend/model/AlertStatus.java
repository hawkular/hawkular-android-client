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

public enum AlertStatus implements Parcelable {
    @SerializedName("OPEN")
    OPEN,

    @SerializedName("ACKNOWLEDGED")
    ACKNOWLEDGED,

    @SerializedName("RESOLVED")
    RESOLVED;

    public static Creator<AlertStatus> CREATOR = new Creator<AlertStatus>() {
        @Override
        public AlertStatus createFromParcel(Parcel parcel) {
            return AlertStatus.valueOf(parcel.readString());
        }

        @Override
        public AlertStatus[] newArray(int size) {
            return new AlertStatus[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
