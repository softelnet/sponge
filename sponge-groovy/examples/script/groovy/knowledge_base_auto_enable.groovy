/**
 * Sponge Knowledge base
 * Auto-enable
 */

import java.util.concurrent.atomic.AtomicInteger

 void onInit() {
     // Variables for assertions only
     EPS.setVariable("counter", new AtomicInteger(0))
 }

 class AutoAction extends Action {
     Object onCall(Object[] args) {
         this.logger.debug("Running")
         EPS.getVariable("counter").incrementAndGet()
         return args
     }
 }

 class AutoFilter extends Filter {
     void onConfigure() {
         this.event = "e1"
     }
     boolean onAccept(Event event) {
         this.logger.debug("Received event: {}", event.name)
         EPS.getVariable("counter").incrementAndGet()
         return true
     }
 }

class AutoTrigger extends Trigger {
    void onConfigure() {
        this.event = "e1"
    }
    void onRun(Event event) {
        this.logger.debug("Received event: {}", event.name)
        EPS.getVariable("counter").incrementAndGet()
    }
}

class AutoRule extends Rule {
    void onConfigure() {
        this.events = ["e1", "e2"]
    }
    void onRun(Event event) {
        this.logger.debug("Running for sequence: {}", this.eventSequence)
        EPS.getVariable("counter").incrementAndGet()
    }
}

class AutoCorrelator extends Correlator {
    void onConfigure() {
        this.events = ["e1", "e2"]
    }
    boolean onAcceptAsFirst(Event event) {
        return event.name == "e1"
    }
    void onEvent(Event event) {
        this.logger.debug("Received event: {}", event.name)
        if (event.name == "e2") {
            EPS.getVariable("counter").incrementAndGet()
            this.finish()
        }
    }
}

void onStartup() {
    EPS.call("AutoAction")
    EPS.event("e1").send()
    EPS.event("e2").send()
}
