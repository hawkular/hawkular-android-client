package org.hawkular.client.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ResourceType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class SectionedResourcesAdapter extends BaseAdapter {
    private static final class Types {
        private Types() {
        }

        public static final int RESOURCE = 0;
        public static final int RESOURCE_TYPE = 1;
    }

    static final class ResourceViewHolder {
        @InjectView(R.id.text)
        TextView nameText;

        public ResourceViewHolder(@NonNull View view) {
            ButterKnife.inject(this, view);
        }
    }

    static final class ResourceTypeViewHolder {
        @InjectView(R.id.text)
        TextView nameText;

        public ResourceTypeViewHolder(@NonNull View view) {
            ButterKnife.inject(this, view);
        }
    }

    private final LayoutInflater inflater;

    private final List<Resource> resources;
    private final Map<ResourceType, Integer> resourceTypes;

    public SectionedResourcesAdapter(@NonNull Context context, @NonNull List<Resource> resources) {
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

    @Override
    public int getCount() {
        return resources.size() + resourceTypes.size();
    }

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
        View view = inflater.inflate(R.layout.layout_list_item, viewContainer, false);

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
            if (resourceType.getValue() == position) {
                return resourceType.getKey();
            }
        }

        return null;
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
