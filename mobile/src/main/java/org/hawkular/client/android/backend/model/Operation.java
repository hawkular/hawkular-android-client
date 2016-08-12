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

import org.jboss.aerogear.android.core.RecordId;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class Operation implements Parcelable {

    @RecordId
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("path")
    private String path;

    @SerializedName("properties")
    private OperationProperties operationProperties;

    public Operation(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public OperationProperties getOperationProperties() {
        return operationProperties;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Creator<Operation> CREATOR = new Creator<Operation>() {
        @Override
        public Operation createFromParcel(Parcel parcel) {
            return new Operation(parcel);
        }

        @Override
        public Operation[] newArray(int size) {
            return new Operation[size];
        }
    };

    private Operation(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.path = parcel.readString();
        this.operationProperties = parcel.readParcelable(OperationProperties.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeParcelable(operationProperties, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
