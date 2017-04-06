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
package org.hawkular.client.android.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.AlertEvaluation;
import org.hawkular.client.android.backend.model.AlertType;
import org.hawkular.client.android.util.Formatter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Alerts adapter.
 * <p/>
 * Transforms a list of alerts to a human-readable interpretation, including proper formatting for alerts, based on
 * Hawkular Web UI. Has an ability to attach a listener for alert-related operations menu.
 */

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.RecyclerViewHolder> {

    public Context context;

    public interface AlertListener {
        void onAlertMenuClick(View alertView, int alertPosition);
        void onAlertBodyClick(View alertView, int alertPosition);
    }

    private final AlertListener alertListener;

    private final List<Alert> alerts;

    public AlertsAdapter(@NonNull Context context, @NonNull AlertListener alertListener,
                         @NonNull List<Alert> alerts) {
        this.context = context;
        this.alertListener = alertListener;
        this.alerts = new ArrayList<>(alerts);
    }

    public Alert getItem(int position) {
        return alerts.get(position);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_list_item_alert, parent, false);
        view.setTag(new RecyclerViewHolder(view));
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Alert currentAlert = getItem(position);
        holder.titleText.setText(getAlertTitle(context, currentAlert));
        holder.messageText.setText(getAlertMessage(context, currentAlert));
        holder.statusText.setText(currentAlert.getStatus());
        holder.triggerText.setText(currentAlert.getTrigger().getId());

        final int alertPosition = position;

        holder.dataBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertListener.onAlertBodyClick(view, alertPosition);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertListener.onAlertMenuClick(view, alertPosition);
            }
        });
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    private String getAlertTitle(Context context, Alert alert) {
        return getAlertTimestamp(context, getAlertStartTimestamp(alert));
    }

    private String getAlertTimestamp(Context context, long timestamp) {
        return Formatter.formatDateTime(context, timestamp);
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
                alertValuesSum += Double.valueOf(alertEvaluation.getValue());

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

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_title) TextView titleText;
        @BindView(R.id.text_message) TextView messageText;
        @BindView(R.id.text_status) TextView statusText;
        @BindView(R.id.button_menu) View menuButton;
        @BindView(R.id.data_box) LinearLayout dataBox;
        @BindView(R.id.text_trigger) TextView triggerText;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
