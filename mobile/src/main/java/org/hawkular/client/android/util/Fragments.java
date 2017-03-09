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
package org.hawkular.client.android.util;

import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.fragment.AlertDetailFragment;
import org.hawkular.client.android.fragment.AlertsFragment;
import org.hawkular.client.android.fragment.FavMetricsFragment;
import org.hawkular.client.android.fragment.MetricAvailabilityFragment;
import org.hawkular.client.android.fragment.MetricCounterFragment;
import org.hawkular.client.android.fragment.MetricGaugeFragment;
import org.hawkular.client.android.fragment.MetricsFragment;
import org.hawkular.client.android.fragment.SettingsFragment;
import org.hawkular.client.android.fragment.TriggerDetailFragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Fragment utilities.
 * <p/>
 * {@link org.hawkular.client.android.util.Fragments.Arguments} contains argument-related constants.
 * {@link org.hawkular.client.android.util.Fragments.Builder} helps with building fragments with right arguments.
 * {@link org.hawkular.client.android.util.Fragments.Operator} helps with setting fragments to right places.
 */
public final class Fragments {
    private Fragments() {
    }

    public static final class Arguments {
        private Arguments() {
        }

        public static final String ALERT = "alert";
        public static final String ENVIRONMENT = "environment";
        public static final String METRIC = "metric";
        public static final String OPERATION = "operation";
        public static final String RESOURCE = "resource";
        public static final String TRIGGER = "trigger";
    }

    public static final class Builder {
        private Builder() {
        }

        @NonNull
        public static Fragment buildAlertsFragment(@NonNull Resource resource) {
            Fragment fragment = new AlertsFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(Arguments.RESOURCE, resource);

            fragment.setArguments(arguments);

            return fragment;
        }

        @NonNull
        public static Fragment buildAlertDetailFragment(@NonNull Alert alert) {
            Fragment fragment = new AlertDetailFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(Arguments.ALERT, alert);

            fragment.setArguments(arguments);

            return fragment;
        }
        public static Fragment buildTriggerDetailFragment(@NonNull Trigger trigger){
            Fragment fragment = new TriggerDetailFragment();

            Bundle args = new Bundle();
            args.putParcelable(Arguments.TRIGGER,trigger);

            fragment.setArguments(args);

            return fragment;
        }

        @NonNull
        public static Fragment buildMetricAvailabilityFragment(@NonNull Metric metric) {
            Fragment fragment = new MetricAvailabilityFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(Arguments.METRIC, metric);

            fragment.setArguments(arguments);

            return fragment;
        }

        public static Fragment buildMetricCounterFragment(@NonNull Metric metric) {
            Fragment fragment = new MetricCounterFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(Arguments.METRIC, metric);

            fragment.setArguments(arguments);

            return fragment;
        }

        @NonNull
        public static Fragment buildMetricGaugeFragment(@NonNull Metric metric) {
            Fragment fragment = new MetricGaugeFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(Arguments.METRIC, metric);

            fragment.setArguments(arguments);

            return fragment;
        }

        @NonNull
        public static Fragment buildMetricsFragment(@NonNull Environment environment, @NonNull Resource resource) {
            Fragment fragment = new MetricsFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(Arguments.ENVIRONMENT, environment);
            arguments.putParcelable(Arguments.RESOURCE, resource);

            fragment.setArguments(arguments);

            return fragment;
        }

        @NonNull
        public static Fragment buildSettingsFragment() {
            return new SettingsFragment();
        }
    }

    public static final class Operator {
        private final FragmentManager fragmentManager;

        @NonNull
        public static Operator of(@NonNull FragmentActivity activity) {
            return new Operator(activity);
        }

        private Operator(FragmentActivity activity) {
            this.fragmentManager = activity.getSupportFragmentManager();
        }

        public void set(@IdRes int fragmentContainerId, @NonNull Fragment fragment) {
            if (fragmentManager.findFragmentById(fragmentContainerId) != null) {
                return;
            }

            fragmentManager
                    .beginTransaction()
                    .add(fragmentContainerId, fragment)
                    .commit();
        }

        public void reset(@IdRes int fragmentContainerId, @NonNull Fragment fragment) {
            fragmentManager
                    .beginTransaction()
                    .replace(fragmentContainerId, fragment)
                    .commit();
        }
    }
}
