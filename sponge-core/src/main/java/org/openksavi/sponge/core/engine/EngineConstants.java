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

package org.openksavi.sponge.core.engine;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.action.BaseAction;
import org.openksavi.sponge.core.correlator.BaseCorrelator;
import org.openksavi.sponge.core.filter.BaseFilter;
import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.core.trigger.BaseTrigger;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.event.EventName;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Engine constants.
 */
public final class EngineConstants {

    public static final String IGNORED_EVENTS_LOGGER_NAME = "sponge.event.ignored";

    /** Predefined events names, e.g. system event names. */
    public static final Set<String> PREDEFINED_EVENT_NAMES = Collections.unmodifiableSet(Sets.newHashSet(EventName.STARTUP));

    public static final Boolean DEFAULT_ON_RUN_FUNCTION_RESULT = true;

    public static final String MATCH_ALL_REGEXP = ".*";

    public static final String DEFAULT_SECURITY_ALGORITHM = "SunX509";

    //@formatter:off
    public static final Map<Class<?>, Class<?>> BASE_PROCESSOR_CLASSES = SpongeUtils.immutableMapOf(
            Action.class, BaseAction.class,
            Filter.class, BaseFilter.class,
            Trigger.class, BaseTrigger.class,
            Rule.class, BaseRule.class,
            Correlator.class, BaseCorrelator.class
            );
    //@formatter:on

    private EngineConstants() {
        //
    }
}
