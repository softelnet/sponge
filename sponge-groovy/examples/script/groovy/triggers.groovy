/**
 * Sponge Knowledge Base
 * Triggers - Generating events and using triggers
 */

import java.util.concurrent.atomic.*
import org.openksavi.sponge.examples.SampleJavaTrigger

void onInit() {
    // Variables for assertions only
    sponge.setVariable("receivedEventA", new AtomicBoolean(false))
    sponge.setVariable("receivedEventBCount", new AtomicInteger(0))
    sponge.setVariable("receivedEventTestJavaCount", new AtomicInteger(0))
}

class TriggerA extends Trigger {
    void onConfigure() {
        this.withEvent("a")
    }
    void onRun(Event event) {
        this.logger.debug("Received event: {}", event.name)
        sponge.getVariable("receivedEventA").set(true)
    }
}

class TriggerB extends Trigger {
    void onConfigure() {
        this.withEvent("b")
    }
    void onRun(Event event) {
        this.logger.debug("Received event: {}", event.name)
        def receivedEventBCount = sponge.getVariable("receivedEventBCount")
        if (receivedEventBCount.get() == 0) {
            this.logger.debug("Statistics: {}", sponge.statisticsSummary)
        }
        receivedEventBCount.incrementAndGet()
    }
}

void onLoad() {
    sponge.enableJava(SampleJavaTrigger)
}

void onStartup() {
    sponge.logger.debug("Startup {}, triggers: {}", sponge.info, sponge.engine.triggers)
    sponge.logger.debug("Knowledge base name: {}", sponge.kb.name)
    sponge.event("a").send()
    sponge.event("b").sendAfter(200, 200)
    sponge.event("testJava").send()
}

void onShutdown() {
    sponge.logger.debug("Shutting down")
}
