package org.openksavi.sponge.engine.processing;

import java.util.List;
import java.util.concurrent.Executor;

import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.EventSetProcessorDefinition;
import org.openksavi.sponge.event.Event;

/**
 * Main processing unit handler that handles event set processors.
 *
 * @param <G>
 *            event set processor group.
 * @param <T>
 *            event set processor.
 */
@SuppressWarnings("rawtypes")
public interface EventSetProcessorMainProcessingUnitHandler<G extends EventSetProcessorAdapterGroup<T>, T extends EventSetProcessorAdapter>
        extends MainProcessingUnitHandler {

    /**
     * Adds (if needed) the duration for the specified event set processor.
     *
     * @param adapter
     *            event set processor adapter.
     */
    void addDuration(T adapter);

    /**
     * Removes the duration for the specified event set processor.
     *
     * @param adapter
     *            event set processor adapter.
     */
    void removeDuration(T adapter);

    /**
     * Removes durations for all instances of the specified event set processor.
     *
     * @param adapterGroup
     *            event set processor adapter group.
     */
    void removeDurations(G adapterGroup);

    /**
     * Returns a thread pool for applying during processing instances in event set processor group.
     *
     * @return thread pool.
     */
    Executor getAsyncEventSetProcessorExecutor();

    /**
     * Processes the incoming event for event set processor adapters from the event set processor adapter group.
     *
     * @param processorDefinition
     *            event set processor definition.
     * @param eventSetProcessorAdapters
     *            event set processor adapters.
     * @param event
     *            the incoming event.
     */
    void processEventForEventSetProcessorAdapters(EventSetProcessorDefinition processorDefinition, List<T> eventSetProcessorAdapters,
            Event event);
}
