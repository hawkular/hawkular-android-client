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

import android.net.Uri;
import android.support.annotation.NonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public final class Uris {
    private Uris() {
    }

    public static URI getUri(@NonNull String path) {
        Uri uri = new Uri.Builder()
            .appendPath(path)
            .build();

        return Uris.getUriFromString(uri.toString());
    }

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

    private static URI getUriFromString(@NonNull String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
