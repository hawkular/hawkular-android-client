package org.hawkular.client.android.backend.model;

import android.support.annotation.NonNull;

public enum MetricAvailability {
    UP(Names.UP),
    DOWN(Names.DOWN),
    UNKNOWN(Names.UNKNOWN);

    private static final class Names {
        private Names() {
        }

        public static final String UP = "up";
        public static final String DOWN = "down";
        public static final String UNKNOWN = "unknown";
    }

    private final String name;

    MetricAvailability(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NonNull
    public static MetricAvailability from(@NonNull String name) {
        switch (name.toLowerCase()) {
            case Names.UP:
                return MetricAvailability.UP;

            case Names.DOWN:
                return MetricAvailability.DOWN;

            default:
                return MetricAvailability.UNKNOWN;
        }
    }
}
