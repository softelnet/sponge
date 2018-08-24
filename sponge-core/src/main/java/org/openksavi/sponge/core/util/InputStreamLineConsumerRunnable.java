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

package org.openksavi.sponge.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * Input stream line consumer runnable.
 */
public class InputStreamLineConsumerRunnable implements Runnable {

    private InputStream inputStream;

    private Consumer<String> lineConsumer;

    private Runnable onEndOfStream;

    private Charset charset;

    public InputStreamLineConsumerRunnable(InputStream inputStream, Consumer<String> lineConsumer, Runnable onEndOfStream,
            Charset charset) {
        this.inputStream = inputStream;
        this.lineConsumer = lineConsumer;
        this.onEndOfStream = onEndOfStream;
        this.charset = charset;
    }

    @Override
    public void run() {
        try {
            new BufferedReader(new InputStreamReader(inputStream, charset)).lines().forEach(lineConsumer);
        } finally {
            if (onEndOfStream != null) {
                onEndOfStream.run();
            }
        }
    }
}
