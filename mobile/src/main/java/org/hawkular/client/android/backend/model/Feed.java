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

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by anuj on 7/6/16.
 */
public class Feed implements Parcelable {

    @SerializedName("feed")
    private List<String> feed = null;
    @SerializedName("module")
    private List<String> module = null;

    public final static Parcelable.Creator<Feed> CREATOR = new Creator<Feed>() {

        @SuppressWarnings({"unchecked"})
        public Feed createFromParcel(Parcel in) {
            Feed instance = new Feed();
            in.readList(instance.feed, (java.lang.String.class.getClassLoader()));
            in.readList(instance.module, (java.lang.String.class.getClassLoader()));
            return instance;
        }

        public Feed[] newArray(int size) {
            return (new Feed[size]);
        }

    };

    public List<String> getFeed() {
        return feed;
    }

    public void setFeed(List<String> feed) {
        this.feed = feed;
    }

    public List<String> getModule() {
        return module;
    }

    public void setModule(List<String> module) {
        this.module = module;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(feed);
        dest.writeList(module);
    }

    public int describeContents() {
        return 0;
    }

}