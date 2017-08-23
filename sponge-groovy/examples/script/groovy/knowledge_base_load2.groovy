/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

class Trigger1 extends Trigger {
    void onConfigure() {
        this.displayName = "Trigger1, file2"
        this.event = "e1"
    }
    void onRun(Event event) {
        //this.logger.debug("file2: Received event {}", event)
        EPS.getVariable("eventCounter").get(this.displayName).incrementAndGet()
    }
}

class Trigger2 extends Trigger {
    void onConfigure() {
        this.displayName = "Trigger2, file2"
        this.event = "e2"
    }
    void onRun(Event event) {
        //this.logger.debug("file2: Received event {}", event)
        EPS.getVariable("eventCounter").get(this.displayName).incrementAndGet()
    }
}

EPS.enableAll(Trigger1, Trigger2)

void onShutdown() {
    EPS.logger.debug("onShutdown, file2")
}
