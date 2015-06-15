package org.hawkular.client.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.AlertsAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Alert;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public final class AlertsActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.list)
    ListView list;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_list);

        setUpBindings();

        setUpToolbar();

        setUpAlerts();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpAlerts() {
        showProgress();

        BackendClient.getInstance().getAlerts(this, new AlertsCallback());
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private void setUpAlerts(List<Alert> alerts) {
        list.setAdapter(new AlertsAdapter(this, alerts));

        hideProgress();
    }

    private void hideProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.list);
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

    private static final class AlertsCallback extends AbstractActivityCallback<List<Alert>> {
        @Override
        public void onSuccess(List<Alert> alerts) {
            Timber.d("Alert :: Success!");

            AlertsActivity activity = (AlertsActivity) getActivity();

            activity.setUpAlerts(alerts);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Alert :: Failure...");
        }
    }
}
