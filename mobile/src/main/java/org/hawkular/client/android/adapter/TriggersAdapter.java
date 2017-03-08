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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Favourite Triggers adapter.
 * <p/>
 * Transforms a list of Triggers to a human-readable interpretation.
 */

public class TriggersAdapter extends BindableAdapter<Trigger> {

    public interface TriggerListener {
        void onTriggerMenuClick(View TriggerView, int triggerPosition);
        void onTriggerTextClick(View TriggerView, int triggerPosition);
    }

    private final List<Trigger> triggers;

    private final TriggerListener triggerListener;

    public TriggersAdapter(@NonNull Context context, @NonNull TriggerListener triggerMenuListener,
                           @NonNull List<Trigger> triggers) {
        super(context);
        this.triggerListener = triggerMenuListener;
        this.triggers = triggers;
    }

    @Override
    public int getCount() {
        return triggers.size();
    }

    @NonNull
    @Override
    public Trigger getItem(int position) {
        return triggers.get(position);
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
    protected void bindView(Trigger trigger, final int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.titleText.setText(trigger.getId());
        viewHolder.messageText.setText(trigger.getDescription());
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerListener.onTriggerTextClick(view, position);
            }
        });

        viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerListener.onTriggerMenuClick(view, position);
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

        @BindView(R.id.text_wrapper)
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View view) {
            ButterKnife.bind(this, view);
        }
    }
}
