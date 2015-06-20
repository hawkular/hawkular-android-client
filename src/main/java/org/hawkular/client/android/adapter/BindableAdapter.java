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
import android.widget.BaseAdapter;

abstract class BindableAdapter<T> extends BaseAdapter {
    private final LayoutInflater inflater;

    protected BindableAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public final View getView(int position, View view, ViewGroup viewContainer) {
        if (view == null) {
            view = newView(inflater, viewContainer);
        }

        bindView(getItem(position), view);

        return view;
    }

    @NonNull
    protected abstract View newView(LayoutInflater inflater, ViewGroup viewContainer);

    protected abstract void bindView(T item, View view);
}