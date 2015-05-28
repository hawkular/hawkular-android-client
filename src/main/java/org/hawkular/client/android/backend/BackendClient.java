package org.hawkular.client.android.backend;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.hawkular.client.android.backend.model.Tenant;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.util.UrlUtils;

import java.net.MalformedURLException;
import java.net.URL;

public final class BackendClient
{
	public static final class Pipes
	{
		private Pipes() {
		}

		public static final String TENANTS = "tenants";
	}

	public static final class PipePaths
	{
		private PipePaths() {
		}

		public static final String TENANTS = "hawkular/inventory/tenant";
	}

	private final String serverUrl;

	public BackendClient(@NonNull String serverUrl) {
		this.serverUrl = serverUrl;

		setUpPipes();
	}

	private void setUpPipes() {
		PipeManager.config(Pipes.TENANTS, RestfulPipeConfiguration.class)
			.withUrl(getPipeUrl(PipePaths.TENANTS))
			.forClass(Tenant.class);
	}

	private URL getPipeUrl(String pipePath) {
		try {
			return UrlUtils.appendQueryToBaseURL(new URL(serverUrl), pipePath);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> LoaderPipe<T> getPipe(@NonNull String pipe, @NonNull Activity activity) {
		return PipeManager.getPipe(pipe, activity);
	}
}
