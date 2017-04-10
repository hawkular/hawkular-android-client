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


import java.util.List;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Metric;

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
 * Favourite Metrics adapter.
 * <p/>
 * Transforms a list of Metrics to a human-readable interpretation.
 */

public class FavMetricsAdapter extends RecyclerView.Adapter<FavMetricsAdapter.RecyclerViewHolder> {

    public Context context;

    public interface MetricListener {
        void onMetricMenuClick(View MetricView, int metricPosition);
        void onMetricTextClick(View MetricView, int metricPosition);
    }

    private final List<Metric> metrics;

    private final MetricListener metricListener;

    public FavMetricsAdapter(@NonNull Context context, @NonNull MetricListener metricMenuListener,
                         @NonNull List<Metric> metrics) {
        this.context = context;
        this.metricListener = metricMenuListener;
        this.metrics = metrics;
    }


    public Metric getItem(int position) {
        return metrics.get(position);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item_with_menu, parent, false);
        view.setTag(new RecyclerViewHolder(view));
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final Metric currentMetric = getItem(position);

        holder.titleText.setText(currentMetric.getName());
        holder.messageText.setText(currentMetric.getId());

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metricListener.onMetricTextClick(view, position);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metricListener.onMetricMenuClick(view, position);
            }
        });
    }



    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return metrics.size();
    }



    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_title) TextView titleText;
        @BindView(R.id.text_message) TextView messageText;
        @BindView(R.id.button_menu) View menuButton;
        @BindView(R.id.data_box) LinearLayout listItem;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
