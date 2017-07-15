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

/**
 * Created by pallavi on 14/07/17.
 */

public class MetricInfo implements Parcelable{
    private String id;
    private String name;

    public MetricInfo(String id, String name, String metricTypePath) {
        this.id = id;
        this.name = name;
        this.metricTypePath = metricTypePath;
    }

    private String metricTypePath;

    public MetricInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        metricTypePath = in.readString();
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetricTypePath() {
        return metricTypePath;
    }

    public void setMetricTypePath(String metricTypePath) {
        this.metricTypePath = metricTypePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(metricTypePath);
    }
    public static final Creator<MetricInfo> CREATOR = new Creator<MetricInfo>() {
        @Override
        public MetricInfo createFromParcel(Parcel in) {
            return new MetricInfo(in);
        }

        @Override
        public MetricInfo[] newArray(int size) {
            return new MetricInfo[size];
        }
    };

}
