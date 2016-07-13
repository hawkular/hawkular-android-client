
/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
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

package org.hawkular.client.android.auth;

import org.jboss.aerogear.android.core.RecordId;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is a wrapper for various bits of authorization metadata.
 */
public class Session implements Parcelable {

    @RecordId
    private String accountId = "";
    private String username = "";
    private String password = "";
    private String url = "";

    private Session(Parcel in) {
        username = in.readString();
        password = in.readString();
        url = in.readString();
        accountId = in.readString();
    }

    public Session() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * AccountId represents the ID of the account type used to fetch sessions
     * for the type
     *
     * @return the current account type.
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * AccountId represents the ID of the account type used to fetch sessions
     * for the type
     *
     * @param accountId an accountId
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(url);
        dest.writeString(accountId);
    }

    public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }

    };


}