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

import java.util.ArrayList;
import java.util.List;

import org.jboss.aerogear.android.core.RecordId;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public final class Alert implements Parcelable {
    @RecordId
    @SerializedName("id")
    private String id;

    @SerializedName("severity")
    private String severity;

    @SerializedName("status")
    private String status;

    @SerializedName("ctime")
    private long timestamp;

    @SerializedName("evalSets")
    private List<Lister> evaluations;

    @SerializedName("notes")
    private List<Note> notes;

    @SerializedName("trigger")
    private Trigger trigger;

    @VisibleForTesting
    public Alert(@NonNull String id, long timestamp, @NonNull List<Lister> evaluations, @NonNull String severity,
                 @NonNull String status, @NonNull List<Note> notes) {
        this.id = id;
        this.timestamp = timestamp;
        this.evaluations = evaluations;
        this.severity = severity;
        this.status = status;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Lister> getEvaluations() {
        return evaluations;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public static Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel parcel) {
            return new Alert(parcel);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };

    private Alert(Parcel parcel) {
        this.id = parcel.readString();
        this.severity = parcel.readString();
        this.status = parcel.readString();
        this.timestamp = parcel.readLong();

        evaluations = new ArrayList<>();
        notes = new ArrayList<>();

        parcel.readList(evaluations, Lister.class.getClassLoader());
        parcel.readList(notes, Note.class.getClassLoader());

        this.trigger = parcel.readParcelable(Trigger.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(severity);
        parcel.writeString(status);
        parcel.writeLong(timestamp);

        parcel.writeList(evaluations);
        parcel.writeList(notes);

        parcel.writeParcelable(trigger, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static class Lister extends ArrayList<AlertEvaluation> implements Parcelable {

        protected Lister(Parcel in) {
            this.addAll(in.readArrayList(AlertEvaluation.class.getClassLoader()));
        }

        public static final Creator<Lister> CREATOR = new Creator<Lister>() {
            @Override
            public Lister createFromParcel(Parcel in) {
                return new Lister(in);
            }

            @Override
            public Lister[] newArray(int size) {
                return new Lister[size];
            }
        };

        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeList(this);
        }
    }

}

