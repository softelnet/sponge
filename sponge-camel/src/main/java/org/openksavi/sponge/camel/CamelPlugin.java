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

package org.openksavi.sponge.camel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedStartupListener;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;

/**
 * Sponge plugin that provides integration with Apache Camel.
 */
public class CamelPlugin extends JPlugin implements CamelContextAware, ExtendedStartupListener {

    private static final Logger logger = LoggerFactory.getLogger(CamelPlugin.class);

    public static final String NAME = "camel";

    public static final String PRODUCER_TEMPLATE = "spongeProducerTemplate";

    private CamelContext camelContext;

    private List<CamelConsumer> consumers = new CopyOnWriteArrayList<>();

    private volatile ProducerTemplate producerTemplate;

    private boolean producerTemplateCreatedManually = false;

    /**
     * If {@code true}, the plugin will wait for the Camel context to be fully started. The default value is {@code false}. Note that if you
     * enable this option, you have to add this plugin to the Camel context as a startup listener, e.g.:
     * camelContext.addStartupListener(camelPlugin()) in a Spring Camel configuration.
     */
    private boolean waitForContextFullyStarted = false;

    private AtomicBoolean contextFullyStarted = new AtomicBoolean(false);

    private final CountDownLatch contextFullyStartedLatch = new CountDownLatch(1);

    private Lock lock = new ReentrantLock(true);

    public CamelPlugin(CamelContext camelContext) {
        this();
        setContext(camelContext);
    }

    public CamelPlugin() {
        super(NAME);
    }

    public CamelPlugin(String name) {
        super(name);
    }

    public CamelContext getContext() {
        return camelContext;
    }

    /**
     * Waits for Camel context fully started.
     */
    public void waitForContextFullyStarted() {
        if (waitForContextFullyStarted && !contextFullyStarted.get()) {
            try {
                contextFullyStartedLatch.await();
                contextFullyStarted.set(true);
            } catch (InterruptedException e) {
                throw SpongeUtils.wrapException(e);
            }
        }
    }

    public void setContext(CamelContext camelContext) {
        synchronized (this) {
            if (this.camelContext != null && this.camelContext != camelContext) {
                throw new SpongeException("Camel plugin has already been initialized with a different Camel context");
            }

            this.camelContext = camelContext;
        }
    }

    public List<CamelConsumer> getConsumers() {
        return consumers;
    }

    public void addConsumer(CamelConsumer consumer) {
        logger.debug("Adding consumer {}", consumer);
        consumers.add(consumer);
    }

    public boolean removeConsumer(CamelConsumer consumer) {
        logger.debug("Removing consumer {}", consumer);
        return consumers.remove(consumer);
    }

    public SpongeCamelEvent makeInputEvent(String name, Exchange exchange) {
        return SpongeCamelEvent.create(getEngine(), name, exchange);
    }

    /**
     * Emits (sends) the body to all current consumers.
     *
     * @param body the body.
     */
    public void emit(Object body) {
        if (consumers.isEmpty()) {
            logger.debug("No consumer to emit a message");
        } else {
            waitForContextFullyStarted();

            consumers.forEach(consumer -> {
                logger.debug("Emitting to consumer {}", consumer);
                consumer.emit(body);
            });
        }
    }

    /**
     * Sends the body to an endpoint. The shortcut for {@code getProducerTemplate().sendBody(uri, body)}.
     *
     * @param uri the URI.
     * @param body the body.
     */
    public void sendBody(String uri, Object body) {
        getProducerTemplate().sendBody(uri, body);
    }

    /**
     * Sends the body to an endpoint returning any result output body. The shortcut for
     * {@code getProducerTemplate().requestBody(uri, body)}.
     *
     * @param uri the URI.
     * @param body the body.
     *
     * @return the result.
     */
    public Object requestBody(String uri, Object body) {
        return getProducerTemplate().requestBody(uri, body);
    }

    /**
     * Returns the producer template for working with Camel and sending Camel messages to an endpoint.
     *
     * @return the producer template.
     */
    public ProducerTemplate getProducerTemplate() {
        ProducerTemplate result = producerTemplate;

        if (result == null) {
            lock.lock();
            try {
                result = producerTemplate;
                if (result == null) {
                    result = camelContext.getRegistry().lookupByNameAndType(PRODUCER_TEMPLATE, ProducerTemplate.class);
                }

                if (result == null) {
                    // Create a new ProducerTemplate when there is none in the Camel registry.
                    result = camelContext.createProducerTemplate();
                    producerTemplateCreatedManually = true;
                }

                waitForContextFullyStarted();

                producerTemplate = result;
            } finally {
                lock.unlock();
            }
        }

        return result;
    }

    public void addAttachment(Message message, String attachmentFile) {
        message.addAttachment(FilenameUtils.getName(attachmentFile), new DataHandler(new FileDataSource(attachmentFile)));
    }

    public String getRouteId(Exchange exchange) {
        return exchange.getUnitOfWork().getRouteContext().getRoute().getId();
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    public boolean isWaitForContextFullyStarted() {
        return waitForContextFullyStarted;
    }

    public void setWaitForContextFullyStarted(boolean waitForContextFullyStarted) {
        this.waitForContextFullyStarted = waitForContextFullyStarted;
    }

    @Override
    public void onShutdown() {
        if (producerTemplateCreatedManually && producerTemplate != null) {
            try {
                producerTemplate.stop();
            } catch (Exception e) {
                throw SpongeUtils.wrapException(e);
            }
        }
    }

    @Override
    public void onCamelContextStarted(CamelContext context, boolean alreadyStarted) throws Exception {
        // Ignored.
    }

    @Override
    public void onCamelContextFullyStarted(CamelContext context, boolean alreadyStarted) throws Exception {
        contextFullyStartedLatch.countDown();
    }
}
