/**
 * Sponge Knowledge base
 * Deep and shallow event clone policy.
 */

function onInit() {
    // Variables for assertions only
    var events = java.util.Collections.synchronizedMap(new java.util.HashMap());
    events.put("defaultClonePolicy", new java.util.ArrayList());
    events.put("deepClonePolicy", new java.util.ArrayList());
    events.put("shallowClonePolicy", new java.util.ArrayList());
    sponge.setVariable("events", events);
}

var ClonePolicyTrigger = Java.extend(Trigger, {
    onConfigure: function(self) {
        self.events = ["defaultClonePolicy", "deepClonePolicy", "shallowClonePolicy"];
    },
    onRun: function(self, event) {
        var events = sponge.getVariable("events");
        events.get(event.name).add(event);
        	self.logger.debug("Processing event: {}", event.name);
        	var map = event.get("map");
        	self.logger.debug("map attribute (before): {}", map);
        	map.put("a", "Value " + events.get(event.name).size());
        	self.logger.debug("map attribute (after): {}", map);
    }
});

function setEventAttributes(event) {
    var hash = new java.util.HashMap();
    hash.put("a", "Value 0");
    hash.put("b", Java.to([java.lang.Boolean.TRUE])); // Nashorn array is not serializable so we have to convert to Java array.
    event.set("map", hash);
    event.set("integer", new java.lang.Integer(10));
}

function onStartup() {
    sponge.event("defaultClonePolicy").modify(setEventAttributes).sendAfter(100, 1000);
    sponge.event("deepClonePolicy", EventClonePolicy.DEEP).modify(setEventAttributes).sendAfter(200, 1000);
    sponge.event("shallowClonePolicy", EventClonePolicy.SHALLOW).modify(setEventAttributes).sendAfter(400, 1000);
}
