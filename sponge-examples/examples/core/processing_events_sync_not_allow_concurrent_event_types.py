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
    EPS.setVariable("testStatus", None)

def doWork(rule):
    running.get(rule.name).set(True)
    rule.logger.debug("Work start")
    TimeUnit.SECONDS.sleep(2)
    rule.logger.debug("Work stop")
    running.get(rule.name).set(False)

def assertState(condition, message):
    if not condition:
        EPS.setVariable("testStatus", message)
        raise EpsException(message)

class Rule1(Rule):
    def configure(self):
        self.events = ["e1", "e2"]
    def run(self, event):
        doWork(self)

class Rule2(Rule):
    def configure(self):
        self.events = ["e2", "e3"]
    def run(self, event):
        assertState(not running.get("Rule1").get(), "Rule1 should be finished by now")
        doWork(self)
        EPS.setVariable("testStatus", "OK")

class Rule3(Rule):
    def configure(self):
        self.events = ["e4", "e5"]
    def run(self, event):
        global running
        assertState(running.get("Rule1").get(), "Rule1 should be running")
        assertState(not running.get("Rule2").get(), "Rule2 should be waiting")
        doWork(self)

class Rule4(Rule):
    def configure(self):
        self.events = ["e6", "e7"]
    def run(self, event):
        global running
        assertState(running.get("Rule1").get(), "Rule1 should be running")
        assertState(not running.get("Rule2").get(), "Rule2 should be waiting")
        assertState(running.get("Rule3").get(), "Rule3 should be running")
        doWork(self)

def onStartup():
    EPS.event("e1").sendAfter(100)
    EPS.event("e2").sendAfter(200)
    EPS.event("e3").sendAfter(300)
    EPS.event("e4").sendAfter(400)
    EPS.event("e5").sendAfter(500)
    EPS.event("e6").sendAfter(600)
    EPS.event("e7").sendAfter(700)
