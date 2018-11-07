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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.Mutable;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.TreeNode;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventMode;

/**
 * Unordered rule adapter runtime.
 */
public class UnorderedRuleAdapterRuntime extends AbstractRuleAdapterRuntime {

    private List<String> noneModeEventAliases;

    public UnorderedRuleAdapterRuntime(BaseRuleAdapter adapter) {
        super(adapter);

        noneModeEventAliases = createNoneModeEventAliases();
    }

    private List<String> createNoneModeEventAliases() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < adapter.getEventCount(); i++) {
            if (getDefinition().getEventSpec(i).getMode() == EventMode.NONE) {
                result.add(getDefinition().getEventSpec(i).getAlias());
            }
        }

        return result;
    }

    @Override
    public boolean isCandidateForFirstEvent(Event event) {
        return true;
    }

    protected Set<Integer> getPreviousHappenedEventIndexes(TreeNode<NodeValue> node) {
        Set<Integer> happenedEventIndexes = new HashSet<>();
        for (TreeNode<NodeValue> n = node.getParent(); n != null; n = n.getParent()) {
            happenedEventIndexes.add(n.getValue().getIndex());
        }

        return happenedEventIndexes;
    }

    @Override
    protected int getExpectedEventIndex(TreeNode<NodeValue> node, Event event) {
        List<String> eventNames = adapter.getDefinition().getEventNames();
        Set<Integer> happenedEventIndexes = getPreviousHappenedEventIndexes(node);

        for (int i = 0; i < eventNames.size(); i++) {
            if (!happenedEventIndexes.contains(i) && adapter.getKnowledgeBase().getEngineOperations().getEngine().getPatternMatcher()
                    .matches(eventNames.get(i), event.getName())) {
                node.getValue().setIndex(i);
                return i;
            }
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
        int index = getExpectedEventIndex(newNode, event);
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
        handleNoneEventHappenedButShouldNot(parentNode, newNodeHolder.getValue(), event);

        return false;
    }

    @Override
    protected boolean shouldRunRule() {
        return true;
    }

    protected List<TreeNode<NodeValue>> resolveSubNodesToRun(TreeNode<NodeValue> node) {
        List<TreeNode<NodeValue>> result = new ArrayList<>();

        Map<Integer, TreeNode<NodeValue>> mapFirst = new LinkedHashMap<>();
        Map<Integer, TreeNode<NodeValue>> mapLast = new LinkedHashMap<>();

        boolean endLoop = false;
        Iterator<TreeNode<NodeValue>> treeNodeIterator = node.getChildren().listIterator();
        while (!endLoop && treeNodeIterator.hasNext()) {
            TreeNode<NodeValue> child = treeNodeIterator.next();

            int index = getEventIndex(child);
            EventMode eventMode = getDefinition().getEventSpec(index).getMode();
            switch (eventMode) {
            case FIRST:
                if (!mapFirst.containsKey(index)) {
                    mapFirst.put(index, child);
                    result.add(child);
                }
                break;
            case LAST:
                TreeNode<NodeValue> oldLast = mapLast.get(index);
                mapLast.put(index, child);
                if (oldLast != null) {
                    result.remove(oldLast);
                }

                result.add(child);
                break;
            case ALL:
                result.add(child);
                break;
            case NONE:
                throw new SpongeException(EventMode.NONE + " mode event should not be present in the event tree");
            default:
                throw new SpongeException("Unsupported value: " + eventMode);
            }
        }

        return result;
    }

    @Override
    protected boolean runRuleForNonFinalNode(TreeNode<NodeValue> node) {
        boolean fired = false;

        for (TreeNode<NodeValue> child : resolveSubNodesToRun(node)) {
            if (runRule(child)) {
                fired = true;
            }

            // Remove the child node from the tree because its event has already caused the rule to fire.
            if (child.getValue() == null) {
                node.removeChild(child);
            }
        }

        // case NONE:
        // // For NONE mode consider only one (empty) node (that may not exist).
        //
        // // If duration has been triggered and there is no child node and the next level is the leaf level (maxLevel).
        // if (adapter.isDurationTriggered() && !node.hasChildren() && node.getLevel() + 1 == maxLevel) {
        // // Add a new node with no event since there should be none.
        // node.addChild(new TreeNode<>(new NodeValue(null)));
        // }
        //
        // // Consider the first (and only) node.
        // if (node.hasChildren()) {
        // child = node.getChildren().get(0);
        // if (child != null) {
        // return runRule(child);
        // }
        // }

        return fired;

    }

    @Override
    protected int getEventIndex(TreeNode<NodeValue> node) {
        return node.getValue().getIndex();
    }

    @Override
    protected EventMode getEventMode(TreeNode<NodeValue> node) {
        if (node.getValue().getIndex() == null) {
            int index = getExpectedEventIndex(node, node.getValue().getEvent());
            if (index >= 0) {
                node.getValue().setIndex(index);
            } else {
                return null;
            }
        }

        return getDefinition().getEventSpec(getEventIndex(node)).getMode();
    }

    @Override
    protected boolean isLeafLevel(TreeNode<NodeValue> node) {
        if (adapter.hasDuration() && !adapter.isDurationTriggered()) {
            return node.getLevel() == adapter.getEventCount() - 1;
        } else {
            return node.getLevel() == adapter.getEventCount() - 1 - noneModeEventAliases.size();
        }
    }

    @Override
    protected void prepareEventAliasMap(TreeNode<NodeValue> node) {
        eventAliasMap.clear();

        eventTree.getPath(node).forEach(
                n -> eventAliasMap.put(adapter.getDefinition().getEventSpec(getEventIndex(n)).getAlias(), n.getValue().getEvent()));

        // For event with NONE mode put null.
        noneModeEventAliases.forEach(alias -> eventAliasMap.put(alias, null));
    }

    @Override
    public void validate() {
        //
    }
}
