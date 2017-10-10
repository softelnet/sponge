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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.Tree;
import org.openksavi.sponge.core.util.TreeNode;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.EventMode;

/**
 * Abstract rule adapter runtime.
 */
public abstract class AbstractRuleAdapterRuntime implements RuleAdapterRuntime {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRuleAdapterRuntime.class);

    protected BaseRuleAdapter adapter;

    /** Event tree. Contains events that are used by this rule. */
    protected Tree<NodeValue> eventTree = new Tree<>();

    /** Map of (event alias, event instance). */
    protected transient Map<String, Event> eventAliasMap = new LinkedHashMap<>();

    public AbstractRuleAdapterRuntime(BaseRuleAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Checks conditions for the given node (containing level and event).
     *
     * @param node event tree node.
     * @return {@code true} if all conditions are met.
     */
    protected boolean checkConditions(TreeNode<NodeValue> node) {
        List<EventCondition> conditions = adapter.getConditions(adapter.getEventAlias(getEventIndex(node)));
        boolean result = true;

        if (conditions == null) {
            return result;
        }

        prepareEventAliasMap(node);
        for (EventCondition condition : conditions) {
            result = result && condition.condition(adapter.getProcessor(), node.getValue().getEvent());
            if (!result) {
                return result;
            }
        }

        return result;
    }

    @Override
    public boolean acceptAsFirst(Event event) {
        TreeNode<NodeValue> newNode = new TreeNode<>(new NodeValue(event));
        eventTree.setRoot(newNode);
        // Note that this check will be performed later one more time if this event is accepted as the first. The mode for the first event
        // is always FIRST.
        try {
            return shouldAddToEventTreeForFlaModes(newNode, event);
        } finally {
            eventTree.setRoot(null);
        }
    }

    /**
     * Processes the incoming event.
     */
    @Override
    public void onEvent(Event event) {
        // Early return if the rule has already been finished.
        if (!adapter.isRunning()) {
            return;
        }

        // Continue building the event tree (starting at the root) according to the incoming event.
        buildEventTree(eventTree.getRoot(), event);

        if (eventTree.isEmpty()) {
            // If the event tree is empty after trying to process the new event then the rule should be finished immediately.
            adapter.finish();
        } else {
            if (shouldRunRule()) {
                if (runRule()) {
                    // Finishing the rule because it has been run successfully and is no longer needed.
                    adapter.finish();
                }
            }
        }
    }

    protected abstract int getEventIndex(TreeNode<NodeValue> node);

    protected abstract boolean shouldRunRule();

    protected abstract boolean shouldAddToEventTreeForFlaModes(TreeNode<NodeValue> newNode, Event event);

    protected abstract int getExpectedEventIndex(TreeNode<NodeValue> node, Event event);

    /**
     * Checks if the event should be added to the event tree for mode NONE. Holder allows returning changed newNode.
     *
     * @param parentNode parent event tree node.
     * @param newNodeHolder new event tree node holder.
     * @param event new event.
     * @return {@code true} if the event should be added to the event tree.
     */
    protected abstract boolean shouldAddToEventTreeForNMode(TreeNode<NodeValue> parentNode, Mutable<TreeNode<NodeValue>> newNodeHolder,
            Event event);

    /**
     * Continues building the event tree for the incoming event starting at the specified node.
     *
     * @param node event tree node.
     * @param event incoming event.
     */
    protected void buildEventTree(TreeNode<NodeValue> node, Event event) {
        // Check if this event is the first event that starts the rule instance.
        boolean isFirstNode = (node == null);

        // Create a new node for the incoming event and add to the event tree. This node may be removed later when the event doesn't match.
        TreeNode<NodeValue> newNode = new TreeNode<>(new NodeValue(event));
        if (isFirstNode) { // First event that starts the rule.
            node = newNode;
            // Add the new node to the event tree as root.
            eventTree.setRoot(node);
        } else {
            // Recursively try to continue building the event tree, but only for modes FIRST, LAST and ALL.
            node.getChildren().forEach(child -> {
                // NONE events are processed in shouldAddToEventTreeForNMode(), not here.
                if (adapter.getEventMode(getEventIndex(child)) != EventMode.NONE) {
                    buildEventTree(child, event);
                }
            });

            // Return if reached the last level.
            if (node.getLevel() + 1 >= adapter.getEventCount()) {
                return;
            }

            // Add the new node to the event tree.
            node.addChild(newNode);
        }

        boolean rememberEvent = false; // Should this event be added to the event tree in this place.

        EventMode eventMode = getEventMode(newNode);

        if (eventMode != null) {
            switch (eventMode) {
            case FIRST:
            case LAST:
            case ALL:
                rememberEvent = shouldAddToEventTreeForFlaModes(newNode, event);
                break;
            case NONE:
                Mutable<TreeNode<NodeValue>> newNodeHolder = new MutableObject<>(newNode);
                rememberEvent = shouldAddToEventTreeForNMode(node, newNodeHolder, event);
                newNode = newNodeHolder.getValue(); // shouldAddToEventTreeForNMode() may change newNode.
                break;
            default:
                throw new SpongeException("Unsupported value: " + eventMode);
            }
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
     * @param node event tree node.
     * @return event mode.
     */
    protected EventMode getEventMode(TreeNode<NodeValue> node) {
        return adapter.getEventMode(getEventIndex(node));
    }

    /**
     * Attempts to run (fire) this rule.
     *
     * @return {@code true} if this rule has been run all times it ought to. In that case it will be finished.
     */
    @Override
    public boolean runRule() {
        // Attempt to run the rule only if the event tree is not empty (i.e. events have been remembered) starting at the root.
        return runRule(eventTree.getRoot());
    }

    /**
     * Attempts to run (fire) this rule for the specified node in the event tree.
     *
     * @param node event tree node.
     * @return {@code true} if this rule has been run (fired) all times it ought to. In that case it will be finished.
     */
    protected boolean runRule(TreeNode<NodeValue> node) {
        if (node == null) {
            return false;
        }

        if (isLeafLevel(node)) { // If the leaf of the event tree.
            prepareEventAliasMap(node);

            if (logger.isDebugEnabled()) {
                logger.debug("Running {}. Event tree: {}", adapter.getName(), eventTree);
            }

            // Running the rule for the calculated event sequence (there may be many such sequences for ALL mode).
            adapter.getProcessor().onRun(node.getValue().getEvent());

            EventMode eventMode = adapter.getEventMode(getEventIndex(node));
            switch (eventMode) {
            case FIRST:
                return true;
            case LAST:
            case NONE:
                // This line is executed only when a duration has been triggered.
                return true;
            case ALL:
                if (adapter.isDurationTriggered()) {
                    return true;
                } else {
                    // Mark the node to be removed from the tree because its event has already caused the rule to fire.
                    node.setValue(null);

                    // Return false because there could be new suitable event sequences in the future.
                    return false;
                }
            default:
                throw new SpongeException("Unsupported value: " + eventMode);
            }
        } else { // The node is not a leaf of the event tree.
            return runRuleForNonFinalNode(node);
        }
    }

    protected abstract boolean isLeafLevel(TreeNode<NodeValue> node);

    protected abstract boolean runRuleForNonFinalNode(TreeNode<NodeValue> node);

    /**
     * Clears event tree and event alias map.
     */
    @Override
    public void clear() {
        eventTree.clear();
        eventAliasMap.clear();
    }

    @Override
    public Event getEvent(String eventAlias) {
        Event event = eventAliasMap.get(eventAlias);

        if (event == null) {
            throw new SpongeException("Event with alias " + eventAlias + " doesn't exist");
        }

        return event;
    }

    /**
     * Clears and sets up event alias map (event alias - event instance).
     *
     * @param node the node for the last event.
     */
    protected void prepareEventAliasMap(TreeNode<NodeValue> node) {
        eventAliasMap.clear();

        eventTree.getPath(node).forEach(n -> eventAliasMap.put(adapter.getEventAlias(getEventIndex(n)), n.getValue().getEvent()));
    }

    /**
     * Returns the event tree.
     *
     * @return the event tree.
     */
    @Override
    public Tree<NodeValue> getEventTree() {
        return eventTree;
    }

    /**
     * Returns event alias map.
     *
     * @return event alias map.
     */
    @Override
    public Map<String, Event> getEventAliasMap() {
        return eventAliasMap;
    }

    /**
     * Returns event sequence.
     *
     * @return event sequence.
     */
    @Override
    public List<Event> getEventSequence() {
        return new ArrayList<>(eventAliasMap.values());
    }

    @Override
    public Event getFirstEvent() {
        return eventTree.getRoot() != null ? eventTree.getRoot().getValue().getEvent() : null;
    }

    protected boolean handleNoneEventHappenedButShouldNot(TreeNode<NodeValue> parentNode, TreeNode<NodeValue> node, Event event) {
        // If the event is expected NOT to happen, since this is NONE mode, but happened anyway.
        if (getExpectedEventIndex(node, event) >= 0) {
            // Check conditions for this event.
            if (checkConditions(node)) {
                // Force finishing the rule without firing.
                eventTree.setRoot(null);
                parentNode.removeAllChildren();
                return true;
            }
        }

        return false;
    }
}
