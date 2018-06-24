package org.openksavi.sponge.core.kb;

import java.nio.charset.Charset;

/**
 * File-based knowledge base script definition.
 */
public class FileKnowledgeBaseScript extends BaseKnowledgeBaseScript {

    /** The script file is required by default. */
    public static final boolean DEFAULT_REQUIRED = true;

    /** A knowledge base script charset. */
    private Charset charset;

    /** Informs if the knowledge base script file is required. */
    private boolean required;

    /**
     * Creates a new knowledge base script instance.
     *
     * @param fileName the knowledge base script file name.
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
    public FileKnowledgeBaseScript(String fileName, Charset charset) {
        this(fileName, charset, DEFAULT_REQUIRED);
    }

    /**
     * Creates a new knowledge base script instance.
     *
     * @param fileName file name.
     * @param required required.
     */
    public FileKnowledgeBaseScript(String fileName, boolean required) {
        this(fileName, null, required);
    }

    /**
     * Creates a new knowledge base script instance.
     *
     * @param fileName file name.
     * @param charset charset.
     * @param required required.
     */
    public FileKnowledgeBaseScript(String fileName, Charset charset, boolean required) {
        super(fileName);

        this.charset = charset;
        this.required = required;
    }

    public String getFileName() {
        return getName();
    }

    /**
     * Returns the charset.
     *
     * @return the charset.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Sets the charset.
     *
     * @param charset the charset.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Returns {@code true} if the file is required.
     *
     * @return {@code true} if the file is required.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets if the file is required.
     *
     * @param required {@code true} if the file is required.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }
}
