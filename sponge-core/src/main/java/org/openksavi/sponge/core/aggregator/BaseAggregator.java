/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.core.aggregator;

import org.openksavi.sponge.aggregator.Aggregator;
import org.openksavi.sponge.aggregator.AggregatorAdapter;
import org.openksavi.sponge.core.BaseEventSetProcessor;

public abstract class BaseAggregator extends BaseEventSetProcessor<AggregatorAdapter> implements Aggregator {

    @Override
    public final void finish() {
        getAdapter().finish();
    }

    @Override
    public void onDuration() {
        //
    }

    @Override
    public final AggregatorAdapter createAdapter() {
        return new BaseAggregatorAdapter(new BaseAggregatorDefinition());
    }
}
