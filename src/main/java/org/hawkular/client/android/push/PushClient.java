package org.hawkular.client.android.push;

import android.content.Context;
import android.support.annotation.NonNull;

import org.hawkular.client.android.util.Uris;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.gcm.AeroGearGCMPushConfiguration;

import timber.log.Timber;

public final class PushClient implements Callback<Void> {
    private final Context context;

    public static PushClient of(@NonNull Context context) {
        return new PushClient(context);
    }

    private PushClient(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setUpPush() {
        RegistrarManager.config(PushConfiguration.NAME, AeroGearGCMPushConfiguration.class)
            .setPushServerURI(Uris.getUri(PushConfiguration.Ups.URL))
            .setSecret(PushConfiguration.Ups.SECRET)
            .setVariantID(PushConfiguration.Ups.VARIANT)
            .setSenderIds(PushConfiguration.Gcm.SENDER)
            .asRegistrar();

        RegistrarManager.getRegistrar(PushConfiguration.NAME).register(context, this);
    }

    public void onSuccess(Void ignored) {
        Timber.d("Push registration succeed.");
    }

    @Override
    public void onFailure(Exception e) {
        Timber.d(e, "Push registration failed.");
    }
}
