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
package org.hawkular.client.android.backend;

/**
 * Backend authorization constants.
 *
 * Contains constants used for authorization configuration, especially useful
 * for {@link org.jboss.aerogear.android.authorization.AuthzModule}.
 */
final class BackendAuthorization {
    private BackendAuthorization() {
    }

    public static final String NAME = "hawkular";

    public static final class Ids {
        private Ids() {
        }

        public static final String ACCOUNT = "keycloak-token";
        public static final String CLIENT = "hawkular-ui";
    }

    public static final class Endpoints {
        private Endpoints() {
        }

        public static final String ACCESS = "/realms/hawkular/tokens/access/codes";
        public static final String AUTHZ = "/realms/hawkular/tokens/login";
        public static final String REFRESH = "/realms/hawkular/tokens/refresh";
    }

    public static final class Paths {
        private Paths() {
        }

        public static final String BASE = "/auth";
        public static final String REDIRECT = "/oauth2";
    }
}
