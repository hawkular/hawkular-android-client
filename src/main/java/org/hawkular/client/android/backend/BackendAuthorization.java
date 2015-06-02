package org.hawkular.client.android.backend;

public final class BackendAuthorization
{
	private BackendAuthorization() {
	}

	public static final String NAME = "hawkular";

	static final class Paths
	{
		private Paths() {
		}

		public static final String BASE = "/auth";

		public static final String ENDPOINT_AUTHZ = "/realms/hawkular/tokens/login";
		public static final String ENDPOINT_ACCESS = "/realms/hawkular/tokens/access/codes";
		public static final String ENDPOINT_REFRESH = "/realms/hawkular/tokens/refresh";

		public static final String REDIRECT = "/oauth2";
	}

	static final class Ids
	{
		private Ids() {
		}

		public static final String ACCOUNT = "keycloak-token";
		public static final String CLIENT = "hawkular-ui";
	}
}
