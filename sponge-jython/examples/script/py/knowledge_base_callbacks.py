"""
Sponge Knowledge base
Using knowledge base callbacks.
"""

from java.util.concurrent.atomic import AtomicBoolean, AtomicInteger
from org.openksavi.sponge.examples.util import TestStatus

class ReloadTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("reload")
    def onRun(self, event):
        self.logger.debug("Received event: {}", event.name)
        sponge.reload()

def onInit():
    # Variables for assertions only
    sponge.setVariable("onInitCalled", AtomicBoolean(False))
    sponge.setVariable("onBeforeLoadCalled", AtomicInteger(0))
    sponge.setVariable("onLoadCalled", AtomicInteger(0))
    sponge.setVariable("onAfterLoadCalled", AtomicInteger(0))
    sponge.setVariable("onStartupCalled", AtomicBoolean(False))
    sponge.setVariable("onBeforeReloadCalled", AtomicBoolean(False))
    sponge.setVariable("onAfterReloadCalled", AtomicBoolean(False))

    sponge.logger.debug("onInit")
    sponge.getVariable("onInitCalled").set(True)

def onBeforeLoad():
    sponge.logger.debug("onBeforeLoad")
    sponge.getVariable("onBeforeLoadCalled").incrementAndGet()

def onLoad():
    sponge.logger.debug("onLoad")
    sponge.getVariable("onLoadCalled").incrementAndGet()

def onAfterLoad():
    sponge.logger.debug("onAfterLoad")
    sponge.getVariable("onAfterLoadCalled").incrementAndGet()

def onStartup():
    sponge.logger.debug("onStartup")
    sponge.getVariable("onStartupCalled").set(True)
    sponge.event("reload").sendAfter(1000)

def onShutdown():
    sponge.logger.debug("onShutdown")
    # Using Java static field because all variables will be lost after shutdown.
    TestStatus.onShutdownCalled = True

def onBeforeReload():
    sponge.logger.debug("onBeforeReload")
    sponge.getVariable("onBeforeReloadCalled").set(True)

def onAfterReload():
    sponge.logger.debug("onAfterReload")
    sponge.getVariable("onAfterReloadCalled").set(True)
