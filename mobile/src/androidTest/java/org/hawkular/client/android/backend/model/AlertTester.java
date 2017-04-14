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
package org.hawkular.client.android.backend.model;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.hawkular.client.android.util.Parceler;
import org.hawkular.client.android.util.Randomizer;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public final class AlertTester {
    @Test
    public void parcelable() {
        Alert originalAlert = new Alert(
                Randomizer.generateString(), Randomizer.generateNumber(), new ArrayList<Alert.Lister>(),
                Randomizer.generateString(), Randomizer.generateString(), new ArrayList<Note>());
        Alert parceledAlert = Parceler.parcel(Alert.CREATOR, originalAlert);

        Assertions.assertThat(parceledAlert.getId()).isEqualTo(originalAlert.getId());
        Assertions.assertThat(parceledAlert.getSeverity()).isEqualTo(originalAlert.getSeverity());
        Assertions.assertThat(parceledAlert.getStatus()).isEqualTo(originalAlert.getStatus());
        Assertions.assertThat(parceledAlert.getTimestamp()).isEqualTo(originalAlert.getTimestamp());
        Assertions.assertThat(parceledAlert.getEvaluations()).isEqualTo(originalAlert.getEvaluations());
        Assertions.assertThat(parceledAlert.getNotes()).isEqualTo(originalAlert.getNotes());
    }
}
