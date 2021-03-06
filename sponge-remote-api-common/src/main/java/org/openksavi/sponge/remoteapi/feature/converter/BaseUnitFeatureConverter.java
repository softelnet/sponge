/*
 * Copyright 2016-2020 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.feature.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseUnitFeatureConverter implements UnitFeatureConverter {

    protected BaseUnitFeatureConverter(String name, String... names) {
        this.names.add(name);
        this.names.addAll(Arrays.asList(names));
    }

    protected List<String> names = new ArrayList<>();

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public Object marshal(FeatureConverter converter, Object value) {
        return value;
    }

    @Override
    public Object unmarshal(FeatureConverter converter, Object value) {
        return value;
    }
}
