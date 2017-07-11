/**
 * Sponge Knowledge base
 * Triggers - Generating events and using triggers
 */

import java.util.concurrent.atomic.*
import org.openksavi.sponge.examples.SampleJavaTrigger

void onInit() {
    // Variables for assertions only
    EPS.setVariable("receivedEventA", new AtomicBoolean(false))
    EPS.setVariable("receivedEventBCount", new AtomicInteger(0))
    EPS.setVariable("receivedEventTestJavaCount", new AtomicInteger(0))
}

class TriggerA extends Trigger {
    void configure() {
        this.event = "a"
    }
    void run(Event event) {
        this.logger.debug("Received event: {}", event.name)
        EPS.getVariable("receivedEventA").set(true)
    }
}

class TriggerB extends Trigger {
    void configure() {
        this.event = "b"
    }
    void run(Event event) {
        this.logger.debug("Received event: {}", event.name)
        def receivedEventBCount = EPS.getVariable("receivedEventBCount")
        if (receivedEventBCount.get() == 0) {
            this.logger.debug("Statistics: {}", EPS.statisticsSummary)
        }
        receivedEventBCount.incrementAndGet()
    }
}

void onLoad() {
    EPS.enableJava(SampleJavaTrigger)
}

void onStartup() {
    EPS.logger.debug("Startup {}, triggers: {}", EPS.description, EPS.engine.triggers)
    EPS.logger.debug("Knowledge base name: {}", EPS.kb.name)
    EPS.event("a").sendAfter(100)
    EPS.event("b").sendAfter(200, 200)
    EPS.event("testJava").sendAfter(100)
}

void onShutdown() {
    EPS.logger.debug("Shutting down")
}
