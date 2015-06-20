package org.hawkular.client.android.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import org.jboss.aerogear.android.pipe.util.UrlUtils;

import java.net.MalformedURLException;
import java.net.URL;

public final class Urls {
    private Urls() {
    }

    private static final class Protocols {
        private Protocols() {
        }

        public static final String HTTP = "http";
    }

    @NonNull
    public static URL getUrl(@NonNull String host, @IntRange(from = 0) int port) {
        try {
            return new URL(Protocols.HTTP, host, port, null);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static URL getUrl(@NonNull URL url, @NonNull String path) {
        return UrlUtils.appendToBaseURL(url, path);
    }
}
