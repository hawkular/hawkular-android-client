package org.hawkular.client.android.util;

import android.support.annotation.IntRange;

public final class Ports {
    private Ports() {
    }

    public static final int MINIMUM = 0;
    public static final int MAXIMUM = 0xFFFF;

    public static boolean isCorrect(@IntRange(from = Ports.MINIMUM, to = Ports.MAXIMUM) int port) {
        return (port >= MINIMUM) && (port <= MAXIMUM);
    }
}
