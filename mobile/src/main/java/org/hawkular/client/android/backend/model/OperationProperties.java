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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


public class OperationProperties implements Parcelable {

    @SerializedName("value")
    private Map<String, OperationParameter> operationParameters;

    public Map<String, OperationParameter> getOperationParameters() {
        return operationParameters;
    }

    protected OperationProperties(Parcel parcel) {
        operationParameters = new HashMap<>();

        int size = parcel.readInt();
        for(int i = 0; i < size; i++){
            String key = parcel.readString();
            OperationParameter value = (OperationParameter) parcel.readValue(OperationParameter.class.getClassLoader());
            operationParameters.put(key,value);
        }
    }

    public static final Creator<OperationProperties> CREATOR = new Creator<OperationProperties>() {
        @Override
        public OperationProperties createFromParcel(Parcel in) {
            return new OperationProperties(in);
        }

        @Override
        public OperationProperties[] newArray(int size) {
            return new OperationProperties[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeInt(operationParameters.size());
        for(Map.Entry<String,OperationParameter> entry : operationParameters.entrySet()){
            parcel.writeString(entry.getKey());
            parcel.writeValue(entry.getValue());
        }
    }
}
