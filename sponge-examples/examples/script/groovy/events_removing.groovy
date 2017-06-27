/**
 * Sponge Knowledge base
 * Removing scheduled events
 */

void onInit() {
    EPS.setVariable("eventCounter", 0)
    EPS.setVariable("allowNumber", 3)
}

class Trigger1 extends Trigger {
    void configure() {
        this.eventName = "e1"
    }
    void run(Event event) {
    	int eventCounter = EPS.getVariable("eventCounter")
    	eventCounter += 1
        EPS.setVariable("eventCounter", eventCounter)
        this.logger.debug("Received event {}, counter: {}", event.name, eventCounter)
        if (eventCounter > EPS.getVariable("allowNumber")) {
        	this.logger.debug("This line should not be displayed!")
        }
    }
}

class Trigger2 extends Trigger {
    void configure() {
        this.eventName = "e2"
    }
    void run(Event event) {
        this.logger.debug("Removing entry")
        EPS.removeEvent(EPS.getVariable("eventEntry"))
    }
}

void onStartup() {
    int start = 100
    int interval = 500
    EPS.setVariable("eventEntry", EPS.event("e1").sendAfter(start, interval))
    EPS.event("e2").sendAfter(interval * EPS.getVariable("allowNumber"))
}