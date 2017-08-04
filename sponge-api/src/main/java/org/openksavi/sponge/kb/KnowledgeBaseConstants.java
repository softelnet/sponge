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

package org.openksavi.sponge.kb;

/**
 * Knowledge base constants.
 */
public final class KnowledgeBaseConstants {

    /** Initialization function name. */
    public static final String FUN_ON_INIT = "onInit";

    /** On load function name. */
    public static final String FUN_ON_LOAD = "onLoad";

    /** On startup function name. */
    public static final String FUN_ON_STARTUP = "onStartup";

    /** On shutdown function name. */
    public static final String FUN_ON_SHUTDOWN = "onShutdown";

    /** Before reload function name. */
    public static final String FUN_ON_BEFORE_RELOAD = "onBeforeReload";

    /** After reload function name. */
    public static final String FUN_ON_AFTER_RELOAD = "onAfterReload";

    /** The engine operations variable name. */
    public static final String VAR_ENGINE_OPERATIONS = "EPS";

    /** Logger name prefix. */
    public static final String LOGGER_NAME_PREFIX = "sponge.kb";

    /** Global logger name. */
    public static final String GLOBAL_LOGGER_NAME = "global";

    private KnowledgeBaseConstants() {
        //
    }
}
