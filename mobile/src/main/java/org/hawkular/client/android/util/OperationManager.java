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


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hawkular.client.android.R;
import org.hawkular.client.android.auth.AuthData;
import org.hawkular.client.android.auth.Session;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import android.content.Context;
import okio.Buffer;
import timber.log.Timber;

public class OperationManager {

    private String username;
    private String password;
    private final Context context;
    private static Callback<String> callback;
    private final SQLStore<Session> sessionStore;
    private String uri;
    private static OperationManager instance;
    private static WebSocket webSocket;
    private static boolean destroyed;
    private static final int RETRY = 500;

    public static final String REQUEST_TOKEN = "GenericSuccessResponse";
    public static final String SUCCESS_TOKEN = "ExecuteOperationResponse";

    private OperationManager(Context context) {
        this.context = context;
        this.sessionStore = openSessionStore();
        sessionStore.openSync();
    }

    public static OperationManager getInstance(Context context, Callback<String> callback) {
        OperationManager.callback = callback;
        if (instance != null && webSocket != null && !destroyed) {
            return instance;
        } else {
            instance = new OperationManager(context);
            instance.create();
            try {
                for (int i = 0; i < RETRY && webSocket == null; i++) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return instance;
        }
    }

    private void create() {

        fetchCredentials();

        generateUri();

        WebSocketClientGenerator.Configuration config = new WebSocketClientGenerator.Configuration.Builder()
                .username(username)
                .password(password)
                .useSsl(false)
                .sslContext(null)
                .keystorePath("")
                .keystorePassword("")
                .readTimeout(0)
                .connectTimeout(0)
                .build();

        WebSocketClientGenerator base = new WebSocketClientGenerator(config);
        Map<String, String> map = new HashMap<String, String>();
        map.put("Hawkular-Tenant", Preferences.of(context).personaId().get());
        WebSocketCall webSocketCall = base.createWebSocketCall(uri, map);

        webSocketCall.enqueue(new WebSocketListener() {

            @Override public void onOpen(WebSocket webSocket, Response response) {
                OperationManager.webSocket = webSocket;
                destroyed = false;
            }

            @Override public void onFailure(IOException e, Response response) {
                Timber.d(e.getMessage());
                destroyed = true;
                disconnect();
            }

            @Override public void onMessage(ResponseBody message) throws IOException {
                String m = message.string();
                String[] temp = m.split("=", 2);
                if (temp[0].equals(REQUEST_TOKEN)) {
                    callback.onSuccess(context.getString(R.string.success_websocket_request));
                } else if (temp[0].equals(SUCCESS_TOKEN)) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(temp[1]);
                        if (jsonObject.getString("status").equals("OK")) {
                            callback.onSuccess(context.getString(R.string.success_websocket_operation_complete));
                        } else {
                            callback.onSuccess(context.getString(R.string.fail_websocket_operation_complete));
                        }
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }

                }
            }

            @Override public void onPong(Buffer payload) {
            }

            @Override public void onClose(int code, String reason) {
                destroyed = true;
                disconnect();
            }
        });
    }

    private void generateUri() {
        String url = Preferences.of(context).host().get();
        if (Preferences.of(context).port().isSet()) {
            url += ":" + Preferences.of(context).port().get();
        }

        url += "/hawkular/command-gateway/ui/ws";
        uri = "ws://" + url;

    }

    private void disconnect() {
        try {
            if (webSocket != null) {
                webSocket.close(2000, "Destroyed");
            }
        } catch (IOException e) {
            Timber.e(e.getMessage());
        }
    }

    private void fetchCredentials() {
        Session session = sessionStore.read(AuthData.NAME);
        username = session.getUsername();
        password = session.getPassword();
    }

    private SQLStore<Session> openSessionStore() {
        DataManager.config(AuthData.STORE, SQLStoreConfiguration.class)
                .withContext(context)
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public String generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store(Session.class);
        return (SQLStore<Session>) DataManager.getStore(AuthData.STORE);
    }

    public void sendRequest(String messageString) {
        messageString = "ExecuteOperationRequest=" + messageString;
        if (webSocket == null) {
            callback.onFailure(new IllegalStateException(context.getString(R.string.error_websocket_close)));
        } else {
            Buffer buffer = new Buffer();
            buffer.writeUtf8(messageString);
            RequestBody requestBody = RequestBody.create(WebSocket.TEXT, buffer.readByteArray());
            try {
                webSocket.sendMessage(requestBody);
            } catch (IOException e) {
                callback.onFailure(e);
            }
        }
    }

}
