/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
