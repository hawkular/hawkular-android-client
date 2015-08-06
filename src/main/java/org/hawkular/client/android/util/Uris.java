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
package org.hawkular.client.android.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

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
            .appendPath(Uris.getEncodedParameter(path))
            .build();

        return Uris.getUriFromString(uri.toString());
    }

    @NonNull
    public static URI getUri(@NonNull String path, @NonNull Map<String, String> parameters) {
        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.appendPath(path);

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
    private static String getEncodedParameter(@NonNull String parameter) {
        try {
            return URLEncoder.encode(parameter, Charsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return parameter;
        }
    }
}
