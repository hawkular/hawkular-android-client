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

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.AlertEvaluation;
import org.hawkular.client.android.backend.model.AlertType;
import org.hawkular.client.android.util.Formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class AlertsAdapter extends BindableAdapter<Alert> {
    static final class ViewHolder {
        @InjectView(R.id.text_title)
        TextView titleText;

        @InjectView(R.id.text_message)
        TextView messageText;

        public ViewHolder(@NonNull View view) {
            ButterKnife.inject(this, view);
        }
    }

    private final List<Alert> alerts;

    public AlertsAdapter(@NonNull Context context, @NonNull List<Alert> alerts) {
        super(context);

        this.alerts = alerts;
    }

    @Override
    public Alert getItem(int position) {
        return alerts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return alerts.size();
    }

    @NonNull
    @Override
    public View newView(LayoutInflater inflater, ViewGroup viewContainer) {
        View view = inflater.inflate(R.layout.layout_list_item_alert, viewContainer, false);

        view.setTag(new ViewHolder(view));

        return view;
    }

    @Override
    public void bindView(Alert alert, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.titleText.setText(getAlertTitle(view.getContext(), alert));
        viewHolder.messageText.setText(getAlertMessage(view.getContext(), alert));
    }

    private String getAlertTitle(Context context, Alert alert) {
        return getAlertTimestamp(context, getAlertStartTimestamp(alert));
    }

    private String getAlertTimestamp(Context context, long timestamp) {
        return Formatter.formatTime(context, timestamp);
    }

    private String getAlertMessage(Context context, Alert alert) {
        switch (getAlertType(alert)) {
            case AVAILABILITY:
                return getAvailabilityAlertMessage(context, alert);

            case THRESHOLD:
                return getThresholdAlertMessage(context, alert);

            default:
                throw new RuntimeException("Alert is not supported.");
        }
    }

    private AlertType getAlertType(Alert alert) {
        for (List<AlertEvaluation> alertEvaluations : alert.getEvaluations()) {
            for (AlertEvaluation alertEvaluation : alertEvaluations) {
                AlertType alertType = alertEvaluation.getCondition().getType();

                if (alertType != null) {
                    return alertType;
                }
            }
        }

        throw new RuntimeException("No alert type found.");
    }

    private String getAvailabilityAlertMessage(Context context, Alert alert) {
        long alertStartTimestamp = getAlertStartTimestamp(alert);
        long alertFinishTimestamp = getAlertFinishTimestamp(alert);

        return context.getString(R.string.mask_alert_availability,
            getAlertDuration(alertStartTimestamp, alertFinishTimestamp),
            getAlertTimestamp(context, alertFinishTimestamp));
    }

    private long getAlertStartTimestamp(Alert alert) {
        List<Long> alertStartTimestamps = new ArrayList<>();

        for (List<AlertEvaluation> alertEvaluations : alert.getEvaluations()) {
            for (AlertEvaluation alertEvaluation : alertEvaluations) {
                alertStartTimestamps.add(alertEvaluation.getDataTimestamp());
            }
        }

        return Collections.min(alertStartTimestamps);
    }

    private long getAlertFinishTimestamp(Alert alert) {
        return alert.getTimestamp();
    }

    private int getAlertDuration(long alertStartTimestamp, long alertFinishTimestamp) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(alertFinishTimestamp - alertStartTimestamp);
    }

    private String getThresholdAlertMessage(Context context, Alert alert) {
        long alertStartTimestamp = getAlertStartTimestamp(alert);
        long alertFinishTimestamp = getAlertFinishTimestamp(alert);

        double alertAverage = getAlertAverage(alert);
        double alertThreshold = getAlertThreshold(alert);

        return context.getString(R.string.mask_alert_threshold,
            alertThreshold,
            getAlertDuration(alertStartTimestamp, alertFinishTimestamp),
            getAlertTimestamp(context, alertFinishTimestamp),
            alertAverage);
    }

    private double getAlertAverage(Alert alert) {
        double alertValuesSum = 0;
        long alertValuesCount = 0;

        for (List<AlertEvaluation> alertEvaluations : alert.getEvaluations()) {
            for (AlertEvaluation alertEvaluation : alertEvaluations) {
                alertValuesSum += alertEvaluation.getValue();

                alertValuesCount++;
            }
        }

        return alertValuesSum / alertValuesCount;
    }

    private double getAlertThreshold(Alert alert) {
        for (List<AlertEvaluation> alertEvaluations : alert.getEvaluations()) {
            for (AlertEvaluation alertEvaluation : alertEvaluations) {
                double alertThreshold = alertEvaluation.getCondition().getThreshold();

                if (alertThreshold >= 0) {
                    return alertThreshold;
                }
            }
        }

        throw new RuntimeException("No alert threshold found.");
    }
}
