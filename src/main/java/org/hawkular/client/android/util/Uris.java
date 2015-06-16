package org.hawkular.client.android.util;

import android.support.annotation.NonNull;

import java.net.URI;
import java.net.URISyntaxException;

public final class Uris {
    private Uris() {
    }

    public static URI getUri(@NonNull String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
