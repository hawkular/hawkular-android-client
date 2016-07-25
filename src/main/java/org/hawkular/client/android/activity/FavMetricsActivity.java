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
package org.hawkular.client.android.activity;

import org.hawkular.client.android.R;
import org.hawkular.client.android.util.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavMetricsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Fragment metricsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);

        setUpBindings();

        setUpToolbar();

        setUpMetrics();

    }

    private void setUpBindings() {
        ButterKnife.bind(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpMetrics() {
        metricsFragment = getMetricsFragment();
        Fragments.Operator.of(this).set(R.id.layout_container, metricsFragment);
    }

    public Fragment getMetricsFragment() {
        return Fragments.Builder.buildFavMetricsFragment();
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
