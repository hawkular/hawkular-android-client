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

import java.util.Map;

import org.jboss.aerogear.android.core.RecordId;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public class Trigger implements Parcelable {
    @RecordId
    @SerializedName("id")
    private String id;
    @SerializedName("tags")
    private Map<String, String> tags;
    @SerializedName("description")
    private String description;
    @SerializedName("enabled")
    private boolean enabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public boolean getEnableStatus(){
        return enabled;
    }

    public void setEnabledStatus(boolean enabled) {
        this.enabled=enabled;
    }

    @VisibleForTesting
    public Trigger(@NonNull String id, @NonNull Map<String, String> tags, @NonNull String description, @NonNull
                   boolean enabled) {
        this.id = id;
        this.tags = tags;
        this.description = description;
        this.enabled = enabled;
    }

    public static Creator<Trigger> CREATOR = new Creator<Trigger>() {
        @Override
        public Trigger createFromParcel(Parcel parcel) {
            return new Trigger(parcel);
        }

        @Override
        public Trigger[] newArray(int size) {
            return new Trigger[size];
        }
    };

    protected Trigger(Parcel parcel) {
        this.id = parcel.readString();
        this.description = parcel.readString();
        this.tags = parcel.readHashMap(String.class.getClassLoader());
        this.enabled = parcel.readString().equals("true");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(description);
        parcel.writeMap(tags);
        parcel.writeString(enabled ? "true" : "false");
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
