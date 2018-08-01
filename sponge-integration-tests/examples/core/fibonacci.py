"""
Sponge Knowledge base
Fibonacci numbers

Note that this example is intended ONLY to show how to send a chain of events (each carrying a Fibonacci number as an attribute).
If you need to generate Fibonacci numbers you should use a simpler method.
"""
maxIndex = 100

def onInit():
    sponge.setVariable("f(maxIndex)", None)

class FibonacciRule(Rule):
    def onConfigure(self):
        self.events = ["f f1", "f f2"]
        self.addConditions("f2", lambda rule, event: event.get("index") - rule.getEvent("f1").get("index") == 1)
    def onRun(self, event):
        f1 = self.getEvent("f1")
        f2 = self.getEvent("f2")
        index = f2.get("index") + 1
        value = f1.get("value") + f2.get("value")
        global maxIndex
        if index <= maxIndex:
            sponge.event("f").set("index", index).set("value", value).send()
        if index == maxIndex:
            sponge.setVariable("f(maxIndex)", value)

class LogTrigger(Trigger):
    def onConfigure(self):
        self.event = "f"
    def onRun(self, event):
        self.logger.debug("f({}) = {}", event.get("index"), event.get("value"))

def onStartup():
    sponge.event("f").set("index", 0).set("value", 0).send()
    sponge.event("f").set("index", 1).set("value", 1).send()
