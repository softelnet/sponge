"""
Sponge Knowledge base
Test - event overload
"""

from java.util.concurrent import TimeUnit
from org.openksavi.sponge.engine import QueueFullException

import random
import string

def onInit():
    EPS.setVariable("testStatus", None)

class A(Trigger):
    def onConfigure(self):
        self.event = "a"
    def onRun(self, event):
        self.logger.debug("A start")
        index = 0
        while True:
            if index % 5 == 0:
                self.logger.debug("Statistics {}", EPS.engine.statisticsManager.summary)
            try:
                EPS.event("b").set("payload", ''.join(['*' for i in xrange(0, 1000)])).send()
            except QueueFullException as e:
                self.logger.debug("Statistics {}", EPS.engine.statisticsManager.summary)
                EPS.setVariable("testStatus", e)
                self.logger.debug("Expected exception message: {}", e.message)
                break;
            index += 1
        self.logger.debug("A stop")

class B(Trigger):
    def onConfigure(self):
        self.event = "b"
    def onRun(self, event):
        self.logger.debug("Received {}", event.name)

def onStartup():
    EPS.event("a").send()
