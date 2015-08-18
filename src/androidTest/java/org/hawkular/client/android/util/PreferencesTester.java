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
package org.hawkular.client.android.util;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public final class PreferencesTester {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void environment() {
        String environment = Randomizer.generateString();

        Preferences.of(context).environment().set(environment);

        Assertions.assertThat(Preferences.of(context).environment().get()).isEqualTo(environment);
    }

    @Test
    public void host() {
        String host = Randomizer.generateString();

        Preferences.of(context).host().set(host);

        Assertions.assertThat(Preferences.of(context).host().get()).isEqualTo(host);
    }

    @Test
    public void port() {
        int port = (int) Randomizer.generateNumber();

        Preferences.of(context).port().set(port);

        Assertions.assertThat(Preferences.of(context).port().get()).isEqualTo(port);
    }

    @Test
    public void personaId() {
        String personaId = Randomizer.generateString();

        Preferences.of(context).personaId().set(personaId);

        Assertions.assertThat(Preferences.of(context).personaId().get()).isEqualTo(personaId);
    }

    @Test
    public void personaName() {
        String personaName = Randomizer.generateString();

        Preferences.of(context).personaName().set(personaName);

        Assertions.assertThat(Preferences.of(context).personaName().get()).isEqualTo(personaName);
    }

    @After
    public void tearDown() {
        Preferences.of(context).environment().delete();

        Preferences.of(context).host().delete();
        Preferences.of(context).port().delete();

        Preferences.of(context).personaId().delete();
        Preferences.of(context).personaName().delete();
    }
}
