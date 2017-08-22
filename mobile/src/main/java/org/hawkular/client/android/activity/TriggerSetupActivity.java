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
package org.hawkular.client.android.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.AvailabilityCondition;
import org.hawkular.client.android.backend.model.Error;
import org.hawkular.client.android.backend.model.FullTrigger;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.ThresholdCondition;
import org.hawkular.client.android.util.Intents;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by pallavi on 19/06/17.
 */


public class TriggerSetupActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_tenant_id)
    EditText id;

    @BindView(R.id.et_name)
    EditText textName;

    @BindView(R.id.typeSpinner)
    Spinner spinnerType;

    @BindView(R.id.eventTypeSpinner)
    Spinner spinnerEventType;

    @BindView(R.id.severitySpinner)
    Spinner severitySpinner;

    @BindView(R.id.switch_autoDisable)
    Switch switchAutoDisable;

    @BindView(R.id.switch_autoEnable)
    Switch switchAutoEnable;

    @BindView(R.id.switch_enabled)
    Switch switchEnabled;

    Snackbar snackbar;
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_setup_trigger);

        setUpBindings();

        setUpToolbar();

        setUpState(state);
    }

    private void setUpState(Bundle state) {
        Icepick.restoreInstanceState(this, state);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            return;
        }



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trigger Setup");
    }

    private void setUpBindings() {
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_submit)
    void setUpTriggerValues(){

        FullTrigger trigger = null;

        if (textName.getText().toString()!=null) {
            trigger = new FullTrigger(id.getText().toString(),null,textName.getText().toString(),switchEnabled.isChecked(),severitySpinner.getSelectedItem().toString());
        }

        else{
            Toast.makeText(getApplicationContext(),"Trigger Name cannot be empty",Toast.LENGTH_SHORT);
        }
        trigger.setAutoDisable(switchAutoDisable.isChecked());
        trigger.setType(spinnerType.getSelectedItem().toString());
        trigger.setEventType(spinnerEventType.getSelectedItem().toString());
        trigger.setAutoEnable(switchAutoEnable.isChecked());

        BackendClient.of(this).createTrigger(trigger,new TriggerCreateCallback());

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

    private class TriggerCreateCallback implements Callback {

        @Override
        public void onResponse(Call call, Response response) {

            if (response.code() == 200) {
                final FullTrigger t = (FullTrigger) response.body();

                CharSequence options[] = new CharSequence[] {"Add conditions to the trigger", "Finish Creating trigger"};
                 AlertDialog.Builder builder = new AlertDialog.Builder(TriggerSetupActivity.this);
                        builder.setTitle("Select one");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            if(getMetric().getConfiguration().getType().equalsIgnoreCase("gauge"))
                                showDialogBox(t.getId(),"gauge");
                            else if(getMetric().getConfiguration().getType().equalsIgnoreCase("availability"))
                                showDialogBox(t.getId(),"availability");

                        }
                        if(which == 1){
                         finish();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

            }
            else {
                Gson gson = new GsonBuilder().create();
                Error mApiError = null;
                try {
                    mApiError = gson.fromJson(response.errorBody().string(), Error.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Snackbar snackbar = Snackbar.make(getCurrentFocus(),mApiError.getErrorMsg(),Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
        @Override
        public void onFailure(Call call, Throwable t) {
            Timber.d(t, "Triggers fetching failed.");
        }
    }

    void showDialogBox(final String t_id, final String type){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);

        if(type.equalsIgnoreCase("gauge")){
            View promptsView = li.inflate(R.layout.custom_threshold, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final  Spinner optional1 = (Spinner) promptsView.findViewById(R.id.spinner_threshold);
            final EditText threshold = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput1);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    prepareCallBack(optional1,threshold,t_id, type);

                                }
                            })
                    .setNegativeButton("Cancel", null);

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

        if(type.equalsIgnoreCase("availability")){
            View promptsView = li.inflate(R.layout.custom_availability, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final Spinner optional1 = (Spinner) promptsView.findViewById(R.id.spinner_avail);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    prepareCallBack(optional1,null,t_id, type);

                                }
                            })
                    .setNegativeButton("Cancel", null);

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }


    }

    void prepareCallBack (Spinner optional, EditText threshold, String id, String type){


        if(type.equalsIgnoreCase("gauge")){

            ArrayList<ThresholdCondition> list = new ArrayList<>();
            ThresholdCondition condition = new ThresholdCondition();

            String dataId = "hm_g_" + getMetric().getId();
            condition.setType("THRESHOLD");
            condition.setDataId(dataId);

            condition.setOperator(optional.getSelectedItem().toString().toUpperCase());
            condition.setThreshold(Integer.parseInt(threshold.getText().toString()));

            list.add(0,condition);
            BackendClient.of(this).putTriggerThresholdCondition(id,"FIRING",list,new ConditionCallback());
        }

        if(type.equalsIgnoreCase("availability")){

            ArrayList<AvailabilityCondition> list = new ArrayList<>();
            AvailabilityCondition condition = new AvailabilityCondition();

            String dataId = "hm_a_" + getMetric().getId();
            condition.setType("AVAILABILITY");
            condition.setDataId(dataId);

            condition.setOperator(optional.getSelectedItem().toString().toUpperCase());
            list.add(0,condition);
            BackendClient.of(this).putTriggerAvailabilityCondition(id,"FIRING",list,new ConditionCallback());
        }


    }

    private class ConditionCallback implements Callback{


        @Override
        public void onResponse(Call call, Response response) {
            finish();
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            finish();
        }
    }
    private Metric getMetric() {
        return getIntent().getParcelableExtra(Intents.Extras.METRIC);
    }

}

