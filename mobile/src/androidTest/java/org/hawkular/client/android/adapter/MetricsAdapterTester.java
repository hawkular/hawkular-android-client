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
package org.hawkular.client.android.adapter;

import java.util.ArrayList;
import java.util.List;

import org.assertj.android.api.Assertions;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricConfiguration;
import org.hawkular.client.android.backend.model.MetricProperties;
import org.hawkular.client.android.backend.model.MetricType;
import org.hawkular.client.android.util.Randomizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public final class MetricsAdapterTester {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void count() {
        List<Metric> metrics = generateMetrics();

        MetricsAdapter metricsAdapter = new MetricsAdapter(context, metrics);

        Assertions.assertThat(metricsAdapter).hasCount(metrics.size());
    }

    @Test
    public void item() {
        Metric metric = generateMetric();

        List<Metric> metrics = new ArrayList<>();
        metrics.add(metric);
        metrics.addAll(generateMetrics());

        MetricsAdapter metricsAdapter = new MetricsAdapter(context, metrics);

        for(int i=0; i< metrics.size(); i++) {
            Assertions.assertThat(metricsAdapter).hasItem(metrics.get(i), i);
        }
    }

    private List<Metric> generateMetrics() {
        List<Metric> metrics = new ArrayList<>();
        long random = Randomizer.generateNumber();

        for (int metricPosition = 0; metricPosition < random ; metricPosition++) {
            metrics.add(generateMetric());
        }

        return metrics;
    }

    private Metric generateMetric() {
        return new Metric(
            Randomizer.generateString(),
            new MetricProperties(Randomizer.generateString()),
            new MetricConfiguration(MetricType.AVAILABILITY));
    }
}
