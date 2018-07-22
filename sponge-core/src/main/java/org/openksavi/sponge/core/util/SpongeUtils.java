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
import java.io.File;
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
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.common.util.concurrent.MoreExecutors;

import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorOperations;
import org.openksavi.sponge.ProcessorQualifiedName;
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
import org.openksavi.sponge.type.ActionType;
import org.openksavi.sponge.type.AnyType;
import org.openksavi.sponge.type.BinaryType;
import org.openksavi.sponge.type.BooleanType;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.MapType;
import org.openksavi.sponge.type.NumberType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.Type;
import org.openksavi.sponge.type.VoidType;

/**
 * This class defines a set of utility methods. It also wraps some of the external dependencies like Guava to avoid version conflicts in the
 * client code. All Sponge projects except sponge-core should use only such wrapper methods defined here.
 */
public abstract class SpongeUtils {

    private static final Logger logger = LoggerFactory.getLogger(SpongeUtils.class);

    public static final String TAG_SECURITY_KEY_STORE_PASSWORD = "keyStorePassword";

    public static final String TAG_SECURITY_KEY_PASSWORD = "keyPassword";

    public static final String TAG_SECURITY_KEY_STORE = "keyStore";

    public static final String TAG_SECURITY_ALGORITHM = "algorithm";

    public static final String DEFAULT_SECURITY_ALGORITHM = "SunX509";

    private static final List<Class<? extends Type>> SUPPORTED_TYPES =
            Arrays.asList(ActionType.class, AnyType.class, BinaryType.class, BooleanType.class, IntegerType.class, ListType.class,
                    MapType.class, NumberType.class, ObjectType.class, StringType.class, VoidType.class);

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

    public static Reader getReader(String fileName) {
        return getReader(fileName, Charset.defaultCharset());
    }

