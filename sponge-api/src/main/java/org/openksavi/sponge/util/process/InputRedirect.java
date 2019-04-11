package org.openksavi.sponge.util.process;

/**
 * A subprocess input redirect type.
 */
public enum InputRedirect {

    /**
     * Indicates that subprocess standard input will be connected to the current Java process over a pipe. This is the default handling of
     * subprocess standard standard input.
     */
    PIPE,

    /** Sets the source for subprocess standard input to be the same as those of the current Java process. */
    INHERIT,

    /** Sets the subprocess input as the {@link org.openksavi.sponge.util.process.ProcessConfiguration#getInputString()} string. */
    STRING,

    /** Sets the subprocess input as the {@link org.openksavi.sponge.util.process.ProcessConfiguration#getInputBinary()} binary. */
    BINARY,

    /**
     * Sets the subprocess input as the {@link org.openksavi.sponge.util.process.ProcessConfiguration#getInputFile()} file specified as the
     * filename.
     */
    FILE,

    /**
     * Sets the subprocess input as a stream. This is a special case of {@code PIPE} that makes easier writing to and closing the subprocess
     * standard input {@link org.openksavi.sponge.util.process.ProcessInstance#getInput() ProcessInstance.getInput()} after start. Then you
     * should invoke manually {@link org.openksavi.sponge.util.process.ProcessInstance#waitForReady() ProcessInstance.waitForReady()}.
     */
    STREAM
}
