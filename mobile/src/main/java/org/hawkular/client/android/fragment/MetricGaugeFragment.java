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
package org.hawkular.client.android.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hawkular.client.android.HawkularApplication;
import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricBucket;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.ErrorUtil;
import org.hawkular.client.android.util.Formatter;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Time;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractSupportFragmentCallback;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import butterknife.BindView;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Metric fragment.
 * <p/>
 * Displays metric gauge data as a line chart.
 */
public final class MetricGaugeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.metric_name)
    TextView metric_name;

    @BindView(R.id.chart)
    LineChartView chart;

    @BindView(R.id.content)
    SwipeRefreshLayout contentLayout;

    @State
    ArrayList<MetricBucket> metricBucket;

    @State
    @IdRes
    int timeMenu;

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

        setUpMenu();

        setUpRefreshing();

        setUpMetricData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);

        menuInflater.inflate(R.menu.toolbar_time, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(timeMenu).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_time_hour:
            case R.id.menu_time_day:
            case R.id.menu_time_week:
            case R.id.menu_time_month:
            case R.id.menu_time_year:
                timeMenu = menuItem.getItemId();
                menuItem.setChecked(true);

                setUpMetricDataForced();

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }


    private void setUpState(Bundle state) {
        Icepick.restoreInstanceState(this, state);
    }

    private void setUpBindings() {
        ButterKnife.bind(this, getView());
    }

    private void setUpMenu() {
        setHasOptionsMenu(true);
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
        metric_name.setText(getMetric().getName());
        BackendClient.of(this).getMetricData(
                getMetric(), getBuckets(), getMetricStartTime(), getMetricFinishTime(), new MetricDataCallback(this));
    }

    @OnClick(R.id.button_retry)
    public void setUpMetricData() {
        if (metricBucket == null) {
            showProgress();
            timeMenu = R.id.menu_time_hour;
            setUpMetricDataForced();
        } else {
            setUpMetricData(metricBucket);
        }
    }

    private Date getMetricStartTime() {
        switch (timeMenu) {
            case R.id.menu_time_hour:
                return Time.hourAgo();

            case R.id.menu_time_day:
                return Time.dayAgo();

            case R.id.menu_time_week:
                return Time.weekAgo();

            case R.id.menu_time_month:
                return Time.monthAgo();

            case R.id.menu_time_year:
                return Time.yearAgo();

            default:
                return Time.hourAgo();
        }
    }


    private Date getMetricFinishTime() {
        return Time.current();
    }

    private long getBuckets() {
        return (60);
    }


    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Metric getMetric() {
        return getArguments().getParcelable(Fragments.Arguments.METRIC);
    }

    private void setUpMetricData(List<MetricBucket> metricDataList) {
        this.metricBucket = new ArrayList<>(metricDataList);

        sortMetricData(metricBucket);

        setUpChartLine();
        setUpChartArea();

        hideRefreshing();

        showChart();
    }

    private void sortMetricData(List<MetricBucket> metricBucketList) {
        Collections.sort(metricBucketList, new MetricBucketComparator());
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
        List<PointValue> chartPoints = new ArrayList<>(metricBucket.size());

        for (MetricBucket metricBucket : this.metricBucket) {
            float chartPointHorizontal = getChartRelativeTimestamp(metricBucket.getStartTimestamp());
            float chartPointVertical = metricBucket.isEmpty()
                    ? 0 : Float.valueOf(metricBucket.getValue());

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
            String chartAxisPointHorizontalLabel = "";

            switch (timeMenu) {
                case R.id.menu_time_hour:
                    chartAxisPointHorizontalLabel = Formatter.formatTime(chartCalendar.getTime().getTime());

                    chartAxisPoints.add(new AxisValue(chartAxisPointHorizontal)
                            .setLabel(chartAxisPointHorizontalLabel));
                    chartCalendar.add(Calendar.MINUTE, 1);
                    break;

                case R.id.menu_time_day:
                    chartAxisPointHorizontalLabel = Formatter.formatTime(chartCalendar.getTime().getTime());

                    chartAxisPoints.add(new AxisValue(chartAxisPointHorizontal)
                            .setLabel(chartAxisPointHorizontalLabel));
                    chartCalendar.add(Calendar.HOUR, 1);
                    break;

                case R.id.menu_time_week:
                    chartAxisPointHorizontalLabel = Formatter.formatDate(chartCalendar.getTime().getTime());

                    chartAxisPoints.add(new AxisValue(chartAxisPointHorizontal)
                            .setLabel(chartAxisPointHorizontalLabel));
                    chartCalendar.add(Calendar.HOUR, 24);
                    break;

                case R.id.menu_time_month:
                    chartAxisPointHorizontalLabel = Formatter.formatDate(chartCalendar.getTime().getTime());

                    chartAxisPoints.add(new AxisValue(chartAxisPointHorizontal)
                            .setLabel(chartAxisPointHorizontalLabel));
                    chartCalendar.add(Calendar.HOUR, 24 * 3);
                    break;

                case R.id.menu_time_year:
                    chartAxisPointHorizontalLabel = Formatter.formatDate(chartCalendar.getTime().getTime());

                    chartAxisPoints.add(new AxisValue(chartAxisPointHorizontal)
                            .setLabel(chartAxisPointHorizontalLabel));
                    chartCalendar.add(Calendar.HOUR, 24 * 7);
                    break;

                default:
                    chartAxisPointHorizontalLabel = Formatter.formatTime(chartCalendar.getTime().getTime());

                    chartAxisPoints.add(new AxisValue(chartAxisPointHorizontal)
                            .setLabel(chartAxisPointHorizontalLabel));
                    chartCalendar.add(Calendar.MINUTE, 1);
            }

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

        chart.setZoomEnabled(true);
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

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        tearDownState(state);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detectLeaks();
    }

    private void detectLeaks() {
        RefWatcher refWatcher = HawkularApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    private void tearDownState(Bundle state) {
        Icepick.saveInstanceState(this, state);
    }

    private static final class MetricDataCallback implements Callback<List<MetricBucket>> {

        MetricGaugeFragment metricGaugeFragment;

        public MetricDataCallback(MetricGaugeFragment metricGaugeFragment) {
            this.metricGaugeFragment = metricGaugeFragment;
        }

        private MetricGaugeFragment getMetricFragment() {
            return metricGaugeFragment;
        }

        @Override
        public void onResponse(Call<List<MetricBucket>> call, Response<List<MetricBucket>> response) {
            if(!response.body().isEmpty()){
                getMetricFragment().setUpMetricData(response.body());
            }
            else {
                getMetricFragment().showMessage();
            }
        }

        @Override
        public void onFailure(Call<List<MetricBucket>> call, Throwable t) {
            Timber.d(t, "Metric data fetching failed.");

            ErrorUtil.showError(getMetricFragment(),R.id.animator,R.id.error);
        }
    }

    private static final class MetricBucketComparator implements Comparator<MetricBucket> {
        @Override
        public int compare(MetricBucket leftMetricBucket, MetricBucket rightMetricBucket) {
            Date leftMetricBucketTimestamp = new Date(leftMetricBucket.getStartTimestamp());
            Date rightMetricBucketTimestamp = new Date(rightMetricBucket.getStartTimestamp());

            return leftMetricBucketTimestamp.compareTo(rightMetricBucketTimestamp);
        }
    }
}
