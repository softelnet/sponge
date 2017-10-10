package org.openksavi.sponge.core.kb;

import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.ScriptKnowledgeBase;

/**
 * File knowledge base script definition.
 */
public class FileKnowledgeBaseScript implements KnowledgeBaseScript {

    /** A knowledge base that uses this script. */
    private ScriptKnowledgeBase knowledgeBase;

    /** A knowledge base script file name. */
    private String fileName;

    /** A knowledge base script charset. */
    private String charset;

    /** Informs if the knowledge base script file is required. */
    private boolean required;

    /**
     * Creates a new knowledge base script instance.
     *
     * @param fileName file name.
     */
    public FileKnowledgeBaseScript(String fileName) {
        this(fileName, null);
    }

    /**
     * Creates a new knowledge base script instance.
     *
     * @param fileName file name.
     * @param charset charset.
     */
    public FileKnowledgeBaseScript(String fileName, String charset) {
        this(fileName, charset, KnowledgeBaseScript.DEFAULT_REQUIRED);
    }

    /**
     * Creates a new knowledge base script instance.
     *
     * @param fileName file name.
     * @param charset charset.
     * @param required required.
     */
    public FileKnowledgeBaseScript(String fileName, String charset, boolean required) {
        this.fileName = fileName;
        this.charset = charset;
        this.required = required;
    }

    @Override
    public ScriptKnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    @Override
    public void setKnowledgeBase(ScriptKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Returns string representation.
     *
     * @return string representation.
     */
    @Override
    public String toString() {
        return fileName;
    }
}
