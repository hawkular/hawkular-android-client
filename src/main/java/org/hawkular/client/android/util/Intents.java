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

import org.hawkular.client.android.activity.AlertsActivity;
import org.hawkular.client.android.activity.MetricDataActivity;
import org.hawkular.client.android.activity.MetricsActivity;
import org.hawkular.client.android.activity.ResourceTypesActivity;
import org.hawkular.client.android.activity.ResourcesActivity;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ResourceType;
import org.hawkular.client.android.backend.model.Tenant;

public class Intents {
    private Intents() {
    }

    public static final class Extras {
        private Extras() {
        }

        public static final String METRIC = "metric";
        public static final String RESOURCE = "resource";
        public static final String RESOURCE_TYPE = "resource-type";
        public static final String TENANT = "tenant";
    }

    public static final class Builder {
        private final Context context;

        public static Builder of(@NonNull Context context) {
            return new Builder(context);
        }

        private Builder(Context context) {
            this.context = context;
        }

        public Intent buildResourceTypesIntent(@NonNull Tenant tenant) {
            Intent intent = new Intent(context, ResourceTypesActivity.class);
            intent.putExtra(Extras.TENANT, tenant);

            return intent;
        }

        public Intent buildResourcesIntent(@NonNull Tenant tenant, @NonNull ResourceType resourceType) {
            Intent intent = new Intent(context, ResourcesActivity.class);
            intent.putExtra(Extras.TENANT, tenant);
            intent.putExtra(Extras.RESOURCE_TYPE, resourceType);

            return intent;
        }

        public Intent buildMetricsIntent(@NonNull Tenant tenant, @NonNull Resource resource) {
            Intent intent = new Intent(context, MetricsActivity.class);
            intent.putExtra(Extras.TENANT, tenant);
            intent.putExtra(Extras.RESOURCE, resource);

            return intent;
        }

        public Intent buildMetricDataIntent(@NonNull Tenant tenant, @NonNull Metric metric) {
            Intent intent = new Intent(context, MetricDataActivity.class);
            intent.putExtra(Extras.TENANT, tenant);
            intent.putExtra(Extras.METRIC, metric);

            return intent;
        }

        public Intent buildAlertsIntent() {
            return new Intent(context, AlertsActivity.class);
        }
    }
}
