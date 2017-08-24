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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pallavi on 19/08/17.
 */

public class ThresholdCondition extends Condition implements Parcelable {

    @SerializedName("operator")
    private String operator;

    @SerializedName("threshold")
    private int threshold;



    public ThresholdCondition(Parcel in) {
        operator = in.readString();
        type = in.readString();
        dataId = in.readString();
        threshold = in.readInt();
    }

    public ThresholdCondition() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operator);
        dest.writeString(type);
        dest.writeString(dataId);
        dest.writeInt(threshold);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ThresholdCondition> CREATOR = new Creator<ThresholdCondition>() {
        @Override
        public ThresholdCondition createFromParcel(Parcel in) {
            return new ThresholdCondition(in);
        }

        @Override
        public ThresholdCondition[] newArray(int size) {
            return new ThresholdCondition[size];
        }
    };

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }


}
