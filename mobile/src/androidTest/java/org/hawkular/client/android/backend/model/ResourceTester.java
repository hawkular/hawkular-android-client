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
package org.hawkular.client.android.backend.model;

import org.assertj.core.api.Assertions;
import org.hawkular.client.android.util.Parceler;
import org.hawkular.client.android.util.Randomizer;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public final class ResourceTester {
    @Test
    public void parcelable() {
        Resource originalResource = new Resource(
            Randomizer.generateString(),
            new ResourceType(Randomizer.generateString()),
            new ResourceProperties(Randomizer.generateString()));
        Resource parceledResource = Parceler.parcel(Resource.CREATOR, originalResource);

        Assertions.assertThat(parceledResource.getId()).isEqualTo(
            originalResource.getId());
        Assertions.assertThat(parceledResource.getType().getId()).isEqualTo(
            originalResource.getType().getId());
        Assertions.assertThat(parceledResource.getProperties().getUrl()).isEqualTo(
            originalResource.getProperties().getUrl());
    }
}
