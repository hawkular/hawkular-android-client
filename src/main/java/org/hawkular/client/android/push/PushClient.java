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
        if (!isPushServerUriCorrect()) {
            return;
        }

        RegistrarManager.config(PushConfiguration.NAME, AeroGearGCMPushConfiguration.class)
            .setPushServerURI(Uris.getUri(PushConfiguration.Ups.URL))
            .setSecret(PushConfiguration.Ups.SECRET)
            .setVariantID(PushConfiguration.Ups.VARIANT)
            .setSenderIds(PushConfiguration.Gcm.SENDER)
            .asRegistrar();

        RegistrarManager.getRegistrar(PushConfiguration.NAME).register(context, this);
    }

    private boolean isPushServerUriCorrect() {
        try {
            Uris.getUri(PushConfiguration.Ups.URL);

            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public void onSuccess(Void ignored) {
        Timber.d("Push registration succeed.");
    }

    @Override
    public void onFailure(Exception e) {
        Timber.d(e, "Push registration failed.");
    }
}
