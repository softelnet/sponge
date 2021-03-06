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

package org.openksavi.sponge.java.core;

import java.util.Arrays;
import java.util.Map;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.kb.GenericKnowledgeBaseType;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.java.JCorrelator;
import org.openksavi.sponge.java.JFilter;
import org.openksavi.sponge.java.JRule;
import org.openksavi.sponge.java.JTrigger;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Java constants.
 */
public final class JavaConstants {

    /** Java-based knowledge base type. */
    public static final KnowledgeBaseType TYPE = new GenericKnowledgeBaseType("java", "java", Arrays.asList("java"), false);

    //@formatter:off
    public static final Map<Class<?>, Class<?>> PROCESSOR_CLASSES = SpongeUtils.immutableMapOf(
            Action.class, JAction.class,
            Filter.class, JFilter.class,
            Trigger.class, JTrigger.class,
            Rule.class, JRule.class,
            Correlator.class, JCorrelator.class
            );
    //@formatter:on

    private JavaConstants() {
        //
    }
}
