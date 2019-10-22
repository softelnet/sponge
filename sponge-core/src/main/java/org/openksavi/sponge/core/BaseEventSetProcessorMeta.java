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

package org.openksavi.sponge.core;

import java.time.Duration;

import org.openksavi.sponge.EventSetProcessorMeta;

/**
 * A base event set processor metadata.
 */
public class BaseEventSetProcessorMeta extends BaseEventProcessorMeta implements EventSetProcessorMeta {

    /** Duration. */
    protected Duration duration;

    /** Synchronous. */
    private Boolean synchronous;

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean hasDuration() {
        return duration != null;
    }

    @Override
    public Boolean isSynchronous() {
        return synchronous;
    }

    @Override
    public void setSynchronous(Boolean synchronous) {
        this.synchronous = synchronous;
    }

    @Override
    public void update(BaseProcessorMeta source) {
        super.update(source);

        if (source instanceof BaseEventSetProcessorMeta) {
            BaseEventSetProcessorMeta sourceMeta = (BaseEventSetProcessorMeta) source;
            setDuration(sourceMeta.getDuration());
            setSynchronous(sourceMeta.isSynchronous());
        }
    }
}
