package org.hawkular.client.android.explorer.holder;

import org.hawkular.client.android.R;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;
    private TreeNode node;
    private IconTreeItem value;


    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        this.node = node;
        this.value = value;
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageResource(value.icon);

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);


        return view;
    }

    @Override
    public void toggle(boolean active) {
        if(value.type!=IconTreeItem.Type.METRIC) {
            arrowView.setIconText(context.getResources()
                    .getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
        }

    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public Object value;
        public Type type;

        public IconTreeItem(int icon, Type type, String text, Object value) {
            this.icon = icon;
            this.text = text;
            this.value = value;
            this.type = type;
        }

        public enum Type {
            FEED,
            RESOURCE,
            METRIC
        }
    }
}
