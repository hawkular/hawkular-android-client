package org.hawkular.client.android.event;

import android.support.annotation.NonNull;

import org.hawkular.client.android.backend.model.Resource;

public class ResourceSelectedEvent {
    private final Resource resource;

    public ResourceSelectedEvent(@NonNull Resource resource) {
        this.resource = resource;
    }

    @NonNull
    public Resource getResource() {
        return resource;
    }
}
