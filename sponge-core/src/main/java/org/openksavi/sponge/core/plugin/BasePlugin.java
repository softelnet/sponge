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

package org.openksavi.sponge.core.plugin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.engine.BaseEngineModule;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.plugin.Plugin;

/**
 * Base plugin.
 */
public abstract class BasePlugin extends BaseEngineModule implements Plugin {

    /** Plugin configuration. */
    private Configuration configuration;

    /** Knowledge base associated with this plugin. */
    private KnowledgeBase knowledgeBase;

    /**
     * Sets plugin configuration.
     *
     * @param configuration plugin configuration.
     * @param configure whether this configuration should be applied immediately.
     */
    @Override
    public void setConfiguration(Configuration configuration, boolean configure) {
        this.configuration = configuration;

        if (configure) {
            configure(configuration);
        }
    }

    /**
     * Returns plugin configuration.
     *
     * @return plugin configuration.
     */
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    @Override
    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public final void doStartup() {
        onStartup();
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public final void doShutdown() {
        onShutdown();
    }

    /**
     * On startup callback method.
     */
    @Override
    public void onStartup() {
        //
    }

    /**
     * On shutdown callback method.
     */
    @Override
    public void onShutdown() {
        //
    }

    /**
     * Before reload callback method.
     */
    @Override
    public void onBeforeReload() {
        //
    }

    /**
     * After reload callback method.
     */
    @Override
    public void onAfterReload() {
        //
    }

    @Override
    public void init() {
        //
    }

    @Override
    public void configure(Configuration configuration) {
        //
    }

    protected KnowledgeBaseEngineOperations getEngineOperations() {
        return knowledgeBase.getEngineOperations();
    }

    public KnowledgeBaseEngineOperations getEps() {
        return getEngineOperations();
    }

    /**
     * Returns string representation.
     *
     * @return string representation.
     */
    @Override
    public String toString() {
        //@formatter:off
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", getName())
                .append("class", getClass().getName())
                .toString();
        //@formatter:on
    }

    /**
     * Returns the logger.
     *
     * @return logger.
     */
    public Logger getLogger() {
        return LoggerFactory
                .getLogger(KnowledgeBaseConstants.LOGGER_NAME_PREFIX + "." + getKnowledgeBase().getInterpreter().getType().getTypeCode()
                        + "." + (getName() != null ? getName() : getClass().getName()));
    }
}
