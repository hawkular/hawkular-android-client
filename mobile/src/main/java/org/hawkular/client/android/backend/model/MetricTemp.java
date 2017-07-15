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

public class MetricTemp implements Parcelable{

    MetricInfo metricInfo;

    public MetricTemp(MetricInfo metricInfo) {
        this.metricInfo = metricInfo;
    }

    protected MetricTemp(Parcel in) {
        metricInfo = in.readParcelable(MetricInfo.class.getClassLoader());
    }

    public static final Creator<MetricTemp> CREATOR = new Creator<MetricTemp>() {
        @Override
        public MetricTemp createFromParcel(Parcel in) {
            return new MetricTemp(in);
        }

        @Override
        public MetricTemp[] newArray(int size) {
            return new MetricTemp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(metricInfo, flags);
    }

    public MetricInfo getMetricInfo() {
        return metricInfo;
    }

    public void setMetricInfo(MetricInfo metricInfo) {
        this.metricInfo = metricInfo;
    }

    public static Creator<MetricTemp> getCREATOR() {
        return CREATOR;
    }
}

