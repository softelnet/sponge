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

package org.openksavi.sponge.camel;

import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.AsyncProcessorConverterHelper;
import org.apache.camel.util.ObjectHelper;

/**
 * Default Camel consumer facade to Sponge.
 */
public class DefaultCamelConsumer implements CamelConsumer {

    protected final SpongeEndpoint spongeEndpoint;

    protected final AsyncProcessor processor;

    public DefaultCamelConsumer(SpongeEndpoint spongeEndpoint, Processor processor) {
        ObjectHelper.notNull(spongeEndpoint, "spongeEndpoint");
        ObjectHelper.notNull(processor, "processor");

        this.spongeEndpoint = spongeEndpoint;
        this.processor = AsyncProcessorConverterHelper.convert(processor);
    }

    @Override
    public boolean emit(Object body) {
        return emit(spongeEndpoint.createExchange(body));
    }

    @Override
    public boolean emit(final Exchange exchange) {
        return emit(exchange, callback -> {
        });
    }

    @Override
    public boolean emit(final Exchange exchange, AsyncCallback callback) {
        return processor.process(exchange, callback);
    }

    @Override
    public Endpoint getEndpoint() {
        return spongeEndpoint;
    }

    @Override
    public Processor getProcessor() {
        return processor;
    }

    @Override
    public String toString() {
        return spongeEndpoint != null ? spongeEndpoint.toString() : super.toString();
    }
}
