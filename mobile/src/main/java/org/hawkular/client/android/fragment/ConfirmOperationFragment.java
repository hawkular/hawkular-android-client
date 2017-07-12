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
package org.hawkular.client.android.fragment;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Operation;
import org.hawkular.client.android.backend.model.OperationParameter;
import org.hawkular.client.android.backend.model.OperationProperties;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.OperationManager;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.callback.AbstractSupportFragmentCallback;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import timber.log.Timber;

public class ConfirmOperationFragment extends AppCompatDialogFragment {

    private TableLayout table;
    private Button execute, cancel;
    private TextView operationDetail;
    private Toolbar toolbar;
    private SwitchCompat custom;
    private ScrollView scroll;
    private Callback<String> callback;

    private Resource resource;
    private Operation operation;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_operation, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        table = (TableLayout) view.findViewById(R.id.operation_param_table);
        execute = (Button) view.findViewById(R.id.execute);
        cancel = (Button) view.findViewById(R.id.cancel);
        operationDetail = (TextView) view.findViewById(R.id.operation_detail);
        custom = (SwitchCompat) view.findViewById(R.id.custom);
        scroll = (ScrollView) view.findViewById(R.id.scroll);

        resource = getResource();
        operation = getOperation();
        scroll.setVisibility(View.GONE);

        toolbar.setTitle(R.string.operation_confirm_title);

        setOperationDetail();

        BackendClient.of(ConfirmOperationFragment.this).getOperationProperties(new ConfirmOperationFragment.OperationPropertiesCallback(),
                operation, resource);

        execute.setOnClickListener(new ClickListner());
        cancel.setOnClickListener(new ClickListner());

        custom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    scroll.setVisibility(View.VISIBLE);
                } else {
                    scroll.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setCallback(Callback<String> callback) {
        this.callback = callback;
    }

    private void setOperationDetail() {
        operationDetail.setText(operation.getName() + " on " + resource.getId());
    }

    private Resource getResource() {
        return getArguments().getParcelable(Fragments.Arguments.RESOURCE);
    }

    private Operation getOperation() {
        return getArguments().getParcelable(Fragments.Arguments.OPERATION);
    }

    private void createTable(OperationProperties operationProperties) {
        TableRow row = null;
        Map<String, OperationParameter> operationParameters = operationProperties.getOperationParameters();

        if (operationParameters != null) {
            for (Map.Entry<String, OperationParameter> entry : operationParameters.entrySet()) {
                switch (entry.getValue().getType()) {
                    case "string": {
                        row = (TableRow) LayoutInflater.from(getActivity()).inflate(R.layout.row_edit, null);
                        EditText data = (EditText) row.findViewById(R.id.data);
                        data.setText(entry.getValue().getDefaultValue());
                        data.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    }

                    case "int": {
                        row = (TableRow) LayoutInflater.from(getActivity()).inflate(R.layout.row_edit, null);
                        EditText data = (EditText) row.findViewById(R.id.data);
                        data.setText(entry.getValue().getDefaultValue());
                        data.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    }

                    case "float": {
                        row = (TableRow) LayoutInflater.from(getActivity()).inflate(R.layout.row_edit, null);
                        EditText data = (EditText) row.findViewById(R.id.data);
                        data.setText(entry.getValue().getDefaultValue());
                        data.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                    }

                    case "bool": {
                        row = (TableRow) LayoutInflater.from(getActivity()).inflate(R.layout.row_toggle, null);
                        SwitchCompat data = (SwitchCompat) row.findViewById(R.id.data);
                        data.setChecked(entry.getValue().getDefaultValue().equals("true"));
                        break;
                    }
                }
                if (row != null) {
                    TextView name = (TextView) row.findViewById(R.id.name);
                    name.setText(entry.getKey());
                    table.addView(row);
                }
            }
            table.requestLayout();
        }
    }

    private void sendRequest(Map<String, String> map) {
        JSONObject body = new JSONObject();
        try {
            body.put("operationName", operation.getId());
           // body.put("resourcePath", // TODO: 05/07/17 Fix it );
            if (custom.isChecked()) {
                Set<Map.Entry<String, String>> set = map.entrySet();
                JSONObject params = new JSONObject();

                body.put("parameters", params);

                for (Map.Entry<String, String> entry : set) {
                    params.put(entry.getKey(), entry.getValue());
                }
            }

        } catch (JSONException e) {
            Timber.e(e.getMessage());
        }

        OperationManager operationManager =
                OperationManager.getInstance(getActivity(), callback);
        operationManager.sendRequest(body.toString());
        dismiss();
    }


    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    private class ClickListner implements View.OnClickListener {
        @Override public void onClick(View view) {
            if (view.getId() == R.id.cancel) {
                dismiss();
            } else if (view.getId() == R.id.execute) {
                boolean valid = true;
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < table.getChildCount(); i++) {
                    View v = table.getChildAt(i);
                    String name = ((TextView) v.findViewById(R.id.name)).getText().toString();
                    View data = v.findViewById(R.id.data);
                    if (data instanceof SwitchCompat) {
                        map.put(name, ((SwitchCompat) data).isChecked() ? "true" : "false");
                    } else if (data instanceof EditText) {
                        String value = ((EditText) data).getText().toString();
                        if (value.trim().equals("")) {
                            valid = false;
                            ((EditText) data).setError(getString(R.string.error_empty));
                        } else {
                            map.put(name, value);
                        }

                    }
                }

                if (valid) {
                    sendRequest(map);
                }
            }

        }
    }

    private void setUpParams(List<OperationProperties> operationProperties) {

        if (operationProperties.size() == 0 ||
                operationProperties.get(0).getOperationParameters().size() == 0 ) {
            custom.setEnabled(false);
        } else {
            createTable(operationProperties.get(0));
        }

    }

    private static final class OperationPropertiesCallback extends AbstractSupportFragmentCallback<List<OperationProperties>> {
        @Override
        public void onSuccess(List<OperationProperties> result) {
            getConfirmOperationFragment().setUpParams(result);
        }

        @Override
        public void onFailure(Exception e) {
        }

        private ConfirmOperationFragment getConfirmOperationFragment() {
            return (ConfirmOperationFragment) getSupportFragment();
        }
    }


}
