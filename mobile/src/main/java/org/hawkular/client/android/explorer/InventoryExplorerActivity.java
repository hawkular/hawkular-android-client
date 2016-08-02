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
package org.hawkular.client.android.explorer;

import java.util.List;
import java.util.UUID;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Feed;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.Operation;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.explorer.holder.IconTreeItemHolder;
import org.hawkular.client.android.util.Intents;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Toast;
import timber.log.Timber;

/**
 * Inventory Explorer activity.
 *
 * Manage explorer interaction and presentation.
 */

public class InventoryExplorerActivity extends AppCompatActivity {

    private AndroidTreeView tView;
    private TreeNode.BaseNodeViewHolder holder;
    private TreeNode root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_explorer);

        ViewGroup containerView = (ViewGroup) findViewById(R.id.container);

        root = TreeNode.root();

        holder = root.getViewHolder();

        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);
        containerView.addView(tView.getView());

        BackendClient.of(this).getFeeds(new FeedsCallback());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }

    }

    private void setUpFeeds(List<Feed> feeds) {
        for (Feed feed : feeds) {
            int icon = getResources().getIdentifier("drawable/" + "feed_icon", null, getPackageName());
            TreeNode newFeed = new TreeNode(new IconTreeItemHolder.IconTreeItem(
                    icon, IconTreeItemHolder.IconTreeItem.Type.FEED, feed.getId(), feed));
            tView.addNode(root, newFeed);
        }

    }

    private void setUpOperations(List<Operation> operations, TreeNode parent) {
        for (Operation operation : operations) {
            int icon = getResources().getIdentifier("drawable/" + "operation_icon", null, getPackageName());
            TreeNode newFeed = new TreeNode(new IconTreeItemHolder.IconTreeItem(
                    icon, IconTreeItemHolder.IconTreeItem.Type.OPERATION, operation.getId(), operation));
            tView.addNode(parent, newFeed);
        }

    }

    private void setUpResources(List<Resource> resources, TreeNode parent) {
        for (Resource resource : resources) {
            int icon = getResources().getIdentifier("drawable/" + "resource_icon", null, getPackageName());
            TreeNode newResource = new TreeNode(new IconTreeItemHolder.IconTreeItem(
                    icon, IconTreeItemHolder.IconTreeItem.Type.RESOURCE, resource.getName(), resource));
            tView.addNode(parent, newResource);
        }

    }

    private void setUpMetrics(List<Metric> metrics, TreeNode parent) {
        for (Metric metric : metrics) {
            int icon = getResources().getIdentifier("drawable/" + "metric_icon", null, getPackageName());
            TreeNode newMetric = new TreeNode(new IconTreeItemHolder.IconTreeItem(
                    icon, IconTreeItemHolder.IconTreeItem.Type.METRIC, metric.getName(), metric));
            tView.addNode(parent, newMetric);
        }

    }

    private InventoryExplorerActivity getInventoryExplorerActivity() {
        return this;
    }


    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            if (item.type == IconTreeItemHolder.IconTreeItem.Type.FEED) {
                if (node.size() == 0) {
                    BackendClient.of(getInventoryExplorerActivity()).getResourcesFromFeed(
                            new ResourcesCallback(node), (Feed) item.value);
                }
            } else if (item.type == IconTreeItemHolder.IconTreeItem.Type.RESOURCE) {
                if (node.size() == 0) {
                    BackendClient.of(getInventoryExplorerActivity()).getRecResourcesFromFeed(
                            new ResourcesCallback(node), (Resource) item.value);
                    BackendClient.of(getInventoryExplorerActivity()).getMetricsFromFeed(
                            new MetricsCallback(node), (Resource) item.value);
                    BackendClient.of(getInventoryExplorerActivity()).getOpreationsFromFeed(
                            new OpertationsCallback(node), (Resource) item.value);
                }
            } else if (item.type == IconTreeItemHolder.IconTreeItem.Type.METRIC) {
                Intent intent = Intents.Builder.of(getApplicationContext()).buildMetricIntent((Metric) item.value);
                startActivity(intent);
            }
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, final Object value) {
            final IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            if (item.type == IconTreeItemHolder.IconTreeItem.Type.FEED) {

            } else if (item.type == IconTreeItemHolder.IconTreeItem.Type.RESOURCE) {

            } else if (item.type == IconTreeItemHolder.IconTreeItem.Type.METRIC) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getInventoryExplorerActivity(), R.style.AlertDialogStyle);
                builder.setTitle("Hey");
                builder.setMessage("Do you want to add this Metric to Favourite list?");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addMetricToFav((Metric)item.value);
                            }
                        });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
            return true;
        }
    };


    private void addMetricToFav(final Metric metric) {
        SQLStore<Metric> store = openStore(getApplicationContext());
        store.openSync();
        store.save(metric);
        Toast.makeText(getApplicationContext(), "Metric added to favourite", Toast.LENGTH_SHORT).show();

    }

    private SQLStore<Metric> openStore(Context context) {
        DataManager.config("Favourite", SQLStoreConfiguration.class)
                .withContext(context)
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public String generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store(Metric.class);
        return (SQLStore<Metric>) DataManager.getStore("Favourite");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private final class FeedsCallback extends AbstractActivityCallback<List<Feed>> {
        @Override
        public void onSuccess(List<Feed> feeds) {
            if (!feeds.isEmpty()) {
                getInventoryExplorerActivity().setUpFeeds(feeds);
            }
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Resources fetching failed.");

        }

        private InventoryExplorerActivity getInventoryExplorerActivity() {
            return (InventoryExplorerActivity) getActivity();
        }
    }

    private final class OpertationsCallback extends AbstractActivityCallback<List<Operation>> {

        private TreeNode parent;

        OpertationsCallback(TreeNode parent) {
            this.parent = parent;
        }

        @Override
        public void onSuccess(List<Operation> operations) {
            if (!operations.isEmpty()) {
                getInventoryExplorerActivity().setUpOperations(operations, parent);
            }
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Resources fetching failed.");

        }

        private InventoryExplorerActivity getInventoryExplorerActivity() {
            return (InventoryExplorerActivity) getActivity();
        }
    }

    private final class ResourcesCallback extends AbstractActivityCallback<List<Resource>> {

        private TreeNode parent;

        ResourcesCallback(TreeNode parent) {
            this.parent = parent;
        }

        @Override
        public void onSuccess(List<Resource> resources) {
            if (!resources.isEmpty()) {
                getInventoryExplorerActivity().setUpResources(resources, parent);
            } else {
            }
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Resources fetching failed.");

        }

        private InventoryExplorerActivity getInventoryExplorerActivity() {
            return (InventoryExplorerActivity) getActivity();
        }

    }

    private final class MetricsCallback extends AbstractActivityCallback<List<Metric>> {

        private TreeNode parent;

        MetricsCallback(TreeNode parent) {
            this.parent = parent;
        }

        @Override
        public void onSuccess(List<Metric> metrics) {
            if (!metrics.isEmpty()) {
                getInventoryExplorerActivity().setUpMetrics(metrics, parent);
            }
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Resources fetching failed.");

        }

        private InventoryExplorerActivity getInventoryExplorerActivity() {
            return (InventoryExplorerActivity) getActivity();
        }
    }
}
