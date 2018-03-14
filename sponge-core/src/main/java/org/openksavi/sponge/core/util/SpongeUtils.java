/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.core.util;

import static org.awaitility.Awaitility.await;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.google.common.util.concurrent.MoreExecutors;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.engine.EngineConstants;
import org.openksavi.sponge.core.event.EventId;
import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.WrappedException;
import org.openksavi.sponge.event.ControlEvent;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;

/**
 * This class defines a set of utility methods. It also wraps some of the external dependencies like Guava to avoid version conflicts in the
 * client code. All Sponge projects except sponge-core should use only such wrapper methods defined here.
 */
public abstract class SpongeUtils {

    private static final Logger logger = LoggerFactory.getLogger(SpongeUtils.class);

    /**
     * Trial run of the engine. Shuts down after {@code timeout} seconds after startup.
     *
     * @param engine the engine.
     * @param timeout timeout in seconds.
     */
    public static void trialRunEngine(SpongeEngine engine, int timeout) {
        final Semaphore semaphore = new Semaphore(0, true);

        // Startup the engine. After startup the engine runs on the threads other than the current one.
        engine.startup();

        try {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                // Release the semaphore after timeout.
                semaphore.release();
            }, timeout, TimeUnit.SECONDS);

            try {
                // Wait for releasing the semaphore after timeout.
                semaphore.acquire();
            } catch (InterruptedException e) {
                logger.warn("trialRunEngine", e);
            }

