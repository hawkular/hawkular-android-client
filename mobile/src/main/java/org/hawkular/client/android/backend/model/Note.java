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


import org.jboss.aerogear.android.core.RecordId;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public final class Note implements Parcelable {

    @RecordId
    @SerializedName("alertid")
    private String alertid;

    @SerializedName("user")
    private String user;

    @SerializedName("text")
    private String message;

    @SerializedName("ctime")
    private long timestamp;

    @VisibleForTesting
    public Note(@NonNull String user, @NonNull long timestamp, @NonNull String message) {
        this.user = user;
        this.timestamp = timestamp;
        this.message = message;
    }


    public Note(String alertid, String user, String message, long timestamp) {
        this.alertid = alertid;
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getAlertid() {
        return alertid;
    }

    public void setAlertid(String alertid) {
        this.alertid = alertid;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel parcel) {
            return new Note(parcel);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private Note(Parcel parcel) {
        this.alertid = parcel.readString();
        this.user = parcel.readString();
        this.timestamp = parcel.readLong();
        this.message = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(alertid);
        parcel.writeString(user);
        parcel.writeLong(timestamp);
        parcel.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}