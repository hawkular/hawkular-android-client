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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * URI utilities.
 *
 * Handles {@link java.net.URI} creation from various components.
 */
public final class Uris {
    private Uris() {
    }

    private static final class Charsets {
        private Charsets() {
        }

        public static final String UTF_8 = "UTF-8";
    }

    @NonNull
    public static URI getUriFromString(@NonNull String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static URI getUri(@NonNull String path) {
        Uri uri = new Uri.Builder()
                .appendEncodedPath(path)
                .build();

        return Uris.getUriFromString(uri.toString());
    }

    @NonNull
    public static URI getUri(@NonNull String path, @NonNull Map<String, String> parameters) {
        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.appendEncodedPath(path);

        for (String parameterKey : parameters.keySet()) {
            String parameterValue = parameters.get(parameterKey);

            uriBuilder.appendQueryParameter(parameterKey, parameterValue);
        }

        Uri uri = uriBuilder.build();

        return Uris.getUriFromString(uri.toString());
    }

    @NonNull
    public static String getParameter(@NonNull List<String> parameters) {
        return TextUtils.join(",", parameters);
    }

    @NonNull
    public static String getEncodedParameter(@NonNull String parameter) {

        return Uri.encode(parameter, Charsets.UTF_8);
    }
}
