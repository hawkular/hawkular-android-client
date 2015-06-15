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
package org.hawkular.client.android.backend.model;

import com.google.gson.annotations.SerializedName;

public final class AlertEvaluation {
    @SerializedName("condition")
    private AlertEvaluationCondition condition;

    @SerializedName("value")
    private double value;

    @SerializedName("dataTimestamp")
    private long dataTimestamp;

    @SerializedName("evalTimestamp")
    private long evaluationTimestamp;

    public AlertEvaluationCondition getCondition() {
        return condition;
    }

    public double getValue() {
        return value;
    }

    public long getDataTimestamp() {
        return dataTimestamp;
    }

    public long getEvaluationTimestamp() {
        return evaluationTimestamp;
    }
}