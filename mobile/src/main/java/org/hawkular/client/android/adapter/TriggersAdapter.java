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
import org.hawkular.client.android.backend.model.Trigger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Favourite Triggers adapter.
 * <p/>
 * Transforms a list of Triggers to a human-readable interpretation.
 */

public class TriggersAdapter extends RecyclerView.Adapter<TriggersAdapter.RecyclerViewHolder>{

    public interface TriggerListener {
        void onTriggerToggleChanged(View TriggerView, int triggerPosition,boolean state);
        void onTriggerTextClick(View TriggerView, int triggerPosition);
    }

    private final List<Trigger> triggers;
    private final TriggerListener triggerListener;
    public Context context;

    public TriggersAdapter(@NonNull Context context, @NonNull TriggerListener triggerMenuListener,
                           @NonNull List<Trigger> triggers) {
        this.context = context;
        this.triggerListener = triggerMenuListener;
        this.triggers = triggers;
    }

    public Trigger getItem(int position) {
        return triggers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return triggers.size();
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_token, parent, false);

        return new RecyclerViewHolder(itemView);
    }

    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final Trigger currentTrigger = getItem(position);

        holder.titleText.setText(currentTrigger.getId());
        holder.messageText.setText(currentTrigger.getDescription());
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerListener.onTriggerTextClick(view, position);
            }
        });

        holder.toggleTrigger.setChecked(currentTrigger.getEnableStatus());

        holder.toggleTrigger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                triggerListener.onTriggerToggleChanged(compoundButton,position,b);
            }
        });
    }

    static final class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_title) TextView titleText;
        @BindView(R.id.text_message) TextView messageText;
        @BindView(R.id.toggle_trigger) SwitchCompat toggleTrigger;
        @BindView(R.id.list_item) LinearLayout listItem;

        RecyclerViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
