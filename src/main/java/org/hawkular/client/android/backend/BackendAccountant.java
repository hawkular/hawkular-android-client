package org.hawkular.client.android.backend;

import android.support.annotation.NonNull;

import org.hawkular.client.android.backend.model.Tenant;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.module.ModuleFields;
import org.jboss.aerogear.android.pipe.module.PipeModule;

import java.net.URI;

final class BackendAccountant implements PipeModule
{
    private final Tenant tenant;

    public BackendAccountant(@NonNull Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public ModuleFields loadModule(URI uri, String method, byte[] contents) {
        ModuleFields fields = new ModuleFields();

        fields.addHeader(BackendPipes.Headers.PERSONA, tenant.getId());
        fields.addHeader(BackendPipes.Headers.TENANT, tenant.getId());

        return fields;
    }

    @Override
    public boolean handleError(HttpException e) {
        return false;
    }
}
