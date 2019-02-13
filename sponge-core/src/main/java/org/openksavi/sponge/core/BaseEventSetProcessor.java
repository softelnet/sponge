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

package org.openksavi.sponge.core;

import java.time.Duration;

import org.openksavi.sponge.EventSetProcessor;
import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.event.Event;

public abstract class BaseEventSetProcessor<T extends EventSetProcessorAdapter<?>> extends BaseEventProcessor<T>
        implements EventSetProcessor<T> {

    @Override
    public BaseEventSetProcessorMeta getMeta() {
        return (BaseEventSetProcessorMeta) super.getMeta();
    }

    @SuppressWarnings({ "rawtypes" })
    protected final BaseEventSetProcessorAdapter getEventSetProcessorAdapterImpl() {
        return (BaseEventSetProcessorAdapter) super.getAdapter();
    }

    @Override
    public final Event getFirstEvent() {
        return getEventSetProcessorAdapterImpl().getFirstEvent();
    }

    @Override
    public final EventSetProcessorAdapterGroup<EventSetProcessorAdapter<?>> getGroup() {
        return getAdapter().getGroup();
    }

    public BaseEventSetProcessor<T> withDuration(Duration duration) {
        getMeta().setDuration(duration);
        return this;
    }

    public BaseEventSetProcessor<T> withSynchronous(Boolean synchronous) {
        getMeta().setSynchronous(synchronous);
        return this;
    }

    public BaseEventSetProcessor<T> withDuration(Duration duration) {
        setDuration(duration);
        return this;
    }

    public BaseEventSetProcessor<T> withSynchronous(Boolean synchronous) {
        setSynchronous(synchronous);
        return this;
    }
}
