"""
Sponge Knowledge base
Test - events synchronization
"""

from java.util.concurrent import TimeUnit
from java.util import Collections, LinkedHashMap
from java.util.concurrent.atomic import AtomicBoolean

def onInit():
    global running
    running = Collections.synchronizedMap(LinkedHashMap())
    running.put("Rule1", AtomicBoolean(False))
    running.put("Rule2", AtomicBoolean(False))
    running.put("Rule3", AtomicBoolean(False))
    running.put("Rule4", AtomicBoolean(False))
    sponge.setVariable("testStatus", None)

def doWork(rule):
    running.get(rule.meta.name).set(True)
    rule.logger.debug("Work start")
    TimeUnit.SECONDS.sleep(10)
    rule.logger.debug("Work stop")
    running.get(rule.meta.name).set(False)

def assertState(condition, message):
    if not condition:
        sponge.setVariable("testStatus", message)
        raise SpongeException(message)

class Rule1(Rule):
    def onConfigure(self):
        self.withEvents(["e1", "e2"])
    def onRun(self, event):
        doWork(self)

class Rule2(Rule):
    def onConfigure(self):
        self.withEvents(["e2", "e3"])
    def onRun(self, event):
        assertState(not running.get("Rule1").get(), "Rule1 should be finished by now")
        doWork(self)
        sponge.setVariable("testStatus", "OK")

class Rule3(Rule):
    def onConfigure(self):
        self.withEvents(["e4", "e5"])
    def onRun(self, event):
        global running
        assertState(running.get("Rule1").get(), "Rule1 should be running")
        assertState(not running.get("Rule2").get(), "Rule2 should be waiting")
        doWork(self)

class Rule4(Rule):
    def onConfigure(self):
        self.withEvents(["e6", "e7"])
    def onRun(self, event):
        global running
        assertState(running.get("Rule1").get(), "Rule1 should be running")
        assertState(not running.get("Rule2").get(), "Rule2 should be waiting")
        assertState(running.get("Rule3").get(), "Rule3 should be running")
        doWork(self)

def onStartup():
    sponge.event("e1").sendAfter(1000)
    sponge.event("e2").sendAfter(2000)
    sponge.event("e3").sendAfter(3000)
    sponge.event("e4").sendAfter(4000)
    sponge.event("e5").sendAfter(5000)
    sponge.event("e6").sendAfter(6000)
    sponge.event("e7").sendAfter(7000)
