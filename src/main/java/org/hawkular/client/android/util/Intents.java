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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.hawkular.client.android.activity.AuthorizationActivity;
import org.hawkular.client.android.activity.MetricDataActivity;
import org.hawkular.client.android.activity.MetricsActivity;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Tenant;

public final class Intents {
    private Intents() {
    }

    public static final class Extras {
        private Extras() {
        }

        public static final String TENANT = "tenant";
        public static final String ENVIRONMENT = "environment";
        public static final String RESOURCE = "resource";
        public static final String METRIC = "metric";
    }

    public static final class Requests {
        private Requests() {
        }

        public static final int AUTHORIZATION = 42;
    }

    public static final class Builder {
        private final Context context;

        @NonNull
        public static Builder of(@NonNull Context context) {
            return new Builder(context);
        }

        private Builder(Context context) {
            this.context = context;
        }

        @NonNull
        public Intent buildAuthorizationIntent() {
            return new Intent(context, AuthorizationActivity.class);
        }

        @NonNull
        public Intent buildMetricsIntent(@NonNull Tenant tenant, @NonNull Environment environment,
                                         @NonNull Resource resource) {
            Intent intent = new Intent(context, MetricsActivity.class);
            intent.putExtra(Extras.TENANT, tenant);
            intent.putExtra(Extras.ENVIRONMENT, environment);
            intent.putExtra(Extras.RESOURCE, resource);

            return intent;
        }

        @NonNull
        public Intent buildMetricDataIntent(@NonNull Tenant tenant, @NonNull Metric metric) {
            Intent intent = new Intent(context, MetricDataActivity.class);
            intent.putExtra(Extras.TENANT, tenant);
            intent.putExtra(Extras.METRIC, metric);

            return intent;
        }
    }
}
