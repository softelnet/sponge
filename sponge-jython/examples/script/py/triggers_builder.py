"""
Sponge Knowledge base
Trigger builders
"""

from java.util.concurrent.atomic import AtomicBoolean, AtomicInteger

def onInit():
    # Variables for assertions only
    sponge.setVariable("receivedEventA", AtomicBoolean(False))
    sponge.setVariable("receivedEventBCount", AtomicInteger(0))

def onLoad():
    def triggerAOnRun(trigger, event):
        trigger.logger.debug("Received event: {}", event.name)
        sponge.getVariable("receivedEventA").set(True)

    sponge.enable(TriggerBuilder("TriggerA").withEvent("a").withOnRun(triggerAOnRun))

    def triggerBOnRun(trigger, event):
        trigger.logger.debug("Received event: {}", event.name)
        receivedEventBCount = sponge.getVariable("receivedEventBCount")
        if receivedEventBCount.get() == 0:
            trigger.logger.debug("Statistics: {}", sponge.statisticsSummary)
        receivedEventBCount.incrementAndGet()

    sponge.enable(TriggerBuilder("TriggerB").withEvent("b").withOnRun(triggerBOnRun))

def onStartup():
    sponge.logger.debug("Startup {}, triggers: {}", sponge.info, sponge.engine.triggers)
    sponge.logger.debug("Knowledge base name: {}", sponge.kb.name)
    sponge.event("a").send()
    sponge.event("b").sendAfter(200, 200)
