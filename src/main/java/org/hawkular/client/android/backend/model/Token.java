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
package org.hawkular.client.android.backend.model;

import org.jboss.aerogear.android.core.RecordId;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public final class Token implements Parcelable {
    public static Creator<Token> CREATOR = new Creator<Token>() {
        @Override public Token createFromParcel(Parcel parcel) {
            return new Token(parcel);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };
    @RecordId
    @SerializedName("persona")
    private String persona;
    @SerializedName("key")
    private String key;
    @SerializedName("secret")
    private String secret;

    public Token(@NonNull String persona, @NonNull String key, @NonNull String secret) {
        this.persona = persona;
        this.key = key;
        this.secret = secret;
    }

    private Token(Parcel parcel) {
        this.persona = parcel.readString();
        this.key = parcel.readString();
        this.secret = parcel.readString();
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(persona);
        parcel.writeString(key);
        parcel.writeString(secret);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
