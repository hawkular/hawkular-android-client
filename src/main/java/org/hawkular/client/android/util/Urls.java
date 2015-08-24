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

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.aerogear.android.pipe.util.UrlUtils;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * URL utilities.
 *
 * Handles {@link java.net.URL} creation from various components.
 */
public final class Urls {
    private Urls() {
    }

    private static final class Protocols {
        private Protocols() {
        }

        public static final String HTTP = "http";
    }

    private static final class Files {
        private Files() {
        }

        public static final String EMPTY = "";
    }

    @NonNull
    public static URL getUrl(@NonNull String host) {
        try {
            return new URL(Protocols.HTTP, host, Files.EMPTY);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static URL getUrl(@NonNull String host, @IntRange(from = Ports.MINIMUM, to = Ports.MAXIMUM) int port) {
        try {
            return new URL(Protocols.HTTP, host, port, Files.EMPTY);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static URL getUrl(@NonNull URL url, @NonNull String path) {
        return UrlUtils.appendToBaseURL(url, path);
    }
}
