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

package org.openksavi.sponge.action;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorBuilder;
import org.openksavi.sponge.ProcessorOnInitCallback;
import org.openksavi.sponge.type.DataType;

/**
 * An action builder.
 */
public interface ActionBuilder extends ProcessorBuilder<Action> {

    @Override
    ActionBuilder withName(String name);

    @Override
    ActionBuilder withLabel(String label);

    @Override
    ActionBuilder withDescription(String description);

    @Override
    ActionBuilder withVersion(Integer version);

    @Override
    ActionBuilder withFeatures(Map<String, Object> features);

    @Override
    ActionBuilder withFeature(String name, Object value);

    @Override
    ActionBuilder withCategory(String category);

    @SuppressWarnings("rawtypes")
    ActionBuilder withArgs(List<DataType> argTypes);

    @SuppressWarnings("rawtypes")
    ActionBuilder withArg(DataType argType);

    ActionBuilder withNoArgs();

    @SuppressWarnings("rawtypes")
    ActionBuilder withResult(DataType resultType);

    ActionBuilder withNoResult();

    ActionBuilder withCallable(boolean callable);

    @Override
    ActionBuilder withOnInit(ProcessorOnInitCallback<Action> onInitCallback);

    ActionBuilder withOnIsActive(ActionOnIsActiveCallback onIsActiveCallback);

    ActionBuilder withOnCallArgs(ActionOnCallResultCallback onCallCallback);

    ActionBuilder withOnCallArgsNoResult(ActionOnCallNoResultCallback onCallCallback);

    ActionBuilder withOnProvideArgs(ActionOnProvideArgsCallback onProvideArgsCallback);
}
