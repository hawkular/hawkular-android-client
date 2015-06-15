package org.hawkular.client.android.backend;

import android.content.Context;
import android.os.Bundle;

import org.jboss.aerogear.android.unifiedpush.MessageHandler;

import timber.log.Timber;

public final class BackendPushHandler implements MessageHandler
{
    @Override
    public void onMessage(Context context, Bundle bundle) {
        Timber.d("Push :: Message: %s.", bundle.getString("alert"));
    }

    @Override
    public void onError() {
        Timber.d("Push :: Error...");
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {
        Timber.d("Push :: Delete.");
    }
}
