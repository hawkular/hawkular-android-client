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
package org.hawkular.client.android.adapter;

import java.util.ArrayList;
import java.util.List;

import org.assertj.android.api.Assertions;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.AlertEvaluation;
import org.hawkular.client.android.util.Randomizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

@RunWith(AndroidJUnit4.class)
public final class AlertsAdapterTester {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void count() {
        List<Alert> alerts = generateAlerts();

        AlertsAdapter alertsAdapter = new AlertsAdapter(context, new AlertMenuAdapter(), alerts);

        Assertions.assertThat(alertsAdapter).hasCount(alerts.size());
    }

    @Test
    public void item() {
        Alert alert = generateAlert();

        List<Alert> alerts = new ArrayList<>();
        alerts.add(alert);
        alerts.addAll(generateAlerts());

        AlertsAdapter alertsAdapter = new AlertsAdapter(context, new AlertMenuAdapter(), alerts);

        Assertions.assertThat(alertsAdapter).hasItem(alert, 0);
    }

    private List<Alert> generateAlerts() {
        List<Alert> alerts = new ArrayList<>();

        for (int alertPosition = 0; alertPosition < Randomizer.generateNumber(); alertPosition++) {
            alerts.add(generateAlert());
        }

        return alerts;
    }

    private Alert generateAlert() {
        return new Alert(
            Randomizer.generateString(),
            Randomizer.generateNumber(),
            new ArrayList<List<AlertEvaluation>>());
    }

    private static final class AlertMenuAdapter implements AlertsAdapter.AlertMenuListener {
        @Override
        public void onAlertMenuClick(View alertView, int alertPosition) {
        }
    }
}
