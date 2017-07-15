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
package org.hawkular.client.android.activity;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Metric activity.
 * <p/>
 * Can be considered as a wrapper for {@link org.hawkular.client.android.fragment.MetricAvailabilityFragment}
 * and {@link org.hawkular.client.android.fragment.MetricGaugeFragment} depending
 * on a passed {@link org.hawkular.client.android.backend.model.Resource}.
 */
public final class MetricActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_container);

        setUpBindings();

        setUpToolbar();

        setUpMetric();
    }

    private void setUpBindings() {
        ButterKnife.bind(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpMetric() {
        Fragments.Operator.of(this).set(R.id.layout_container, getMetricFragment());
    }

    private Fragment getMetricFragment() {
        switch (getMetric().getConfiguration().getType()) {
            case "AVAILABILITY":
                return Fragments.Builder.buildMetricAvailabilityFragment(getMetric());

            case "GAUGE":
                return Fragments.Builder.buildMetricGaugeFragment(getMetric());

            case "COUNTER":
                return Fragments.Builder.buildMetricCounterFragment(getMetric());

            default:
                return Fragments.Builder.buildMetricGaugeFragment(getMetric());
        }
    }

    private Resource getResource() {
        return getIntent().getParcelableExtra(Intents.Extras.RESOURCE);
    }

    private Metric getMetric() {
        return getIntent().getParcelableExtra(Intents.Extras.METRIC);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
