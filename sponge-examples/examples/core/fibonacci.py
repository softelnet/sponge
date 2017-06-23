"""
Sponge Knowledge base
Fibonacci numbers

Note that this example is intended ONLY to show how to send a chain of events (each carrying a Fibonacci number as an attribute).
If you need to generate Fibonacci numbers you should use a simpler method.
"""
maxIndex = 1000

def onInit():
    EPS.setVariable("f(maxIndex)", None)

class FibonacciRule(Rule):
    def configure(self):
        self.events = ["f f1", "f f2"]
        self.setConditions("f2", lambda rule, event: event.get("index") - rule.getEvent("f1").get("index") == 1)
    def run(self, event):
        f1 = self.getEvent("f1")
        f2 = self.getEvent("f2")
        index = f2.get("index") + 1
        value = f1.get("value") + f2.get("value")
        global maxIndex
        if index <= maxIndex:
            EPS.event("f").set("index", index).set("value", value).send()
        if index == maxIndex:
            EPS.setVariable("f(maxIndex)", value)

class LogTrigger(Trigger):
    def configure(self):
        self.eventName = "f"
    def run(self, event):
        self.logger.debug("f({}) = {}", event.get("index"), event.get("value"))

def onStartup():
    EPS.event("f").set("index", 0).set("value", 0).send()
    EPS.event("f").set("index", 1).set("value", 1).send()
