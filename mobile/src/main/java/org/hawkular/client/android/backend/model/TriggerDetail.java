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

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class TriggerDetail extends Trigger implements Parcelable {

    @SerializedName("autoResolve")
    private boolean autoResolve;

    @SerializedName("autoEnable")
    private boolean autoEnable;

    @SerializedName("autoDisable")
    private boolean autoDisable;

    public boolean isAutoResolve() {
        return autoResolve;
    }

    public boolean isAutoEnable() {
        return autoEnable;
    }

    public boolean isAutoDisable() {
        return autoDisable;
    }

    public static Creator<TriggerDetail> CREATOR = new Creator<TriggerDetail>() {
        @Override
        public TriggerDetail createFromParcel(Parcel parcel) {
            return new TriggerDetail(parcel);
        }

        @Override
        public TriggerDetail[] newArray(int size) {
            return new TriggerDetail[size];
        }
    };

    protected TriggerDetail(Parcel parcel) {
        super(parcel);
        this.autoResolve = Boolean.getBoolean(parcel.readString());
        this.autoEnable = Boolean.getBoolean(parcel.readString());
        this.autoDisable = Boolean.getBoolean(parcel.readString());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(Boolean.toString(this.autoResolve));
        parcel.writeString(Boolean.toString(this.autoEnable));
        parcel.writeString(Boolean.toString(this.autoDisable));
    }

}
