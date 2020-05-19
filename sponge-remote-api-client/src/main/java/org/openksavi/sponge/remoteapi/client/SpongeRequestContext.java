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

package org.openksavi.sponge.remoteapi.client;

import org.openksavi.sponge.remoteapi.client.listener.OnRequestSerializedListener;
import org.openksavi.sponge.remoteapi.client.listener.OnResponseDeserializedListener;

/**
 * The request context.
 */
public class SpongeRequestContext {

    private OnRequestSerializedListener onRequestSerializedListener;

    private OnResponseDeserializedListener onResponseDeserializedListener;

    private SpongeRequestContext() {
        //
    }

    public OnRequestSerializedListener getOnRequestSerializedListener() {
        return onRequestSerializedListener;
    }

    public void setOnRequestSerializedListener(OnRequestSerializedListener onRequestSerializedListener) {
        this.onRequestSerializedListener = onRequestSerializedListener;
    }

    public OnResponseDeserializedListener getOnResponseDeserializedListener() {
        return onResponseDeserializedListener;
    }

    public void setOnResponseDeserializedListener(OnResponseDeserializedListener onResponseDeserializedListener) {
        this.onResponseDeserializedListener = onResponseDeserializedListener;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private SpongeRequestContext context = new SpongeRequestContext();

        /**
         * Builds the context.
         *
         * @return the context.
         */
        public SpongeRequestContext build() {
            return context;
        }

        public Builder onRequestSerializedListener(OnRequestSerializedListener onRequestSerializedListener) {
            context.setOnRequestSerializedListener(onRequestSerializedListener);
            return this;
        }

        public Builder onResponseDeserializedListener(OnResponseDeserializedListener onResponseDeserializedListener) {
            context.setOnResponseDeserializedListener(onResponseDeserializedListener);
            return this;
        }
    }
}
