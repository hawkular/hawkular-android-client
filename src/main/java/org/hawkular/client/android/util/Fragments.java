/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
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

import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.fragment.AlertsFragment;
import org.hawkular.client.android.fragment.MetricFragment;
import org.hawkular.client.android.fragment.MetricsFragment;
import org.hawkular.client.android.fragment.ResourcesFragment;
import org.hawkular.client.android.fragment.SettingsFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

public final class Fragments {
    private Fragments() {
    }

    public static final class Arguments {
        private Arguments() {
        }

        public static final String ENVIRONMENT = "environment";
        public static final String METRIC = "metric";
        public static final String RESOURCE = "resource";
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
        public static Fragment buildMetricFragment(@NonNull Metric metric) {
            Fragment fragment = new MetricFragment();

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
        public static Fragment buildResourcesFragment(@NonNull Environment environment) {
            Fragment fragment = new ResourcesFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(Arguments.ENVIRONMENT, environment);

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
        public static Operator of(@NonNull Activity activity) {
            return new Operator(activity);
        }

        private Operator(Activity activity) {
            this.fragmentManager = activity.getFragmentManager();
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
