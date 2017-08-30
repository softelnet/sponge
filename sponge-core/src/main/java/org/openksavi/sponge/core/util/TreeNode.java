package org.openksavi.sponge.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TreeNode<T> {

    private static final int TREE_INDENT = 4;

    protected T value;

    protected TreeNode<T> parent;

    protected List<TreeNode<T>> children = new ArrayList<>();

    protected int level = 0;

    public TreeNode() {
    }

    public TreeNode(T value) {
        this.value = value;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public T getValue() {
        return value;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean hasParent() {
        return parent != null;
    }

    public void addChild(TreeNode<T> node) {
        children.add(node);
        node.parent = this;
        node.level = level + 1;
    }

    public void removeChild(TreeNode<T> node) {
        children.remove(node);
        if (node.parent == this) {
            node.parent = null;
            node.level = 0;
        }
    }

    public void removeChild(int index, TreeNode<T> node) {
        children.remove(index);
        if (node.parent == this) {
            node.parent = null;
            node.level = 0;
        }
    }

    public void removeAllChildren() {
        children.clear();
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return toString(0, new StringBuilder(level * 128)).toString();
    }

    private StringBuilder toString(int level, StringBuilder s) {
        s.append("\n" + StringUtils.repeat(' ', level * TREE_INDENT) + value);
        children.forEach(node -> node.toString(level + 1, s));

        return s;
    }

    public List<T> getPathValues(TreeNode<T> node) {
        if (node == null) {
            return Collections.emptyList();
        }

        List<T> path = new ArrayList<>(node.level + 1);
        for (TreeNode<T> n = node; n != null; n = n.getParent()) {
            path.add(n.getValue());
        }

        Collections.reverse(path);

        return path;
    }

    public List<TreeNode<T>> getPath(TreeNode<T> node) {
        if (node == null) {
            return Collections.emptyList();
        }

        List<TreeNode<T>> path = new ArrayList<>(node.level + 1);
        for (TreeNode<T> n = node; n != null; n = n.getParent()) {
            path.add(n);
        }

        Collections.reverse(path);

        return path;
    }
}
