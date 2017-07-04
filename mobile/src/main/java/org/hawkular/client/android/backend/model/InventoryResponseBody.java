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
 * Created by pallavi on 02/07/17.
 */

public class InventoryResponseBody implements Parcelable {

    @SerializedName("fromEarliest")
    private String fromEarliest;

    @SerializedName("order")
    private String order;

    @SerializedName("tags")
    private String tags;



    public String getFromEarliest() {
        return fromEarliest;
    }

    public void setFromEarliest(String fromEarliest) {
        this.fromEarliest = fromEarliest;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public InventoryResponseBody(String fromEarliest, String order, String tags) {
        this.fromEarliest = fromEarliest;
        this.order = order;
        this.tags = tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromEarliest);
        dest.writeString(order);
        dest.writeString(tags);
    }

    protected InventoryResponseBody(Parcel in) {
        fromEarliest = in.readString();
        order = in.readString();
        tags = in.readString();
    }

    public static final Creator<InventoryResponseBody> CREATOR = new Creator<InventoryResponseBody>() {
        @Override
        public InventoryResponseBody createFromParcel(Parcel in) {
            return new InventoryResponseBody(in);
        }

        @Override
        public InventoryResponseBody[] newArray(int size) {
            return new InventoryResponseBody[size];
        }
    };
}
