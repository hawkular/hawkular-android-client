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

final class BackendPipes {
    private BackendPipes() {
    }

    public static final class Names {
        private Names() {
        }

        public static final String ALERTS = "alerts";
        public static final String TENANTS = "tenants";
        public static final String ENVIRONMENTS = "environments";
        public static final String PERSONAS = "personas";
        public static final String RESOURCES = "resources";
        public static final String METRICS = "metrics";
        public static final String METRIC_DATA = "metric-data";
    }

    public static final class Paths {
        private Paths() {
        }

        public static final String ROOT = "hawkular";

        public static final String ALERTS = "alerts";
        public static final String TENANTS = "inventory/tenant";
        public static final String ENVIRONMENTS = "inventory/environments";
        public static final String PERSONAS = "hawkular-accounts/personas";
        public static final String RESOURCES = "inventory/%s/resources";
        public static final String METRICS = "inventory/%s/resources/%s/metrics";
        public static final String METRIC_DATA = "metrics/gauges/%s/data";
    }

    public static final class Parameters {
        private Parameters() {
        }

        public static final String START = "start";
        public static final String FINISH = "end";

        public static final String START_TIME = "startTime";
        public static final String FINISH_TIME = "finishTime";
    }

    public static final class Headers {
        private Headers() {
        }

        public static final String PERSONA = "Hawkular-Persona";
        public static final String TENANT = "Hawkular-Tenant";
    }
}
