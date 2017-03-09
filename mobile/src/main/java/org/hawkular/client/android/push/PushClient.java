/*
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
package org.hawkular.client.android.push;

import java.util.Arrays;
import java.util.List;

import org.hawkular.client.android.util.Uris;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.fcm.AeroGearFCMPushConfiguration;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import timber.log.Timber;

/**
 * Push client.
 *
 * Serves as a configurator for push notifications. To use it properly it is necessary
 * to call {@link #setUpPush()} method on the application startup. The registration will succeed
 * only if {@link org.hawkular.client.android.push.PushConfiguration} has correct values.
 */
public final class PushClient implements Callback<Void> {
    private final Context context;

    @NonNull
    @RequiresPermission(allOf = {Manifest.permission.INTERNET, Manifest.permission.WAKE_LOCK})
    public static PushClient of(@NonNull Context context) {
        return new PushClient(context);
    }

    private PushClient(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setUpPush() {
        if (!isPushAvailable()) {
            return;
        }

        FirebaseApp.initializeApp(this.context, new FirebaseOptions.Builder()
                .setApiKey(PushConfiguration.Fcm.API_KEY)
                .setApplicationId(PushConfiguration.Fcm.APPLICATION_ID)
                .setDatabaseUrl(PushConfiguration.Fcm.DATABASE_URL)
                .setGcmSenderId(PushConfiguration.Fcm.SENDER)
                .setStorageBucket(PushConfiguration.Fcm.STORAGE_BUCKET).build());

        RegistrarManager.config(PushConfiguration.NAME, AeroGearFCMPushConfiguration.class)
            .setPushServerURI(Uris.getUriFromString(PushConfiguration.Ups.URL))
            .setSecret(PushConfiguration.Ups.SECRET)
            .setVariantID(PushConfiguration.Ups.VARIANT)
            .setSenderId(PushConfiguration.Fcm.SENDER)
            .asRegistrar();

        RegistrarManager.getRegistrar(PushConfiguration.NAME).register(context, this);
    }

    private boolean isPushAvailable() {
        List<String> pushConfigurationFields = Arrays.asList(
            PushConfiguration.Ups.URL,
            PushConfiguration.Ups.SECRET,
            PushConfiguration.Ups.VARIANT,
            PushConfiguration.Fcm.SENDER);

        for (String pushConfigurationField : pushConfigurationFields) {
            if (pushConfigurationField.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void onSuccess(Void ignored) {
        Timber.d("Push registration succeed.");
    }

    @Override
    public void onFailure(Exception e) {
        Timber.d(e, "Push registration failed.");
    }
}
