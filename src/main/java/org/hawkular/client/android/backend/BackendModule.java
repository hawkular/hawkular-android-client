package org.hawkular.client.android.backend;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
	complete = false,
	library = true
)
public class BackendModule
{
	@Provides
	@Singleton
	BackendClient provideClient() {
		return new BackendClient();
	}
}
