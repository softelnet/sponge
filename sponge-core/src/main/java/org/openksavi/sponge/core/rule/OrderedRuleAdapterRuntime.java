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

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;

import org.apache.commons.lang3.mutable.Mutable;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.TreeNode;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventMode;

/**
 * Ordered rule adapter runtime.
 */
public class OrderedRuleAdapterRuntime extends AbstractRuleAdapterRuntime {

    public OrderedRuleAdapterRuntime(BaseRuleAdapter adapter) {
        super(adapter);
    }

    @Override
    public boolean isCandidateForFirstEvent(Event event) {
        TreeNode<NodeValue> newNode = new TreeNode<>(new NodeValue(event));
        eventTree.setRoot(newNode);

        try {
            return getExpectedEventIndex(newNode, event) >= 0;
        } finally {
            eventTree.setRoot(null);
        }
    }

    /**
     * Checks if the specified event is expected for the given level.
     *
     * @param node tree node for the event.
     * @param event event instance.
     * @return {@code true} if the specified event is expected for the given level.
     */
    @Override
    protected int getExpectedEventIndex(TreeNode<NodeValue> node, Event event) {
        int level = node.getLevel();
        List<String> eventNames = adapter.getEventNames();
        if (level < eventNames.size() && adapter.getKnowledgeBase().getEngineOperations().getEngine().getPatternMatcher()
                .matches(eventNames.get(level), event.getName())) {
            node.getValue().setIndex(level);
            return level;
        }

        return -1;
    }

    /**
     * Checks if the event should be added to the event tree for modes FIRST, LAST or ALL.
     *
     * @param newNode new event tree node.
     * @param event new event.
     * @return {@code true} if the event should be added to the event tree.
     */
    @Override
    protected boolean shouldAddToEventTreeForFlaModes(TreeNode<NodeValue> newNode, Event event) {
        // Checks if the incoming event is expected by this rule. The preliminary check (for level 0) has been done in
        // isCandidateForFirstEvent.
        int index = newNode.getLevel() == 0 ? 0 : getExpectedEventIndex(newNode, event);

        if (newNode.getValue().getIndex() == null) {
            newNode.getValue().setIndex(index);
        }

        if (index >= 0) {
            // Check conditions for this event.
            if (checkConditions(newNode)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean shouldAddToEventTreeForNMode(TreeNode<NodeValue> parentNode, Mutable<TreeNode<NodeValue>> newNodeHolder,
            Event event) {
        boolean result = false;

        if (handleNoneEventHappenedButShouldNot(parentNode, newNodeHolder.getValue(), event)) {
            return false;
        }

        // We need only one node marking NONE event that has not happened, so others are removed.
        parentNode.getChildren().subList(1, parentNode.getChildren().size()).clear();

        // Because an event hasn't happened, the value of the node will be set to null.
        TreeNode<NodeValue> emptyNode = parentNode.getChildren().get(0);
        if (emptyNode.getValue().getEvent() != null) {
            emptyNode.getValue().setEvent(null);
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
     * Checks if there should be an attempt to run the rule.
     *
     * @return {@code true} if there should be an attempt to run the rule.
     */
    @Override
    protected boolean shouldRunRule() {
        EventMode lastMode = getDefinition().getEventSpec(getDefinition().getEventSpecs().size() - 1).getMode();

        // If the mode of the last specified event is FIRST or ALL always try to run the rule.
        if (lastMode == EventMode.FIRST || lastMode == EventMode.ALL) {
            return true;
        }

        // If the mode of the last specified event is LAST or NONE try to run the rule only when a duration timeout occurred.
        return adapter.isDurationTriggered();
    }

    @Override
    protected boolean runRuleForNonFinalNode(TreeNode<NodeValue> node) {
        int maxLevel = adapter.getEventCount() - 1;
        boolean fired = false;
        TreeNode<NodeValue> child;

        EventMode eventMode = getDefinition().getEventSpec(node.getLevel() + 1).getMode();
        switch (eventMode) {
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
            Iterator<TreeNode<NodeValue>> treeNodeIterator = node.getChildren().listIterator();
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
            if (adapter.isDurationTriggered() && !node.hasChildren() && node.getLevel() + 1 == maxLevel) {
                // Add a new node with no event since there should be none.
                node.addChild(new TreeNode<>(new NodeValue(null)));
            }

            // Consider the first (and only) node.
            if (node.hasChildren()) {
                child = node.getChildren().get(0);
                if (child != null) {
                    return runRule(child);
                }
            }
            break;
        default:
            throw new SpongeException("Unsupported value: " + eventMode);
        }

        return fired;
    }

    @Override
    protected int getEventIndex(TreeNode<NodeValue> node) {
        return node.getLevel();
    }

    @Override
    protected boolean isLeafLevel(TreeNode<NodeValue> node) {
        return node.getLevel() == adapter.getEventCount() - 1;
    }

    @Override
    public void validate() {
        EventMode firstMode = getDefinition().getEventSpec(0).getMode();
        if (firstMode != EventMode.FIRST) {
            throw adapter.createValidationException("The mode of the first event in the sequence must be " + EventMode.FIRST + ".");
        }

        EventMode lastMode = getDefinition().getEventSpec(getDefinition().getEventSpecs().size() - 1).getMode();
        if (lastMode == null) {
            throw adapter.createValidationException("The mode of the last event in the sequence is not set");
        }

        if ((lastMode == EventMode.LAST || lastMode == EventMode.NONE) && !adapter.hasDuration()) {
            throw adapter.createValidationException(
                    "If the mode of the last event in the sequence is " + lastMode + " a duration should be set.");
        }
    }
}
