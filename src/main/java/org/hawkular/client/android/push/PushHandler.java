package org.hawkular.client.android.push;

import android.content.Context;
import android.os.Bundle;

import org.hawkular.client.android.util.Notifications;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;

import timber.log.Timber;

public final class PushHandler implements MessageHandler
{
    private static final class MessageFields {
        private MessageFields() {
        }

        public static final String TEXT = "alert";
    }

    @Override
    public void onMessage(Context context, Bundle message) {
        Timber.d("Push notification delivered a message: %s", message.getString(MessageFields.TEXT));

        Notifications.of(context).sendAlertNotification();
    }

    @Override
    public void onError() {
        Timber.d("Push notification delivered an error.");
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {
        Timber.d("Push notification was deleted.");
    }
}
