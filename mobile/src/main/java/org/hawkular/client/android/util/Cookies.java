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

import android.annotation.TargetApi;
import android.webkit.CookieManager;

/**
 * Cookie utilities.
 *
 * The main goal at this point is clearing cookies the right way on all supported OS versions.
 */
public final class Cookies {
    private Cookies() {
    }

    public static void clear() {
        if (Android.isLollipopOrLater()) {
            clearAfterLollipop();
        } else {
            clearBeforeLollipop();
        }
    }

    @TargetApi(Android.Versions.TARGET)
    private static void clearAfterLollipop() {
        CookieManager.getInstance().removeAllCookies(null);
    }

    @SuppressWarnings("deprecation")
    private static void clearBeforeLollipop() {
        CookieManager.getInstance().removeAllCookie();
    }
}
