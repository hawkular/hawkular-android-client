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

import android.support.annotation.NonNull;

import org.hawkular.client.android.backend.model.Persona;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.module.ModuleFields;
import org.jboss.aerogear.android.pipe.module.PipeModule;

import java.net.URI;

final class BackendAccountant implements PipeModule {
    private final Persona persona;

    public BackendAccountant(@NonNull Persona persona) {
        this.persona = persona;
    }

    @Override
    public ModuleFields loadModule(URI uri, String method, byte[] contents) {
        ModuleFields fields = new ModuleFields();

        fields.addHeader(BackendPipes.Headers.PERSONA, persona.getId());
        fields.addHeader(BackendPipes.Headers.TENANT, persona.getId());

        return fields;
    }

    @Override
    public boolean handleError(HttpException e) {
        return false;
    }
}
