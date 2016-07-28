/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
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
package org.hawkular.client.android.adapter;

import java.util.ArrayList;
import java.util.List;

import org.assertj.android.api.Assertions;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.ResourceProperties;
import org.hawkular.client.android.backend.model.ResourceType;
import org.hawkular.client.android.util.Randomizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public final class ResourcesAdapterTester {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void count() {
        List<Resource> resources = generateResources();

        ResourcesAdapter resourcesAdapter = new ResourcesAdapter(context, resources);

        Assertions.assertThat(resourcesAdapter).hasCount(resources.size() * resourcesAdapter.getViewTypeCount());
    }

    @Test
    public void item() {
        Resource resource = generateResource();

        List<Resource> resources = new ArrayList<>();
        resources.add(resource);
        resources.addAll(generateResources());

        ResourcesAdapter resourcesAdapter = new ResourcesAdapter(context, resources);

        Assertions.assertThat(resourcesAdapter).hasItem(resource, 0);
    }

    private List<Resource> generateResources() {
        List<Resource> resources = new ArrayList<>();

        for (int resourcePosition = 0; resourcePosition < Randomizer.generateNumber(); resourcePosition++) {
            resources.add(generateResource());
        }

        return resources;
    }

    private Resource generateResource() {
        return new Resource(
            Randomizer.generateString(),
            new ResourceType(Randomizer.generateString()),
            new ResourceProperties(Randomizer.generateString()));
    }
}
