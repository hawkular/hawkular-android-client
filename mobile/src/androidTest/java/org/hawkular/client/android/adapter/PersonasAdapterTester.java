/*
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
package org.hawkular.client.android.adapter;

import java.util.ArrayList;
import java.util.List;

import org.assertj.android.api.Assertions;
import org.hawkular.client.android.backend.model.Persona;
import org.hawkular.client.android.util.Randomizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public final class PersonasAdapterTester {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void count() {
        List<Persona> personas = generatePersonas();

        PersonasAdapter personasAdapter = new PersonasAdapter(context, personas);

        Assertions.assertThat(personasAdapter).hasCount(personas.size());
    }

    @Test
    public void item() {
        Persona persona = generatePersona();

        List<Persona> personas = new ArrayList<>();
        personas.add(persona);
        personas.addAll(generatePersonas());

        PersonasAdapter personasAdapter = new PersonasAdapter(context, personas);

        Assertions.assertThat(personasAdapter).hasItem(persona, 0);
    }

    private List<Persona> generatePersonas() {
        List<Persona> personas = new ArrayList<>();

        for (int personaPosition = 0; personaPosition < Randomizer.generateNumber(); personaPosition++) {
            personas.add(generatePersona());
        }

        return personas;
    }

    private Persona generatePersona() {
        return new Persona(Randomizer.generateString());
    }
}
