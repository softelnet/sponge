"""
Sponge Knowledge base
Triggers load test.
"""

class A(Trigger):
    def onConfigure(self):
        self.event = "a"
    def onRun(self, event):
        pass

class B(Trigger):
    def onConfigure(self):
        self.event = "b"
    def onRun(self, event):
        pass

class C(Trigger):
    def onConfigure(self):
        self.event = "c"
    def onRun(self, event):
        pass

class Stats(Trigger):
    def onConfigure(self):
        self.event = "stats"
    def onRun(self, event):
        self.logger.debug("Statistics: {}", sponge.engine.statisticsManager.summary)

def onStartup():
    sponge.event("stats").sendAfter(1000, 10000)
    interval = 1

    for i in range(10):
        sponge.event("a").sendAfter(0, interval)
        sponge.event("b").sendAfter(0, interval)
        sponge.event("c").sendAfter(0, interval)
