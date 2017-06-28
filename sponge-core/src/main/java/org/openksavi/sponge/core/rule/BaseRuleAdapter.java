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

package org.openksavi.sponge.core.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.Tree;
import org.openksavi.sponge.core.util.TreeNode;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.rule.Rule;

/**
 * Base rule adapter.
 */
public class BaseRuleAdapter extends AbstractRuleAdapter<Rule> {

    private static final Logger logger = LoggerFactory.getLogger(BaseRuleAdapter.class);

    /** Event tree. Contains events that are used by this rule. */
    private Tree<Event> eventTree = new Tree<>();

    /** Map of (event alias, event instance). */
    private transient Map<String, Event> eventAliasMap = new LinkedHashMap<>();

    public BaseRuleAdapter(BaseRuleDefinition definition) {
        super(definition);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.RULE;
    }

    @Override
    public boolean acceptsAsFirst(Event event) {
        TreeNode<Event> newNode = new TreeNode<>(event);
        eventTree.setRoot(newNode);
        // Note that this check will be performed later one more time if this event is accepted as the first. The mode for the first event
        // is always FIRST.
        shouldAddToEventTreeForFLAModes(newNode, event);
        eventTree.setRoot(null);
        return true;
    }

    /**
     * Checks if the specified event is expected for the given level.
     *
     * @param level
     *            level for the event.
     * @param event
     *            event instance.
     * @return {@code true} if the specified event is expected for the given level.
     */
    protected boolean isEventExpected(int level, Event event) {
        String[] eventNames = getEventNames();
        return level < eventNames.length && eventNames[level].equals(event.getName());
    }

    /**
     * Checks conditions for the given node (containing level and event).
     *
     * @param node
     *            event tree node.
     * @return {@code true} if all conditions are met.
     */
    protected boolean checkConditions(TreeNode<Event> node) {
        List<EventCondition> conditions = getConditions(getEventAlias(node.getLevel()));
        boolean result = true;

        if (conditions == null) {
            return result;
        }

        prepareEventAliasMap(node);
        for (EventCondition condition : conditions) {
            result = result && condition.condition(getProcessor(), node.getValue());
            if (!result) {
                return result;
            }
        }

        return result;
    }

