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

public class FavMetricsAdapter extends BindableAdapter<Metric> {

    public interface MetricListener {
        void onMetricMenuClick(View MetricView, int metricPosition);
        void onMetricTextClick(View MetricView, int metricPosition);
    }

    private final List<Metric> metrics;

    private final MetricListener metricListener;

    public FavMetricsAdapter(@NonNull Context context, @NonNull MetricListener metricMenuListener,
                         @NonNull List<Metric> metrics) {
        super(context);
        this.metricListener = metricMenuListener;
        this.metrics = metrics;
    }

    @Override
    public int getCount() {
        return metrics.size();
    }

    @NonNull
    @Override
    public Metric getItem(int position) {
        return metrics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    protected View newView(LayoutInflater inflater, ViewGroup viewContainer) {
        View view = inflater.inflate(R.layout.layout_list_item_token, viewContainer, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    protected void bindView(Metric metric, final int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.titleText.setText(metric.getName());
        viewHolder.messageText.setText(metric.getId());

        viewHolder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metricListener.onMetricTextClick(view, position);
            }
        });

        viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metricListener.onMetricMenuClick(view, position);
            }
        });
    }

    static final class ViewHolder {
        @BindView(R.id.text_title)
        TextView titleText;

        @BindView(R.id.text_message)
        TextView messageText;

        @BindView(R.id.button_menu)
        View menuButton;

        @BindView(R.id.list_item)
        LinearLayout listItem;

        public ViewHolder(@NonNull View view) {
            ButterKnife.bind(this, view);
        }
    }
}
