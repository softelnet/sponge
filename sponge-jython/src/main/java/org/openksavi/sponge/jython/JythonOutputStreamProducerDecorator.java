/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.jython;

import java.io.OutputStream;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.jython.util.JythonUtils;
import org.openksavi.sponge.type.value.OutputStreamProducer;

public class JythonOutputStreamProducerDecorator implements OutputStreamProducer {

    private final OutputStreamProducer producer;

    public JythonOutputStreamProducerDecorator(OutputStreamProducer producer) {
        this.producer = producer;
    }

    @Override
    public void produce(OutputStream output) {
        try {
            producer.produce(output);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException("output stream producer", JythonUtils.getSpecificExceptionMessage(e), e);
        }
    }
}
