/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

class Trigger1 extends Trigger {
    void configure() {
        this.displayName = "Trigger1, file3"
        this.event = "e1"
    }
    void run(Event event) {
        this.logger.debug("file3: Received event {}", event)
        EPS.getVariable("eventCounter").get(this.displayName).incrementAndGet()
    }
}

class Trigger3 extends Trigger {
    void configure() {
        this.displayName = "Trigger3, file3"
        this.event = "e3"
    }
    void run(Event event) {
        this.logger.debug("file3: Received event {}", event)
        EPS.getVariable("eventCounter").get(this.displayName).incrementAndGet()
    }
}

EPS.enableAll(Trigger1, Trigger3)

void onShutdown() {
    EPS.logger.debug("onShutdown, file3")
}
