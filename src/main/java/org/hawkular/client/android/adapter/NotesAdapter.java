/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
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
import org.hawkular.client.android.backend.model.Note;
import org.hawkular.client.android.util.Formatter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Notes adapter.
 *
 * Transforms a list of notes on alert to a human-readable interpretation.
 */
public class NotesAdapter extends BindableAdapter<Note> {

    private final List<Note> notes;
    private final Context context;

    public NotesAdapter(@NonNull Context context, @NonNull List<Note> notes) {
        super(context);
        this.context = context;
        this.notes = notes;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @NonNull
    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    protected View newView(LayoutInflater inflater, ViewGroup viewContainer) {
        View view = inflater.inflate(R.layout.layout_list_item_three, viewContainer, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    protected void bindView(Note note, final int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.userText.setText(note.getUser());

        viewHolder.dateText.setText(Formatter.formatDateTime(context, note.getTimestamp()));
        viewHolder.messageText.setText(note.getMessage());
    }

    static final class ViewHolder {
        @BindView(R.id.text_user)
        TextView userText;

        @BindView(R.id.text_date)
        TextView dateText;

        @BindView(R.id.text_message)
        TextView messageText;


        public ViewHolder(@NonNull View view) {
            ButterKnife.bind(this, view);
        }
    }
}