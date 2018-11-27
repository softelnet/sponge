package org.openksavi.sponge.util.process;

/**
 * A subprocess error redirect type.
 */
public enum ErrorRedirect {

    /**
     * Indicates that subprocess error output will be connected to the current Java process over a pipe. This is the default handling of
     * subprocess error output.
     */
    PIPE,

    /** Sets the destination for subprocess error output to be the same as those of the current Java process. */
    INHERIT,

    /**
     * Writes all subprocess error output to the {@link org.openksavi.sponge.util.process.ProcessInstance#getErrorString()} string. The
     * thread that started the subprocess will wait for the subprocess to exit.
     */
    STRING,

    /**
     * Writes all subprocess error output to the {@link org.openksavi.sponge.util.process.ProcessConfiguration#getErrorFile()} file. The
     * thread that started the subprocess will wait for the subprocess to exit.
     */
    FILE,

    /** Throw an exception if the error output is not empty. The thread that started the subprocess will wait for the subprocess to exit. */
    EXCEPTION,

    /** Logs the subprocess error output to the logger (as WARN). */
    LOGGER
}
