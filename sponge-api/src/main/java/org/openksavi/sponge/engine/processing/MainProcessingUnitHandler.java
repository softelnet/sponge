package org.openksavi.sponge.engine.processing;

import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.event.Event;

/**
 * Main processing unit handler. It provides support for different event processing for different processors.
 */
public interface MainProcessingUnitHandler {

    /**
     * Processes the incoming event by the specified processor.
     *
     * @param adatper processor adapter.
     * @param event the incoming event.
     */
    void processEvent(ProcessorAdapter<?> adatper, Event event);

    /**
     * Starts up this handler.
     */
    default void startup() {
        //
    }

    /**
     * Shuts down this handler.
     */
    default void shutdown() {
        //
    }
}
