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
package org.hawkular.client.android;

import android.app.Application;
import android.os.StrictMode;

import org.hawkular.client.android.backend.BackendPush;
import org.hawkular.client.android.util.Android;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.gcm.AeroGearGCMPushConfiguration;

import java.net.URI;
import java.net.URISyntaxException;

import timber.log.Timber;

public class HawkularApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        setUpLogging();
        setUpDetections();

        setUpPush();
    }

    private void setUpLogging() {
        if (Android.isDebugging()) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void setUpDetections() {
        if (Android.isDebugging()) {
            StrictMode.enableDefaults();
        }
    }

    public void setUpPush() {
        RegistrarManager.config(BackendPush.NAME, AeroGearGCMPushConfiguration.class)
            .setPushServerURI(getUri(BackendPush.Ups.URL))
            .setSecret(BackendPush.Ups.SECRET)
            .setVariantID(BackendPush.Ups.VARIANT)
            .setSenderIds(BackendPush.Gcm.SENDER)
            .asRegistrar();

        PushRegistrar registrar = RegistrarManager.getRegistrar(BackendPush.NAME);

        registrar.register(getApplicationContext(), new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Timber.d("Push :: Success!");
            }

            @Override
            public void onFailure(Exception e) {
                Timber.d("Push :: Failure...");
            }
        });
    }

    private URI getUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
