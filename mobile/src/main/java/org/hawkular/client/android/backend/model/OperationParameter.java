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

public class OperationParameter implements Parcelable {

    @SerializedName("type")
    private String type;

    @SerializedName("description")
    private String description;

    @SerializedName("defaultValue")
    private String defaultValue;

    @SerializedName("required")
    private boolean required;

    public OperationParameter(Parcel parcel) {
        type = parcel.readString();
        description = parcel.readString();
        defaultValue = parcel.readString();
        required = parcel.readString().equals("true");
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isRequired() {
        return required;
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(description);
        parcel.writeString(defaultValue);
        parcel.writeString(required ? "true" : "false");
    }

    public static Creator<OperationParameter> CREATOR = new Creator<OperationParameter>() {
        @Override
        public OperationParameter createFromParcel(Parcel parcel) {
            return new OperationParameter(parcel);
        }

        @Override
        public OperationParameter[] newArray(int size) {
            return new OperationParameter[size];
        }
    };
}