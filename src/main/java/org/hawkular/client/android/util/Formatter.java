package org.hawkular.client.android.util;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

public final class Formatter {
    private Formatter() {
    }

    public static String formatTime(@NonNull Context context, @IntRange(from = 0) long millis) {
        return DateUtils.formatDateTime(context, millis, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
    }
}
