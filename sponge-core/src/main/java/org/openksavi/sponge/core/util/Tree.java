package org.openksavi.sponge.core.util;

import java.util.Collections;
import java.util.List;

public class Tree<T> {

    protected TreeNode<T> root;

    public Tree() {
    }

    public void setRoot(TreeNode<T> node) {
        root = node;
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
    }

    public List<T> getPathValues(TreeNode<T> node) {
        if (root == null) {
            return Collections.emptyList();
        } else {
            return root.getPathValues(node);
        }
    }

    @Override
    public String toString() {
        if (root != null) {
            return root.toString();
        } else {
            return "";
        }
    }
}
