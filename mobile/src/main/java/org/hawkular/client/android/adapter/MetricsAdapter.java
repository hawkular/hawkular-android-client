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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Metrics adapter.
 *
 * Transforms a list of metrics to a human-readable interpretation.
 */

public class MetricsAdapter extends RecyclerView.Adapter<MetricsAdapter.RecyclerViewHolder> {

    public Context context;
    public List<Metric> metrics;

    public MetricsAdapter(Context context, List<Metric> metrics) {
        this.context = context;
        this.metrics = metrics;
    }

    public Metric getItem(int position) {
        return metrics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item, parent, false);
        itemView.setTag(new RecyclerViewHolder(itemView));
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return metrics.size();
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final Metric currentMetric = getItem(position);

        holder.nameText.setText(currentMetric.getProperties().getDescription());
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text) TextView nameText;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
