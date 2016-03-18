/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
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
package org.hawkular.client.android.auth;

/**
 * Contain Hard Coded Strings which are to be used by package.
 */

public final class AuthData {
    public static final String NAME = "hawkular";
    public static final String STORE = "sessionStore";

    public static final class Endpoints {
        private Endpoints() {
        }

        public static final String ACCESS = "/secret-store/v1/tokens/create";
        public static final String PERSONA = "/hawkular/accounts/personas/current";
    }

    public static final class Credentials {
        private Credentials() {
        }

        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String EXPIRES_ON = "expiresAt";
        public static final String KEY = "key";
        public static final String SECRET = "secret";
        public static final String URL = "url";
        public static final String CONTAIN = "contain";
    }

}
