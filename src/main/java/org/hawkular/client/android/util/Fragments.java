package org.hawkular.client.android.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import org.hawkular.client.android.backend.model.Environment;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.fragment.AlertsFragment;
import org.hawkular.client.android.fragment.ResourcesFragment;

public final class Fragments {
    private Fragments() {
    }

    public static final class Arguments {
        private Arguments() {
        }

        public static final String TENANT = "tenant";
        public static final String ENVIRONMENT = "environment";
    }

    public static final class Builder
    {
        private Builder() {
        }

        @NonNull
        public static Fragment buildResourcesFragment(@NonNull Tenant tenant, @NonNull Environment environment) {
            Fragment fragment = new ResourcesFragment();

            Bundle arguments = new Bundle();
            arguments.putParcelable(Arguments.TENANT, tenant);
            arguments.putParcelable(Arguments.ENVIRONMENT, environment);

            fragment.setArguments(arguments);

            return fragment;
        }

        @NonNull
        public static Fragment buildAlertsFragment() {
            return new AlertsFragment();
        }
    }

    public static final class Operator
    {
        private final FragmentManager fragmentManager;

        public static Operator of(@NonNull Activity activity) {
            return new Operator(activity);
        }

        private Operator(Activity activity) {
            this.fragmentManager = activity.getFragmentManager();
        }

        public void reset(@IdRes int fragmentContainerId, @NonNull Fragment fragment) {
            fragmentManager
                .beginTransaction()
                .replace(fragmentContainerId, fragment)
                .commit();
        }
    }
}
