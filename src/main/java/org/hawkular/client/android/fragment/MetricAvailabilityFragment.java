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
import org.hawkular.client.android.backend.model.MetricAvailability;
import org.hawkular.client.android.backend.model.MetricData;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.Formatter;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Time;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractFragmentCallback;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
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
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import timber.log.Timber;

public final class MetricAvailabilityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final class Defaults {
        private Defaults() {
        }

        public static final int AXIS_INTERVAL = 3;
    }

    @Bind(R.id.chart)
    ColumnChartView chart;

    @Bind(R.id.content)
    SwipeRefreshLayout contentLayout;

    @State
    @Nullable
    ArrayList<MetricData> metricData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_chart_column, container, false);
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
        setUpMetricDataRefreshed();
    }

    private void setUpMetricDataRefreshed() {
        BackendClient.of(this).getMetricDataAvailability(
            getResource(), getMetricStartTime(), getMetricFinishTime(), new MetricDataCallback());
    }

    @OnClick(R.id.button_retry)
    public void setUpMetricData() {
        if (metricData == null) {
            showProgress();

            BackendClient.of(this).getMetricDataAvailability(
                getResource(), getMetricStartTime(), getMetricFinishTime(), new MetricDataCallback());
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

    private Resource getResource() {
        return getArguments().getParcelable(Fragments.Arguments.RESOURCE);
    }

    private void setUpMetricData(List<MetricData> metricDataList) {
        this.metricData = new ArrayList<>(metricDataList);

        sortMetricData(metricData);

        setUpChartColumns();
        setUpChartArea();

        hideRefreshing();

        showChart();
    }

    private void sortMetricData(List<MetricData> metricDataList) {
        Collections.sort(metricDataList, new MetricDataComparator());
    }

    private void setUpChartColumns() {
        List<Column> chartColumns = getChartColumns();
        List<AxisValue> chartAxisPoints = getChartAxisPoints();
        List<AxisValue> chartAxisValues = getChartAxisValues();

        ColumnChartData chartData = new ColumnChartData()
            .setColumns(chartColumns);
        chartData.setAxisXBottom(new Axis()
            .setValues(chartAxisPoints));
        chartData.setAxisYLeft(new Axis()
            .setValues(chartAxisValues));

        chart.setColumnChartData(chartData);
    }

    private List<Column> getChartColumns() {
        List<Column> chartColumns = new ArrayList<>(metricData.size());

        for (MetricData metricData : this.metricData) {
            MetricAvailability metricAvailability = MetricAvailability.from(metricData.getValue());

            float columnValue = getColumnValue(metricAvailability);
            int columnColor = getColumnColor(metricAvailability);

            chartColumns.add(new Column(Collections.singletonList(new SubcolumnValue(columnValue, columnColor))));
        }

        return chartColumns;
    }

    @FloatRange(from = -1.0, to = 1.0)
    private float getColumnValue(MetricAvailability metricAvailability) {
        switch (metricAvailability) {
            case UP:
                return 1;

            case DOWN:
                return -1;

            default:
                return 0;
        }
    }

    @ColorInt
    private int getColumnColor(MetricAvailability metricAvailability) {
        return getResources().getColor(getColumnColorRes(metricAvailability));
    }

    @ColorRes
    private int getColumnColorRes(MetricAvailability metricAvailability) {
        switch (metricAvailability) {
            case UP:
                return R.color.background_secondary;

            case DOWN:
                return R.color.background_primary;

            default:
                return R.color.background_context;
        }
    }

    private List<AxisValue> getChartAxisPoints() {
        List<AxisValue> chartAxisPoints = new ArrayList<>();

        for (int metricDataPosition = 0; metricDataPosition < metricData.size(); metricDataPosition += Defaults.AXIS_INTERVAL) {
            float chartAxisPointHorizontal = metricDataPosition;
            String chartAxisPointLabel = Formatter.formatTime(metricData.get(metricDataPosition).getTimestamp());

            chartAxisPoints.add(new AxisValue(chartAxisPointHorizontal)
                .setLabel(chartAxisPointLabel));
        }

        return chartAxisPoints;
    }

    private List<AxisValue> getChartAxisValues() {
        List<AxisValue> chartAxisValues = new ArrayList<>();

        chartAxisValues.add(new AxisValue(getColumnValue(MetricAvailability.UP) / 2)
            .setLabel(getString(R.string.label_availability_up)));
        chartAxisValues.add(new AxisValue(getColumnValue(MetricAvailability.DOWN) / 2)
            .setLabel(getString(R.string.label_availability_down)));

        return chartAxisValues;
    }

    private void setUpChartArea() {
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

        private MetricAvailabilityFragment getMetricFragment() {
            return (MetricAvailabilityFragment) getFragment();
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
