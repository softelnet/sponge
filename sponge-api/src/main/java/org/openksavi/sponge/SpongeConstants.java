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

package org.openksavi.sponge;

import org.openksavi.sponge.rule.EventMode;

/**
 * Sponge constants.
 */
public final class SpongeConstants {

    public static final String EVENT_NAME_RESERVED_CHARS = ":";

    public static final String ACTION_SUB_ARG_SEPARATOR = ".";

    /** Control event name prefix. */
    public static final String CONTROL_EVENT_PREFIX = "$";

    /** Default rule event event mode: first. */
    public static final EventMode DEFAULT_RULE_EVENT_MODE = EventMode.FIRST;

    public static final boolean DEFAULT_KNOWLEDGE_BASE_CLEAR_ON_RELOAD = false;

    private SpongeConstants() {
    }
}
