/**
 * Sponge Knowledge Base
 * Concurrency
 */

import java.util.concurrent.*
import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    sponge.setVariable("value", new AtomicReference(""))
}

class A extends Trigger {
    void onConfigure() {
        this.withEvent("a")
    }
    void onRun(Event event) {
        TimeUnit.SECONDS.sleep(1)
        sponge.getVariable("value").set("A1")
        TimeUnit.SECONDS.sleep(3)
        sponge.getVariable("value").set("A2")
    }
}

class B extends Trigger {
    void onConfigure() {
        this.withEvent("b")
    }
    void onRun(Event event) {
        TimeUnit.SECONDS.sleep(2)
        sponge.getVariable("value").set("B1")
        TimeUnit.SECONDS.sleep(4)
        sponge.getVariable("value").set("B2")
    }
}

class C extends Trigger {
    void onConfigure() {
        this.withEvent("c")
    }
    void onRun(Event event) {
        TimeUnit.SECONDS.sleep(8)
        sponge.getVariable("value").set("C1")
        TimeUnit.SECONDS.sleep(1)
        sponge.getVariable("value").set("C2")
    }
}

void onStartup() {
    sponge.event("a").send()
    sponge.event("b").send()
    sponge.event("c").send()
}

