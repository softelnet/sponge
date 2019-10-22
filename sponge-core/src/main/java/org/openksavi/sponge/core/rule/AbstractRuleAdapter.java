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

package org.openksavi.sponge.core.rule;

import org.openksavi.sponge.core.BaseEventSetProcessorAdapter;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleAdapter;

/**
 * Abstract rule adapter.
 */
public abstract class AbstractRuleAdapter<T extends Rule> extends BaseEventSetProcessorAdapter<Rule> implements RuleAdapter {

    protected AbstractRuleAdapter(BaseRuleDefinition definition) {
        super(definition);
    }

    @Override
    public BaseRuleMeta getMeta() {
        return (BaseRuleMeta) super.getMeta();
    }

    @Override
    protected void onDuration() {
        runRule();
    }

    /**
     * Attempts to run (fire) this rule.
     *
     * @return {@code true} if this rule has been run.
     */
    protected abstract boolean runRule();

    @Override
    public BaseRuleDefinition getDefinition() {
        return (BaseRuleDefinition) super.getDefinition();
    }

    @Override
    public int getEventCount() {
        return getMeta().getEventNames().size();
    }
}
