"""
Sponge Knowledge base
Rules - Event pattern
"""

from org.openksavi.sponge.examples.util import CorrelationEventsLog

def onInit():
    # Variables for assertions only
    global correlationEventsLog
    correlationEventsLog = CorrelationEventsLog()
    sponge.setVariable("correlationEventsLog", correlationEventsLog)

class NameRule(Rule):
    def onConfigure(self):
        self.withEvents(["a1 a", "b1 b"])
    def onRun(self, event):
        global correlationEventsLog
        correlationEventsLog.addEvents(self.meta.name, self)

class PatternRule(Rule):
    def onConfigure(self):
        self.withEvents(["a.+ a", "b.+ b"])
    def onRun(self, event):
        self.logger.debug("Received matching events ({}, {})", self.getEvent("a").name, event.name)
        global correlationEventsLog
        correlationEventsLog.addEvents(self.meta.name, self)

def onStartup():
    for name in ["a1", "b1", "a2", "b2", "a", "b", "a1", "b2"]:
        sponge.event(name).set("label", name).send()
