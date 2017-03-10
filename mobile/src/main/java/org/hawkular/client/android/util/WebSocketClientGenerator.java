/**
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
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
package org.hawkular.client.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ws.WebSocketCall;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

/**
 * Can be used to generate HTTP clients including those that require SSL.
 *
 * Note that if the configuration's sslContext is null, this object will use the configured keystorePath
 * and keystorePassword to build one itself. If sslContext is provided (not-null) then the configuration's
 * keystorePath and keystorePassword are ignored.
 *
 * Note that if the configuration says to NOT use SSL in the first place, the given SSL context (if any)
 * as well as the configured keystorePath and keystorePassword will all be ignored since
 * we are being told to not use SSL at all.
 */
public class WebSocketClientGenerator {

    public static class Configuration {
        public static class Builder {
            private String username;
            private String password;
            private boolean useSSL;
            private String keystorePath;
            private String keystorePassword;
            private SSLContext sslContext;
            private int connectTimeoutSeconds = -1;
            private int readTimeoutSeconds = -1;
            public Builder() {
            }

            public Configuration build() {
                return new Configuration(username, password, useSSL, keystorePath, keystorePassword, sslContext,
                        connectTimeoutSeconds, readTimeoutSeconds);
            }

            public Builder username(String s) {
                this.username = s;
                return this;
            }

            public Builder password(String s) {
                this.password = s;
                return this;
            }

            public Builder useSsl(boolean b) {
                this.useSSL = b;
                return this;
            }

            public Builder keystorePath(String s) {
                this.keystorePath = s;
                return this;
            }

            public Builder keystorePassword(String s) {
                this.keystorePassword = s;
                return this;
            }

            public Builder sslContext(SSLContext s) {
                this.sslContext = s;
                return this;
            }
            public Builder connectTimeout(int connectTimeoutSeconds) {
                this.connectTimeoutSeconds = connectTimeoutSeconds;
                return this;
            }
            public Builder readTimeout(int readTimeoutSeconds) {
                this.readTimeoutSeconds = readTimeoutSeconds;
                return this;
            }
        }

        private final String username;
        private final String password;
        private final boolean useSSL;
        private final String keystorePath;
        private final String keystorePassword;
        private final SSLContext sslContext;
        private final int connectTimeoutSeconds;
        private final int readTimeoutSeconds;

        private Configuration(String username, String password, boolean useSSL, String keystorePath,
                              String keystorePassword, SSLContext sslContext, int connectTimeoutSeconds,
                              int readTimeoutSeconds) {
            this.username = username;
            this.password = password;
            this.useSSL = useSSL;
            this.keystorePath = keystorePath;
            this.keystorePassword = keystorePassword;
            this.sslContext = sslContext;
            this.connectTimeoutSeconds = connectTimeoutSeconds;
            this.readTimeoutSeconds = readTimeoutSeconds;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public boolean isUseSSL() {
            return useSSL;
        }

        public String getKeystorePath() {
            return keystorePath;
        }

        public String getKeystorePassword() {
            return keystorePassword;
        }

        public SSLContext getSslContext() {
            return sslContext;
        }

        public int getConnectTimeoutSeconds() {
            return connectTimeoutSeconds;
        }

        public int getReadTimeoutSeconds() {
            return readTimeoutSeconds;
        }
    }

    /** the configuration for our httpclient generator */
    private final Configuration configuration;

    /** The configured client singleton */
    private final OkHttpClient httpClient;

    public WebSocketClientGenerator(Configuration configuration) {
        this.configuration = configuration;

        OkHttpClient httpClient = new OkHttpClient();

        if(configuration.getConnectTimeoutSeconds()!=-1){
            httpClient.setConnectTimeout(configuration.getConnectTimeoutSeconds(), TimeUnit.SECONDS);
        }

        if(configuration.getReadTimeoutSeconds()!=-1){
            httpClient.setReadTimeout(configuration.getReadTimeoutSeconds(), TimeUnit.SECONDS);
        }
        if (this.configuration.isUseSSL()) {
            SSLContext theSslContextToUse;

            if (this.configuration.getSslContext() == null) {
                if (this.configuration.getKeystorePath() != null) {
                    theSslContextToUse = buildSSLContext(this.configuration.getKeystorePath(),
                            this.configuration.getKeystorePassword());
                } else {
                    theSslContextToUse = null; // rely on the JVM default
                }
            } else {
                theSslContextToUse = this.configuration.getSslContext();
            }

            if (theSslContextToUse != null) {
                httpClient.setSslSocketFactory(theSslContextToUse.getSocketFactory());
            }

            // does not perform any hostname verification when looking at the remote end's cert
            /*
            httpClient.setHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    log.debugf("HTTP client is blindly approving cert for [%s]", hostname);
                    return true;
                }
            });
            */
        }

        this.httpClient = httpClient;
    }

    /**
     * @return the fully configured HTTP client
     */
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Creates a websocket that connects to the given URL.
     *
     * @param url where the websocket server is
     * @param headers headers to pass in the connect request
     * @return the websocket
     */
    public WebSocketCall createWebSocketCall(String url, Map<String, String> headers) {
        String base64Credentials = buildBase64Credentials();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Basic " + base64Credentials)
                .addHeader("Accept", "application/json");

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }

        Request request = requestBuilder.build();
        WebSocketCall wsc = WebSocketCall.create(getHttpClient(), request);
        return wsc;
    }

    /**
     * @return The configuration used to build the HTTP client.
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * For client requests that need to send a Basic authorization header, this builds the
     * base-64 encoding of the username and password that is needed for that header.
     *
     * @return base-64 encoding of the "username:password" as required for Basic auth.
     */
    public String buildBase64Credentials() {
        // see http://en.wikipedia.org/wiki/Basic_access_authentication#Client_side
        return new String(Base64.encode((this.configuration.getUsername() +
                ":" + this.configuration.getPassword()).getBytes(), Base64.NO_WRAP));
    }

    private SSLContext buildSSLContext(String keystorePath, String keystorePassword) {
        try {
            KeyStore keyStore = readKeyStore(keystorePath, keystorePassword);
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory
                    .getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                    new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot create SSL context from keystore [%s]", keystorePath), e);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private KeyStore readKeyStore(String keystorePath, String keystorePassword) throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        // get user password and file input stream
        char[] password = keystorePassword.toCharArray();
        File file = new File(keystorePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            ks.load(fis, password);
        }
        return ks;
    }
}
