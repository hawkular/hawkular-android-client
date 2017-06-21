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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import org.hawkular.client.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;

/**
 * Created by pallavi on 19/06/17.
 */


public class TriggerSetupActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
    void getValues(){
        // a test printing statement to check if bindings are setup properly
        Log.d("Value", "" + switchAutoDisable.isChecked() + spinnerEventType.getSelectedItem().toString());
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

}

