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

package org.openksavi.sponge.jython.util;

import java.util.function.Consumer;

/**
 * Jython hack [https://stackoverflow.com/questions/43970506/how-to-use-java-8-lambdas-in-jython].
 */
@SuppressWarnings("rawtypes")
public class PyConsumer implements Consumer {

    private PyConsumerInterface consumer;

    public PyConsumer(PyConsumerInterface consumer) {
        this.consumer = consumer;
    }

    @Override
    public void accept(Object t) {
        consumer.accept(t);
    }
}
