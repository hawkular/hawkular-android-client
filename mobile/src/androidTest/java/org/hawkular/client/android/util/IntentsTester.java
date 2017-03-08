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
package org.hawkular.client.android.util;

import org.assertj.android.api.Assertions;
import org.hawkular.client.android.activity.AlertsActivity;
import org.hawkular.client.android.activity.AuthorizationActivity;
import org.hawkular.client.android.activity.MetricActivity;
import org.hawkular.client.android.activity.MetricsActivity;
import org.hawkular.client.android.activity.SettingsActivity;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricConfiguration;
import org.hawkular.client.android.backend.model.MetricProperties;
import org.hawkular.client.android.backend.model.MetricType;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ResourceProperties;
import org.hawkular.client.android.backend.model.ResourceType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public final class IntentsTester {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void alerts() {
        Intent intent = Intents.Builder.of(context).buildAlertsIntent(generateResource());

        Assertions.assertThat(intent).hasComponent(context, AlertsActivity.class);
        Assertions.assertThat(intent).hasExtra(Intents.Extras.RESOURCE);
    }

    @Test
    public void metric() {
        Intent intent = Intents.Builder.of(context).buildMetricIntent(generateMetric());

        Assertions.assertThat(intent).hasComponent(context, MetricActivity.class);
        Assertions.assertThat(intent).hasExtra(Intents.Extras.METRIC);
    }

    @Test
    public void metrics() {
        Intent intent = Intents.Builder.of(context).buildMetricsIntent(generateEnvironment(), generateResource());

        Assertions.assertThat(intent).hasComponent(context, MetricsActivity.class);
        Assertions.assertThat(intent).hasExtra(Intents.Extras.ENVIRONMENT);
        Assertions.assertThat(intent).hasExtra(Intents.Extras.RESOURCE);
    }

    private Resource generateResource() {
        return new Resource(
            Randomizer.generateString(),
            new ResourceType(Randomizer.generateString()),
            new ResourceProperties(Randomizer.generateString()));
    }

    private Metric generateMetric() {
        return new Metric(
            Randomizer.generateString(),
            new MetricProperties(Randomizer.generateString()),
            new MetricConfiguration(MetricType.AVAILABILITY));
    }

    private Environment generateEnvironment() {
        return new Environment(Randomizer.generateString());
    }
}
