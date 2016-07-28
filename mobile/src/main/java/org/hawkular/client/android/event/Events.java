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
package org.hawkular.client.android.event;

import com.squareup.otto.Bus;

import android.support.annotation.NonNull;

/**
 * Events handler.
 *
 * Basically handles a {@link com.squareup.otto.Bus} reference.
 *
 * Use it carefully as a callbacks replacement for application components,
 * such as {@link android.app.Activity}, {@link android.app.Fragment},
 * {@link android.app.Service} and {@link android.content.BroadcastReceiver}.
 */
public final class Events {
    private Events() {
    }

    private static final class EventsBusHolder {
        private EventsBusHolder() {
        }

        public static final Bus BUS = new Bus();
    }

    @NonNull
    public static Bus getBus() {
        return EventsBusHolder.BUS;
    }
}
