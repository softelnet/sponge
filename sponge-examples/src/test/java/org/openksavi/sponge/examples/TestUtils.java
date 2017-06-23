/* Copyright 2016-2017 Softelnet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openksavi.sponge.examples;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.util.CorrelationEventsLog;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class TestUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    public static final String DEFAULT_KB = "kb";

    public static void testExample(String config, int timeout) {
        logger.info("Testing {}", config);

        Engine engine = DefaultEngine.builder().config(config).build();
        Utils.trialRunEngine(engine, timeout);
        if (engine.isError()) {
            throw Utils.wrapException("testExample", engine.getError());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Event> getEvents(Engine engine, String eventName) {
        Map<String, Object> events = engine.getOperations().getVariable(Map.class, "events");
        return (List<Event>) events.get(eventName);
    }

    @SuppressWarnings("unchecked")
    public static int getEventCounter(Engine engine, String key) {
        return ((Map<String, Number>) engine.getOperations().getVariable("eventCounter")).get(key).intValue();
    }

    public static void assertEventSequences(CorrelationEventsLog eventsLog, String key, String firstEventLabel, String[][] expected) {
        assertEventSequences(eventsLog, key, firstEventLabel, expected, false);
    }

    public static void assertEventSequences(CorrelationEventsLog eventsLog, String key, String firstEventLabel, String[][] expected,
            boolean ignoreOrderOfSequences) {
        List<List<Event>> lists = eventsLog.getEvents(key, firstEventLabel);
        assertEquals(expected.length, lists.size());

        Object expectedSequenceReport = null;
        Object realSequenceReport = null;

        try {
            if (ignoreOrderOfSequences) {
                // The order of sequences doesn't matter, however the order inside a sequence is important.
                Set<List<String>> expectedSet =
                        Stream.of(expected).map(sequence -> Arrays.asList(sequence)).collect(Collectors.toCollection(LinkedHashSet::new));
                Set<List<String>> realSet = new LinkedHashSet<>();
                lists.forEach(list -> realSet.add(list.stream()
                        .map(event -> event != null ? event.get(CorrelationEventsLog.LABEL_ATTRIBUTE_NAME, String.class) : null)
                        .collect(Collectors.toCollection(ArrayList::new))));
                expectedSequenceReport = expectedSet;
                realSequenceReport = realSet;
                assertEquals(expectedSet, realSet);
            } else {
                for (int i = 0; i < expected.length; i++) {
                    String[] expectedSequence = expected[i];
                    String[] realSequence = lists.get(i).stream()
                            .map(event -> event != null ? event.get(CorrelationEventsLog.LABEL_ATTRIBUTE_NAME, String.class) : null)
                            .toArray(String[]::new);
                    expectedSequenceReport = Arrays.asList(expectedSequence);
                    realSequenceReport = Arrays.asList(realSequence);
                    assertArrayEquals(expectedSequence, realSequence);
                }
            }
        } catch (AssertionError e) {
            logger.error("ERROR key=" + key + ", expcted=" + expectedSequenceReport + ", real=" + realSequenceReport, e);
            throw e;
        }
    }

    public static StandaloneEngineMain startupStandaloneEngineMain(String... args) {
        System.out.println("Starting up standalone engine with args: " + Arrays.asList(args));
        StandaloneEngineMain result = new StandaloneEngineMain(true);
        result.startup(args);

        return result;
    }

    public static void shutdownStandaloneEngineMain(StandaloneEngineMain standaloneEngineMain) {
        if (standaloneEngineMain != null) {
            standaloneEngineMain.shutdown();
        }
    }

    public static void reloadLogback(String configFile) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            configurator.doConfigure(configFile);
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
}
