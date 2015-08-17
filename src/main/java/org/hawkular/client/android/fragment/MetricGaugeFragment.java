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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricData;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.Formatter;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Time;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractFragmentCallback;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import timber.log.Timber;

public final class MetricGaugeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final class Defaults {
        private Defaults() {
        }

        public static final int AXIS_INTERVAL_IN_MINUTES = 1;
    }

    @Bind(R.id.chart)
    LineChartView chart;

    @Bind(R.id.content)
    SwipeRefreshLayout contentLayout;

    @State
    ArrayList<MetricData> metricData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_chart_line, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        setUpState(state);

        setUpBindings();

        setUpRefreshing();

        setUpMetricData();
    }

    private void setUpState(Bundle state) {
        Icepick.restoreInstanceState(this, state);
    }

    private void setUpBindings() {
        ButterKnife.bind(this, getView());
    }

    private void setUpRefreshing() {
        contentLayout.setOnRefreshListener(this);
        contentLayout.setColorSchemeResources(ColorSchemer.getScheme());
    }

    @Override
    public void onRefresh() {
        setUpMetricDataForced();
    }

    private void setUpMetricDataForced() {
        BackendClient.of(this).getMetricDataGauge(
            getMetric(), getMetricStartTime(), getMetricFinishTime(), new MetricDataCallback());
    }

    @OnClick(R.id.button_retry)
    public void setUpMetricData() {
        if (metricData == null) {
            showProgress();

            setUpMetricDataForced();
        } else {
            setUpMetricData(metricData);
        }
    }

    private Date getMetricStartTime() {
        return Time.hourAgo();
    }

    private Date getMetricFinishTime() {
        return Time.current();
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Metric getMetric() {
        return getArguments().getParcelable(Fragments.Arguments.METRIC);
    }

    private void setUpMetricData(List<MetricData> metricDataList) {
        this.metricData = new ArrayList<>(metricDataList);

        sortMetricData(metricData);

        setUpChartLine();
        setUpChartArea();

        hideRefreshing();

        showChart();
    }

    private void sortMetricData(List<MetricData> metricDataList) {
        Collections.sort(metricDataList, new MetricDataComparator());
    }

    private void setUpChartLine() {
        List<PointValue> chartPoints = getChartPoints();
        List<AxisValue> chartAxisPoints = getChartAxisPoints();

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
    }

    private List<PointValue> getChartPoints() {
        List<PointValue> chartPoints = new ArrayList<>(metricData.size());

        for (MetricData metricData : this.metricData) {
            float chartPointHorizontal = getChartRelativeTimestamp(metricData.getTimestamp());
            float chartPointVertical = Float.valueOf(metricData.getValue());

            chartPoints.add(new PointValue(chartPointHorizontal, chartPointVertical));
        }

        return chartPoints;
    }

    private List<AxisValue> getChartAxisPoints() {
        List<AxisValue> chartAxisPoints = new ArrayList<>();

        Date chartStartTime = getMetricStartTime();
        Date chartFinishTime = getMetricFinishTime();

        Calendar chartCalendar = GregorianCalendar.getInstance();
        chartCalendar.setTime(chartStartTime);
        chartCalendar.set(Calendar.MINUTE, 0);
        chartCalendar.set(Calendar.SECOND, 0);
        chartCalendar.set(Calendar.MILLISECOND, 0);

        while (chartCalendar.getTime().before(chartFinishTime)) {
            float chartAxisPointHorizontal = getChartRelativeTimestamp(chartCalendar.getTime().getTime());
            String chartAxisPointHorizontalLabel = Formatter.formatTime(chartCalendar.getTime().getTime());

            chartAxisPoints.add(new AxisValue(chartAxisPointHorizontal)
                .setLabel(chartAxisPointHorizontalLabel));

            chartCalendar.add(Calendar.MINUTE, Defaults.AXIS_INTERVAL_IN_MINUTES);
        }

        return chartAxisPoints;
    }

    private long getChartRelativeTimestamp(long timestamp) {
        return timestamp - getMetricStartTime().getTime();
    }

    private void setUpChartArea() {
        Viewport maximumViewport = new Viewport(chart.getMaximumViewport());

        maximumViewport.bottom = 0;
        maximumViewport.top = (float) (maximumViewport.top * 1.1);

        chart.setMaximumViewport(maximumViewport);

        Viewport currentViewport = new Viewport(chart.getMaximumViewport());

        currentViewport.left = (float) (chart.getMaximumViewport().left * 1.9);
        currentViewport.right = (float) (chart.getMaximumViewport().right * 0.1);

        chart.setCurrentViewport(currentViewport);

        chart.setZoomEnabled(false);
    }

    private void hideRefreshing() {
        contentLayout.setRefreshing(false);
    }

    private void showChart() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.content);
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

        private MetricGaugeFragment getMetricFragment() {
            return (MetricGaugeFragment) getFragment();
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
