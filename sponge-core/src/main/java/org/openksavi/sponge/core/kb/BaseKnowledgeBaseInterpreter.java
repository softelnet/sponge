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

package org.openksavi.sponge.core.kb;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.rule.GenericRuleEventSpec;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleEventSpec;

/**
 * Base knowledge base (KB) interpreter.
 */
public abstract class BaseKnowledgeBaseInterpreter implements KnowledgeBaseInterpreter {

    private KnowledgeBaseType type;

    private KnowledgeBaseEngineOperations engineOperations;

    protected BaseKnowledgeBaseInterpreter(KnowledgeBaseEngineOperations engineOperations, KnowledgeBaseType type) {
        this.engineOperations = engineOperations;
        this.type = type;
    }

    /**
     * Creates processor instance.
     *
     * @param processorClassName processor class name.
     * @return processor instance.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public <T extends Processor> T createProcessorInstance(String processorClassName, Class<T> javaClass) {
        return doCreateInstance(processorClassName, javaClass);
    }

    /**
     * Creates an instance of the given class.
     *
     * @param className class name.
     * @param javaClass processor class.
     * @param <T> instance type
     * @return processor instance.
     */
    protected abstract <T> T doCreateInstance(String className, Class<T> javaClass);

    /**
     * Creates plugin instance.
     *
     * @param pluginClassName plugin class name.
     * @return plugin instance.
     */
    @Override
    public Plugin createPluginInstance(String pluginClassName) {
        return doCreateInstance(pluginClassName, Plugin.class);
    }

    @Override
    public KnowledgeBaseEngineOperations getEngineOperations() {
        return engineOperations;
    }

    @Override
    public KnowledgeBaseType getType() {
        return type;
    }

    @Override
    public RuleEventSpec getRuleEventSpec(Object eventSpecEntry) {
        RuleEventSpec result;
        if (eventSpecEntry == null) {
            throw new SpongeException("Event specification entry cannot be null");
        }

        if (eventSpecEntry instanceof RuleEventSpec) {
            result = (RuleEventSpec) eventSpecEntry;
        } else if (eventSpecEntry instanceof String) {
            return getCustomRuleEventSpec((String) eventSpecEntry);
        } else {
            throw new SpongeException(
                    "Unknown event specification " + eventSpecEntry + " (type: " + eventSpecEntry.getClass().getName() + ")");
        }

        return result;
    }

    /**
     * Resolves event specification "&lt;name&gt; &lt;alias&gt; : &lt;mode&gt;". Uses default value when one not provided.
     *
     * @param eventSpecString event specification.
     * @return rule event specification, i.e. a triple of (name, alias, mode).
     */
    protected RuleEventSpec getCustomRuleEventSpec(String eventSpecString) {
        if (eventSpecString == null) {
            throw new SpongeException("Event specification is null");
        }

        List<String> mainList =
                Arrays.stream(eventSpecString.split(":")).map(s -> s.trim()).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (mainList.isEmpty()) {
            throw new SpongeException("Event specification is empty");
        } else if (mainList.size() > 2) {
            throw new SpongeException("Event specification has too many elements separated by ':'");
        }

        ImmutablePair<String, String> nameAlias = resolveEventNameAndAlias(mainList.get(0));
        EventMode eventMode = RuleAdapter.DEFAULT_MODE;
        if (mainList.size() == 2) {
            try {
                eventMode = EventMode.valueOf(mainList.get(1).toUpperCase());
            } catch (Exception e) {
                throw new SpongeException("Event mode is incorrect: " + mainList.get(1));
            }
        }

        return new GenericRuleEventSpec(nameAlias.getLeft(), nameAlias.getRight(), eventMode);
    }

    protected ImmutablePair<String, String> resolveEventNameAndAlias(String eventSpecString) {
        if (eventSpecString == null) {
            throw new SpongeException("Event specification is null");
        }

        if (eventSpecString.trim().length() < 1) {
            throw new SpongeException("Event specification is empty");
        }

        StringTokenizer st = new StringTokenizer(eventSpecString, " \t\n\r\f", false);

        String eventName = st.nextToken();
        String eventAlias = st.hasMoreTokens() ? st.nextToken() : eventName;

        return new ImmutablePair<>(eventName, eventAlias);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getVariable(String name, Class<T> cls) {
        return (T) getVariable(name);
    }

    public Logger getLogger() {
        return LoggerFactory.getLogger(Utils.createGlobalLoggerName(getEngineOperations()));
    }
}
