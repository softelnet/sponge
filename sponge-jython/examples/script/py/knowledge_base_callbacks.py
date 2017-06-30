"""
Sponge Knowledge base
Using knowledge base callbacks.
"""

from java.util.concurrent.atomic import AtomicBoolean, AtomicInteger
from org.openksavi.sponge.test import TestStatus

class ReloadTrigger(Trigger):
    def configure(self):
        self.eventName = "reload"
    def run(self, event):
        self.logger.debug("Received event: {}", event.name)
        # EPS.requestReload()
        EPS.reload()

def onInit():
    # Variables for assertions only
    EPS.setVariable("onInitCalled", AtomicBoolean(False))
    EPS.setVariable("onLoadCalled", AtomicInteger(0))
    EPS.setVariable("onStartupCalled", AtomicBoolean(False))
    EPS.setVariable("onBeforeReloadCalled", AtomicBoolean(False))
    EPS.setVariable("onAfterReloadCalled", AtomicBoolean(False))

    EPS.logger.debug("onInit")
    EPS.getVariable("onInitCalled").set(True)

def onLoad():
    EPS.logger.debug("onLoad")
    EPS.getVariable("onLoadCalled").incrementAndGet()

def onStartup():
    EPS.logger.debug("onStartup")
    EPS.getVariable("onStartupCalled").set(True)
    EPS.event("reload").sendAfter(1000)

def onShutdown():
    EPS.logger.debug("onShutdown")
    # Using Java static field because all variables will be lost after shutdown.
    TestStatus.onShutdownCalled = True

def onBeforeReload():
    EPS.logger.debug("onBeforeReload")
    EPS.getVariable("onBeforeReloadCalled").set(True)

def onAfterReload():
    EPS.logger.debug("onAfterReload")
    EPS.getVariable("onAfterReloadCalled").set(True)
