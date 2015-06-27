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
package org.hawkular.client.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricData;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractFragmentCallback;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.Icicle;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import timber.log.Timber;

public final class MetricFragment extends Fragment {
    @InjectView(R.id.chart)
    LineChartView chart;

    @Icicle
    @Nullable
    ArrayList<MetricData> metricData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        setUpState(state);

        setUpBindings();

        setUpMetricData();
    }

    private void setUpState(Bundle state) {
        Icepick.restoreInstanceState(this, state);
    }

    private void setUpBindings() {
        ButterKnife.inject(this, getView());
    }

    @OnClick(R.id.button_retry)
    public void setUpMetricData() {
        if (metricData == null) {
            showProgress();

            BackendClient.of(this).getMetricData(
                getTenant(), getMetric(), getMetricStartTime(), getMetricFinishTime(), new MetricDataCallback());
        } else {
            setUpMetricData(metricData);
        }
    }

    private Date getMetricStartTime() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.MINUTE, -10);

        return calendar.getTime();
    }

    private Date getMetricFinishTime() {
        return GregorianCalendar.getInstance().getTime();
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Tenant getTenant() {
        return getArguments().getParcelable(Fragments.Arguments.TENANT);
    }

    private Metric getMetric() {
        return getArguments().getParcelable(Fragments.Arguments.METRIC);
    }

    private void setUpMetricData(List<MetricData> metricDataList) {
        this.metricData = new ArrayList<>(metricDataList);

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

        showChart();
    }

    private void sortMetricData(List<MetricData> metricDataList) {
        Collections.sort(metricDataList, new MetricDataComparator());
    }

    private void showChart() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.chart);
    }

    private void showMessage() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.message);
    }

    private void showError() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.error);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        tearDownState(state);
    }

    private void tearDownState(Bundle state) {
        Icepick.saveInstanceState(this, state);
    }

    private static final class MetricDataCallback extends AbstractFragmentCallback<List<MetricData>> {
        @Override
        public void onSuccess(List<MetricData> metricData) {
            if (!metricData.isEmpty()) {
                getMetricFragment().setUpMetricData(metricData);
            } else {
                getMetricFragment().showMessage();
            }
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Metric data fetching failed.");

            getMetricFragment().showError();
        }

        private MetricFragment getMetricFragment() {
            return (MetricFragment) getFragment();
        }
    }

    private static final class MetricDataComparator implements Comparator<MetricData> {
        @Override
        public int compare(MetricData leftMetricData, MetricData rightMetricData) {
            Date leftMetricDataTimestamp = new Date(leftMetricData.getTimestamp());
            Date rightMetricDataTimestamp = new Date(rightMetricData.getTimestamp());

            return leftMetricDataTimestamp.compareTo(rightMetricDataTimestamp);
        }
    }
}
