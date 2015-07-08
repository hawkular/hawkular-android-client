package org.hawkular.client.android.util;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public final class Time {
    private Time() {
    }

    @NonNull
    public static Date current() {
        return Calendar.getInstance().getTime();
    }

    @NonNull
    public static Date hourAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        return calendar.getTime();
    }

    @NonNull
    public static Date dayAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        return calendar.getTime();
    }

    @NonNull
    public static Date weekAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);

        return calendar.getTime();
    }

    @NonNull
    public static Date monthAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        return calendar.getTime();
    }

    @NonNull
    public static Date yearAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        return calendar.getTime();
    }
}
