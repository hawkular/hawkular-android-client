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
import org.hawkular.client.android.backend.model.ResourceType;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ResourceTypesAdapter extends BindableAdapter<ResourceType>
{
	static final class ViewHolder
	{
		@InjectView(R.id.text)
		public TextView nameText;

		public ViewHolder(@NonNull View view) {
			ButterKnife.inject(this, view);
		}
	}

	private final List<ResourceType> resourceTypes;

	public ResourceTypesAdapter(@NonNull Context context, @NonNull List<ResourceType> resourceTypes) {
		super(context);

		this.resourceTypes = resourceTypes;
	}

	@Override
	public ResourceType getItem(int position) {
		return resourceTypes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return resourceTypes.size();
	}

	@NonNull
	@Override
	public View newView(LayoutInflater inflater, int position, ViewGroup viewContainer) {
		View view = inflater.inflate(R.layout.layout_list_item, viewContainer, false);

		view.setTag(new ViewHolder(view));

		return view;
	}

	@Override
	public void bindView(ResourceType resourceType, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();

		viewHolder.nameText.setText(resourceType.getId());
	}
}
