/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.tensorflow;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.Experimental;

/**
 * An image classifier service interface.
 */
public interface ImageClassifierService {

    boolean isReady();

    List<String> getLabels();

    /**
     * Returns a map of the labels and the corresponding probabilities.
     *
     * @param image the image.
     * @return the probability map.
     */
    Map<String, Double> predict(byte[] image);

    void addToLearn(byte[] image, String label);

    void learn(byte[] image, String label);

    void reset();
}
