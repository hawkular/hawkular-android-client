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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ResourceType;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ResourcesAdapter extends BaseAdapter {
    private static final class Types {
        private Types() {
        }

        public static final int RESOURCE = 0;
        public static final int RESOURCE_TYPE = 1;
    }

    static final class ResourceViewHolder {
        @Bind(R.id.text)
        TextView nameText;

        public ResourceViewHolder(@NonNull View view) {
            ButterKnife.bind(this, view);
        }
    }

    static final class ResourceTypeViewHolder {
        @Bind(R.id.text)
        TextView nameText;

        public ResourceTypeViewHolder(@NonNull View view) {
            ButterKnife.bind(this, view);
        }
    }

    private final LayoutInflater inflater;

    private final List<Resource> resources;
    private final Map<ResourceType, Integer> resourceTypes;

    public ResourcesAdapter(@NonNull Context context, @NonNull List<Resource> resources) {
        this.inflater = LayoutInflater.from(context);

        this.resources = resources;
        this.resourceTypes = getResourceTypes(resources);
    }

    private Map<ResourceType, Integer> getResourceTypes(List<Resource> resources) {
        Map<ResourceType, Integer> resourceTypes = new HashMap<>();

        int resourcesCount = 0;
        int resourceTypesCount = 0;

        for (Resource resource : resources) {
            if (!containsResourceType(resourceTypes, resource.getType())) {
                resourceTypes.put(resource.getType(), resourcesCount + resourceTypesCount);

                resourceTypesCount++;
            }

            resourcesCount++;
        }

        return resourceTypes;
    }

    private boolean containsResourceType(Map<ResourceType, Integer> resourceTypes, ResourceType resourceType) {
        for (ResourceType availableResourceType : resourceTypes.keySet()) {
            if (availableResourceType.getId().equals(resourceType.getId())) {
                return true;
            }
        }

        return false;
    }

    @NonNull
    @Override
    public Resource getItem(int position) {
        return resources.get(getResourcePosition(position));
    }

    private int getResourcePosition(int position) {
        int resourceTypesCount = 0;

        for (int resourceTypePosition : resourceTypes.values()) {
            if (resourceTypePosition < position) {
                resourceTypesCount++;
            }
        }

        return position - resourceTypesCount;
    }

    @Override
    public long getItemId(int position) {
        return getResourcePosition(position);
    }

    @Override
    public int getCount() {
        return resources.size() + resourceTypes.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewContainer) {
        if (view == null) {
            if (getItemViewType(position) == Types.RESOURCE) {
                view = newResourceView(viewContainer);
            } else {
                view = newResourceTypeView(viewContainer);
            }
        }

        if (getItemViewType(position) == Types.RESOURCE) {
            bindResourceView(position, view);
        } else {
            bindResourceTypeView(position, view);
        }

        return view;
    }

    private View newResourceView(ViewGroup viewContainer) {
        View view = inflater.inflate(R.layout.layout_list_item, viewContainer, false);

        view.setTag(new ResourceViewHolder(view));

        return view;
    }

    private View newResourceTypeView(ViewGroup viewContainer) {
        View view = inflater.inflate(R.layout.layout_list_item_section, viewContainer, false);

        view.setTag(new ResourceTypeViewHolder(view));

        return view;
    }

    private void bindResourceView(int position, View view) {
        ResourceViewHolder resourceViewHolder = (ResourceViewHolder) view.getTag();

        resourceViewHolder.nameText.setText(getItem(position).getProperties().getUrl());
    }

    private void bindResourceTypeView(int position, View view) {
        ResourceTypeViewHolder resourceTypeViewHolder = (ResourceTypeViewHolder) view.getTag();

        resourceTypeViewHolder.nameText.setText(getResourceType(position).getId());
    }

    private ResourceType getResourceType(int position) {
        for (Map.Entry<ResourceType, Integer> resourceType : resourceTypes.entrySet()) {
            if (resourceType.getValue().equals(position)) {
                return resourceType.getKey();
            }
        }

        throw new RuntimeException("Resource type was not found.");
    }

    @Override
    public int getViewTypeCount() {
        return Arrays.asList(Types.RESOURCE, Types.RESOURCE_TYPE).size();
    }

    @Override
    public int getItemViewType(int position) {
        if (resourceTypes.containsValue(position)) {
            return Types.RESOURCE_TYPE;
        } else {
            return Types.RESOURCE;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == Types.RESOURCE;
    }
}
