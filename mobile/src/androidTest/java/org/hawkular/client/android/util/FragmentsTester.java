/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
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
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricConfiguration;
import org.hawkular.client.android.backend.model.MetricProperties;
import org.hawkular.client.android.backend.model.MetricType;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ResourceProperties;
import org.hawkular.client.android.backend.model.ResourceType;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

@RunWith(AndroidJUnit4.class)
public final class FragmentsTester {
    @Test
    public void alerts() {
        Fragment fragment = Fragments.Builder.buildAlertsFragment(generateResource());

        Assertions.assertThat(fragment.getArguments()).hasKey(Fragments.Arguments.RESOURCE);
    }

    @Test
    public void metricAvailability() {
        Fragment fragment = Fragments.Builder.buildMetricAvailabilityFragment(generateMetric());

        Assertions.assertThat(fragment.getArguments()).hasKey(Fragments.Arguments.METRIC);
    }

    @Test
    public void metricCounter() {
        Fragment fragment = Fragments.Builder.buildMetricCounterFragment(generateMetric());

        Assertions.assertThat(fragment.getArguments()).hasKey(Fragments.Arguments.METRIC);
    }

    @Test
    public void metricGauge() {
        Fragment fragment = Fragments.Builder.buildMetricGaugeFragment(generateMetric());

        Assertions.assertThat(fragment.getArguments()).hasKey(Fragments.Arguments.METRIC);
    }

    @Test
    public void metrics() {
        Fragment fragment = Fragments.Builder.buildMetricsFragment(generateEnvironment(), generateResource());

        Assertions.assertThat(fragment.getArguments()).hasKey(Fragments.Arguments.ENVIRONMENT);
        Assertions.assertThat(fragment.getArguments()).hasKey(Fragments.Arguments.RESOURCE);
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