    public static Reader getReader(String fileName, Charset charset) {
        try {
            if (Files.isRegularFile(Paths.get(fileName))) {
                return Files.newBufferedReader(Paths.get(fileName), charset);
            }

            URL url = getUrlFromClasspath(fileName);

            if (url != null) {
                return new BufferedReader(new InputStreamReader(url.openStream(), charset));
            }

            return null;
        } catch (IOException e) {
            throw new SpongeException("Error reading " + fileName, e);
        }
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

    private static SpongeException doWrapException(String sourceName, KnowledgeBaseInterpreter interpreter, Throwable throwable) {
        if (throwable instanceof SpongeException) {
            return (SpongeException) throwable;
        }

        String specificErrorMessage = interpreter != null ? interpreter.getSpecificExceptionMessage(throwable) : null;
        if (sourceName == null) {
            return specificErrorMessage == null ? new SpongeException(throwable) : new SpongeException(specificErrorMessage, throwable);
        }

        return specificErrorMessage == null ? new WrappedException(sourceName, throwable)
                : new WrappedException(sourceName, specificErrorMessage, throwable);
    }

    /**
     * Wraps or creates a new Sponge exception.
     *
     * @param throwable source throwable.
     * @return Sponge exception.
     */
    public static SpongeException wrapException(Throwable throwable) {
        return doWrapException(null, null, throwable);
    }

    /**
     * Wraps or creates a new Sponge exception.
     *
     * @param sourceName source name of the exception.
     * @param throwable source throwable.
     * @return Sponge exception.
     */
    public static SpongeException wrapException(String sourceName, Throwable throwable) {
        return doWrapException(sourceName, null, throwable);
    }

    public static SpongeException wrapException(Processor<?> processor, Throwable throwable) {
        return doWrapException(getProcessorQualifiedName(processor).toString(), processor.getKnowledgeBase().getInterpreter(), throwable);
    }

    public static SpongeException wrapException(KnowledgeBaseInterpreter interpreter, Throwable throwable) {
        return doWrapException(null, interpreter, throwable);
    }

    public static SpongeException wrapException(String sourceName, KnowledgeBaseInterpreter interpreter, Throwable throwable) {
        return doWrapException(sourceName, interpreter, throwable);
    }

    public static SpongeException wrapInvokeException(Object target, String method, KnowledgeBaseInterpreter interpreter,
            Throwable throwable) {
        String sourceName =
                (target instanceof ProcessorOperations ? getProcessorQualifiedName((ProcessorOperations) target) : target) + "." + method;
        return doWrapException(sourceName, interpreter, throwable);
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

    public static void closeQuietly(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable e) {
            logger.warn("Error closing a reader", e);
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

    public static boolean awaitUntil(Callable<Boolean> callable) {
        try {
            await().atMost(org.awaitility.Duration.FOREVER).until(callable);
            return true;
        } catch (ConditionTimeoutException e) {
            return false;
        }
    }

    public static boolean isSystemEvent(Event event) {
        return EngineConstants.PREDEFINED_EVENT_NAMES.contains(event.getName());
    }

    // kbNameRegexp1:processorNameRegexp1,..,kbNameRegexpN:processorNameRegexpN
    public static List<ProcessorQualifiedName> getProcessorQualifiedNameList(String processorQualifiedNamesSpec) {
        if (processorQualifiedNamesSpec == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(processorQualifiedNamesSpec.split(",")).map(String::trim).map(element -> {
            String[] spec = element.split(":");
            String knowledgeBaseName = spec[0].trim();
            String name = spec.length > 1 ? spec[1].trim() : "";

            return new ProcessorQualifiedName(!knowledgeBaseName.isEmpty() ? knowledgeBaseName : EngineConstants.MATCH_ALL_REGEXP,
                    !name.isEmpty() ? name : EngineConstants.MATCH_ALL_REGEXP);
        }).collect(Collectors.toList());
    }

    // nameRegexp1,..,nameRegexpN
    public static List<String> getNameList(String namesSpec) {
        if (namesSpec == null) {
            return Lists.newArrayList(EngineConstants.MATCH_ALL_REGEXP);
        }

        return Arrays.stream(namesSpec.split(",")).map(String::trim).collect(Collectors.toList());
    }

    public static SSLContext createSslContext(SslConfiguration security) {
        InputStream fis = null;

        try {
            KeyStore ks = KeyStore.getInstance("JKS");

            URL keystoreUrl = SpongeUtils.getUrlFromClasspath(security.getKeyStore());
            if (keystoreUrl == null) {
                throw new SpongeException("Expected a '" + security.getKeyStore() + "' keystore file on the classpath");
            }
            fis = keystoreUrl.openStream();

            ks.load(fis, security.getKeyStorePassword().toCharArray());

            // Setup the key manager factory.
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(security.getAlgorithm());
            kmf.init(ks, security.getKeyPassword().toCharArray());

            // Setup the trust manager factory.
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(security.getAlgorithm());
            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Setup the HTTPS context and parameters.
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            return sslContext;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException
                | KeyManagementException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            SpongeUtils.close(fis);
        }
    }

    public static SslConfiguration createSslConfiguration(Configuration configuration) {
        SslConfiguration sslConfiguration = new SslConfiguration();
        sslConfiguration.setKeyStore(SpongeUtils.getRequiredConfigurationString(configuration, TAG_SECURITY_KEY_STORE));
        sslConfiguration.setKeyStorePassword(SpongeUtils.getRequiredConfigurationString(configuration, TAG_SECURITY_KEY_STORE_PASSWORD));
        sslConfiguration.setKeyPassword(SpongeUtils.getRequiredConfigurationString(configuration, TAG_SECURITY_KEY_PASSWORD));
        sslConfiguration.setAlgorithm(configuration.getString(TAG_SECURITY_ALGORITHM, DEFAULT_SECURITY_ALGORITHM));

        return sslConfiguration;
    }

    public static ProcessorQualifiedName getProcessorQualifiedName(ProcessorOperations processorOperations) {
        return new ProcessorQualifiedName(
                processorOperations.getKnowledgeBase() != null ? processorOperations.getKnowledgeBase().getName() : null,
                processorOperations.getName());
    }

    public static Class<?> getClass(String className) {
        try {
            return ClassUtils.getClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static void doInWrappedException(KnowledgeBase knowledgeBase, Runnable runnable, String sourceName) {
        try {
            runnable.run();
        } catch (Throwable e) {
            throw SpongeUtils.wrapException(sourceName, knowledgeBase.getInterpreter(), e);
        }
    }

    public static void validateType(Type type, String valueName) {
        switch (type.getKind()) {
        case OBJECT:
            Validate.isInstanceOf(ObjectType.class, type, "The type should be an instance of %s", ObjectType.class);
            String className = ((ObjectType) type).getClassName();
            Validate.notNull(className, "Missing class name in the %s", valueName);
            Validate.notNull(getClass(className), "The class %s used in the %s not found", className, valueName);
            break;
        case LIST:
            Validate.isInstanceOf(ListType.class, type, "The type should be an instance of %s", ListType.class);
            validateType(Validate.notNull(((ListType) type).getElementType(), "List element type not specified in the %s", valueName),
                    valueName);
            break;
        case MAP:
            Validate.isInstanceOf(MapType.class, type, "The type should be an instance of %s", MapType.class);
            validateType(Validate.notNull(((MapType) type).getKeyType(), "Map key type not specified in the %s", valueName), valueName);
            validateType(Validate.notNull(((MapType) type).getValueType(), "Map value type not specified in the %s", valueName), valueName);
            break;
        default:
            break;
        }
    }

    public static List<Class<? extends Type>> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * Returns the configuration file directory or {@code null} if the configuration file is not present.
     *
     * @param engine the engine.
     * @return the configuration file directory.
     */
    public static String getConfigurationFileDir(SpongeEngine engine) {
        return getFileDir(engine.getConfigurationManager().getConfigurationFileUrl());
    }

    public static String getFileDir(URL fileUrl) {
        if (fileUrl != null) {
            File configFile = FileLocatorUtils.fileFromURL(fileUrl);
            if (configFile != null) {
                return configFile.getParent();
            } else {
                logger.warn("URL {} cannot be converted to File", fileUrl);
            }
        }

        return null;
    }

    public static File getFileDirAsFile(String filePath) {
        return FileUtils.getFile(filePath).getParentFile();
    }

    public static ProcessInstance startProcess(SpongeEngine engine, ProcessConfiguration processConfiguration) {
        return ProcessUtils.startProcess(engine, processConfiguration);
    }

    protected SpongeUtils() {
        //
    }
}
