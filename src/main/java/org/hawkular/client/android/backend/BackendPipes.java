package org.hawkular.client.android.backend;

public final class BackendPipes
{
	private BackendPipes() {
	}

	public static final class Names
	{
		private Names() {
		}

		public static final String TENANTS = "tenants";
	}

	static final class Paths
	{
		private Paths() {
		}

		public static final String TENANTS = "hawkular/inventory/tenant";
	}
}