    /**
     * Processes the incoming event.
     */
    @Override
    public void onEvent(Event event) {
        lock.lock();

        try {
            // Early return.
            if (!isRunning()) {
                return;
            }

            // Continue building the event tree (starting at the root) according to the incoming event.
            buildEventTree(eventTree.getRoot(), event);

            if (eventTree.isEmpty()) {
                // If the event tree is empty after trying to process the new event then the rule should be finished immediately.
                finish();
            } else {
                if (shouldRunRule()) {
                    if (runRule()) {
                        // Finishing the rule because it has been run successfully and is no longer needed.
                        finish();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if there should be an attempt to run the rule.
     *
     * @return {@code true} if there should be an attempt to run the rule.
     */
    private boolean shouldRunRule() {
        EventMode lastMode = getEventMode(getEventModes().length - 1);

        // If the mode of the last specified event is FIRST or ALL always try to run the rule.
        if (lastMode == EventMode.FIRST || lastMode == EventMode.ALL) {
            return true;
        }

        // If the mode of the last specified event is LAST or NONE try to run the rule only when a duration timeout occurred.
        return isDurationTriggered();
    }

    /**
     * Checks if the event should be added to the event tree for modes FIRST, LAST or ALL.
     *
     * @param newNode
     *            new event tree node.
     * @param event
     *            new event.
     * @return {@code true} if the event should be added to the event tree.
     */
    protected boolean shouldAddToEventTreeForFLAModes(TreeNode<Event> newNode, Event event) {
        // Checks if the incoming event is expected by this rule.
        if (isEventExpected(newNode.getLevel(), event)) {
            // Check conditions for this event.
            if (checkConditions(newNode)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the event should be added to the event tree for mode NONE. Holder allows returning changed newNode.
     *
     * @param parentNode
     *            parent event tree node.
     * @param newNodeHolder
     *            new event tree node holder.
     * @param event
     *            new event.
     * @return {@code true} if the event should be added to the event tree.
     */
    protected boolean shouldAddToEventTreeForNMode(TreeNode<Event> parentNode, Mutable<TreeNode<Event>> newNodeHolder, Event event) {
        boolean result = false;

        // If the event is expected NOT to happen, since this is NONE mode, but happened anyway.
        if (isEventExpected(newNodeHolder.getValue().getLevel(), event)) {
            // Check conditions for this event.
            if (checkConditions(newNodeHolder.getValue())) {
                // Force finishing the rule without firing.
                eventTree.setRoot(null);
                parentNode.removeAllChildren();
                return false;
            }
        }

        // We need only one node marking NONE event that has not happened, so others are removed.
        parentNode.getChildren().subList(1, parentNode.getChildren().size()).clear();

        // Because an event hasn't happened, the value of the node will be set to null.
        TreeNode<Event> emptyNode = parentNode.getChildren().get(0);
        if (emptyNode.getValue() != null) {
            emptyNode.setValue(null);
        }

        // Recursively build event tree because the event may match one of the following expected events for this rule.
        buildEventTree(emptyNode, event);

        // Add to event tree only when the event does match one of the following expected events.
        if (emptyNode.hasChildren()) {
            result = true;
        }

        // Change newNode in the holder for further processing.
        newNodeHolder.setValue(emptyNode);

        return result;
    }

    /**
     * Continues building the event tree for the incoming event starting at the specified node.
     *
     * @param node
     *            event tree node.
     * @param event
     *            incoming event.
     */
    protected void buildEventTree(TreeNode<Event> node, Event event) {
        // Check if this event is the first event that starts the rule instance.
        boolean isFirstNode = (node == null);

        // Create a new node for the incoming event and add to the event tree. This node may be removed later when the event doesn't match.
        TreeNode<Event> newNode = new TreeNode<>(event);
        if (isFirstNode) { // First event that starts the rule.
            node = newNode;
            // Add the new node to the event tree as root.
            eventTree.setRoot(node);
        } else {
            // Recursively try to continue building the event tree, but only for modes FIRST, LAST and ALL.
            node.getChildren().forEach(child -> {
                // NONE events are processed in shouldAddToEventTreeForNMode(), not here.
                if (getEventMode(child.getLevel()) != EventMode.NONE) {
                    buildEventTree(child, event);
                }
            });

            // Return if reached the last level.
            if (node.getLevel() + 1 >= getEventCount()) {
                return;
            }

            // Add the new node to the event tree.
            node.addChild(newNode);
        }

        boolean rememberEvent = false; // Should this event be added to the event tree in this place.

        switch (getEventMode(newNode)) {
        case FIRST:
        case LAST:
        case ALL:
            rememberEvent = shouldAddToEventTreeForFLAModes(newNode, event);
            break;
        case NONE:
            Mutable<TreeNode<Event>> newNodeHolder = new MutableObject<>(newNode);
            rememberEvent = shouldAddToEventTreeForNMode(node, newNodeHolder, event);
            newNode = newNodeHolder.getValue(); // shouldAddToEventTreeForNMode() may change newNode.
            break;
        }

        // Remove the node for the incoming event if the event doesn't match.
        if (!rememberEvent) {
            if (isFirstNode) {
                eventTree.setRoot(null);
            } else {
                node.removeChild(newNode);
            }
        }
    }

    /**
     * Returns event mode.
     *
     * @param node
     *            event tree node.
     * @return event mode.
     */
    protected EventMode getEventMode(TreeNode<Event> node) {
        return node == null ? EventMode.FIRST : getEventMode(node.getLevel());
    }

    /**
     * Attempts to run (fire) this rule.
     *
     * @return {@code true} if this rule has been run all times it ought to. In that case it will be finished.
     */
    @Override
    protected boolean runRule() {
        // Attempt to run the rule only if the event tree is not empty (i.e. events have been remembered) starting at the root.
        return runRule(eventTree.getRoot());
    }

    /**
     * Attempts to run (fire) this rule for the specified node in the event tree.
     *
     * @param node
     *            event tree node.
     * @return {@code true} if this rule has been run (fired) all times it ought to. In that case it will be finished.
     */
    private boolean runRule(TreeNode<Event> node) {
        if (node == null) {
            return false;
        }

        boolean fired = false;
        int maxLevel = getEventCount() - 1;

        if (node.getLevel() == maxLevel) { // maxLevel indicates the leaf of the event tree.
            prepareEventAliasMap(node);
            // Running the rule for the calculated event sequence (there may be many such sequences for ALL mode).
            getProcessor().run(node.getValue());

            if (logger.isDebugEnabled()) {
                logger.debug("Event tree: {}", eventTree);
            }

            switch (getEventMode(maxLevel)) {
            case FIRST:
                return true;
            case LAST:
            case NONE:
                // This line is executed only when a duration has been triggered.
                return true;
            case ALL:
                if (isDurationTriggered()) {
                    return true;
                } else {
                    // Mark the node to be removed from the tree because its event has already caused the rule to fire.
                    node.setValue(null);

                    // Return false because there could be new suitable event sequences in the future.
                    return false;
                }
            }
        } else if (node.getLevel() < maxLevel) { // The node is not a leaf of the event tree.
            TreeNode<Event> child;

            switch (getEventModes()[node.getLevel() + 1]) {
            case FIRST:
                // For FIRST mode consider only the first event in the next level.
                if (node.hasChildren()) {
                    if ((child = node.getChildren().get(0)) != null) {
                        return runRule(child);
                    }
                }
                break;
            case LAST:
                // For LAST mode consider only the last event in the next level.
                if (node.hasChildren()) {
                    if ((child = Iterables.getLast(node.getChildren())) != null) {
                        return runRule(child);
                    }
                }
                break;
            case ALL:
                // For ALL mode consider all events in the next level.
                Iterator<TreeNode<Event>> treeNodeIterator = node.getChildren().listIterator();
                while (treeNodeIterator.hasNext()) {
                    child = treeNodeIterator.next();
                    if (runRule(child)) {
                        fired = true;
                    }

                    // Remove the child node from the tree because its event has already caused the rule to fire.
                    if (child.getValue() == null) {
                        treeNodeIterator.remove();
                    }
                }
                break;
            case NONE:
                // For NONE mode consider only one (empty) node (that may not exist).

                // If duration has been triggered and there is no child node and the next level is the leaf level (maxLevel).
                if (isDurationTriggered() && !node.hasChildren() && node.getLevel() + 1 == maxLevel) {
                    // Add a new node with no event since there should be none.
                    node.addChild(new TreeNode<>(null));
                }

                // Consider the first (and only) node.
                if (node.hasChildren()) {
                    child = node.getChildren().get(0);
                    if (child != null) {
                        return runRule(child);
                    }
                }
                break;
            }
        }

        return fired;
    }

    /**
     * Clears event tree and event alias map.
     */
    @Override
    public void clear() {
        lock.lock();
        try {
            eventTree.clear();
            eventAliasMap.clear();
        } finally {
            lock.unlock();
        }
    }

    public Event getEvent(String eventAlias) {
        Event event = eventAliasMap.get(eventAlias);

        if (event == null) {
            throw new SpongeException("Event with alias " + eventAlias + " doesn't exist");
        }

        return event;
    }

    /**
     * Clears and sets up event alias map (event alias -> event instance).
     *
     * @param node
     *            the node for the last event.
     */
    private void prepareEventAliasMap(TreeNode<Event> node) {
        eventAliasMap.clear();

        List<Event> path = eventTree.getPathValues(node);
        for (int i = 0; i < path.size(); i++) {
            eventAliasMap.put(getEventAlias(i), path.get(i));
        }
    }

    /**
     * Returns the event tree.
     *
     * @return the event tree.
     */
    public Tree<Event> getEventTree() {
        return eventTree;
    }

    /**
     * Returns event alias map.
     *
     * @return event alias map.
     */
    public Map<String, Event> getEventAliasMap() {
        return eventAliasMap;
    }

    /**
     * Returns event sequence.
     *
     * @return event sequence.
     */
    public List<Event> getEventSequence() {
        return new ArrayList<>(eventAliasMap.values());
    }

    private SpongeException createValidationException(String text) {
        return new SpongeException("Invalid rule " + getName() + ". " + text);
    }

    @Override
    public void validate() {
        super.validate();

        if (getEventModes() == null || getEventModes().length < 1) {
            throw createValidationException("Event modes are not specified.");
        }

        if (getEventNames().length != getEventModes().length) {
            throw createValidationException("Each event should have a mode specified (explicitly or implicitly).");
        }

        EventMode firstMode = getEventModes()[0];
        if (firstMode != EventMode.FIRST) {
            throw createValidationException("The mode of the first event in the sequence must be " + EventMode.FIRST + ".");
        }

        EventMode lastMode = getEventModes()[getEventModes().length - 1];
        if (lastMode == null) {
            throw createValidationException("The mode of the last event in the sequence is not set");
        }

        if ((lastMode == EventMode.LAST || lastMode == EventMode.NONE) && !hasDuration()) {
            throw createValidationException("If the mode of the last event in the sequence is " + lastMode + " a duration should be set.");
        }
    }
}
