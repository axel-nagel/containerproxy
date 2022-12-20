/**
 * ContainerProxy
 *
 * Copyright (C) 2016-2021 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.containerproxy.backend.kubernetes;

import eu.openanalytics.containerproxy.model.spec.AbstractSpecExtension;
import eu.openanalytics.containerproxy.spec.expression.SpecExpressionContext;
import eu.openanalytics.containerproxy.spec.expression.SpecExpressionResolver;

import java.util.ArrayList;
import java.util.List;

public class KubernetesSpecExtension extends AbstractSpecExtension {

    private String kubernetesPodPatches;

    private List<String> kubernetesAdditionalManifests = new ArrayList<>();

    private List<String> kubernetesAdditionalPersistentManifests = new ArrayList<>();

    public String getKubernetesPodPatches() {
        return kubernetesPodPatches;
    }

    public void setKubernetesPodPatches(String kubernetesPodPatches) {
        this.kubernetesPodPatches = kubernetesPodPatches;
    }

    public List<String> getKubernetesAdditionalManifests() {
        return kubernetesAdditionalManifests;
    }

    public void setKubernetesAdditionalManifests(List<String> kubernetesAdditionalManifests) {
        this.kubernetesAdditionalManifests = kubernetesAdditionalManifests;
    }

    public List<String> getKubernetesAdditionalPersistentManifests() {
        return kubernetesAdditionalPersistentManifests;
    }

    public void setKubernetesAdditionalPersistentManifests(List<String> kubernetesAdditionalPersistentManifests) {
        this.kubernetesAdditionalPersistentManifests = kubernetesAdditionalPersistentManifests;
    }

    @Override
    public KubernetesSpecExtension resolve(SpecExpressionResolver resolver, SpecExpressionContext context) {
        return this;
    }
}
