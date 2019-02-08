/**
 * Sponge Knowledge base
 * Loading knowledge bases
 */

class Trigger1 extends Trigger {
    void onConfigure() {
        this.withLabel("Trigger1, file2").withEvent("e1")
    }
    void onRun(Event event) {
        sponge.getVariable("eventCounter").get(this.meta.label).incrementAndGet()
    }
}

class Trigger2 extends Trigger {
    void onConfigure() {
        this.withLabel("Trigger2, file2").withEvent("e2")
    }
    void onRun(Event event) {
        sponge.getVariable("eventCounter").get(this.meta.label).incrementAndGet()
    }
}

sponge.enableAll(Trigger1, Trigger2)

void onShutdown() {
    sponge.logger.debug("onShutdown, file2")
}
