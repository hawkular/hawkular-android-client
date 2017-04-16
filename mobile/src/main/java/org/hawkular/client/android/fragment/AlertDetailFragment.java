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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.NotesAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.AlertEvaluation;
import org.hawkular.client.android.backend.model.AlertType;
import org.hawkular.client.android.backend.model.Note;
import org.hawkular.client.android.util.ColorSchemer;
import org.hawkular.client.android.util.ErrorUtil;
import org.hawkular.client.android.util.Formatter;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Preferences;
import org.hawkular.client.android.util.Uris;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractSupportFragmentCallback;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

/**
 * Alert Detail fragment.
 * <p/>
 * Displays detail of alert and allow some alert-related actions, such as acknowledgement,
 * resolving, commenting and viewing the comments.
 */

public class AlertDetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.content)
    SwipeRefreshLayout contentLayout;

    @BindView(R.id.list)
    ListView list;

    @BindView(R.id.alert_detail)
    TextView detail;

    @BindView(R.id.alert_severity)
    TextView severity;

    @BindView(R.id.alert_trigger)
    TextView trigger;

    @BindView(R.id.alert_note_edit)
    EditText editNote;

    @BindView(R.id.alert_fab)
    FloatingActionButton fab;

    @BindView(R.id.alert_state_status)
    Button alertSateButton;

    @State
    @Nullable
    Alert alert;

    @State
    @Nullable
    ArrayList<Note> notes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.container);
        View v = getActivity().getLayoutInflater().inflate(R.layout.layout_alert_detail, parent, false);
        parent.addView(v);

        setUpState(state);

        setUpBindings();

        setUpRefreshing();

        setUpAlertDetail();

        setUpAlertDetail();
    }

    private void setUpAlertDetail() {

        setUpAlert();

        setUpDetail();

        setUpNotes();
    }

    private void setUpAlert() {
        alert = getArguments().getParcelable(Fragments.Arguments.ALERT);
    }

    private void setUpDetail() {

        detail.setText(getAlertMessage(getActivity(), alert));
        alertSateButton.setText(alert.getStatus());
        alertSateButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showAlertMenu(alertSateButton);
            }
        });
        severity.setText(alert.getSeverity());
        trigger.setText(alert.getTrigger().getDescription());
    }


    private void setUpNotes() {

        notes = new ArrayList<>(alert.getNotes());
        list.setAdapter(new NotesAdapter(getActivity(), notes));
        hideRefreshing();
        showList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(editNote.getText().toString().trim().equals("")){
                    ErrorUtil.showError(getActivity().findViewById(android.R.id.content),R.string.message_empty);
                } else {
                editNote.setEnabled(false);
                String name = Preferences.of(getActivity()).personaName().get();
                Map<String, String> parameters = new HashMap<>();
                parameters.put("user", name);
                parameters.put("text", editNote.getText().toString());
                URI uri = Uris.getUri("", parameters);
                    /*Small work around of adding query parameter in the end of recordid of Note
                     *have been done here to use the aerogear pipe in the condition
                     * of PUT request with query parameter*/
                Note note = new Note(alert.getId()+uri.toString(), name,
                        editNote.getText().toString(), (new Date()).getTime());
                BackendClient.of(getFragment()).noteOnAlert(note, new NoteActionCallback(note));
                }
            }
        });
    }

    private void hideRefreshing() {
        contentLayout.setRefreshing(false);
    }

    private void showList() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.content);
    }


    private void setUpState(Bundle state) {
        Icepick.restoreInstanceState(this, state);
    }

    private void setUpBindings() {
        ButterKnife.bind(this, getView());
    }

    private void setUpRefreshing() {
        contentLayout.setOnRefreshListener(this);
        contentLayout.setColorSchemeResources(ColorSchemer.getScheme());
    }

    private void showAlertMenu(final View view) {
        PopupMenu alertMenu = new PopupMenu(getActivity(), view);

        alertMenu.getMenuInflater().inflate(R.menu.popup_alerts, alertMenu.getMenu());

        alertMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.menu_resolve:
                        BackendClient.of(AlertDetailFragment.this).
                                resolveAlert(alert, new AlertActionCallback(R.string.alert_state_res));
                        return true;

                    case R.id.menu_acknowledge:
                        BackendClient.of(AlertDetailFragment.this).
                                acknowledgeAlert(alert, new AlertActionCallback(R.string.alert_state_ack));
                        return true;

                    default:
                        return false;
                }
            }
        });

        alertMenu.show();
    }

    @Override public void onRefresh() {
        setUpAlertDetail();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        tearDownState(state);
    }

    private void tearDownState(Bundle state) {
        Icepick.saveInstanceState(this, state);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        tearDownBindings();
    }

    private void tearDownBindings() {
        //ButterKnife.unbind(this);
    }

    private String getAlertTimestamp(Context context, long timestamp) {
        return Formatter.formatDateTime(context, timestamp);
    }

    private String getAlertMessage(Context context, Alert alert) {
        switch (getAlertType(alert)) {
            case AVAILABILITY:
                return getAvailabilityAlertMessage(context, alert);

            case THRESHOLD:
                return getThresholdAlertMessage(context, alert);

            default:
                throw new RuntimeException("Alert is not supported.");
        }
    }

    private AlertType getAlertType(Alert alert) {
        for (List<AlertEvaluation> alertEvaluations : alert.getEvaluations()) {
            for (AlertEvaluation alertEvaluation : alertEvaluations) {
                AlertType alertType = alertEvaluation.getCondition().getType();

                if (alertType != null) {
                    return alertType;
                }
            }
        }

        throw new RuntimeException("No alert type found.");
    }

    private String getAvailabilityAlertMessage(Context context, Alert alert) {
        long alertStartTimestamp = getAlertStartTimestamp(alert);
        long alertFinishTimestamp = getAlertFinishTimestamp(alert);

        return context.getString(R.string.mask_alert_availability,
                getAlertDuration(alertStartTimestamp, alertFinishTimestamp),
                getAlertTimestamp(context, alertFinishTimestamp));
    }

    private long getAlertStartTimestamp(Alert alert) {
        List<Long> alertStartTimestamps = new ArrayList<>();

        for (List<AlertEvaluation> alertEvaluations : alert.getEvaluations()) {
            for (AlertEvaluation alertEvaluation : alertEvaluations) {
                alertStartTimestamps.add(alertEvaluation.getDataTimestamp());
            }
        }

        return Collections.min(alertStartTimestamps);
    }

    private long getAlertFinishTimestamp(Alert alert) {
        return alert.getTimestamp();
    }

    private int getAlertDuration(long alertStartTimestamp, long alertFinishTimestamp) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(alertFinishTimestamp - alertStartTimestamp);
    }

    private String getThresholdAlertMessage(Context context, Alert alert) {
        long alertStartTimestamp = getAlertStartTimestamp(alert);
        long alertFinishTimestamp = getAlertFinishTimestamp(alert);

        double alertAverage = getAlertAverage(alert);
        double alertThreshold = getAlertThreshold(alert);

        return context.getString(R.string.mask_alert_threshold,
                alertThreshold,
                getAlertDuration(alertStartTimestamp, alertFinishTimestamp),
                getAlertTimestamp(context, alertFinishTimestamp),
                alertAverage);
    }

    private double getAlertAverage(Alert alert) {
        double alertValuesSum = 0;
        long alertValuesCount = 0;

        for (List<AlertEvaluation> alertEvaluations : alert.getEvaluations()) {
            for (AlertEvaluation alertEvaluation : alertEvaluations) {
                alertValuesSum += Double.valueOf(alertEvaluation.getValue());

                alertValuesCount++;
            }
        }

        return alertValuesSum / alertValuesCount;
    }

    private double getAlertThreshold(Alert alert) {
        for (List<AlertEvaluation> alertEvaluations : alert.getEvaluations()) {
            for (AlertEvaluation alertEvaluation : alertEvaluations) {
                double alertThreshold = alertEvaluation.getCondition().getThreshold();

                if (alertThreshold >= 0) {
                    return alertThreshold;
                }
            }
        }

        throw new RuntimeException("No alert threshold found.");
    }

    private AlertDetailFragment getFragment() {
        return this;
    }

    private final class AlertActionCallback extends AbstractSupportFragmentCallback<List<String>> {

        @StringRes int state;

        AlertActionCallback(@StringRes int state) {
            this.state = state;
        }

        @Override
        public void onSuccess(List<String> result) {
            alertSateButton.setText(state);
        }

        @Override
        public void onFailure(Exception e) {
            ErrorUtil.showError(getActivity().findViewById(android.R.id.content),R.string.error_later);
        }

        private AlertsFragment getAlertsFragment() {
            return (AlertsFragment) getSupportFragment();
        }
    }

    private final class NoteActionCallback extends AbstractSupportFragmentCallback<List<String>> {

        Note note;

        public NoteActionCallback(Note note) {
            this.note = note;
        }

        @Override
        public void onSuccess(List<String> result) {
            editNote.setText("");
            editNote.setEnabled(true);
            alert.getNotes().add(note);
            setUpNotes();
        }

        @Override
        public void onFailure(Exception e) {
            editNote.setEnabled(true);
        }

    }

}
