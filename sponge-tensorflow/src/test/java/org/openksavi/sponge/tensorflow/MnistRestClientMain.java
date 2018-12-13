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

import java.util.Arrays;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;

public class MnistRestClientMain {

    /**
     * Main method.
     *
     * @param args arguments.
     */
    public static void main(String... args) {
        try (SpongeRestClient client = new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder().build())) {

            String imageFile = "examples/tensorflow/mnist/data/1_0.png";

            Number recognizedDigit = client.call(Number.class, "MnistPredict", Arrays.asList(SpongeUtils.readFileToByteArray(imageFile)));

            System.out.println(String.format("Recognized digit for image file %s is %s.", imageFile, recognizedDigit));
        }
    }
}
