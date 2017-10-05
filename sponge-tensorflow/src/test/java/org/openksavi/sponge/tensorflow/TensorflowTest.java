/*
 * Copyright 2016-2017 The Sponge authors.
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

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

import org.openksavi.sponge.core.util.Utils;

public class TensorflowTest {

    private static final Logger logger = LoggerFactory.getLogger(TensorflowTest.class);

    @Test
    public void testTf() throws UnsupportedEncodingException {
        try (Graph g = new Graph()) {
            final String value = "Hello from " + TensorFlow.version();

            // Construct the computation graph with a single operation, a constant
            // named "MyConst" with a value "value".
            try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
                // The Java API doesn't yet include convenience functions for adding operations.
                g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
            }

            // Execute the "MyConst" operation in a Session.
            try (Session s = new Session(g); Tensor output = s.runner().fetch("MyConst").run().get(0)) {
                logger.info(new String(output.bytesValue(), "UTF-8"));
                logger.info(output.toString());
            }
        }
    }

    // TODO @Test
    public void testLoadModel() throws Exception {
        String modelDir = "examples/tensorflow/estimator/model";
        SavedModelBundle bundle = SavedModelBundle.load(modelDir + "/" + Utils.getLastSubdirectory(modelDir), "serve");

        try (Session s = bundle.session()/* ; Tensor output = s.runner().fetch("MyConst").run().get(0) */) {
            Tensor x = Tensor.create(new float[] { 2, 5, 8, 1 });
            Tensor y = s.runner().feed("x", x).fetch("y").run().get(0);

            logger.info("y = {}", y.floatValue());
        }
    }
}
