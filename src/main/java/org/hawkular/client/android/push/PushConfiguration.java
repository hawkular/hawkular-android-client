package org.hawkular.client.android.push;

import org.hawkular.client.android.BuildConfig;

final class PushConfiguration {
    private PushConfiguration() {
    }

    public static final String NAME = "Hawkular";

    public static final class Ups {
        private Ups() {
        }

        public static final String URL = BuildConfig.UPS_URL;
        public static final String SECRET = BuildConfig.UPS_SECRET;
        public static final String VARIANT = BuildConfig.UPS_VARIANT;
    }

    public static final class Gcm {
        private Gcm() {
        }

        public static final String SENDER = BuildConfig.GCM_SENDER;
    }
}
