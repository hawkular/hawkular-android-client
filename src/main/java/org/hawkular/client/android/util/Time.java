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

import java.util.Calendar;
import java.util.Date;

import android.support.annotation.NonNull;

public final class Time {
    private Time() {
    }

    @NonNull
    public static Date current() {
        return Calendar.getInstance().getTime();
    }

    @NonNull
    public static Date hourAgo() {
        return timeUnitAgo(Calendar.HOUR_OF_DAY);
    }

    @NonNull
    public static Date dayAgo() {
        return timeUnitAgo(Calendar.DAY_OF_YEAR);
    }

    @NonNull
    public static Date weekAgo() {
        return timeUnitAgo(Calendar.WEEK_OF_YEAR);
    }

    @NonNull
    public static Date monthAgo() {
        return timeUnitAgo(Calendar.MONTH);
    }

    @NonNull
    public static Date yearAgo() {
        return timeUnitAgo(Calendar.YEAR);
    }

    private static Date timeUnitAgo(int timeUnit) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(timeUnit, -1);

        return calendar.getTime();
    }
}
