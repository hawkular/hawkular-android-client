package org.hawkular.client.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

abstract class BindableAdapter<T> extends BaseAdapter
{
	private final LayoutInflater inflater;

	public BindableAdapter(@NonNull Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public abstract T getItem(int position);

	@Override
	public final View getView(int position, View view, ViewGroup viewContainer) {
		if (view == null) {
			view = newView(inflater, position, viewContainer);
		}

		bindView(getItem(position), position, view);

		return view;
	}

	@NonNull
	public abstract View newView(LayoutInflater inflater, int position, ViewGroup viewContainer);

	public abstract void bindView(T item, int position, View view);
}