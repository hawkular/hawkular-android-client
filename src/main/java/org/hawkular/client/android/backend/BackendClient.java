package org.hawkular.client.android.backend;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.hawkular.client.android.backend.model.ResourceType;
import org.hawkular.client.android.backend.model.Tenant;
import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.util.UrlUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public final class BackendClient
{
	private static final class BackendClientHolder
	{
		public static final BackendClient BACKEND_CLIENT = new BackendClient();
	}

	private String serverUrl;

	public static BackendClient getInstance() {
		return BackendClientHolder.BACKEND_CLIENT;
	}

	private BackendClient() {
	}

	public void setServerUrl(@NonNull String serverUrl) {
		this.serverUrl = serverUrl;

		setUpAuthorization();

		setUpPipes();
	}

	private void setUpAuthorization() {
		AuthorizationManager.config(BackendAuthorization.NAME, OAuth2AuthorizationConfiguration.class)
			.setBaseURL(getServerUrl(BackendAuthorization.Paths.BASE))
			.setAccessTokenEndpoint(BackendAuthorization.Paths.ENDPOINT_ACCESS)
			.setAuthzEndpoint(BackendAuthorization.Paths.ENDPOINT_AUTHZ)
			.setRefreshEndpoint(BackendAuthorization.Paths.ENDPOINT_REFRESH)
			.setAccountId(BackendAuthorization.Ids.ACCOUNT)
			.setClientId(BackendAuthorization.Ids.CLIENT)
			.setRedirectURL(getServerUrl(BackendAuthorization.Paths.REDIRECT).toString())
			.asModule();
	}

	private URL getServerUrl(String path) {
		return UrlUtils.appendToBaseURL(getServerUrl(), path);
	}

	private URL getServerUrl() {
		try {
			return new URL(serverUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private void setUpPipes() {
		setUpPipe(BackendPipes.Names.RESOURCE_TYPES, BackendPipes.Paths.RESOURCE_TYPES, ResourceType.class);
		setUpPipe(BackendPipes.Names.TENANTS, BackendPipes.Paths.TENANTS, Tenant.class);
	}

	private void setUpPipe(String pipeName, String pipePath, Class pipeClass) {
		PipeManager.config(pipeName, RestfulPipeConfiguration.class)
			.module(getAuthorizationModule())
			.withUrl(getServerUrl(pipePath))
			.forClass(pipeClass);
	}

	private AuthzModule getAuthorizationModule() {
		return AuthorizationManager.getModule(BackendAuthorization.NAME);
	}

	public boolean isAuthorized() {
		return getAuthorizationModule().isAuthorized();
	}

	public void authorize(@NonNull Activity activity, @NonNull Callback<String> authorizationCallback) {
		getAuthorizationModule().requestAccess(activity, authorizationCallback);
	}

	@SuppressWarnings("unchecked")
	public <T> LoaderPipe<T> getPipe(@NonNull String pipe, @NonNull Activity activity) {
		return PipeManager.getPipe(pipe, activity);
	}

	@SuppressWarnings("unchecked")
	public void getResourceTypes(@NonNull Tenant tenant, @NonNull Activity activity, @NonNull Callback<List<ResourceType>> callback) {
		ReadFilter filter = new ReadFilter();
		filter.setLinkUri(getPathUri(String.format("%s/resourceTypes", tenant.getId())));

		PipeManager.getPipe(BackendPipes.Names.RESOURCE_TYPES, activity).read(filter, callback);
	}

	private URI getPathUri(String path) {
		try {
			return new URI(null, null, path, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
