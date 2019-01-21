/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

class Trigger1 extends Trigger {
    void onConfigure() {
        this.label = "Trigger1, file3"
        this.event = "e1"
    }
    void onRun(Event event) {
        //this.logger.debug("file3: Received event {}", event)
        sponge.getVariable("eventCounter").get(this.label).incrementAndGet()
    }
}

class Trigger3 extends Trigger {
    void onConfigure() {
        this.label = "Trigger3, file3"
        this.event = "e3"
    }
    void onRun(Event event) {
        //this.logger.debug("file3: Received event {}", event)
        sponge.getVariable("eventCounter").get(this.label).incrementAndGet()
    }
}

sponge.enableAll(Trigger1, Trigger3)

void onShutdown() {
    sponge.logger.debug("onShutdown, file3")
}
