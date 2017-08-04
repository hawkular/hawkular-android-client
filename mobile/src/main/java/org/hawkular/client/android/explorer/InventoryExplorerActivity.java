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
package org.hawkular.client.android.explorer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Data;
import org.hawkular.client.android.backend.model.Error;
import org.hawkular.client.android.backend.model.Feed;
import org.hawkular.client.android.backend.model.InventoryResponseBody;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.MetricConfiguration;
import org.hawkular.client.android.backend.model.MetricInfo;
import org.hawkular.client.android.backend.model.MetricTemp;
import org.hawkular.client.android.backend.model.Operation;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.explorer.holder.IconTreeItemHolder;
import org.hawkular.client.android.fragment.ConfirmOperationFragment;
import org.hawkular.client.android.util.CanonicalPath;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Inventory Explorer activity.
 *
 * Manage explorer interaction and presentation.
 */

public class InventoryExplorerActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AndroidTreeView tView;
    private TreeNode.BaseNodeViewHolder holder;
    private TreeNode root;
    private Callback<String> callback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_explorer);


        callback = new PerformOperationCallback();

        setUpBindings();

        setUpToolbar();

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

        //String path = CanonicalPath.getByString("/t;hawkular/f;400e8e5737ca/mt;Runtime%20Availability~VM%20Availability").getMetricType();

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }

    }

    private void setUpBindings() {
        ButterKnife.bind(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setTitle("Explorer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void setUpFeeds(List<String> feeds) {
        for (String feed : feeds) {
            int icon = getResources().getIdentifier("drawable/" + "feed_icon", null, getPackageName());
            TreeNode newFeed = new TreeNode(new IconTreeItemHolder.IconTreeItem(
                    icon, IconTreeItemHolder.IconTreeItem.Type.FEED, feed, feed));
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

    private void setUpResources(List<Resource> resources, Map<String,String> map, TreeNode parent) {
        for (Resource resource : resources) {
            int icon = getResources().getIdentifier("drawable/" + "resource_icon", null, getPackageName());
            TreeNode newResource = new TreeNode(new IconTreeItemHolder.IconTreeItem(
                    icon, IconTreeItemHolder.IconTreeItem.Type.RESOURCE, map.get(resource.getId()), resource));
            tView.addNode(parent, newResource);
        }

    }

    private void setUpMetrics(List<MetricTemp> metrics, TreeNode parent) {
        for (MetricTemp metric : metrics) {
            int icon = getResources().getIdentifier("drawable/" + "metric_icon", null, getPackageName());
            TreeNode newMetric = new TreeNode(new IconTreeItemHolder.IconTreeItem(
                    icon, IconTreeItemHolder.IconTreeItem.Type.METRIC, metric.getMetricInfo().getName(), metric));
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
                    String path1;
                    String feed = (String) item.value;
                    path1 = "feed:" + feed + ",type:r";
                    InventoryResponseBody body = new InventoryResponseBody("true", "DESC", path1);

                    BackendClient.of(getInventoryExplorerActivity()).getResourcesFromFeed(
                            new ResourcesCallback(node), body);
                }
            } else if (item.type == IconTreeItemHolder.IconTreeItem.Type.RESOURCE) {
                if (node.size() == 0) {
                    BackendClient.of(getInventoryExplorerActivity()).getRecResourcesFromFeed(
                            new ResourcesCallback(node), (Resource) item.value);
                    String path1;

                    Resource resource = (Resource) item.value;
                    String temp = resource.getId();
                    String temp1 = temp.substring(temp.indexOf(".r.") + 3);
                    path1 = "id:"+temp1;

                    InventoryResponseBody body = new InventoryResponseBody("true", "DESC", path1);

                    BackendClient.of(getInventoryExplorerActivity()).getMetricsFromFeed(
                           new MetricsCallback(node), body);

                    BackendClient.of(getInventoryExplorerActivity()).getOpreations(
                            new OperationsCallback(node), (Resource) item.value);
                }
            }

            else if (item.type == IconTreeItemHolder.IconTreeItem.Type.METRIC) {

                MetricTemp metricInfo = (MetricTemp) item.value;
                String metricTypePath = metricInfo.getMetricInfo().getMetricTypePath();
                String path = CanonicalPath.getByString(metricTypePath).getMetricType();
                path = "id:"+ path;
                InventoryResponseBody body = new InventoryResponseBody("true", "DESC", path);
                MetricConfiguration configuration = new MetricConfiguration("null");
                Metric metric1 = new Metric(metricInfo.getMetricInfo().getId(),null,configuration);
                BackendClient.of(getInventoryExplorerActivity()).getMetricType(new MetricTypeCallback(metric1,true),body);


                Log.d("Metric",metric1.getConfiguration().getType());


            }


            else if (item.type == IconTreeItemHolder.IconTreeItem.Type.OPERATION) {
                Resource resource = (Resource) ((IconTreeItemHolder.IconTreeItem) node.getParent().getValue()).value;
                Operation operation = (Operation) ((IconTreeItemHolder.IconTreeItem) node.getValue()).value;

                ConfirmOperationFragment dialog = new ConfirmOperationFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Fragments.Arguments.RESOURCE, resource);
                bundle.putParcelable(Fragments.Arguments.OPERATION, operation);
                dialog.setArguments(bundle);
                dialog.setCallback(callback);
                dialog.show(getSupportFragmentManager(), "missiles");
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
                                addMetricToFav((MetricTemp) item.value);
                            }
                        });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
            return true;
        }
    };


    private void addMetricToFav(final MetricTemp metricTemp) {

        String metricTypePath = metricTemp.getMetricInfo().getMetricTypePath();
        String path = CanonicalPath.getByString(metricTypePath).getMetricType();
        path = "id:"+ path;
        InventoryResponseBody body = new InventoryResponseBody("true", "DESC", path);
        MetricConfiguration configuration = new MetricConfiguration("null");
        Metric metric1 = new Metric(metricTemp.getMetricInfo().getId(),null,configuration);

        BackendClient.of(getInventoryExplorerActivity()).getMetricType(new MetricTypeCallback(metric1,false),body);

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


    private final class FeedsCallback implements retrofit2.Callback<Feed> {

        @Override
        public void onResponse(Call<Feed> call, Response<Feed> response) {


            Feed feed = response.body();

            if (response.isSuccessful()) {
                Log.d("response on feed", "id is =" + feed.getFeed().get(0));
                getInventoryExplorerActivity().setUpFeeds(response.body().getFeed());
            }
        }

        @Override
        public void onFailure(Call<Feed> call, Throwable t) {
            Log.d("Fetching Failed", t.getMessage());
        }

        private InventoryExplorerActivity getInventoryExplorerActivity() {
            return InventoryExplorerActivity.this;
        }
    }

    private final class OperationsCallback extends AbstractActivityCallback<List<Operation>> {

        private TreeNode parent;

        OperationsCallback(TreeNode parent) {
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

    private final class ResourcesCallback implements retrofit2.Callback<List<Resource>> {

        private TreeNode parent;

        ResourcesCallback(TreeNode parent) {
            this.parent = parent;
        }

        @Override
        public void onResponse(Call<List<Resource>> call, Response<List<Resource>> response) {
            HashMap<String, String> hashMap = new HashMap<String, String>();

            if (!response.isSuccessful()) {
                Gson gson = new GsonBuilder().create();
                try {
                    Error mApiError = gson.fromJson(response.errorBody().string(), Error.class);
                    Log.d("Response on feed metric", mApiError.getErrorMsg());
                } catch (IOException e) {
                    // handle failure to read error
                }
            } else {

                List<Resource> resources;
                resources = response.body();

                for(Resource resource: resources){
                    String decoded = rebuildFromChunks(resource.getData());

                    try {
                        JSONObject structure =(new JSONObject(decoded)).getJSONObject("inventoryStructure");
                        JSONObject data = structure.getJSONObject("data");
                        String res_name = data.getString("name");
                        hashMap.put(resource.getId(),res_name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(!response.body().isEmpty()) {
                getInventoryExplorerActivity().setUpResources(response.body(),hashMap, parent);
            }
            else {
            }

        }

        @Override
        public void onFailure(Call<List<Resource>> call, Throwable t) {
            Timber.d("Resources fetching failed.");

        }
    }

    private final class MetricsCallback implements retrofit2.Callback<List<Resource>> {

        private TreeNode parent;

        MetricsCallback(TreeNode parent) {
            this.parent = parent;
        }


        private InventoryExplorerActivity getInventoryExplorerActivity() {
            return InventoryExplorerActivity.this;
        }


        @Override
        public void onResponse(Call<List<Resource>> call, Response<List<Resource>> response) {

            if (!response.isSuccessful()) {
                Log.d("Error", response.errorBody().toString());

            } else {

                List<MetricTemp> list = new ArrayList<MetricTemp>();
                List<Resource> resources = response.body();
                String decoded = rebuildFromChunks(resources.get(0).getData());


                try {
                    JSONObject structure = (new JSONObject(decoded)).getJSONObject("inventoryStructure");
                    JSONObject data = structure.getJSONObject("children");
                    if (data != null) {
                        JSONArray jsonArray = data.getJSONArray("metric");

                        if (jsonArray != null) {
                            for(int i =0; i< jsonArray.length();i++){
                                JSONObject data2 = jsonArray.getJSONObject(i);
                                JSONObject data1 = data2.getJSONObject("data");
                                MetricInfo metric_info = new MetricInfo(data1.getString("id"), data1.getString("name"), data1.getString("metricTypePath"));
                                MetricTemp metricTemp = new MetricTemp(metric_info);

                                list.add(metricTemp);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!list.isEmpty()) {
                    getInventoryExplorerActivity().setUpMetrics(list, parent);
                }

            }
        }

        @Override
        public void onFailure(Call<List<Resource>> call, Throwable t) {
            Timber.d(t.getMessage());
        }
    }

    private final class MetricTypeCallback implements retrofit2.Callback<List<Resource>>{

        Metric metric1;
        Boolean flag;
        public MetricTypeCallback(Metric metric, boolean flag) {
            metric1 = metric;
            this.flag = flag;
        }

        @Override
        public void onResponse(Call<List<Resource>> call, Response<List<Resource>> response) {

            if (!response.isSuccessful()) {
                Log.d("Error", response.errorBody().toString());

            } else {

                List<Resource> resources = response.body();
                String decoded = rebuildFromChunks(resources.get(0).getData());

                try {
                    JSONObject structure = (new JSONObject(decoded)).getJSONObject("inventoryStructure");
                    JSONObject data = structure.getJSONObject("data");
                    String type1 = data.getString("type");
                    metric1.getConfiguration().setType(type1);

                    if(flag) {
                        Intent intent = Intents.Builder.of(getApplicationContext()).buildMetricIntent(metric1);
                        startActivity(intent);
                    }
                    else{
                        SQLStore<Metric> store = openStore(getApplicationContext());
                        store.openSync();
                        store.save(metric1);
                        Toast.makeText(getApplicationContext(), "Metric added to favourite", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<List<Resource>> call, Throwable t) {
            Timber.d(t.getMessage());
        }
    }
    private final class PerformOperationCallback implements Callback<String> {

        @Override
        public void onSuccess(String data) {
            Snackbar.make(findViewById(android.R.id.content), data, Snackbar.LENGTH_LONG).show();

        }

        @Override
        public void onFailure(Exception e) {
            Snackbar.make(findViewById(android.R.id.content), R.string.operation_fail, Snackbar.LENGTH_LONG).show();
        }
    }

    private String rebuildFromChunks(List<Data> dataNode) {
        try {
            Data masterNode = dataNode.get(0);

                final byte[] all;
            byte[] all1;

            if (masterNode.getTags() != null && masterNode.getTags().getChunks() != null) {
                    int nbChunks = Integer.parseInt(masterNode.getTags().getChunks());
                    int totalSize = Integer.parseInt(masterNode.getTags().getSize());
                    byte[] master = Base64.decode(masterNode.getValue(), Base64.DEFAULT);

                    Log.d("tags", masterNode.getTags().getChunks());
                    if (master.length == 0) {
                        return "";
                    }
                    if (nbChunks > dataNode.size()) {
                        // Race condition: some, but not all chunks have been written on DB while reading?
                        // Then, caller must just wait a little bit before retrying
                        return "";
                    }

                    all1 = new byte[totalSize];
                    int pos = 0;
                    System.arraycopy(master, 0, all1, pos, master.length);
                    pos += master.length;

                    for (int i = 1; i < nbChunks; i++) {
                        Data slaveNode = dataNode.get(i);
                        byte[] slave = Base64.decode(slaveNode.getValue(), Base64.DEFAULT);
                        System.arraycopy(slave, 0, all1, pos, slave.length);
                        pos += slave.length;

                    }
                } else {
                    // Not chunked
                    all1 = Base64.decode(masterNode.getValue(), Base64.DEFAULT);
                }
            all = all1;
            String decompressed = decompress(all);
                Log.d("decompressed", decompressed);
                return decompressed;

        }catch (Exception e) {
            Log.d("Exceptions", e.getMessage());
        }
        return "";
    }


    private static String decompress(byte[] gzipped) throws IOException {
        if ((gzipped == null) || (gzipped.length == 0)) {
            return "";
        }

        StringBuilder outStr = new StringBuilder();
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(gzipped));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            outStr.append(line);
        }
        bufferedReader.close();
        gis.close();
        return outStr.toString();

    }
}

