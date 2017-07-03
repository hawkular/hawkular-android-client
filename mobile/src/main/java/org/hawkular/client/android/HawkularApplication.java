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
package org.hawkular.client.android;

import java.io.IOException;

import org.hawkular.client.android.push.PushClient;
import org.hawkular.client.android.util.Android;

import android.app.Application;
import android.os.StrictMode;
import android.util.Base64;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

public final class HawkularApplication extends Application {

    public static Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        setUpLogging();
        setUpDetections();

        setUpPush();

    }

    public static void setUpRetrofit(String url, final String username, final String password) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                String cred = new String(Base64.encode((username + ":" +password).getBytes(), Base64.NO_WRAP));

                Request request = original.newBuilder()
                        .header("Hawkular-Tenant", "hawkular")
                        .header("Authorization", "Basic " + cred)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .build();
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

    private void setUpPush() {
        PushClient.of(this).setUpPush();
    }
}
