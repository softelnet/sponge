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

package org.openksavi.sponge.core.rule;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.rule.RuleAdapterRuntime.NodeValue;
import org.openksavi.sponge.core.util.Tree;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleEventSpec;

/**
 * Base rule adapter.
 */
public class BaseRuleAdapter extends AbstractRuleAdapter<Rule> {

    private RuleAdapterRuntime runtime;

    public BaseRuleAdapter(BaseRuleDefinition definition) {
        super(definition);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.RULE;
    }

    /**
     * Processes the incoming event. Must be called inside a locked critical section.
     */
    @Override
    public void onEvent(Event event) {
        runtime.onEvent(event);
    }

    public SpongeException createValidationException(String text) {
        return new SpongeException("Invalid rule " + getName() + ". " + text);
    }

    /**
     * Clears event tree and event alias map.
     */
    @Override
    public void clear() {
        lock.lock();
        try {
            runtime.clear();
        } finally {
            lock.unlock();
        }
    }

    public RuleAdapterRuntime getRuntime() {
        if (runtime == null) {
            runtime = getDefinition().isOrdered() ? new OrderedRuleAdapterRuntime(this) : new UnorderedRuleAdapterRuntime(this);
        }

        return runtime;
    }

    @Override
    public boolean acceptAsFirst(Event event) {
        return getRuntime().acceptAsFirst(event);
    }

    @Override
    protected boolean runRule() {
        return getRuntime().runRule();
    }

    @Override
    public boolean isCandidateForFirstEvent(Event event) {
        return getRuntime().isCandidateForFirstEvent(event);
    }

    @Override
    public Event getFirstEvent() {
        return getRuntime().getFirstEvent();
    }

    public Event getEvent(String eventAlias) {
        return getRuntime().getEvent(eventAlias);
    }

    public final Map<String, Event> getEventAliasMap() {
        return getRuntime().getEventAliasMap();
    }

    public final List<Event> getEventSequence() {
        return getRuntime().getEventSequence();
    }

    /**
     * Returns the event tree.
     *
     * @return the event tree.
     */
    public final Tree<NodeValue> getEventTree() {
        return getRuntime().getEventTree();
    }

    @Override
    public void validate() {
        super.validate();

        if (getDefinition().getEventSpecs() == null || getDefinition().getEventSpecs().isEmpty()) {
            throw createValidationException("Event specifications are missing.");
        }

        if (getDefinition().getEventNames().size() != getDefinition().getEventSpecs().size()) {
            throw createValidationException("Each event should have a specification (explicitly or implicitly).");
        }

        if (getDefinition().getEventSpecs().stream().map(RuleEventSpec::getAlias).distinct().count() < getDefinition().getEventSpecs()
                .size()) {
            throw createValidationException("Event aliases together with names must be unique.");
        }

        // Validate condition event aliases.
        Set<String> eventAliasesSet = getDefinition().getEventSpecs().stream().map(RuleEventSpec::getAlias).collect(Collectors.toSet());
        for (String alias : getEventConditions().keySet()) {
            if (!eventAliasesSet.contains(alias)) {
                throw createValidationException("Condition event alias '" + alias + "' does not exist");
            }
        }

        getRuntime().validate();
    }
}
