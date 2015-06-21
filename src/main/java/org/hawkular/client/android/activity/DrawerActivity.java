package org.hawkular.client.android.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import org.hawkular.client.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.drawer)
    DrawerLayout drawer;

    @InjectView(R.id.navigation)
    NavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        setUpBindings();

        setUpToolbar();

        setUpNavigation();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setUpNavigation() {
        navigation.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawer.closeDrawers();

        menuItem.setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(Gravity.LEFT);
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
