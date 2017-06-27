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
     Object run(Object[] args) {
         this.logger.debug("Running")
         EPS.getVariable("counter").incrementAndGet()
         return args
     }
 }

 class AutoFilter extends Filter {
     void configure() {
         this.eventName = "e1"
     }
     boolean accepts(Event event) {
         this.logger.debug("Received event: {}", event.name)
         EPS.getVariable("counter").incrementAndGet()
         return true
     }
 }

class AutoTrigger extends Trigger {
    void configure() {
        this.eventName = "e1"
    }
    void run(Event event) {
        this.logger.debug("Received event: {}", event.name)
        EPS.getVariable("counter").incrementAndGet()
    }
}

class AutoRule extends Rule {
    void configure() {
        this.events = ["e1", "e2"]
    }
    void run(Event event) {
        this.logger.debug("Running for sequence: {}", this.eventSequence)
        EPS.getVariable("counter").incrementAndGet()
    }
}

class AutoCorrelator extends Correlator {
    void configure() {
        this.eventNames = ["e1", "e2"]
    }
    boolean acceptsAsFirst(Event event) {
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
    EPS.callAction("AutoAction")
    EPS.event("e1").send()
    EPS.event("e2").send()
}
