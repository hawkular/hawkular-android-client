package org.hawkular.client.android;

import org.hawkular.client.android.activity.LauncherActivity;
import org.hawkular.client.android.backend.BackendModule;

import dagger.Module;

@Module(
	includes = {
		BackendModule.class
	},
	injects = {
		LauncherActivity.class
	}
)
final class HawkularModule
{
}
