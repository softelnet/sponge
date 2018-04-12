package org.openksavi.sponge.core.kb;

import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.ScriptKnowledgeBase;

/**
 * Base knowledge base script definition.
 */
public abstract class BaseKnowledgeBaseScript implements KnowledgeBaseScript {

    /** A knowledge base that uses this script. */
    private ScriptKnowledgeBase knowledgeBase;

    /** A knowledge base script name. */
    private String name;

    protected BaseKnowledgeBaseScript(String name) {
        this.name = name;
    }

    @Override
    public ScriptKnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    @Override
    public void setKnowledgeBase(ScriptKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the string representation.
     *
     * @return the string representation.
     */
    @Override
    public String toString() {
        return name;
    }
}
