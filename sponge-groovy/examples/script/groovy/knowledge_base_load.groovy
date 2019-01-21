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
    sponge.setVariable("eventCounter", eventCounter)
}

class Trigger1 extends Trigger {
    void onConfigure() {
        this.label = "Trigger1, file1"
        this.event = "e1"
    }
    void onRun(Event event) {
        //this.logger.debug("file1: Received event {}", event)
        sponge.getVariable("eventCounter").get(this.label).incrementAndGet()
    }
}

class Trigger2 extends Trigger {
    void onConfigure() {
        this.label = "Trigger2, file1"
        this.event = "e2"
    }
    void onRun(Event event) {
        //this.logger.debug("file1: Received event {}", event)
        sponge.getVariable("eventCounter").get(this.label).incrementAndGet()
    }
}


class LoadKbFile extends Trigger {
    void onConfigure() {
        this.event = "loadKbFile"
    }
    void onRun(Event event) {
        def kbFile = event.get("kbFile")
        sponge.kb.load(kbFile)
        this.logger.info("File {} loaded", kbFile)
    }
}

void onLoad() {
    sponge.enableAll(Trigger1, Trigger2, LoadKbFile)
}

void onStartup() {
    sponge.logger.debug("onStartup, file1: {}, triggers: {}", sponge.info, sponge.engine.triggers)
    sponge.event("e1").sendAfter(0, 500)
    sponge.event("e2").sendAfter(0, 500)

    sponge.event("loadKbFile").set("kbFile", "examples/script/groovy/knowledge_base_load2.groovy").sendAfter(2000)
    sponge.event("loadKbFile").set("kbFile", "examples/script/groovy/knowledge_base_load3.groovy").sendAfter(5000)
}

void onShutdown() {
    sponge.logger.debug("onShutdown, file1")
}
