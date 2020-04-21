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

/**
 * Configuration constants.
 */
public final class ConfigurationConstants {

    public static final String CONFIG_SCHEMA = "https://sponge.openksavi.org/schema/config.xsd";

    public static final String CONFIG_SCHEMA_LOCATION = "/org/openksavi/sponge/schema/config.xsd";

    public static final String DEFAULT_CONFIG = "org/openksavi/sponge/config/defaultConfiguration.xml";

    /** Home property name. */
    public static final String PROP_HOME = "sponge.home";

    /** Configuration file directory property name (read only). */
    public static final String PROP_CONFIG_DIR = "sponge.configDir";

    public static final String TAG_ROOT = "sponge";

    public static final String TAG_ENGINE_CONFIG = "engine";

    public static final String TAG_ENGINE_MAIN_PROCESSING_UNIT_THREAD_COUNT = "mainProcessingUnitThreadCount";

    public static final String TAG_ENGINE_EVENT_QUEUE_CAPACITY = "eventQueueCapacity";

    public static final String TAG_ENGINE_DURATION_THREAD_COUNT = "durationThreadCount";

    public static final String TAG_ENGINE_ASYNC_EVENT_SET_PROCESSOR_EXECUTOR_THREAD_COUNT = "asyncEventSetProcessorExecutorThreadCount";

    public static final String TAG_ENGINE_EVENT_SET_PROCESSOR_DEFAULT_SYNCHRONOUS = "eventSetProcessorDefaultSynchronous";

    public static final String TAG_ENGINE_AUTO_ENABLE = "autoEnable";

    public static final String TAG_ENGINE_EVENT_CLONE_POLICY = "eventClonePolicy";

    public static final String TAG_ENGINE_EXECUTOR_SHUTDOWN_TIMEOUT = "executorShutdownTimeout";

    public static final String TAG_PROPERTIES = "properties";

    public static final String TAG_PROPERTY = "property";

    public static final String PROP_ATTRIBUTE_NAME = "name";

    public static final String PROP_ATTRIBUTE_VARIABLE = "variable";

    public static final String PROP_ATTRIBUTE_SYSTEM = "system";

    public static final String ENGINE_ATTRIBUTE_NAME = "name";

    public static final String ENGINE_ATTRIBUTE_LABEL = "label";

    public static final String TAG_ENGINE_DESCRIPTION = "description";

    public static final String TAG_ENGINE_LICENSE = "license";

    private ConfigurationConstants() {
        //
    }
}
