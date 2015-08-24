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
package org.hawkular.client.android.fragment;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.util.Cookies;
import org.hawkular.client.android.util.Preferences;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Settings fragment.
 *
 * Displays settings, at this point contains only the logging out action.
 */
public final class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    @BindString(R.string.settings_key_account_sign_out)
    String accountKey;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setUpBindings();

        setUpSettings();

        setUpListeners();
    }

    private void setUpBindings() {
        ButterKnife.bind(this, getParentView());
    }

    private View getParentView() {
        return getActivity().getWindow().getDecorView();
    }

    private void setUpSettings() {
        addPreferencesFromResource(R.xml.settings);
    }

    private void setUpListeners() {
        findPreference(accountKey).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (accountKey.equals(preference.getKey())) {
            tearDownAuthorization();
        }

        return true;
    }

    private void tearDownAuthorization() {
        Activity activity = getActivity();

        Preferences.of(activity).host().delete();
        Preferences.of(activity).port().delete();
        Preferences.of(activity).personaId().delete();
        Preferences.of(activity).personaName().delete();
        Preferences.of(activity).environment().delete();

        BackendClient.of(this).deauthorize();

        // TODO: remove after upstream fix.
        // https://issues.jboss.org/browse/AGDROID-485
        Cookies.clear();

        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        tearDownBindings();
    }

    private void tearDownBindings() {
        ButterKnife.unbind(this);
    }
}
