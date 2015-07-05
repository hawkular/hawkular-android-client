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
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

public final class Formatter {
    private Formatter() {
    }

    @NonNull
    public static String formatTime(@NonNull Context context, @IntRange(from = 0) long millis) {
        return DateUtils.formatDateTime(context, millis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
    }
}
