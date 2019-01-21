/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

class Trigger1 extends Trigger {
    void onConfigure() {
        this.label = "Trigger1, file2"
        this.event = "e1"
    }
    void onRun(Event event) {
        //this.logger.debug("file2: Received event {}", event)
        sponge.getVariable("eventCounter").get(this.label).incrementAndGet()
    }
}

class Trigger2 extends Trigger {
    void onConfigure() {
        this.label = "Trigger2, file2"
        this.event = "e2"
    }
    void onRun(Event event) {
        //this.logger.debug("file2: Received event {}", event)
        sponge.getVariable("eventCounter").get(this.label).incrementAndGet()
    }
}

sponge.enableAll(Trigger1, Trigger2)

void onShutdown() {
    sponge.logger.debug("onShutdown, file2")
}
