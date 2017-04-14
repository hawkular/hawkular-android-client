/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
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

import android.support.annotation.IntRange;

/**
 * Port utilities.
 *
 * Provides port-related constants and checks a port for correctness.
 */
public final class Ports {
    private Ports() {
    }

    public static final int MINIMUM = 0;
    public static final int MAXIMUM = 0xFFFF;

    public static boolean isCorrect(@IntRange(from = Ports.MINIMUM, to = Ports.MAXIMUM) int port) {
        return (port >= MINIMUM) && (port <= MAXIMUM);
    }
}
