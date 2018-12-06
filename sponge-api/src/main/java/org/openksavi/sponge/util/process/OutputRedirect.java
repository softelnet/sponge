package org.openksavi.sponge.util.process;

/**
 * A subprocess output redirect type.
 */
public enum OutputRedirect {

    /**
     * Indicates that subprocess standard output will be connected to the current Java process over a pipe. This is the default handling of
     * subprocess standard output.
     */
    PIPE,

    /** Sets the destination for subprocess standard output to be the same as those of the current Java process. */
    INHERIT,

    /**
     * Writes all subprocess standard output to the {@link org.openksavi.sponge.util.process.ProcessInstance#getOutputString()} string. The
     * thread that started the subprocess will wait for the subprocess to exit.
     */
    STRING,

    /**
     * Writes all subprocess standard output to the {@link org.openksavi.sponge.util.process.ProcessInstance#getOutputBinary()} byte array.
     * The thread that started the subprocess will wait for the subprocess to exit.
     */
    BINARY,

    /**
     * Writes all subprocess standard output to the {@link org.openksavi.sponge.util.process.ProcessConfiguration#getOutputFile()} file. The
     * thread that started the subprocess will wait for the subprocess to exit.
     */
    FILE,

    /**
     * Sends a subprocess standard output as text lines to a line consumer (if set). It also logs the subprocess standard output to the
     * logger (as INFO).
     */
    CONSUMER
}
