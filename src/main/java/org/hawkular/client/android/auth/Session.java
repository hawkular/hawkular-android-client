
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

import java.util.Date;

import org.jboss.aerogear.android.core.RecordId;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is a wrapper for various bits of authorization metadata.
 */
public class Session implements Parcelable {

    @RecordId
    private String accountId = "";
    private String key = "";
    private String secret = "";
    private String url = "";
    private long expires_on = 0;

    private Session(Parcel in) {
        key = in.readString();
        secret = in.readString();
        url = in.readString();
        accountId = in.readString();
        expires_on = in.readLong();
    }

    public Session() {
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getUrl() {
        return url;
    }

    public long getExpires_on() {
        return expires_on;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void setExpires_on(long expires_on) {
        this.expires_on = expires_on;
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
        dest.writeString(key);
        dest.writeString(secret);
        dest.writeString(url);
        dest.writeString(accountId);
        dest.writeLong(expires_on);
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

    public boolean tokenIsNotExpired() {
        if (expires_on == 0) {
            return true;
        }
        return (expires_on > new Date().getTime());
    }

}