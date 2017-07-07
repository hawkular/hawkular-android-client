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

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

public class Resource implements Parcelable
{

    private String id;
    private List<Data> data = null;
    public final static Creator<Resource> CREATOR = new Creator<Resource>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Resource createFromParcel(Parcel in) {
            Resource instance = new Resource();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.data, (Data.class.getClassLoader()));
            return instance;
        }

        public Resource[] newArray(int size) {
            return (new Resource[size]);
        }

    }
    ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(data);
    }

    public int describeContents() {
        return  0;
    }

}
