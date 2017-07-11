# Sponge Knowledge base
# Deep and shallow event clone policy.

java_import java.lang.Boolean
java_import java.lang.Integer
java_import java.util.Collections
java_import java.util.HashMap
java_import java.util.ArrayList

def onInit
    # Variables for assertions only
    $events = Collections.synchronizedMap(HashMap.new)
    $events.put("defaultClonePolicy", ArrayList.new)
    $events.put("deepClonePolicy", ArrayList.new)
    $events.put("shallowClonePolicy", ArrayList.new)
    $EPS.setVariable("events", $events)
end

class ClonePolicyTrigger < Trigger
    def configure
        self.events = ["defaultClonePolicy", "deepClonePolicy", "shallowClonePolicy"]
    end
    def run(event)
        $events.get(event.name).add(event)
    	self.logger.debug("Processing event: {}", event.name)
    	map = event.get("map")
    	self.logger.debug("map attribute (before) {}", map)
    	map.put("a", "Value " + $events.get(event.name).size().to_s);
    	self.logger.debug("map attribute (after) {}", map)
    end
end

def onStartup
    setEventAttributes = lambda { |event|
        hash = HashMap.new
        hash.put("a", "Value 0")
        hash.put("b", [Boolean::TRUE].to_java(:Boolean)) # Convert to Java array which is serializable.
        event.set("map", hash)
        event.set("integer", Integer.new(10))
    }

    $EPS.event("defaultClonePolicy").modify(setEventAttributes).sendAfter(100, 1000)
    $EPS.event("deepClonePolicy", EventClonePolicy::DEEP).modify(setEventAttributes).sendAfter(200, 1000)
    $EPS.event("shallowClonePolicy", EventClonePolicy::SHALLOW).modify(setEventAttributes).sendAfter(400, 1000)
end
