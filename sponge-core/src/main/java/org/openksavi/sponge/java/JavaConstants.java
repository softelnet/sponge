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

package org.openksavi.sponge.java;

import java.util.Map;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.kb.GenericKnowledgeBaseType;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Java constants.
 */
public final class JavaConstants {

    /** Java-based knowledge base type. */
    public static final KnowledgeBaseType TYPE = new GenericKnowledgeBaseType("java", "java", "java", false);

    @SuppressWarnings("rawtypes")
    //@formatter:off
    public static final Map<Class, Class> PROCESSOR_CLASSES = SpongeUtils.immutableMapOf(
            Action.class, JavaAction.class,
            Filter.class, JavaFilter.class,
            Trigger.class, JavaTrigger.class,
            Rule.class, JavaRule.class,
            Correlator.class, JavaCorrelator.class
            );
    //@formatter:on

    private JavaConstants() {
        //
    }
}
