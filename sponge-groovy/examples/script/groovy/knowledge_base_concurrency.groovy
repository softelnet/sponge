/**
 * Sponge Knowledge base
 * Concurrency
 */

import java.util.concurrent.*
import java.util.concurrent.atomic.*

void onInit() {
    // Variables for assertions only
    EPS.setVariable("value", new AtomicReference(""))
}

class A extends Trigger {
    void configure() {
        this.eventName = "a"
    }
    void run(Event event) {
        TimeUnit.SECONDS.sleep(1)
        EPS.getVariable("value").set("A1")
        TimeUnit.SECONDS.sleep(3)
        EPS.getVariable("value").set("A2")
    }
}

class B extends Trigger {
    void configure() {
        this.eventName = "b"
    }
    void run(Event event) {
        TimeUnit.SECONDS.sleep(2)
        EPS.getVariable("value").set("B1")
        TimeUnit.SECONDS.sleep(4)
        EPS.getVariable("value").set("B2")
    }
}

class C extends Trigger {
    void configure() {
        this.eventName = "c"
    }
    void run(Event event) {
        TimeUnit.SECONDS.sleep(8)
        EPS.getVariable("value").set("C1")
        TimeUnit.SECONDS.sleep(1)
        EPS.getVariable("value").set("C2")
    }
}

void onStartup() {
    EPS.event("a").send()
    EPS.event("b").send()
    EPS.event("c").send()
}

