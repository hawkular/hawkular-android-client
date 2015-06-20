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
package org.hawkular.client.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricData;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import timber.log.Timber;

public final class MetricDataActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.chart)
    LineChartView chart;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_chart);

        setUpBindings();

        setUpToolbar();

        setUpMetricData();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpMetricData() {
        showProgress();

        BackendClient.of(this).getMetricData(getTenant(), getMetric(), new MetricDataCallback());
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Tenant getTenant() {
        return getIntent().getParcelableExtra(Intents.Extras.TENANT);
    }

    private Metric getMetric() {
        return getIntent().getParcelableExtra(Intents.Extras.METRIC);
    }

    private void setUpMetricData(List<MetricData> metricDataList) {
        sortMetricData(metricDataList);

        List<PointValue> chartPoints = new ArrayList<>();

        List<AxisValue> chartAxisPoints = new ArrayList<>();

        for (int metricDataPosition = 0; metricDataPosition < metricDataList.size(); metricDataPosition++) {
            MetricData metricData = metricDataList.get(metricDataPosition);

            chartPoints.add(new PointValue(metricDataPosition, metricData.getValue()));

            chartAxisPoints.add(new AxisValue(metricDataPosition)
                .setLabel(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(metricData.getTimestamp()))));
        }

        Line chartLine = new Line(chartPoints)
            .setColor(getResources().getColor(R.color.background_primary_dark))
            .setCubic(true)
            .setHasPoints(false);

        LineChartData chartData = new LineChartData()
            .setLines(Collections.singletonList(chartLine));
        chartData.setAxisXBottom(new Axis()
            .setValues(chartAxisPoints));
        chartData.setAxisYLeft(new Axis()
            .setHasLines(true));

        chart.setLineChartData(chartData);

        Viewport chartViewport = new Viewport(chart.getMaximumViewport());

        chartViewport.bottom = chart.getMaximumViewport().bottom - 50;
        chartViewport.top = chart.getMaximumViewport().top + 50;

        chart.setMaximumViewport(chartViewport);
        chart.setCurrentViewport(chartViewport);

        hideProgress();
    }

    private void sortMetricData(List<MetricData> metricDataList) {
        Collections.sort(metricDataList, new MetricDataComparator());
    }

    private void hideProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.chart);
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

    private static final class MetricDataCallback extends AbstractActivityCallback<List<MetricData>> {
        @Override
        public void onSuccess(List<MetricData> metricDataList) {
            MetricDataActivity activity = (MetricDataActivity) getActivity();

            activity.setUpMetricData(metricDataList);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Metric data fetching failed.");
        }
    }

    private static final class MetricDataComparator implements Comparator<MetricData> {
        @Override
        public int compare(MetricData leftMetricData, MetricData rightMetricData) {
            long leftTimestamp = leftMetricData.getTimestamp();
            long rightTimestamp = rightMetricData.getTimestamp();

            if (leftTimestamp == rightTimestamp) {
                return 0;
            }

            if (leftTimestamp < rightTimestamp) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
