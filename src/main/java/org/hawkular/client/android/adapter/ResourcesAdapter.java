package org.hawkular.client.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.model.Resource;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ResourcesAdapter extends BindableAdapter<Resource> {
    static final class ViewHolder {
        @InjectView(R.id.text)
        public TextView nameText;

        public ViewHolder(@NonNull View view) {
            ButterKnife.inject(this, view);
        }
    }

    private final List<Resource> resources;

    public ResourcesAdapter(@NonNull Context context, @NonNull List<Resource> resources) {
        super(context);

        this.resources = resources;
    }

    @Override
    public Resource getItem(int position) {
        return resources.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return resources.size();
    }

    @NonNull
    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup viewContainer) {
        View view = inflater.inflate(R.layout.layout_list_item, viewContainer, false);

        view.setTag(new ViewHolder(view));

        return view;
    }

    @Override
    public void bindView(Resource resource, int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.nameText.setText(resource.getProperties().getUrl());
    }
}
