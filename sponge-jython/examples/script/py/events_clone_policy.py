"""
Sponge Knowledge base
Deep and shallow event clone policy.
"""

from java.lang import Boolean, Integer
from java.util import Collections, HashMap, ArrayList

def onInit():
    global events
    # Variables for assertions only
    events = Collections.synchronizedMap(HashMap())
    events.put("defaultClonePolicy", ArrayList())
    events.put("deepClonePolicy", ArrayList())
    events.put("shallowClonePolicy", ArrayList())
    sponge.setVariable("events", events)

class ClonePolicyTrigger(Trigger):
    def onConfigure(self):
        self.events = ["defaultClonePolicy", "deepClonePolicy", "shallowClonePolicy"]
    def onRun(self, event):
        global events
        events.get(event.name).add(event)
        self.logger.debug("Processing event: {}", event.name)
        map = event.get("map")
        self.logger.debug("map attribute (before): {}", map)
        map.put("a", "Value " + str(events.get(event.name).size()));
        self.logger.debug("map attribute (after): {}", map)

def onStartup():
    def setEventAttributes(event):
        hash = HashMap()
        hash.put("a", "Value 0")
        hash.put("b", [Boolean.TRUE])
        event.set("map", hash)
        event.set("integer", Integer(10))

    sponge.event("defaultClonePolicy").modify(setEventAttributes).sendAfter(100, 1000)
    sponge.event("deepClonePolicy", EventClonePolicy.DEEP).modify(setEventAttributes).sendAfter(200, 1000)
    sponge.event("shallowClonePolicy", EventClonePolicy.SHALLOW).modify(setEventAttributes).sendAfter(400, 1000)
