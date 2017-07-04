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
import android.support.annotation.VisibleForTesting;

public class FullTrigger implements Parcelable {

    @RecordId
    @SerializedName("id")
    private String id;
    @SerializedName("tags")
    private Map<String, String> tags;
    @SerializedName("description")
    private String description;
    @SerializedName("enabled")
    private boolean enabled;
    @SerializedName("type")
    private String type;
    @SerializedName("eventtype")
    private String eventType;
    @SerializedName("severity")
    private String severity;
    @SerializedName("autoDisable")
    private boolean autoDisable;
    @SerializedName("autoEnable")
    private boolean autoEnable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isAutoDisable() {
        return autoDisable;
    }

    public void setAutoDisable(boolean autoDisable) {
        this.autoDisable = autoDisable;
    }

    public boolean isAutoEnable() {
        return autoEnable;
    }

    public void setAutoEnable(boolean autoEnable) {
        this.autoEnable = autoEnable;
    }

    @VisibleForTesting
    public FullTrigger(String id, Map<String, String> tags, String description, boolean enabled, String severity) {
        this.id = id;
        this.tags = tags;
        this.description = description;
        this.enabled = enabled;
        this.severity = severity;
    }

    public static Creator<FullTrigger> CREATOR = new Creator<FullTrigger>() {
        @Override
        public FullTrigger createFromParcel(Parcel parcel) {
            return new FullTrigger(parcel);
        }

        @Override
        public FullTrigger[] newArray(int size) {
            return new FullTrigger[size];
        }
    };

    protected FullTrigger(Parcel parcel) {
        this.id = parcel.readString();
        this.description = parcel.readString();
        this.tags = parcel.readHashMap(String.class.getClassLoader());
        this.type = parcel.readString();
        this.eventType = parcel.readString();
        this.autoEnable = Boolean.getBoolean(parcel.readString());
        this.autoDisable = Boolean.getBoolean(parcel.readString());
        this.enabled = parcel.readString().equals("true");
        this.severity = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(description);
        parcel.writeMap(tags);
        parcel.writeString(type);
        parcel.writeString(eventType);
        parcel.writeString(severity);
        parcel.writeString(autoDisable ? "true" : "false");
        parcel.writeString(autoEnable ? "true" : "false");
        parcel.writeString(enabled ? "true" : "false");
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
