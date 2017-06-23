package org.openksavi.sponge.core.kb;

import org.openksavi.sponge.java.JavaKnowledgeBase;

public final class DefaultKnowledgeBase extends JavaKnowledgeBase {

    public static final String NAME = "_DEFAULT_";

    public DefaultKnowledgeBase() {
        setName(NAME);
    }

    @Override
    public void onInit() {
        //
    }
}