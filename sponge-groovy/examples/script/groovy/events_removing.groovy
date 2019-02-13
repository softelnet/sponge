/**
 * Sponge Knowledge base
 * Removing scheduled events
 */

void onInit() {
    sponge.setVariable("eventCounter", 0)
    sponge.setVariable("allowNumber", 2)
}

class Trigger1 extends Trigger {
    void onConfigure() {
        this.withEvent("e1")
    }
    void onRun(Event event) {
    	int eventCounter = sponge.getVariable("eventCounter")
    	eventCounter += 1
        sponge.setVariable("eventCounter", eventCounter)
        this.logger.debug("Received event {}, counter: {}", event.name, eventCounter)
        if (eventCounter > sponge.getVariable("allowNumber")) {
        	this.logger.debug("This line should not be displayed!")
        }
    }
}

class Trigger2 extends Trigger {
    void onConfigure() {
        this.withEvent("e2")
    }
    void onRun(Event event) {
        this.logger.debug("Removing entry")
        sponge.removeEvent(sponge.getVariable("eventEntry"))
    }
}

void onStartup() {
    int start = 500
    int interval = 1000
    sponge.setVariable("eventEntry", sponge.event("e1").sendAfter(start, interval))
    sponge.event("e2").sendAfter(interval * sponge.getVariable("allowNumber"))
}
