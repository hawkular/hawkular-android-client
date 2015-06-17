package org.hawkular.client.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

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
        public static final String BACKEND_TENANT = "tenant";
    }

    private SharedPreferences serverPreferences;

    public static Preferences ofBackend(@NonNull Context context) {
        return new Preferences(context, Locations.BACKEND);
    }

    private Preferences(Context context, String preferencesLocation) {
        this.serverPreferences = context.getSharedPreferences(preferencesLocation, Context.MODE_PRIVATE);
    }

    public StringPreference host() {
        return new StringPreference(serverPreferences, Keys.BACKEND_HOST);
    }

    public StringPreference port() {
        return new StringPreference(serverPreferences, Keys.BACKEND_PORT);
    }

    public StringPreference tenant() {
        return new StringPreference(serverPreferences, Keys.BACKEND_TENANT);
    }
}