            executor.shutdown();
        } finally {
            // Shutdown the engine.
            engine.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String className, Class<T> javaClass) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new SpongeException(e);
        }
    }

    public static Object invokeMethod(Object target, String name, Object... args) {
        try {
            return MethodUtils.invokeMethod(target, name, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new SpongeException(e);
        }
    }

    public static <T extends Serializable> T deepClone(T source) {
        return SerializationUtils.clone(source);
    }

    public static Reader getReader(String fileName) throws IOException {
        return getReader(fileName, Charset.defaultCharset());
    }

    public static Reader getReader(String fileName, Charset charset) throws IOException {
        Path path = Paths.get(fileName);

        if (Files.isRegularFile(path)) {
            return Files.newBufferedReader(Paths.get(fileName), charset);
        }

        URL url = getUrlFromClasspath(fileName);

        if (url != null) {
            return new BufferedReader(new InputStreamReader(url.openStream(), charset));
        }

        return null;
    }

    public static URL getUrlFromClasspath(String resourceName) {
        URL url = null;

        // Attempt to load from the context classpath.
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            url = loader.getResource(resourceName);
        }

        // Attempt to load from the system classpath.
        if (url == null) {
            url = ClassLoader.getSystemResource(resourceName);
        }

        return url;
    }

    /**
     * Dumps configuration to string.
     *
     * @param configuration a configuration.
     * @return a configuration dump string.
     */
    public static String dumpConfiguration(org.apache.commons.configuration2.Configuration configuration) {
        StringWriter sw = new StringWriter();
        ConfigurationUtils.dump(configuration, new PrintWriter(sw));

        return sw.toString();
    }

    public static Object searchConfigurationValueByAttributeValue(Configuration root, String parentKey, String attrName, String attrValue) {
        return Stream.of(root.getChildConfigurationsOf(parentKey))
                .filter(configuration -> Objects.equals(configuration.getAttribute(attrName, null), attrValue)).map(Configuration::getValue)
                .findFirst().orElse(null);
    }

    public static void executeConcurrentlyOnce(SpongeEngine engine, Runnable runnable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                engine.handleError("executeConcurrentlyOnce", e);
            }
        });
        executor.shutdown();
    }

    public static String createGlobalLoggerName(KnowledgeBaseEngineOperations knowledgeBaseEngineOperations) {
        if (knowledgeBaseEngineOperations != null && knowledgeBaseEngineOperations.getKnowledgeBase() != null) {
            return createLoggerName(knowledgeBaseEngineOperations.getKnowledgeBase(), KnowledgeBaseConstants.GLOBAL_LOGGER_NAME);
        } else {
            return KnowledgeBaseConstants.LOGGER_NAME_PREFIX + "." + KnowledgeBaseConstants.GLOBAL_LOGGER_NAME;
        }
    }

    public static String createPluginLoggerName() {
        return KnowledgeBaseConstants.LOGGER_NAME_PREFIX + "." + KnowledgeBaseConstants.PLUGIN_LOGGER_NAME;
    }

    public static String createLoggerName(KnowledgeBase knowledgeBase, String targetName) {
        return KnowledgeBaseConstants.LOGGER_NAME_PREFIX + "." + knowledgeBase.getType().getTypeCode() + "." + knowledgeBase.getName() + "."
                + targetName;
    }

    public static String createNonScriptKnowledgeBaseName(KnowledgeBase knowledgeBase) {
        return knowledgeBase.getClass().getSimpleName();
    }

    public static void shutdownExecutorService(SpongeEngine engine, Object named, ExecutorService executorService) {
        MoreExecutors.shutdownAndAwaitTermination(executorService, engine.getDefaultParameters().getExecutorShutdownTimeout(),
                TimeUnit.SECONDS);
        if (!executorService.isTerminated()) {
            logger.warn("Executor for {} hasn't shutdown gracefully.", named);
        }
    }

    public static ScriptKnowledgeBaseInterpreter getScriptInterpreter(SpongeEngine engine, String kbName) {
        return engine.getKnowledgeBaseManager().getScriptKnowledgeBase(kbName).getInterpreter();
    }

    public static KnowledgeBaseEngineOperations getEps(KnowledgeBaseInterpreter interpreter) {
        return interpreter.getVariable(KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS, KnowledgeBaseEngineOperations.class);
    }

    public static String getAbbreviatedEventSequenceString(BaseRule rule) {
        return "[" + rule.getEventSequence().stream().map(event -> event != null ? (event.getName() + "/" + event.getId()) : "<none>")
                .collect(Collectors.joining(", ")) + "]";
    }

    /**
     * Wraps or creates a new Sponge exception.
     *
     * @param sourceName source name of the exception.
     * @param throwable source throwable.
     * @return Sponge exception.
     */
    public static SpongeException wrapException(String sourceName, Throwable throwable) {
        if (throwable instanceof SpongeException) {
            return (SpongeException) throwable;
        } else {
            return new WrappedException(sourceName, throwable);
        }
    }

    /**
     * Wraps or creates a new Sponge exception.
     *
     * @param throwable source throwable.
     * @return Sponge exception.
     */
    public static SpongeException wrapException(Throwable throwable) {
        if (throwable instanceof SpongeException) {
            return (SpongeException) throwable;
        } else {
            return new SpongeException(throwable);
        }
    }

    public static boolean containsException(Throwable exception, final Class<?> type) {
        return ExceptionUtils.indexOfType(exception, type) > -1;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getException(Throwable exception, final Class<T> type) {
        return (T) ExceptionUtils.getThrowableList(exception).get(ExceptionUtils.indexOfType(exception, type));
    }

    public static String getSourceName(Throwable exception) {
        return exception instanceof WrappedException ? ((WrappedException) exception).getSourceName() : null;
    }

    public static String toStringEventSequence(Collection<Event> events) {
        return toStringEventSequence(events, null);
    }

    public static String toStringEventSequence(Collection<Event> events, String attributeName) {
        return events.stream().map(event -> event != null ? event.getName() + "(" + EventId.fromString(event.getId()).getId() + ")"
                + (attributeName != null ? "/" + event.get(attributeName) : "") : "null").collect(Collectors.joining(", "));
    }

    public static String toStringArrayEventSequence(Collection<Event> events, String attributeName) {
        return "{ " + events.stream().map(event -> event != null ? "\"" + event.get(attributeName) + "\"" : "null")
                .collect(Collectors.joining(", ")) + " }";
    }

    public static String createControlEventName(Class<? extends ControlEvent> controlEventClass) {
        return EngineConstants.CONTROL_EVENT_PREFIX + StringUtils.uncapitalize(controlEventClass.getSimpleName());
    }

    public static int calculateInitialDynamicThreadPoolSize(SpongeEngine engine, int maxPoolSize) {
        int result = (int) Math.round(engine.getDefaultParameters().getInitialDynamicThreadPoolSizeRatio() * maxPoolSize);

        if (result < 1) {
            result = 1;
        }

        return result;
    }

    public static String getPackagePath(Class<?> cls) {
        return cls.getPackage().getName().replace('.', '/');
    }

    public static String getLastSubdirectory(String dir) {
        try {
            List<String> subdirs =
                    Files.list(Paths.get(dir)).map(path -> path.getFileName().toFile().getName()).sorted().collect(Collectors.toList());

            return subdirs.size() > 0 ? subdirs.get(subdirs.size() - 1) : null;
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    public static <K, V> Map<K, V> immutableMapOf(K k1, V v1) {
        return ImmutableMap.of(k1, v1);
    }

    public static <K, V> Map<K, V> immutableMapOf(K k1, V v1, K k2, V v2) {
        return ImmutableMap.of(k1, v1, k2, v2);
    }

    public static <K, V> Map<K, V> immutableMapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        return ImmutableMap.of(k1, v1, k2, v2, k3, v3);
    }

    public static <K, V> Map<K, V> immutableMapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    public static <K, V> Map<K, V> immutableMapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return Streams.stream(iterator);
    }

    public static List<String> split(String text, char separator) {
        return Splitter.on(separator).trimResults().omitEmptyStrings().splitToList(text);
    }

    public static void close(InputStream stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    public static void close(OutputStream stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    public static String getRequiredConfigurationString(Configuration configuration, String key) {
        String value = configuration.getString(key, null);

        if (value == null) {
            throw new IllegalArgumentException("A required configuration parameter '" + key + "' is not set");
        }

        return value;
    }

    public static int toInt(Object value) {
        return ((Number) value).intValue();
    }

    public static void registerShutdownHook(SpongeEngine engine) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                engine.shutdown();
            } catch (Throwable e) {
                logger.error("Shutdown hook error", e);
            }
        }));
    }

    public static String toUpperCamelCaseFromUnderscore(String s) {
        return s != null ? CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s.toLowerCase()) : null;
    }

    public static boolean isAbstract(Class<?> cls) {
        return Modifier.isAbstract(cls.getModifiers());
    }

    public static boolean awaitUntil(Callable<Boolean> callable, long timeout, TimeUnit unit) {
        try {
            await().atMost(timeout, unit).until(callable);
            return true;
        } catch (ConditionTimeoutException e) {
            return false;
        }
    }

    protected SpongeUtils() {
        //
    }
}
