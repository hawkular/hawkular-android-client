package org.hawkular.client.android.event;

import android.support.annotation.NonNull;

import com.squareup.otto.Bus;

public final class Events {
    private static final class EventsBusHolder {
        public static final Bus BUS = new Bus();

        private EventsBusHolder() {
        }
    }

    private Events() {
    }

    @NonNull
    public static Bus getBus() {
        return EventsBusHolder.BUS;
    }
}
