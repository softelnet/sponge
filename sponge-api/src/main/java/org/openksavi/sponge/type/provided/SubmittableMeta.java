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

package org.openksavi.sponge.type.provided;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A submittable object metadata.
 */
public class SubmittableMeta {

    /** The list of object names that this submitted object influences (i.e. can change their values when submitted). */
    private List<String> influences = new ArrayList<>();

    public SubmittableMeta() {
    }

    public List<String> getInfluences() {
        return influences;
    }

    public void setInfluences(List<String> influences) {
        this.influences = influences;
    }

    public SubmittableMeta withInfluences(List<String> influences) {
        this.influences.addAll(influences);
        return this;
    }

    public SubmittableMeta withInfluence(String influence) {
        return withInfluences(Arrays.asList(influence));
    }
}
