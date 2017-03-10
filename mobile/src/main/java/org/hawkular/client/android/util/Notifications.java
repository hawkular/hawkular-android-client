/**
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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.hawkular.client.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

/**
 * Notification utilities.
 *
 * Provides an ability to show notifications without any external configuration.
 */
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

    @NonNull
    public static Notifications of(@NonNull Context context) {
        return new Notifications(context);
    }

    private Notifications(Context context) {
        this.context = context.getApplicationContext();
    }

    public void sendAlertNotification() {
        String notificationTitle = context.getString(R.string.application_name);
        String notificationText = context.getString(R.string.notification_alert);

        getNotificationManager().notify(
            Ids.ALERT, getAlertNotification(notificationTitle, notificationText, new Date()));
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build();
    }
}
