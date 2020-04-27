"""
Sponge Knowledge base
gRPC demo - Counter - sender.
"""

from java.util.concurrent.atomic import AtomicLong

def onInit():
    # Initialize a counter as a Sponge variable.
    sponge.setVariable("counter", AtomicLong(1))

class CounterSender(Trigger):
    def onConfigure(self):
        self.withEvent("counterSender")
    def onRun(self, event):
        # Increment the counter.
        value = sponge.getVariable("counter").incrementAndGet()
        # Send a counterNotification event that can be subscribed by a GUI.
        sponge.event("counterNotification").set({"counter":value}).label("The counter is " + str(value)).send()

def onStartup():
    # Send counterSender event every 5 seconds.
    sponge.event("counterSender").sendEvery(Duration.ofSeconds(5))
