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
package org.hawkular.client.android.push;

import org.hawkular.client.android.BuildConfig;

final class PushConfiguration {
    private PushConfiguration() {
    }

    public static final String NAME = "Hawkular";

    public static final class Ups {
        private Ups() {
        }

        public static final String URL = BuildConfig.UPS_URL;
        public static final String SECRET = BuildConfig.UPS_SECRET;
        public static final String VARIANT = BuildConfig.UPS_VARIANT;
    }

    public static final class Gcm {
        private Gcm() {
        }

        public static final String SENDER = BuildConfig.GCM_SENDER;
    }
}
