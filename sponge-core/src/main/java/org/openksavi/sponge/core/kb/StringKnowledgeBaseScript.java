package org.openksavi.sponge.core.kb;

/**
 * String-based knowledge base script definition.
 */
public class StringKnowledgeBaseScript extends BaseKnowledgeBaseScript {

    /** A knowledge base script body. */
    private StringBuilder body;

    public StringKnowledgeBaseScript(StringBuilder body) {
        super("String");

        this.body = body;
    }

    public StringKnowledgeBaseScript(String body) {
        this(new StringBuilder(body));
    }

    public StringKnowledgeBaseScript() {
        this(new StringBuilder());
    }

    public StringBuilder getBody() {
        return body;
    }
}
