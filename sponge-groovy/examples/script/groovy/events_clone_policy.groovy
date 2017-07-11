/**
 * Sponge Knowledge base
 * Deep and shallow event clone policy.
 */

void onInit() {
    // Variables for assertions only
    Map events = Collections.synchronizedMap(new HashMap())
    events.put("defaultClonePolicy", new ArrayList())
    events.put("deepClonePolicy", new ArrayList())
    events.put("shallowClonePolicy", new ArrayList())
    EPS.setVariable("events", events)
}

class ClonePolicyTrigger extends Trigger {
    void configure() {
        this.events = ["defaultClonePolicy", "deepClonePolicy", "shallowClonePolicy"]
    }
    void run(Event event) {
        Map events = EPS.getVariable("events")
        events.get(event.name).add(event)
    	this.logger.debug("Processing event: {}", event.name)
    	Map map = event.get("map")
    	this.logger.debug("map attribute (before): {}", map)
    	map.put("a", "Value " + events.get(event.name).size())
    	this.logger.debug("map attribute (after): {}", map)
    }
}

void onStartup() {
    def setEventAttributes = { Event event ->
        Map hash = new HashMap()
        hash.put("a", "Value 0")
        hash.put("b", [Boolean.TRUE])
        event.set("map", hash)
        event.set("integer", new Integer(10))
    }

    EPS.event("defaultClonePolicy").modify(setEventAttributes).sendAfter(100, 1000)
    EPS.event("deepClonePolicy", EventClonePolicy.DEEP).modify(setEventAttributes).sendAfter(200, 1000)
    EPS.event("shallowClonePolicy", EventClonePolicy.SHALLOW).modify(setEventAttributes).sendAfter(400, 1000)
}
