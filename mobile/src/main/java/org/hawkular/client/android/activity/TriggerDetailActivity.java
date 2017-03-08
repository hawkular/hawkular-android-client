package org.hawkular.client.android.activity;

import org.hawkular.client.android.backend.model.Trigger;
import org.hawkular.client.android.util.Fragments;
import org.hawkular.client.android.util.Intents;

import android.support.v4.app.Fragment;


public class TriggerDetailActivity extends DetailActivity {
    @Override protected Fragment getDetailFragment() {
        Trigger trigger= getIntent().getParcelableExtra(Intents.Extras.TRIGGER);
        return Fragments.Builder.buildTriggerDetailFragment(trigger);
    }
}
