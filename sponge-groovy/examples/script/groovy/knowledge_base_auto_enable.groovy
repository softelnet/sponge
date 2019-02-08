/**
 * Sponge Knowledge base
 * Auto-enable
 */

import java.util.concurrent.atomic.AtomicInteger

 void onInit() {
     // Variables for assertions only
     sponge.setVariable("counter", new AtomicInteger(0))
 }

 class AutoAction extends Action {
     Object onCall() {
         this.logger.debug("Running")
         sponge.getVariable("counter").incrementAndGet()
         return null
     }
 }

 class AutoFilter extends Filter {
     void onConfigure() {
         this.withEvent("e1")
     }
     boolean onAccept(Event event) {
         this.logger.debug("Received event: {}", event.name)
         sponge.getVariable("counter").incrementAndGet()
         return true
     }
 }

class AutoTrigger extends Trigger {
    void onConfigure() {
        this.withEvent("e1")
    }
    void onRun(Event event) {
        this.logger.debug("Received event: {}", event.name)
        sponge.getVariable("counter").incrementAndGet()
    }
}

class AutoRule extends Rule {
    void onConfigure() {
        this.withEvents(["e1", "e2"])
    }
    void onRun(Event event) {
        this.logger.debug("Running for sequence: {}", this.eventSequence)
        sponge.getVariable("counter").incrementAndGet()
    }
}

class AutoCorrelator extends Correlator {
    void onConfigure() {
        this.withEvents(["e1", "e2"])
    }
    boolean onAcceptAsFirst(Event event) {
        return event.name == "e1"
    }
    void onEvent(Event event) {
        this.logger.debug("Received event: {}", event.name)
        if (event.name == "e2") {
            sponge.getVariable("counter").incrementAndGet()
            this.finish()
        }
    }
}

void onStartup() {
    sponge.call("AutoAction")
    sponge.event("e1").send()
    sponge.event("e2").send()
}
