/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
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
package org.hawkular.client.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import info.metadude.android.typedpreferences.IntPreference;
import info.metadude.android.typedpreferences.StringPreference;

public final class Preferences {
    private static final class Locations {
        private Locations() {
        }

        public static final String BACKEND = "backend";
    }

    public static final class Keys {
        private Keys() {
        }

        public static final String BACKEND_HOST = "host";
        public static final String BACKEND_PORT = "port";
        public static final String BACKEND_PERSONA_ID = "persona-id";
        public static final String BACKEND_PERSONA_NAME = "persona-name";
        public static final String BACKEND_ENVIRONMENT = "environment";
    }

    public static final class Defaults {
        private Defaults() {
        }

        public static final int BACKEND_PORT = Integer.MIN_VALUE;
    }

    private final SharedPreferences serverPreferences;

    @NonNull
    public static Preferences of(@NonNull Context context) {
        return new Preferences(context, Locations.BACKEND);
    }

    private Preferences(Context context, String preferencesLocation) {
        this.serverPreferences = context.getSharedPreferences(preferencesLocation, Context.MODE_PRIVATE);
    }

    @NonNull
    public StringPreference host() {
        return new StringPreference(serverPreferences, Keys.BACKEND_HOST);
    }

    @NonNull
    public IntPreference port() {
        return new IntPreference(serverPreferences, Keys.BACKEND_PORT, Defaults.BACKEND_PORT);
    }

    @NonNull
    public StringPreference personaId() {
        return new StringPreference(serverPreferences, Keys.BACKEND_PERSONA_ID);
    }

    @NonNull
    public StringPreference personaName() {
        return new StringPreference(serverPreferences, Keys.BACKEND_PERSONA_NAME);
    }

    @NonNull
    public StringPreference environment() {
        return new StringPreference(serverPreferences, Keys.BACKEND_ENVIRONMENT);
    }
}
