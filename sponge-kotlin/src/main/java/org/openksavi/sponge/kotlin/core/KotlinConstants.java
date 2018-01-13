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

package org.openksavi.sponge.kotlin.core;

import java.util.Map;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.kb.GenericKnowledgeBaseType;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.kotlin.KAction;
import org.openksavi.sponge.kotlin.KCorrelator;
import org.openksavi.sponge.kotlin.KFilter;
import org.openksavi.sponge.kotlin.KRule;
import org.openksavi.sponge.kotlin.KTrigger;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Kotlin constants.
 */
public final class KotlinConstants {

    /** Script based Kotlin knowledge base type. The type code follows the convention of all supported scripting languages. */
    public static final KnowledgeBaseType TYPE_SCRIPT = new GenericKnowledgeBaseType("kotlin", "kotlin", "kts", true);

    /** Non script based Kotlin knowledge base type. The type code is the same as a Kotlin source file extension. */
    public static final KnowledgeBaseType TYPE_NON_SCRIPT = new GenericKnowledgeBaseType("kt", "kotlin", "kt", false);

    //@formatter:off
    public static final Map<Class<?>, Class<?>> PROCESSOR_CLASSES = SpongeUtils.immutableMapOf(
            Action.class, KAction.class,
            Filter.class, KFilter.class,
            Trigger.class, KTrigger.class,
            Rule.class, KRule.class,
            Correlator.class, KCorrelator.class
            );
    //@formatter:on

    private KotlinConstants() {
        //
    }
}
