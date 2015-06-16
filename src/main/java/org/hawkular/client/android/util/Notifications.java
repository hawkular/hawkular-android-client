package org.hawkular.client.android.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import org.hawkular.client.android.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class Notifications {
    private static final class Ids {
        private Ids() {
        }

        public static final int ALERT = 42;
    }

    private static final class Led {
        private Led() {
        }

        public static final int DURATION_ON = (int) TimeUnit.SECONDS.toMillis(1);
        public static final int DURATION_OFF = (int) TimeUnit.SECONDS.toMillis(10);
    }

    private final Context context;

    public static Notifications of(@NonNull Context context) {
        return new Notifications(context);
    }

    private Notifications(Context context) {
        this.context = context.getApplicationContext();
    }

    public void sendAlertNotification() {
        getNotificationManager().notify(Ids.ALERT, getAlertNotification(
            "Hawkular", "Some metric had not a great mood lately.", new Date()));
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification getAlertNotification(String alertTitle, String alertText, Date alertTime) {
        int notificationColor = context.getResources().getColor(R.color.background_primary);

        return new NotificationCompat.Builder(context)
            .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
            .setContentTitle(alertTitle)
            .setContentText(alertText)
            .setWhen(alertTime.getTime())
            .setShowWhen(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(notificationColor)
            .setLights(notificationColor, Led.DURATION_ON, Led.DURATION_OFF)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build();
    }
}
