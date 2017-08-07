/**
 * Sponge Knowledge base
 * Loading knowledge bases
 * Note that auto-enable is turned off in the configuration.
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    eventCounter = Collections.synchronizedMap(new HashMap())
    eventCounter.put("Trigger1, file1", new AtomicInteger(0))
    eventCounter.put("Trigger2, file1", new AtomicInteger(0))
    eventCounter.put("Trigger1, file2", new AtomicInteger(0))
    eventCounter.put("Trigger2, file2", new AtomicInteger(0))
    eventCounter.put("Trigger1, file3", new AtomicInteger(0))
    eventCounter.put("Trigger3, file3", new AtomicInteger(0))
    EPS.setVariable("eventCounter", eventCounter)
}

class Trigger1 extends Trigger {
    void onConfigure() {
        this.displayName = "Trigger1, file1"
        this.event = "e1"
    }
    void onRun(Event event) {
        this.logger.debug("file1: Received event {}", event)
        EPS.getVariable("eventCounter").get(this.displayName).incrementAndGet()
    }
}

class Trigger2 extends Trigger {
    void onConfigure() {
        this.displayName = "Trigger2, file1"
        this.event = "e2"
    }
    void onRun(Event event) {
        this.logger.debug("file1: Received event {}", event)
        EPS.getVariable("eventCounter").get(this.displayName).incrementAndGet()
    }
}


class LoadKbFile extends Trigger {
    void onConfigure() {
        this.event = "loadKbFile"
    }
    void onRun(Event event) {
        def kbFile = event.get("kbFile")
        EPS.kb.load(kbFile)
        this.logger.info("File {} loaded", kbFile)
    }
}

void onLoad() {
    EPS.enableAll(Trigger1, Trigger2, LoadKbFile)
}

void onStartup() {
    EPS.logger.debug("onStartup, file1: {}, triggers: {}", EPS.description, EPS.engine.triggers)
    EPS.event("e1").sendAfter(0, 100)
    EPS.event("e2").sendAfter(0, 100)

    EPS.event("loadKbFile").set("kbFile", "examples/script/groovy/knowledge_base_load2.groovy").sendAfter(500)
    EPS.event("loadKbFile").set("kbFile", "examples/script/groovy/knowledge_base_load3.groovy").sendAfter(1500)
}

void onShutdown() {
    EPS.logger.debug("onShutdown, file1")
}
