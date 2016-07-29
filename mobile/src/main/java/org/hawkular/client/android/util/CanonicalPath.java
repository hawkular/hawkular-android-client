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

package org.hawkular.client.android.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Canonical Path.
 *
 * Accept string path and break convert into HashMap of components.
 */

public class CanonicalPath {

    public static final class Type {
        public static final String TENANT = "t";
        public static final String RESOURCE = "r";
        public static final String RESOURCE_TYPE = "rt";
        public static final String FEED = "f";
        public static final String ENVIRONMENT = "e";
        public static final String METRIC = "m";
        public static final String METRIC_TYPE = "mt";
    }

    private HashMap<String, Object> data = new HashMap<>();

    private CanonicalPath(String path) {
        String[] temp = path.split("/");

        for (String part : temp) {
            String[] pair = part.split(";");

            switch (pair[0]) {
                case Type.TENANT:
                case Type.ENVIRONMENT:
                case Type.FEED:
                case Type.METRIC_TYPE:
                case Type.METRIC:
                case Type.RESOURCE_TYPE:
                    data.put(pair[0], pair[1]);
                    break;

                case Type.RESOURCE:
                    if (data.containsKey(Type.RESOURCE)) {
                        List<String> resources = (List<String>) data.get("r");
                        resources.add(pair[1]);
                    } else {
                        List<String> resources = new LinkedList<>();
                        resources.add(pair[1]);
                        data.put(Type.RESOURCE, resources);
                    }
            }
        }

    }

    public static CanonicalPath getByString(String path) {
        return new CanonicalPath(path);
    }

    public String getTenant() {
        if (data.containsKey(Type.TENANT)) {
            return (String) data.get(Type.TENANT);
        } else {
            return null;
        }
    }

    public String getFeed() {
        return (String) data.get(Type.FEED);
    }

    public String getEnvironment() {
        return (String) data.get(Type.ENVIRONMENT);
    }

    public String getResourceType() {
        return (String) data.get(Type.RESOURCE_TYPE);
    }

    public String getResource() {
        List<String> resources = (List<String>) data.get(Type.RESOURCE);
        String resource = "";
        for (String r : resources) {
            resource = resource + (resource.isEmpty() ? r : ("/" + r));
        }
        return resource;
    }

    public String getMetricType() {
        return (String) data.get(Type.METRIC_TYPE);
    }

    public String getMetric() {
        return (String) data.get(Type.METRIC);
    }

}
