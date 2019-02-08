/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

class Trigger1 extends Trigger {
    void onConfigure() {
        this.withLabel("Trigger1, file3").withEvent("e1")
    }
    void onRun(Event event) {
        sponge.getVariable("eventCounter").get(this.meta.label).incrementAndGet()
    }
}

class Trigger3 extends Trigger {
    void onConfigure() {
        this.withLabel("Trigger3, file3").withEvent("e3")
    }
    void onRun(Event event) {
        sponge.getVariable("eventCounter").get(this.meta.label).incrementAndGet()
    }
}

sponge.enableAll(Trigger1, Trigger3)

void onShutdown() {
    sponge.logger.debug("onShutdown, file3")
}
