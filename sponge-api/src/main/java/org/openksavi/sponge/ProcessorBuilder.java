/*
 * Copyright 2016-2019 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge;

import java.util.Map;

/**
 * A processor builder.
 */
public interface ProcessorBuilder<T extends Processor<?>> {

    ProcessorBuilder<T> withName(String name);

    ProcessorBuilder<T> withLabel(String label);

    ProcessorBuilder<T> withDescription(String description);

    ProcessorBuilder<T> withVersion(Integer version);

    ProcessorBuilder<T> withFeatures(Map<String, Object> features);

    ProcessorBuilder<T> withFeature(String name, Object value);

    ProcessorBuilder<T> withCategory(String category);

    ProcessorBuilder<T> withOnInit(ProcessorOnInitCallback<T> onInitCallback);

    String getName();

    T build();
}
