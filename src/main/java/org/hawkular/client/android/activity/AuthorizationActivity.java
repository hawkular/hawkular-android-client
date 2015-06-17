package org.hawkular.client.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import org.hawkular.client.android.R;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.BackendEndpoints;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Android;
import org.hawkular.client.android.util.Preferences;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public final class AuthorizationActivity extends AppCompatActivity implements Callback<String> {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.edit_host)
    EditText hostEdit;

    @InjectView(R.id.edit_port)
    EditText portEdit;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_authorization);

        setUpBindings();

        setUpToolbar();

        setUpDefaults();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setUpDefaults() {
        if (Android.isDebugging()) {
            hostEdit.append(BackendEndpoints.Community.HOST);
            portEdit.append(BackendEndpoints.Community.PORT);
        }
    }

    @OnClick(R.id.button_authorize)
    public void setUpAuthorization() {
        if (getHost().isEmpty()) {
            showError(hostEdit, R.string.error_empty);
            return;
        }

        if (getPort().isEmpty()) {
            showError(portEdit, R.string.error_empty);
            return;
        }

        setUpBackendAuthorization();
    }

    private String getHost() {
        return hostEdit.getText().toString().trim();
    }

    private String getPort() {
        return portEdit.getText().toString().trim();
    }

    private void showError(EditText errorEdit, @StringRes int errorMessage) {
        errorEdit.setError(getString(errorMessage));
    }

    private void setUpBackendAuthorization() {
        try {
            BackendClient.getInstance().setUpBackend(getHost(), getPort());

            BackendClient.getInstance().authorize(this, this);
        } catch (RuntimeException e) {
            Timber.d(e, "Authorization failed.");

            showError(R.string.error_authorization_host_port);
        }
    }

    private void showError(@StringRes int errorMessage) {
        Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(String authorization) {
        setUpTenant();
    }

    @Override
    public void onFailure(Exception e) {
        Timber.d(e, "Authorization failed.");

        showError(R.string.error_general);
    }

    private void setUpTenant() {
        BackendClient.getInstance().getTenants(this, new TenantsCallback());
    }

    private void saveBackendPreferences(Tenant tenant) {
        Preferences.ofBackend(this).host().set(getHost());
        Preferences.ofBackend(this).port().set(getPort());
        Preferences.ofBackend(this).tenant().set(tenant.getId());
    }

    private void succeed() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private static final class TenantsCallback extends AbstractActivityCallback<List<Tenant>> {
        @Override
        public void onSuccess(List<Tenant> tenants) {
            if (tenants.isEmpty()) {
                Timber.d("Tenants list is empty, this should not happen.");
                return;
            }

            AuthorizationActivity activity = (AuthorizationActivity) getActivity();

            activity.saveBackendPreferences(tenants.get(0));
            activity.succeed();
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d(e, "Tenants retrieving failed.");
        }
    }
}
