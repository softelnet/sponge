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

package org.openksavi.sponge.core.trigger;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorOnInitCallback;
import org.openksavi.sponge.core.BaseEventProcessorBuilder;
import org.openksavi.sponge.trigger.Trigger;
import org.openksavi.sponge.trigger.TriggerBuilder;
import org.openksavi.sponge.trigger.TriggerOnAcceptCallback;
import org.openksavi.sponge.trigger.TriggerOnRunCallback;

/**
 * A base trigger builder.
 */
public class BaseTriggerBuilder extends BaseEventProcessorBuilder<Trigger> implements TriggerBuilder {

    private TriggerOnAcceptCallback onAcceptCallback;

    private TriggerOnRunCallback onRunCallback;

    public BaseTriggerBuilder(String name) {
        super(new BaseTriggerMeta(), name);
    }

    @Override
    public BaseTriggerMeta getMeta() {
        return (BaseTriggerMeta) super.getMeta();
    }

    @Override
    public BaseTriggerBuilder withName(String name) {
        return (BaseTriggerBuilder) super.withName(name);
    }

    @Override
    public BaseTriggerBuilder withLabel(String label) {
        return (BaseTriggerBuilder) super.withLabel(label);
    }

    @Override
    public BaseTriggerBuilder withDescription(String description) {
        return (BaseTriggerBuilder) super.withDescription(description);
    }

    @Override
    public BaseTriggerBuilder withVersion(Integer version) {
        return (BaseTriggerBuilder) super.withVersion(version);
    }

    @Override
    public BaseTriggerBuilder withFeatures(Map<String, Object> features) {
        return (BaseTriggerBuilder) super.withFeatures(features);
    }

    @Override
    public BaseTriggerBuilder withFeature(String name, Object value) {
        return (BaseTriggerBuilder) super.withFeature(name, value);
    }

    @Override
    public BaseTriggerBuilder withCategory(String category) {
        return (BaseTriggerBuilder) super.withCategory(category);
    }

    @Override
    public BaseTriggerBuilder withEvents(List<String> eventNames) {
        return (BaseTriggerBuilder) super.withEvents(eventNames);
    }

    @Override
    public BaseTriggerBuilder withEvent(String eventName) {
        return (BaseTriggerBuilder) super.withEvent(eventName);
    }

    @Override
    public BaseTriggerBuilder withOnInit(ProcessorOnInitCallback<Trigger> onInitCallback) {
        return (BaseTriggerBuilder) super.withOnInit(onInitCallback);
    }

    @Override
    public BaseTriggerBuilder withOnAccept(TriggerOnAcceptCallback onAcceptCallback) {
        this.onAcceptCallback = onAcceptCallback;

        return this;
    }

    @Override
    public TriggerBuilder withOnRun(TriggerOnRunCallback onRunCallback) {
        this.onRunCallback = onRunCallback;

        return this;
    }

    public TriggerOnAcceptCallback getOnAcceptCallback() {
        return onAcceptCallback;
    }

    public void setOnAcceptCallback(TriggerOnAcceptCallback onAcceptCallback) {
        this.onAcceptCallback = onAcceptCallback;
    }

    public TriggerOnRunCallback getOnRunCallback() {
        return onRunCallback;
    }

    public void setOnRunCallback(TriggerOnRunCallback onRunCallback) {
        this.onRunCallback = onRunCallback;
    }

    @Override
    public Trigger build() {
        return new DefaultBuilderTrigger(this);
    }
}
