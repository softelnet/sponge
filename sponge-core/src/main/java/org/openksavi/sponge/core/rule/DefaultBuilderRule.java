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

package org.openksavi.sponge.core.rule;

import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.RuleEventSpec;

/**
 * A default rule for a builder.
 */
public class DefaultBuilderRule extends BaseRule {

    private BaseRuleBuilder builder;

    public DefaultBuilderRule(BaseRuleBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void onConfigure() {
        getMeta().update(builder.getMeta());

        getMeta().setEventSpecs(builder.getGenericEventSpecs().stream().<RuleEventSpec>map(genericEventSpec -> {
            if (genericEventSpec instanceof String) {
                return SpongeUtils.convertRuleEventSpec(getKnowledgeBase(), (String) genericEventSpec);
            } else if (genericEventSpec instanceof RuleEventSpec) {
                return (RuleEventSpec) genericEventSpec;
            }

            throw new SpongeException(
                    "Unsupported rule event spec: " + (genericEventSpec != null ? genericEventSpec.getClass() : genericEventSpec));
        }).collect(Collectors.toList()));

        Validate.notNull(builder.getOnRunCallback(), "A rule onRun callback must be set");
    }

    @Override
    public void onInit() {
        if (builder.getOnInitCallback() != null) {
            builder.getOnInitCallback().onInit(this);
        } else {
            super.onInit();
        }
    }

    @Override
    public void onRun(Event event) {
        builder.getOnRunCallback().onRun(this, event);
    }
}
